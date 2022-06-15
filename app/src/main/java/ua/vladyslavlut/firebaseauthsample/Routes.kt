package ua.vladyslavlut.firebaseauthsample

import androidx.navigation.NavHostController

interface Navigator {
    fun popUp() {}
    fun navigate(route: String) {}
    fun navigateAndPopUp(route: String, popUp: String) {}
    fun clearAndNavigate(route: String) {}
}

class NavControllerNavigator(
    private val navController: NavHostController
) : Navigator {
    override fun popUp() {
        navController.popBackStack()
    }

    override fun navigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    override fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    override fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}

const val REQUEST_OTP_SCREEN = "RequestOtpScreen"
const val VERIFY_OTP_SCREEN = "VerifyOtpScreen"
const val SUCCESS_SCREEN = "SuccessScreen"