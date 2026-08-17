package bharadwaj.juno.music.ui.adaptive.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveSpacing

/**
 * Container component that injects standard adaptive window padding around content.
 */
@Composable
fun AdaptiveContent(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val spacing = LocalAdaptiveSpacing.current
    Box(
        modifier = modifier.padding(horizontal = spacing.windowMargin, vertical = spacing.contentPadding),
        contentAlignment = contentAlignment,
        content = content
    )
}
