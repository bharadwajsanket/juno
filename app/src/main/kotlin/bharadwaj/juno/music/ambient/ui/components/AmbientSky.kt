package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors

/**
 * Sky gradient background — fills the entire available space with a vertical
 * linear gradient derived from [colors], modulated by [atmosphere].
 *
 * The horizon color ([AmbientSceneColors.skyBottom]) is dynamically blended
 * toward a warm amber tint at high [AmbientAtmosphere.glowIntensity] (dawn/dusk)
 * and toward a cool deep tone at low glow intensity (noon, deep night).
 * This subtle tint follows the real-world atmospheric scattering that warms
 * the horizon at low solar angles.
 *
 * The color transition is animated with a slow 90-second tween so it is
 * imperceptible from moment to moment — the user only notices after several
 * minutes, mirroring the pace of natural sky change.
 *
 * Supports 2-stop or 3-stop gradients (when [AmbientSceneColors.skyMid] is set).
 */
@Composable
fun AmbientSky(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    // Horizon glow tint: warm amber at high glow, no tint at low glow.
    // Blends the scene's skyBottom color toward a warm amber at max glow.
    val glowTint = Color(0xFFFFAB40)  // warm amber
    val horizon = lerp(
        start = colors.skyBottom,
        stop  = lerp(colors.skyBottom, glowTint, 0.35f),
        fraction = atmosphere.glowIntensity.coerceIn(0f, 1f),
    )

    val stops = buildList {
        add(colors.skyTop)
        colors.skyMid?.let { add(it) }
        add(horizon)
    }

    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = stops,
                startY = 0f,
                endY   = size.height,
            ),
        )
    }
}
