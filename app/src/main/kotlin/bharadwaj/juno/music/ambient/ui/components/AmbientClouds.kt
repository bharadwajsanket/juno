package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import bharadwaj.juno.music.ambient.ui.motion.AmbientMotion

/**
 * Three-layer cloud composition drifting at different speeds.
 *
 * Cloud density is driven by [AmbientAtmosphere.cloudDensity]:
 *
 *   density < 0.08  → clouds hidden entirely (perfectly clear sky)
 *   density < 0.32  → front layer only (light cloud, ~1-2 puffs)
 *   density < 0.62  → front + mid layers (partly cloudy)
 *   density ≥ 0.62  → all three layers (overcast/rain/storm)
 *
 * Opacity of each layer is proportional to density so a light PartlyCloudy
 * scene has semi-transparent clouds while a storm renders near-solid ones.
 *
 * At high density (rain/storm), clouds spread to fill more horizontal space.
 *
 * Sun/moon occlusion is a natural consequence of density: at high cloud
 * density the sun or moon is simply hidden behind the opaque cloud layer
 * (controlled by [AmbientSkyLayer], which skips celestial rendering when
 * [AmbientAtmosphere.isSunVisible]/[isMoonVisible] is suppressed by weather).
 */
@Composable
fun AmbientClouds(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val animatedDensity by animateFloatAsState(
        targetValue = atmosphere.cloudDensity,
        animationSpec = tween(durationMillis = 8000),
        label = "cloud_density",
    )

    // Don't pay for animations if clouds aren't visible
    if (animatedDensity < 0.08f) return

    // Speed up cloud drift based on wind speed
    val windSpeed = atmosphere.windSpeedKmh
    val backPeriod = (28_000 / (1f + windSpeed / 20f)).toInt().coerceIn(3000, 35000)
    val midPeriod = (20_000 / (1f + windSpeed / 20f)).toInt().coerceIn(2500, 25000)
    val frontPeriod = (15_000 / (1f + windSpeed / 20f)).toInt().coerceIn(2000, 20000)

    val driftBack  by AmbientMotion.drift(label = "cloud_back",  periodMs = backPeriod, amplitude = 8f)
    val driftMid   by AmbientMotion.drift(label = "cloud_mid",   periodMs = midPeriod, amplitude = 12f)
    val driftFront by AmbientMotion.drift(label = "cloud_front", periodMs = frontPeriod, amplitude = 15f)

    // Layer-level opacities driven by animated cloud density.
    val frontOpacity = ((animatedDensity - 0.08f) / 0.54f).coerceIn(0f, 1f)
    val midOpacity   = ((animatedDensity - 0.32f) / 0.30f).coerceIn(0f, 1f)
    val backOpacity  = ((animatedDensity - 0.62f) / 0.30f).coerceIn(0f, 1f)

    val cf = colors.cloudFill
    // Radius scaling factor: heavy density makes clouds larger
    val scaleFactor = 1.0f + animatedDensity * 0.25f

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dynamic cloud shadow offset opposite to the sun angle
        val angle = (atmosphere.solarProgress - 0.5f) * 2f // -1.0 to 1.0
        val shadowDx = -angle * 12f
        val shadowDy = 7f
        
        // Shadows are cast by the sun, so they disappear at night and soften in fog
        val baseShadowAlpha = if (atmosphere.isSunVisible) {
            (0.08f * (1f - atmosphere.fogIntensity * 0.85f)).coerceIn(0f, 0.08f)
        } else {
            0f
        }
        val shadowColor = Color.Black.copy(alpha = baseShadowAlpha)

        // ── Distant cloud (upper-right, slowest) ─────────────────────────────
        if (backOpacity > 0f) {
            val alpha = cf.alpha * backOpacity * 0.55f
            val backCx = w * 0.72f + driftBack
            val backCy = h * 0.34f
            val backR  = h * 0.11f * scaleFactor

            // Draw shadow circles first
            drawCircle(color = shadowColor, radius = backR,          center = Offset(backCx + shadowDx, backCy + shadowDy))
            drawCircle(color = shadowColor, radius = backR * 0.70f,  center = Offset(backCx - backR * 0.72f + shadowDx, backCy + backR * 0.14f + shadowDy))
            drawCircle(color = shadowColor, radius = backR * 0.78f,  center = Offset(backCx + backR * 0.68f + shadowDx, backCy + backR * 0.10f + shadowDy))

            // Draw clouds
            drawCircle(color = cf.copy(alpha = alpha),          radius = backR,           center = Offset(backCx, backCy))
            drawCircle(color = cf.copy(alpha = alpha),          radius = backR * 0.70f,   center = Offset(backCx - backR * 0.72f, backCy + backR * 0.14f))
            drawCircle(color = cf.copy(alpha = alpha),          radius = backR * 0.78f,   center = Offset(backCx + backR * 0.68f, backCy + backR * 0.10f))
            drawCircle(color = cf.copy(alpha = alpha * 0.70f),  radius = backR * 0.50f,   center = Offset(backCx + backR * 1.40f, backCy + backR * 0.22f))
        }

        // ── Mid cloud (centre, moderate drift) ────────────────────────────────
        if (midOpacity > 0f) {
            val alpha = cf.alpha * midOpacity * 0.76f
            val midCx = w * 0.42f + driftMid
            val midCy = h * 0.50f
            val midR  = h * 0.13f * scaleFactor

            // Draw shadow circles
            drawCircle(color = shadowColor, radius = midR,          center = Offset(midCx + shadowDx, midCy + shadowDy))
            drawCircle(color = shadowColor, radius = midR * 0.75f,  center = Offset(midCx - midR * 0.80f + shadowDx, midCy + midR * 0.12f + shadowDy))
            drawCircle(color = shadowColor, radius = midR * 0.65f,  center = Offset(midCx + midR * 0.82f + shadowDx, midCy + midR * 0.16f + shadowDy))

            // Draw clouds
            drawCircle(color = cf.copy(alpha = alpha),          radius = midR,            center = Offset(midCx, midCy))
            drawCircle(color = cf.copy(alpha = alpha),          radius = midR * 0.75f,    center = Offset(midCx - midR * 0.80f, midCy + midR * 0.12f))
            drawCircle(color = cf.copy(alpha = alpha),          radius = midR * 0.65f,    center = Offset(midCx + midR * 0.82f, midCy + midR * 0.16f))
        }

        // ── Near cloud (lower-right, fastest) ────────────────────────────────
        if (frontOpacity > 0f) {
            val alpha = cf.alpha * frontOpacity
            val frontCx = w * 0.62f + driftFront
            val frontCy = h * 0.62f
            val frontR  = h * 0.15f * scaleFactor

            // Draw shadow circles
            drawCircle(color = shadowColor, radius = frontR,          center = Offset(frontCx + shadowDx, frontCy + shadowDy))
            drawCircle(color = shadowColor, radius = frontR * 0.76f,  center = Offset(frontCx - frontR * 0.78f + shadowDx, frontCy + frontR * 0.10f + shadowDy))
            drawCircle(color = shadowColor, radius = frontR * 0.66f,  center = Offset(frontCx + frontR * 0.82f + shadowDx, frontCy + frontR * 0.14f + shadowDy))

            // Draw clouds
            drawCircle(color = cf.copy(alpha = alpha),          radius = frontR,          center = Offset(frontCx, frontCy))
            drawCircle(color = cf.copy(alpha = alpha),          radius = frontR * 0.76f,  center = Offset(frontCx - frontR * 0.78f, frontCy + frontR * 0.10f))
            drawCircle(color = cf.copy(alpha = alpha),          radius = frontR * 0.66f,  center = Offset(frontCx + frontR * 0.82f, frontCy + frontR * 0.14f))
            drawCircle(color = cf.copy(alpha = alpha * 0.80f),  radius = frontR * 0.50f,  center = Offset(frontCx - frontR * 0.28f, frontCy + frontR * 0.32f))
        }
    }
}
