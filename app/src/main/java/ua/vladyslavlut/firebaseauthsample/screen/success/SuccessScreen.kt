package ua.vladyslavlut.firebaseauthsample.screen.success

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ua.vladyslavlut.firebaseauthsample.R.string as AppText
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicToolbar
import ua.vladyslavlut.firebaseauthsample.common.ext.fieldModifier
import ua.vladyslavlut.firebaseauthsample.screen.AppViewModel


@Composable
fun SuccessScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BasicToolbar(canPopUp = true, popUp = { viewModel.navigator.popUp() })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(AppText.success),
            modifier = Modifier.fieldModifier().align(Alignment.Center),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )
    }
}