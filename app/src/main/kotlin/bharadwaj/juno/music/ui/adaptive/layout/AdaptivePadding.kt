package bharadwaj.juno.music.ui.adaptive.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveSpacing

/**
 * Modifier wrapper component for applying tokenized adaptive padding around elements.
 */
@Composable
fun AdaptivePadding(
    modifier: Modifier = Modifier,
    horizontal: Dp = LocalAdaptiveSpacing.current.windowMargin,
    vertical: Dp = LocalAdaptiveSpacing.current.contentPadding,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.padding(horizontal = horizontal, vertical = vertical),
        content = content
    )
}
