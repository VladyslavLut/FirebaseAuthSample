package ua.vladyslavlut.firebaseauthsample.common.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BasicCheckbox(
    checked: Boolean,
    modifier: Modifier,
    text: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = modifier) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = text,
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        )
    }
}