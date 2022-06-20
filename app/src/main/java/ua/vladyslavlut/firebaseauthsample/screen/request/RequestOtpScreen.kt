package ua.vladyslavlut.firebaseauthsample.screen.request

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.vladyslavlut.firebaseauthsample.AppUiState
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicButton
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicCheckbox
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicField
import ua.vladyslavlut.firebaseauthsample.common.composable.PhoneField
import ua.vladyslavlut.firebaseauthsample.common.ext.basicButton
import ua.vladyslavlut.firebaseauthsample.common.ext.currentActivity
import ua.vladyslavlut.firebaseauthsample.common.ext.fieldModifier
import ua.vladyslavlut.firebaseauthsample.screen.AppViewModel
import ua.vladyslavlut.firebaseauthsample.ui.theme.FirebaseAuthSampleTheme
import ua.vladyslavlut.firebaseauthsample.R.string as AppText

@ExperimentalMaterialApi
@Composable
fun RequestOtpScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val uiState: AppUiState by viewModel.uiState
    val context: Context = LocalContext.current

    LaunchedEffect(Unit) {
        val activity = context.currentActivity ?: return@LaunchedEffect
        viewModel.initFirebasePhoneAuth(activity)
        viewModel.isAuthorized() //TODO
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                text = stringResource(AppText.phone_verification),
                modifier = Modifier.fieldModifier(),
                style = MaterialTheme.typography.h3,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            PhoneField(
                uiState.phone,
                viewModel::onPhoneChange,
                Modifier.fieldModifier()
            )
            BasicField(
                text = AppText.language,
                modifier = Modifier.fieldModifier(),
                value = uiState.language,
                onNewValue = viewModel::onLanguageChange
            )
            BasicCheckbox(
                checked = uiState.useSystemLanguage,
                modifier = Modifier.fieldModifier(),
                text = "Use app language",
                onCheckedChange = viewModel::onUseSystemLanguageChange
            )
            BasicCheckbox(
                checked = uiState.forceCaptchaEnabled,
                modifier = Modifier.fieldModifier(),
                text = "Enable force reCAPTCHA verification",
                onCheckedChange = viewModel::onForceCaptchaChange
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        BasicButton(
            AppText.request_otp,
            Modifier.basicButton(),
            enabled = !uiState.isLoading
        ) {
            viewModel.requestCode()
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun RequestOtpScreenPreview() {
    FirebaseAuthSampleTheme {
        RequestOtpScreen(AppViewModel())
    }
}