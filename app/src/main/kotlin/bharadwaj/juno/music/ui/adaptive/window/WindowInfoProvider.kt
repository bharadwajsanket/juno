package bharadwaj.juno.music.ui.adaptive.window

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * Calculates and remembers the current [AdaptiveWindowInfo] from compose local context.
 */
@Composable
fun rememberAdaptiveWindowInfo(): AdaptiveWindowInfo {
    val configuration = LocalConfiguration.current
    
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val smallestScreenWidthDp = configuration.smallestScreenWidthDp.dp
    
    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Orientation.Landscape
    } else {
        Orientation.Portrait
    }

    val windowSizeClass = WindowSizeClass.calculate(screenWidthDp, screenHeightDp)

    val screenType = when {
        smallestScreenWidthDp >= 600.dp -> ScreenType.Tablet
        else -> ScreenType.Phone
    }

    return remember(screenWidthDp, screenHeightDp, orientation, smallestScreenWidthDp) {
        AdaptiveWindowInfo(
            windowSizeClass = windowSizeClass,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            orientation = orientation,
            posture = DevicePosture.Normal,
            screenType = screenType,
            smallestScreenWidthDp = smallestScreenWidthDp
        )
    }
}
