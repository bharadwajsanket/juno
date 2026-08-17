package bharadwaj.juno.music.ui.adaptive.pane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveSpacing
import bharadwaj.juno.music.ui.adaptive.tokens.ContentWidthTokens

/**
 * Flexible dual-pane layout component abstraction.
 * Renders list-detail or side-by-side split screens when in dual pane mode,
 * or switches smoothly to single pane view on phones.
 */
@Composable
fun AdaptivePaneLayout(
    primaryPane: @Composable () -> Unit,
    secondaryPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    paneState: AdaptivePaneState = rememberAdaptivePaneState(bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveWindowInfo.current),
    paneSpacing: Dp = LocalAdaptiveSpacing.current.gridSpacing,
    primaryRatio: Float = ContentWidthTokens.DualPanePrimaryRatio
) {
    if (paneState.isDualPaneActive) {
        Row(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(primaryRatio)) {
                primaryPane()
            }
            Spacer(modifier = Modifier.width(paneSpacing))
            Box(modifier = Modifier.weight(1.0f - primaryRatio)) {
                secondaryPane()
            }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            when (paneState.currentFocusPane) {
                PaneRole.Primary -> primaryPane()
                PaneRole.Secondary -> secondaryPane()
                else -> primaryPane()
            }
        }
    }
}
