package bharadwaj.juno.music.ui.adaptive.window

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bharadwaj.juno.music.ui.adaptive.navigation.AdaptiveNavigationStyle
import bharadwaj.juno.music.ui.adaptive.pane.PaneConfiguration

/**
 * Single source of truth for adaptive window state across JUNO Music.
 * Every screen and component queries this state rather than computing screen width/height directly.
 */
@Immutable
data class AdaptiveWindowInfo(
    val windowSizeClass: WindowSizeClass,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp,
    val orientation: Orientation,
    val posture: DevicePosture = DevicePosture.Normal,
    val screenType: ScreenType = ScreenType.Phone,
    val smallestScreenWidthDp: Dp = minOf(screenWidthDp, screenHeightDp)
) {
    val widthSizeClass: WindowWidthSizeClass get() = windowSizeClass.widthSizeClass
    val heightSizeClass: WindowHeightSizeClass get() = windowSizeClass.heightSizeClass

    val isCompactWidth: Boolean get() = widthSizeClass.isCompact
    val isMediumWidth: Boolean get() = widthSizeClass.isMedium
    val isExpandedWidth: Boolean get() = widthSizeClass.isExpanded

    val isPortrait: Boolean get() = orientation.isPortrait
    val isLandscape: Boolean get() = orientation.isLandscape

    /**
     * Determines whether the screen qualifies as a tablet or large screen (sw >= 600dp or expanded width).
     */
    val isTablet: Boolean get() = smallestScreenWidthDp >= 600.dp || widthSizeClass.isExpanded || screenType == ScreenType.Tablet

    /**
     * Determines whether the device is in phone mode.
     */
    val isPhone: Boolean get() = !isTablet && screenType != ScreenType.Desktop

    /**
     * Suggested navigation style based on current window size and orientation.
     */
    val preferredNavigationStyle: AdaptiveNavigationStyle
        get() = when {
            widthSizeClass.isExpanded -> AdaptiveNavigationStyle.PermanentDrawer
            widthSizeClass.isMedium || (isLandscape && !isCompactWidth) -> AdaptiveNavigationStyle.NavigationRail
            else -> AdaptiveNavigationStyle.BottomBar
        }

    /**
     * Suggested pane configuration (Single vs Dual Pane).
     */
    val preferredPaneConfiguration: PaneConfiguration
        get() = when {
            widthSizeClass.isExpanded || (widthSizeClass.isMedium && isLandscape) -> PaneConfiguration.DualPane
            else -> PaneConfiguration.SinglePane
        }

    companion object {
        val Default = AdaptiveWindowInfo(
            windowSizeClass = WindowSizeClass(WindowWidthSizeClass.Compact, WindowHeightSizeClass.Medium),
            screenWidthDp = 360.dp,
            screenHeightDp = 800.dp,
            orientation = Orientation.Portrait,
            posture = DevicePosture.Normal,
            screenType = ScreenType.Phone
        )
    }
}
