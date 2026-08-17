package bharadwaj.juno.music.ui.adaptive.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo

/**
 * State manager for adaptive navigation elements.
 */
@Stable
class AdaptiveNavigationState(
    initialStyle: AdaptiveNavigationStyle = AdaptiveNavigationStyle.BottomBar
) {
    var navigationStyle: AdaptiveNavigationStyle by mutableStateOf(initialStyle)
        private set

    var isVisible: Boolean by mutableStateOf(true)

    fun updateStyleFromWindowInfo(windowInfo: AdaptiveWindowInfo) {
        navigationStyle = windowInfo.preferredNavigationStyle
    }

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}

@Composable
fun rememberAdaptiveNavigationState(
    windowInfo: AdaptiveWindowInfo
): AdaptiveNavigationState {
    val state = remember { AdaptiveNavigationState(windowInfo.preferredNavigationStyle) }
    state.updateStyleFromWindowInfo(windowInfo)
    return state
}
