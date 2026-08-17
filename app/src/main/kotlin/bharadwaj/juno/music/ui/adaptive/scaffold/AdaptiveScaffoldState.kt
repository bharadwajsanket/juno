package bharadwaj.juno.music.ui.adaptive.scaffold

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import bharadwaj.juno.music.ui.adaptive.navigation.AdaptiveNavigationState
import bharadwaj.juno.music.ui.adaptive.pane.AdaptivePaneState
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo

/**
 * State holder for [AdaptiveScaffold].
 */
@Stable
class AdaptiveScaffoldState(
    val windowInfo: AdaptiveWindowInfo,
    val navigationState: AdaptiveNavigationState,
    val paneState: AdaptivePaneState,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
)

@Composable
fun rememberAdaptiveScaffoldState(
    windowInfo: AdaptiveWindowInfo,
    navigationState: AdaptiveNavigationState = bharadwaj.juno.music.ui.adaptive.navigation.rememberAdaptiveNavigationState(windowInfo),
    paneState: AdaptivePaneState = bharadwaj.juno.music.ui.adaptive.pane.rememberAdaptivePaneState(windowInfo),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): AdaptiveScaffoldState {
    return remember(windowInfo, navigationState, paneState, snackbarHostState) {
        AdaptiveScaffoldState(
            windowInfo = windowInfo,
            navigationState = navigationState,
            paneState = paneState,
            snackbarHostState = snackbarHostState
        )
    }
}
