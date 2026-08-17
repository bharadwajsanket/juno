package bharadwaj.juno.music.ui.adaptive.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveDimensions
import bharadwaj.juno.music.ui.adaptive.utils.adaptiveMaxWidth

/**
 * Composable container that bounds maximum content width for comfortable scanning on larger viewports.
 */
@Composable
fun AdaptiveContentWidth(
    modifier: Modifier = Modifier,
    maxWidth: Dp = LocalAdaptiveDimensions.current.maxReadableWidth,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.adaptiveMaxWidth(maxWidth),
        contentAlignment = contentAlignment,
        content = content
    )
}
