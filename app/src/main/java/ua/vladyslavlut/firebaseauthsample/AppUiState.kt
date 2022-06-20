package ua.vladyslavlut.firebaseauthsample

data class AppUiState(
    val phone: String = "",
    val code: String = "",
    val language: String = "",
    val timeout: Long = 0L,
    val forceCaptchaEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val useSystemLanguage: Boolean = false
)