package bharadwaj.juno.music.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Readable content width tokens to prevent ultra-wide uncomfortable reading layout lines on large displays.
 */
@Immutable
object ContentWidthTokens {
    /**
     * Ideal maximum width for text content (lyrics, descriptions, settings forms).
     */
    val MaxReadableWidth: Dp = 600.dp

    /**
     * Ideal maximum width for single column content.
     */
    val MaxSingleColumnWidth: Dp = 840.dp

    /**
     * Recommended split pane width ratio (e.g. 0.40f for left pane, 0.60f for right pane).
     */
    const val DualPanePrimaryRatio: Float = 0.40f
    const val DualPaneSecondaryRatio: Float = 0.60f

    /**
     * Fixed master list pane width on large dual pane displays.
     */
    val MasterPaneWidth: Dp = 360.dp
}
