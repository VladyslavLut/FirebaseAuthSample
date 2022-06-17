package ua.vladyslavlut.firebaseauthsample.screen.success

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicButton
import ua.vladyslavlut.firebaseauthsample.common.composable.BasicToolbar
import ua.vladyslavlut.firebaseauthsample.common.ext.fieldModifier
import ua.vladyslavlut.firebaseauthsample.screen.AppViewModel
import ua.vladyslavlut.firebaseauthsample.R.string as AppText


@Composable
fun SuccessScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BasicToolbar(canPopUp = true, popUp = viewModel.navigator::popUp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(AppText.success),
            modifier = Modifier
                .fieldModifier()
                .align(Alignment.Center),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )

        BasicButton(
            text = AppText.signOut,
            modifier = Modifier
                .fieldModifier()
                .align(Alignment.BottomCenter),
            action = viewModel::signOut
        )
    }
}

@Preview
@Composable
fun SuccessScreenPreview(){
    SuccessScreen(viewModel = AppViewModel())
}