package bharadwaj.juno.music.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Padding tokens for screens, cards, items, and containers across device viewports.
 */
@Immutable
object PaddingTokens {
    val ScreenHorizontalCompact: Dp = 16.dp
    val ScreenHorizontalMedium: Dp = 24.dp
    val ScreenHorizontalExpanded: Dp = 32.dp

    val ScreenVerticalCompact: Dp = 8.dp
    val ScreenVerticalMedium: Dp = 16.dp
    val ScreenVerticalExpanded: Dp = 24.dp

    val CardInnerCompact: Dp = 12.dp
    val CardInnerMedium: Dp = 16.dp
    val CardInnerExpanded: Dp = 20.dp

    val ListItemPaddingHorizontal: Dp = 16.dp
    val ListItemPaddingVertical: Dp = 8.dp

    val BottomPlayerPadding: Dp = 8.dp
    val DialogPadding: Dp = 24.dp
}
