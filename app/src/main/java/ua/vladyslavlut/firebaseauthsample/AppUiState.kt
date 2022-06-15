package ua.vladyslavlut.firebaseauthsample

data class AppUiState(
    val phone: String = "",
    val code: String = "",
    val language: String = "en",
    val timeout: Long = 0L
)