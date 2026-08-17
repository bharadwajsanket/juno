package bharadwaj.juno.music.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standardized artwork sizes across player, lists, grids, and header sections.
 */
@Immutable
object ArtworkSizeTokens {
    val Thumbnail: Dp = 40.dp
    val ListItem: Dp = 56.dp
    val MiniPlayer: Dp = 48.dp
    val GridCompact: Dp = 120.dp
    val GridMedium: Dp = 160.dp
    val GridExpanded: Dp = 200.dp
    val HeaderCompact: Dp = 180.dp
    val HeaderExpanded: Dp = 260.dp
    val ExpandedPlayerCompact: Dp = 320.dp
    val ExpandedPlayerExpanded: Dp = 420.dp
}
