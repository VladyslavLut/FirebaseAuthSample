package ua.vladyslavlut.firebaseauthsample.screen

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import java.util.concurrent.TimeUnit
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
    private val language: String get() = uiState.value.language

    private val phoneVerificationListener = PhoneVerificationListener()
    private var storedVerificationId: String? = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var credential: PhoneAuthCredential? = null

    private var phoneVerificationBuilder: PhoneAuthOptions.Builder? = auth?.let {
        PhoneAuthOptions.newBuilder(auth)
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
        auth?.setLanguageCode(newValue)
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

    fun verifyCode(navigate: (route: String) -> Unit) {
        credential = PhoneAuthProvider.getCredential(storedVerificationId ?: TODO("handle"), code)
        Log.d(TAG, "codeVerified:$credential")
        navigate(SUCCESS_SCREEN)
    }

    fun onError(error: Throwable) {
        SnackbarManager.showMessage(error.toSnackbarMessage())
    }

    private fun signInWithPhoneAuthCredential(activity: Activity, credential: PhoneAuthCredential) {
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
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
            navigator.navigate(SUCCESS_SCREEN)
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