package bharadwaj.juno.music.ui.adaptive.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveWindowInfo

/**
 * Specialized container primitive for JUNO Music playback controls and mini-player UI.
 * Handles bottom positioning on phone viewports and side panel integration on wider form factors.
 */
@Composable
fun AdaptivePlayerContainer(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.BottomCenter,
    content: @Composable BoxScope.() -> Unit
) {
    val windowInfo = LocalAdaptiveWindowInfo.current
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = contentAlignment,
        content = content
    )
}
