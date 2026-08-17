package bharadwaj.juno.music.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing token design system for JUNO Music.
 */
@Immutable
object SpacingTokens {
    val None: Dp = 0.dp
    val Micro: Dp = 2.dp
    val ExtraSmall: Dp = 4.dp
    val Small: Dp = 8.dp
    val Medium: Dp = 12.dp
    val Normal: Dp = 16.dp
    val Large: Dp = 24.dp
    val ExtraLarge: Dp = 32.dp
    val Huge: Dp = 48.dp
    val Giant: Dp = 64.dp

    /**
     * Compact (phone portrait) spacing values.
     */
    val CompactGutter: Dp = Normal
    val CompactItemSpacing: Dp = Small

    /**
     * Medium (foldables/tablets portrait) spacing values.
     */
    val MediumGutter: Dp = Large
    val MediumItemSpacing: Dp = Medium

    /**
     * Expanded (tablets landscape/desktop) spacing values.
     */
    val ExpandedGutter: Dp = ExtraLarge
    val ExpandedItemSpacing: Dp = Normal
}
