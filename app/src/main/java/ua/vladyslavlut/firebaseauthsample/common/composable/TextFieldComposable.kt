package ua.vladyslavlut.firebaseauthsample.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.vladyslavlut.firebaseauthsample.ui.theme.FirebaseAuthSampleTheme
import ua.vladyslavlut.firebaseauthsample.R.string as AppText

@Composable
fun BasicField(
    @StringRes text: Int,
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        placeholder = { Text(stringResource(text)) }
    )
}

@Composable
fun PhoneField(value: String, onNewValue: (String) -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(AppText.phone)
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        placeholder = { Text(description) },
        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = description) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        )
    )
}

//@Composable
//fun CodeField(value: String, onNewValue: (String) -> Unit, modifier: Modifier = Modifier) {
//    CodeField(value, AppText.code, onNewValue, modifier)
//}
//
//@Composable
//private fun CodeField(
//    value: String,
//    @StringRes placeholder: Int,
//    onNewValue: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    OutlinedTextField(
//        modifier = modifier,
//        value = value,
//        onValueChange = { onNewValue(it) },
//        placeholder = { Text(text = stringResource(placeholder)) },
//        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//    )
//}

@Preview(showBackground = true)
@Composable
fun CodeCell(
    modifier: Modifier = Modifier,
    value: String = "",
    isCursorVisible: Boolean = false
) {
    val scope = rememberCoroutineScope()
    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }

    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
        if (isCursorVisible) {
            scope.launch {
                delay(350)
                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        Text(
            text = if (isCursorVisible) cursorSymbol else value,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun CodeField(
    modifier: Modifier = Modifier,
    length: Int = 6,
    value: String = "",
    onValueChanged: (String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    TextField(
        value = value,
        onValueChange = {
            if (it.length <= length) {
                if (it.all { c -> c in '0'..'9' }) {
                    onValueChanged(it)
                }
                if (it.length >= length) {
                    keyboard?.hide()
                }
            }
        },
        modifier = Modifier
            .size(0.dp)
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(length) {
            CodeCell(
                modifier = modifier
                    .size(width = 45.dp, height = 60.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colors.surface)
                    .clickable {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    },
                value = value.getOrNull(it)?.toString() ?: "",
                isCursorVisible = value.length == it
            )
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun CodeFieldPreview() {
    FirebaseAuthSampleTheme {
        CodeField(value = "1234")
    }
}
