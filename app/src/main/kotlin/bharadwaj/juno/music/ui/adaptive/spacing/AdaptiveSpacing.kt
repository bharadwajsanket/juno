package bharadwaj.juno.music.ui.adaptive.spacing

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import bharadwaj.juno.music.ui.adaptive.tokens.PaddingTokens
import bharadwaj.juno.music.ui.adaptive.tokens.SpacingTokens
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo
import bharadwaj.juno.music.ui.adaptive.window.WindowWidthSizeClass

/**
 * Adaptive spacing values dynamically computed from the window info.
 */
@Immutable
data class AdaptiveSpacing(
    val windowMargin: Dp,
    val contentPadding: Dp,
    val itemSpacing: Dp,
    val cardPadding: Dp,
    val gridSpacing: Dp
) {
    companion object {
        fun fromWindowInfo(windowInfo: AdaptiveWindowInfo): AdaptiveSpacing = when (windowInfo.widthSizeClass) {
            WindowWidthSizeClass.Compact -> AdaptiveSpacing(
                windowMargin = PaddingTokens.ScreenHorizontalCompact,
                contentPadding = PaddingTokens.ScreenVerticalCompact,
                itemSpacing = SpacingTokens.CompactItemSpacing,
                cardPadding = PaddingTokens.CardInnerCompact,
                gridSpacing = SpacingTokens.Small
            )
            WindowWidthSizeClass.Medium -> AdaptiveSpacing(
                windowMargin = PaddingTokens.ScreenHorizontalMedium,
                contentPadding = PaddingTokens.ScreenVerticalMedium,
                itemSpacing = SpacingTokens.MediumItemSpacing,
                cardPadding = PaddingTokens.CardInnerMedium,
                gridSpacing = SpacingTokens.Medium
            )
            WindowWidthSizeClass.Expanded -> AdaptiveSpacing(
                windowMargin = PaddingTokens.ScreenHorizontalExpanded,
                contentPadding = PaddingTokens.ScreenVerticalExpanded,
                itemSpacing = SpacingTokens.ExpandedItemSpacing,
                cardPadding = PaddingTokens.CardInnerExpanded,
                gridSpacing = SpacingTokens.Normal
            )
        }
    }
}
