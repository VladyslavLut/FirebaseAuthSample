package ua.vladyslavlut.firebaseauthsample

import android.content.res.Resources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import ua.vladyslavlut.firebaseauthsample.common.snackbar.SnackbarManager
import ua.vladyslavlut.firebaseauthsample.screen.AppViewModel
import ua.vladyslavlut.firebaseauthsample.screen.request.RequestOtpScreen
import ua.vladyslavlut.firebaseauthsample.screen.success.SuccessScreen
import ua.vladyslavlut.firebaseauthsample.screen.verify.VerifyOtpScreen
import ua.vladyslavlut.firebaseauthsample.ui.theme.FirebaseAuthSampleTheme

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun FirebaseAuthApp() {
    FirebaseAuthSampleTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val appState = rememberAppState()
            val appViewModelFactory = AppViewModel.Factory(
                Firebase.auth,
                NavControllerNavigator(appState.navController)
            )
            val appViewModel: AppViewModel = viewModel(factory = appViewModelFactory)

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(snackbarData, contentColor = MaterialTheme.colors.onPrimary)
                        }
                    )
                },
                scaffoldState = appState.scaffoldState
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = REQUEST_OTP_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) { makeNavGraph(appViewModel, appState) }
            }
        }
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
    AppState(scaffoldState, navController, snackbarManager, resources, coroutineScope)
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun NavGraphBuilder.makeNavGraph(
    viewModel: AppViewModel,
    appState: AppState
) {
    composable(REQUEST_OTP_SCREEN) {
        RequestOtpScreen(viewModel)
    }

    composable(VERIFY_OTP_SCREEN) {
        VerifyOtpScreen(viewModel)
    }

    composable(SUCCESS_SCREEN) {
        SuccessScreen(viewModel)
    }
}