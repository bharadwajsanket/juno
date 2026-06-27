package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import kotlin.math.sin

/**
 * Renders procedural ocean water with waves, reflections, and shimmer.
 */
@Composable
fun AmbientOcean(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ocean_motion")

    // Slow wave oscillation cycles
    val waveCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave_cycle",
    )

    // Camera drift for horizontal parallax
    val parallaxDrift by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15_000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "parallax",
    )

    // Shimmer shimmer flicker pulse
    val shimmerFlicker by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmer",
    )

    // Wind gust modifier
    val windSway = sin(waveCycle) * (atmosphere.windSpeedKmh / 15f).coerceIn(0.1f, 2.5f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val fill = colors.mountainFill

        // Horizon sits at 68% of height
        val horizonY = h * 0.68f

        // ── 1. Draw Water Base Linear Gradient ───────────────────────────────
        val waterTopColor = lerp(colors.skyBottom, fill, 0.40f)
        val waterBottomColor = fill
        
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(waterTopColor, waterBottomColor),
                startY = horizonY,
                endY = h,
            ),
            topLeft = Offset(0f, horizonY),
        )

        // ── 2. Draw Waves with Parallax & Wind Sway ──────────────────────────
        // Wave 1: Distant wave (least displacement, slowest, opacity 0.45)
        val wavePath1 = Path().apply {
            val drift = parallaxDrift * 0.25f + windSway * 1f
            moveTo(0f, h)
            lineTo(0f, horizonY)
            var x = 0f
            while (x <= w) {
                val y = horizonY + sin(x * 0.04f + waveCycle + drift) * 1.5f
                lineTo(x, y)
                x += 10f
            }
            lineTo(w, h)
            close()
        }
        drawPath(wavePath1, fill.copy(alpha = fill.alpha * 0.45f))

        // Wave 2: Midground wave (moderate displacement, opacity 0.70)
        val wavePath2 = Path().apply {
            val drift = parallaxDrift * 0.60f + windSway * 1.8f
            val baseMidY = horizonY + h * 0.10f
            moveTo(0f, h)
            lineTo(0f, baseMidY)
            var x = 0f
            while (x <= w) {
                val y = baseMidY + sin(x * 0.025f - waveCycle * 1.2f + drift) * 2.8f
                lineTo(x, y)
                x += 10f
            }
            lineTo(w, h)
            close()
        }
        drawPath(wavePath2, fill.copy(alpha = fill.alpha * 0.70f))

        // Wave 3: Near wave (most displacement, full opacity)
        val wavePath3 = Path().apply {
            val drift = parallaxDrift * 1.0f + windSway * 2.8f
            val baseNearY = horizonY + h * 0.22f
            moveTo(0f, h)
            lineTo(0f, baseNearY)
            var x = 0f
            while (x <= w) {
                val y = baseNearY + sin(x * 0.015f + waveCycle * 1.5f + drift) * 4.2f
                lineTo(x, y)
                x += 10f
            }
            lineTo(w, h)
            close()
        }
        drawPath(wavePath3, fill)

        // ── 3. Draw Celestial Reflections ──────────────────────────────────
        // Only draw reflection if the celestial body is visible and not hidden by clouds
        val cloudsFactor = (1f - atmosphere.cloudDensity * 1.25f).coerceIn(0f, 1f)
        if (cloudsFactor > 0.05f) {
            val isSun = atmosphere.isSunVisible
            val reflectX = if (isSun) {
                lerp(w * 0.18f, w * 0.88f, atmosphere.solarProgress)
            } else {
                lerp(w * 0.84f, w * 0.16f, atmosphere.lunarProgress)
            }

            val reflectColor = if (isSun) {
                colors.celestialGlow.copy(alpha = 0.25f * cloudsFactor)
            } else {
                Color.White.copy(alpha = 0.20f * cloudsFactor)
            }

            // Segmented rippling water reflection column (warps in response to wave/wind)
            val segments = 12
            val segmentHeight = (h - horizonY) / segments
            
            // Soften reflections heavily during foggy conditions
            val fogMitigation = (1f - atmosphere.fogIntensity * 0.80f).coerceIn(0.1f, 1.0f)
            val baseReflectColor = reflectColor.copy(alpha = reflectColor.alpha * fogMitigation)

            for (i in 0 until segments) {
                val segY = horizonY + i * segmentHeight
                val yProgress = i.toFloat() / segments

                // Widens and fades out as it stretches towards foreground
                val stripWidth = lerp(w * 0.08f, w * 0.24f, yProgress)
                val waveOffset = sin(segY * 0.07f + waveCycle * 1.5f) * (3f + windSway * 1.2f)
                val stripX = reflectX - stripWidth / 2f + waveOffset

                val alphaMultiplier = lerp(0.95f, 0.40f, yProgress)
                val finalColor = baseReflectColor.copy(alpha = baseReflectColor.alpha * alphaMultiplier)

                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, finalColor, Color.Transparent),
                        startX = stripX,
                        endX = stripX + stripWidth,
                    ),
                    topLeft = Offset(stripX, segY),
                    size = androidx.compose.ui.geometry.Size(stripWidth, segmentHeight + 1f),
                )
            }

            // Draw horizontal shimmer highlights centered on the reflection column
            val shimmerColor = if (isSun) {
                colors.celestialBody.copy(alpha = 0.50f * shimmerFlicker * cloudsFactor * fogMitigation)
            } else {
                Color.White.copy(alpha = 0.45f * shimmerFlicker * cloudsFactor * fogMitigation)
            }

            // Draw 4 distinct shimmer highlights
            drawRect(
                color = shimmerColor,
                topLeft = Offset(reflectX - w * 0.06f + windSway * 2f, horizonY + h * 0.06f),
                size = androidx.compose.ui.geometry.Size(w * 0.12f, 1.5f),
            )
            drawRect(
                color = shimmerColor,
                topLeft = Offset(reflectX - w * 0.10f - windSway * 1f, horizonY + h * 0.14f),
                size = androidx.compose.ui.geometry.Size(w * 0.20f, 1.5f),
            )
            drawRect(
                color = shimmerColor,
                topLeft = Offset(reflectX - w * 0.08f + windSway * 3f, horizonY + h * 0.20f),
                size = androidx.compose.ui.geometry.Size(w * 0.16f, 1.5f),
            )
            drawRect(
                color = shimmerColor,
                topLeft = Offset(reflectX - w * 0.14f + windSway * 1.5f, horizonY + h * 0.26f),
                size = androidx.compose.ui.geometry.Size(w * 0.28f, 1.8f),
            )
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)
