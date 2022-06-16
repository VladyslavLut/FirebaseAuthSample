package ua.vladyslavlut.firebaseauthsample.screen

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.vladyslavlut.firebaseauthsample.*
import ua.vladyslavlut.firebaseauthsample.common.ext.isValidPhone
import ua.vladyslavlut.firebaseauthsample.common.snackbar.SnackbarManager
import ua.vladyslavlut.firebaseauthsample.common.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import ua.vladyslavlut.firebaseauthsample.error.*
import java.util.concurrent.TimeUnit
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import ua.vladyslavlut.firebaseauthsample.R.string as AppText

class AppViewModel(
    private val auth: FirebaseAuth? = null,
    val navigator: Navigator = object : Navigator {}
) : ViewModel() {
    var uiState = mutableStateOf(AppUiState())
        private set

    private val showErrorExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    private val phone: String get() = uiState.value.phone
    private val code: String get() = uiState.value.code

    private val phoneVerificationListener = PhoneVerificationListener()
    private var storedVerificationId: String? = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var credential: PhoneAuthCredential? = null

    private var phoneVerificationBuilder: PhoneAuthOptions.Builder? = auth?.let {
        PhoneAuthOptions.newBuilder(auth)
    }

    override fun onCleared() {
        auth?.signOut()
        super.onCleared()
    }

    fun initFirebasePhoneAuth(activity: Activity) {
        phoneVerificationBuilder?.apply {
            setActivity(activity)
            setTimeout(RESEND_OTP_TIMEOUT, TimeUnit.SECONDS)
            setCallbacks(phoneVerificationListener)
        }
    }

    fun isAuthorized() {
        val currentUser = auth?.currentUser
        //TODO
    }

    fun onPhoneChange(newValue: String) {
        uiState.value = uiState.value.copy(phone = newValue)
    }

    fun onCodeChange(newValue: String) {
        uiState.value = uiState.value.copy(code = newValue)
    }

    fun onLanguageChange(newValue: String) {
        uiState.value = uiState.value.copy(language = newValue)
        if (newValue.length >= 2) {
            auth?.setLanguageCode(newValue)
        }
    }

    fun onForceCaptchaChange(newValue: Boolean) {
        uiState.value = uiState.value.copy(forceCaptchaEnabled = newValue)
        FirebaseAuth.getInstance().firebaseAuthSettings.forceRecaptchaFlowForTesting(newValue)
    }

    fun requestCode() {
        if (!phone.isValidPhone()) {
            SnackbarManager.showMessage(AppText.phone_error)
            return
        }

        val options = phoneVerificationBuilder?.setPhoneNumber(phone)?.build() ?: return
        PhoneAuthProvider.verifyPhoneNumber(options)
        viewModelScope.launch(showErrorExceptionHandler) {
            timeoutResendCode(RESEND_OTP_TIMEOUT)
        }
    }

    fun resendCode() {
        val token = resendToken ?: return
        val options = phoneVerificationBuilder
            ?.setForceResendingToken(token)
            ?.build() ?: return
        PhoneAuthProvider.verifyPhoneNumber(options)
        viewModelScope.launch(showErrorExceptionHandler) {
            timeoutResendCode(RESEND_OTP_TIMEOUT)
        }
    }

    fun verifyCode() {
        credential = PhoneAuthProvider.getCredential(storedVerificationId ?: TODO("handle"), code)
        viewModelScope.launch(showErrorExceptionHandler) {
            val creds = credential ?: return@launch
            val token = signInWithPhoneAuthCredential(creds)
            Log.d(TAG, "idToken:$token")
            navigator.navigate(SUCCESS_SCREEN)
        }
    }

    fun onError(error: Throwable) {
        SnackbarManager.showMessage(error.toSnackbarMessage())
    }

    private suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): String {
        val auth = auth ?: throw FirebaseAuthNotInitialized()
        val user: FirebaseUser = suspendCoroutine {
            auth.signInWithCredential(credential).addOnCompleteListener(AuthCompleteListener(it))
        }
        val tokenId: String = suspendCoroutine {
            user.getIdToken(false).addOnCompleteListener(TokenIdCompleteListener(it))
        }
        return tokenId
    }

    private suspend fun timeoutResendCode(timeout: Long) {
        uiState.value = uiState.value.copy(timeout = timeout)
        delay(ONE_SECOND)
        val remains = timeout - 1
        if (remains >= 0) timeoutResendCode(remains)
    }

    private inner class PhoneVerificationListener :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            this@AppViewModel.credential = credential
            viewModelScope.launch(showErrorExceptionHandler) {
                val token = signInWithPhoneAuthCredential(credential)
                navigator.navigate(SUCCESS_SCREEN)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
            onError(e)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token
            navigator.navigate(VERIFY_OTP_SCREEN)
        }
    }

    private class AuthCompleteListener(
        private val continuation: Continuation<FirebaseUser>
    ) : OnCompleteListener<AuthResult> {
        override fun onComplete(task: Task<AuthResult>) {
            if (task.isSuccessful) {
                val user = task.result?.user
                if (user == null) continuation.resumeWith(failure(UserIsNullError()))
                else continuation.resumeWith(success(user))
            } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                continuation.resumeWith(failure(InvalidVerificationCodeError()))
            } else {
                continuation.resumeWith(failure(UnexpectedError()))
            }
        }
    }

    private class TokenIdCompleteListener(
        private val continuation: Continuation<String>
    ) : OnCompleteListener<GetTokenResult> {
        override fun onComplete(task: Task<GetTokenResult>) {
            if (task.isSuccessful) {
                val token = task.result?.token
                if (token.isNullOrEmpty()) continuation.resumeWith(failure(TokenIdIsNullError()))
                else continuation.resumeWith(success(token))
            } else {
                continuation.resumeWith(failure(UnexpectedError()))
            }
        }
    }

    class Factory(
        private val auth: FirebaseAuth,
        private val navigator: Navigator
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(auth, navigator) as T
        }
    }

    companion object {
        private const val TAG = "FirebaseAuth"
        private const val RESEND_OTP_TIMEOUT = 60L
        private const val ONE_SECOND = 1000L
    }
}