package bharadwaj.juno.music.ui.adaptive.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveWindowInfo

/**
 * Architectural slot host for adaptive navigation.
 * Dynamically arranges navigation elements (Rail/Drawer on side, BottomBar on bottom) around content.
 */
@Composable
fun AdaptiveNavigationHost(
    modifier: Modifier = Modifier,
    navigationStyle: AdaptiveNavigationStyle = LocalAdaptiveWindowInfo.current.preferredNavigationStyle,
    bottomBarSlot: @Composable (() -> Unit)? = null,
    navigationRailSlot: @Composable (() -> Unit)? = null,
    permanentDrawerSlot: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    when (navigationStyle) {
        AdaptiveNavigationStyle.NavigationRail -> {
            Row(modifier = modifier.fillMaxSize()) {
                navigationRailSlot?.invoke()
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
        AdaptiveNavigationStyle.PermanentDrawer -> {
            Row(modifier = modifier.fillMaxSize()) {
                permanentDrawerSlot?.invoke()
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
        else -> {
            // Default Phone / Compact style: BottomBar layout handled via Scaffold or column
            Box(modifier = modifier.fillMaxSize()) {
                content()
            }
        }
    }
}
