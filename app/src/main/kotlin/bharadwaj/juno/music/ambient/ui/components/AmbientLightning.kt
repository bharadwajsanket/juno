package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.model.AmbientWeather

/**
 * Renders occasional subtle lightning flashes to illuminate the sky during thunderstorms.
 * Restricts the flash sequence to a rare cycle period (e.g. 9s) with 0 noise and no sound.
 */
@Composable
fun AmbientLightning(
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    if (atmosphere.condition != AmbientWeather.Condition.Thunderstorm) return

    val infiniteTransition = rememberInfiniteTransition(label = "lightning")
    
    // A 9-second repetition loop
    val cycleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
        ),
        label = "lightning_cycle",
    )

    // Lightning flashes occur only during a short 300ms window at the start of the 9s cycle (progress < 0.033)
    val flashAlpha = if (cycleProgress < 0.033f) {
        val localProgress = cycleProgress / 0.033f // normalized 0 to 1
        // Simulate a double flash sequence
        if (localProgress < 0.4f) {
            // First flash
            kotlin.math.sin(localProgress / 0.4f * kotlin.math.PI.toFloat()) * 0.25f
        } else if (localProgress < 0.6f) {
            // Dark period
            0f
        } else {
            // Second flash (softer)
            kotlin.math.sin((localProgress - 0.6f) / 0.4f * kotlin.math.PI.toFloat()) * 0.14f
        }
    } else {
        0f
    }

    if (flashAlpha > 0f) {
        Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height

            // 1. Faint ambient background flash
            drawRect(color = Color(0xFFD5E8FF).copy(alpha = flashAlpha * 0.20f))

            // 2. Localized radial cloud illumination glow
            // Deterministic center X based on cycle time
            val cx = w * (0.3f + ((cycleProgress * 1000f).toInt() % 5) * 0.12f)
            val cy = h * 0.38f
            drawCircle(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF2F8FF).copy(alpha = flashAlpha * 0.90f),
                        Color(0xFFD5E8FF).copy(alpha = flashAlpha * 0.40f),
                        Color.Transparent,
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.36f,
                ),
                radius = w * 0.36f,
                center = Offset(cx, cy),
            )
        }
    }
}
