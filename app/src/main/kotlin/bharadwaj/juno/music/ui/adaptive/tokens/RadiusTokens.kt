package bharadwaj.juno.music.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Corner radius tokens for shape scaling across viewport configurations.
 */
@Immutable
object RadiusTokens {
    val None: Dp = 0.dp
    val Small: Dp = 4.dp
    val Medium: Dp = 8.dp
    val Large: Dp = 16.dp
    val ExtraLarge: Dp = 24.dp
    val Giant: Dp = 32.dp
    val Full: Dp = 9999.dp

    val CardCorner: Dp = Large
    val SheetCorner: Dp = Giant
    val ButtonCorner: Dp = Full
    val ArtworkCorner: Dp = Medium
    val PlayerCorner: Dp = ExtraLarge
}
