package ua.vladyslavlut.firebaseauthsample.common.composable

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.vladyslavlut.firebaseauthsample.R.string as AppText

private val ToolBarHeight = 56.dp

@Preview
@Composable
fun BasicToolbar(
    modifier: Modifier = Modifier,
    canPopUp: Boolean = false,
    popUp: () -> Unit = {},
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .height(ToolBarHeight)
            .fillMaxWidth()
            .then(modifier)
    ) {
        if (canPopUp) {
            IconButton(
                onClick = {
                    Log.d("Navigation", "click")
                    popUp()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(AppText.back),
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        content()
    }

}

@Composable
fun ActionToolbar(
    @StringRes title: Int,
    @DrawableRes endActionIcon: Int,
    modifier: Modifier,
    endAction: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(title)) },
        backgroundColor = toolbarColor(),
        actions = {
            Box(modifier) {
                IconButton(onClick = endAction) {
                    Icon(
                        painter = painterResource(endActionIcon),
                        contentDescription = "Action"
                    )
                }
            }
        }
    )
}

@Composable
private fun toolbarColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return MaterialTheme.colors.background
}
