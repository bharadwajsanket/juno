package bharadwaj.juno.music.ui.adaptive.dimensions

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bharadwaj.juno.music.ui.adaptive.tokens.ArtworkSizeTokens
import bharadwaj.juno.music.ui.adaptive.tokens.ContentWidthTokens
import bharadwaj.juno.music.ui.adaptive.tokens.SizeTokens
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo
import bharadwaj.juno.music.ui.adaptive.window.WindowWidthSizeClass

/**
 * Breakpoint constants used for window size classification.
 */
@Immutable
object Breakpoints {
    val CompactMaxWidth: Dp = 599.dp
    val MediumMinWidth: Dp = 600.dp
    val MediumMaxWidth: Dp = 839.dp
    val ExpandedMinWidth: Dp = 840.dp

    val TabletSmallestWidth: Dp = 600.dp
}

/**
 * Dynamic layout dimensions based on adaptive window state.
 */
@Immutable
data class AdaptiveDimensions(
    val maxReadableWidth: Dp,
    val maxContentWidth: Dp,
    val artworkSize: Dp,
    val miniPlayerHeight: Dp,
    val topBarHeight: Dp,
    val isMultiPane: Boolean
) {
    companion object {
        fun fromWindowInfo(windowInfo: AdaptiveWindowInfo): AdaptiveDimensions {
            val isMultiPane = windowInfo.isExpandedWidth || (windowInfo.isMediumWidth && windowInfo.isLandscape)
            return AdaptiveDimensions(
                maxReadableWidth = ContentWidthTokens.MaxReadableWidth,
                maxContentWidth = if (isMultiPane) Dp.Unspecified else ContentWidthTokens.MaxSingleColumnWidth,
                artworkSize = when (windowInfo.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> ArtworkSizeTokens.GridCompact
                    WindowWidthSizeClass.Medium -> ArtworkSizeTokens.GridMedium
                    WindowWidthSizeClass.Expanded -> ArtworkSizeTokens.GridExpanded
                },
                miniPlayerHeight = SizeTokens.MiniPlayerHeight,
                topBarHeight = SizeTokens.TopAppBarHeight,
                isMultiPane = isMultiPane
            )
        }
    }
}
