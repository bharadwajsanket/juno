package bharadwaj.juno.music.ui.adaptive.window

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents the width size class of the window based on Material 3 design guidelines.
 */
enum class WindowWidthSizeClass {
    /**
     * Compact width (e.g. phones in portrait, < 600dp).
     */
    Compact,

    /**
     * Medium width (e.g. foldables unfolded, small tablets, 600dp - 840dp).
     */
    Medium,

    /**
     * Expanded width (e.g. tablets, desktop, > 840dp).
     */
    Expanded;

    val isCompact: Boolean get() = this == Compact
    val isMedium: Boolean get() = this == Medium
    val isExpanded: Boolean get() = this == Expanded

    companion object {
        fun fromWidth(width: Dp): WindowWidthSizeClass = when {
            width < 600.dp -> Compact
            width < 840.dp -> Medium
            else -> Expanded
        }
    }
}

/**
 * Represents the height size class of the window based on Material 3 design guidelines.
 */
enum class WindowHeightSizeClass {
    /**
     * Compact height (e.g. phones in landscape, < 480dp).
     */
    Compact,

    /**
     * Medium height (e.g. phones in portrait, tablets, 480dp - 900dp).
     */
    Medium,

    /**
     * Expanded height (e.g. large displays in portrait, > 900dp).
     */
    Expanded;

    val isCompact: Boolean get() = this == Compact
    val isMedium: Boolean get() = this == Medium
    val isExpanded: Boolean get() = this == Expanded

    companion object {
        fun fromHeight(height: Dp): WindowHeightSizeClass = when {
            height < 480.dp -> Compact
            height < 900.dp -> Medium
            else -> Expanded
        }
    }
}

/**
 * Holds both width and height window size classes.
 */
@Immutable
data class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
) {
    val isCompactWidth: Boolean get() = widthSizeClass.isCompact
    val isMediumWidth: Boolean get() = widthSizeClass.isMedium
    val isExpandedWidth: Boolean get() = widthSizeClass.isExpanded

    companion object {
        fun calculate(width: Dp, height: Dp): WindowSizeClass = WindowSizeClass(
            widthSizeClass = WindowWidthSizeClass.fromWidth(width),
            heightSizeClass = WindowHeightSizeClass.fromHeight(height)
        )
    }
}
