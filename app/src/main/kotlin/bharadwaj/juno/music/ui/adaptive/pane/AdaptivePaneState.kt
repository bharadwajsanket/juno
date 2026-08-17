package bharadwaj.juno.music.ui.adaptive.pane

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo

/**
 * State container managing multi-pane visibility and active pane focus.
 */
@Stable
class AdaptivePaneState(
    initialConfiguration: PaneConfiguration = PaneConfiguration.SinglePane
) {
    var paneConfiguration: PaneConfiguration by mutableStateOf(initialConfiguration)
        private set

    var currentFocusPane: PaneRole by mutableStateOf(PaneRole.Primary)

    val isDualPaneActive: Boolean get() = paneConfiguration.isDualPane || paneConfiguration.isTriplePane

    fun updateFromWindowInfo(windowInfo: AdaptiveWindowInfo) {
        paneConfiguration = windowInfo.preferredPaneConfiguration
    }

    fun navigateToDetail() {
        currentFocusPane = PaneRole.Secondary
    }

    fun navigateToPrimary() {
        currentFocusPane = PaneRole.Primary
    }
}

@Composable
fun rememberAdaptivePaneState(
    windowInfo: AdaptiveWindowInfo
): AdaptivePaneState {
    val state = remember { AdaptivePaneState(windowInfo.preferredPaneConfiguration) }
    state.updateFromWindowInfo(windowInfo)
    return state
}
