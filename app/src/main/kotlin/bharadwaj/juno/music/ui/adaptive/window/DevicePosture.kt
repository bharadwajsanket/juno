package bharadwaj.juno.music.ui.adaptive.window

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect

/**
 * Represents the physical posture of a device (especially relevant for foldables).
 */
@Immutable
sealed interface DevicePosture {
    /**
     * Standard flat posture (normal screen layout).
     */
    object Normal : DevicePosture

    /**
     * Tabletop mode or half-folded posture with hinge bounds.
     */
    data class Tabletop(
        val hingeBounds: Rect,
        val isVerticalHinge: Boolean = false
    ) : DevicePosture

    /**
     * Book mode posture with a vertical hinge separating left and right panes.
     */
    data class Book(
        val hingeBounds: Rect
    ) : DevicePosture
}
