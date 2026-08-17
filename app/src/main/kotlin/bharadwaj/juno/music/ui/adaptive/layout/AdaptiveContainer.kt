package bharadwaj.juno.music.ui.adaptive.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bharadwaj.juno.music.ui.adaptive.utils.adaptiveContentWidth

/**
 * Surface/Box wrapper that automatically applies readable content width constraints and adaptive margins.
 */
@Composable
fun AdaptiveContainer(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.adaptiveContentWidth(),
        contentAlignment = contentAlignment,
        content = content
    )
}
