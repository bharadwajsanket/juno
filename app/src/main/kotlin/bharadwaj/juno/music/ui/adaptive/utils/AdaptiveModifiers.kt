package bharadwaj.juno.music.ui.adaptive.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import bharadwaj.juno.music.ui.adaptive.tokens.ContentWidthTokens

/**
 * Restricts the maximum layout width of a element on large displays to preserve comfortable line length.
 */
fun Modifier.adaptiveContentWidth(
    maxWidth: Dp = ContentWidthTokens.MaxReadableWidth
): Modifier = this.composed {
    this.widthIn(max = maxWidth)
}

/**
 * Restricts maximum layout width to a specified Dp value.
 */
fun Modifier.adaptiveMaxWidth(
    maxWidth: Dp
): Modifier = this.composed {
    if (maxWidth == Dp.Unspecified) this else this.widthIn(max = maxWidth)
}

/**
 * Applies tokenized adaptive window padding (horizontal margin & vertical content padding).
 */
@Composable
fun Modifier.adaptiveWindowPadding(): Modifier = this.composed {
    val spacing = LocalAdaptiveSpacing.current
    this.padding(horizontal = spacing.windowMargin, vertical = spacing.contentPadding)
}

/**
 * Applies tokenized adaptive card inner padding.
 */
@Composable
fun Modifier.adaptiveCardPadding(): Modifier = this.composed {
    val spacing = LocalAdaptiveSpacing.current
    this.padding(spacing.cardPadding)
}
