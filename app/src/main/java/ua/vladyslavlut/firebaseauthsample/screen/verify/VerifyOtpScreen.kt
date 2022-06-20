package ua.vladyslavlut.firebaseauthsample.screen.verify

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.vladyslavlut.firebaseauthsample.AppUiState
import ua.vladyslavlut.firebaseauthsample.R
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicButton
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicTextButton
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicToolbar
import ua.vladyslavlut.firebaseauthsample.common.composable.CodeField
import ua.vladyslavlut.firebaseauthsample.common.ext.basicButton
import ua.vladyslavlut.firebaseauthsample.common.ext.fieldModifier
import ua.vladyslavlut.firebaseauthsample.common.ext.textButton
import ua.vladyslavlut.firebaseauthsample.screen.AppViewModel
import ua.vladyslavlut.firebaseauthsample.ui.theme.FirebaseAuthSampleTheme
import ua.vladyslavlut.firebaseauthsample.R.string as AppText

@ExperimentalComposeUiApi
@Composable
fun VerifyOtpScreen(
    viewModel: AppViewModel
) {
    val uiState: AppUiState by viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicToolbar(canPopUp = true, popUp = { viewModel.navigator.popUp() })
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Text(
                text = stringResource(R.string.phone_verification),
                modifier = Modifier.fieldModifier(),
                style = MaterialTheme.typography.h3,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            CodeField(value = uiState.code, onValueChanged = viewModel::onCodeChange)

            val enabled = uiState.timeout <= 0
            val resendCodeText = stringResource(AppText.resend_otp)
            BasicTextButton(
                if (enabled) resendCodeText else "$resendCodeText (Wait ${uiState.timeout} seconds)",
                Modifier.textButton(),
                enabled
            ) {
                viewModel.resendCode()
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        BasicButton(
            R.string.verify_otp,
            Modifier.basicButton(),
            enabled = !uiState.isLoading
        ) {
            viewModel.verifyCode()
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun VerifyOtpScreenPreview() {
    FirebaseAuthSampleTheme {
        VerifyOtpScreen(AppViewModel())
    }
}