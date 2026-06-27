package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import kotlin.random.Random

/**
 * Handles weather-driven rain and snow falling animations.
 * Uses lightweight infinite transitions for smooth movement with zero thread blocking.
 */
@Composable
fun AmbientPrecipitation(
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isReduceMotion = remember(context) {
        try {
            android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }

    val animatedRain by animateFloatAsState(
        targetValue = atmosphere.rainIntensity,
        animationSpec = tween(durationMillis = 6000),
        label = "rain_intensity",
    )
    val animatedSnow by animateFloatAsState(
        targetValue = atmosphere.snowIntensity,
        animationSpec = tween(durationMillis = 6000),
        label = "snow_intensity",
    )

    if (animatedRain <= 0.02f && animatedSnow <= 0.02f) return

    val infiniteTransition = rememberInfiniteTransition(label = "precipitation")

    // Rain drop progress (0 to 1) looping very fast
    val rainProgressAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = LinearEasing),
        ),
        label = "rain_fall",
    )

    // Snow drop progress (0 to 1) looping slowly
    val snowProgressAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
        ),
        label = "snow_fall",
    )

    val rainProgress = if (isReduceMotion) 0f else rainProgressAnim
    val snowProgress = if (isReduceMotion) 0f else snowProgressAnim

    // Generate a fixed set of drop offsets so drawing is deterministic
    val rainDrops = remember {
        List(40) {
            Offset(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
            ) to (0.8f + Random.nextFloat() * 0.4f) // speed scale factor
        }
    }

    val snowflakes = remember {
        List(25) {
            Offset(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
            ) to (0.5f + Random.nextFloat() * 0.5f) // speed scale
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // ── Draw Rain ────────────────────────────────────────────────────────
        if (animatedRain > 0.02f) {
            // Wind angle tilt is derived from wind speed
            val windAngleOffset = (atmosphere.windSpeedKmh * 0.4f).coerceIn(-15f, 15f)
            val dropColor = Color(0xFFAEC4FF).copy(alpha = animatedRain * 0.40f)
            val strokeWidth = 1.5f + animatedRain * 1f
            val dropLength = 15f + animatedRain * 12f

            rainDrops.forEach { (baseOffset, speedScale) ->
                val progress = (rainProgress * speedScale + baseOffset.y) % 1f
                val startY = progress * h
                val startX = (baseOffset.x * w + startY * (windAngleOffset / 30f)) % w
                
                // Wrap horizontal coordinates safely
                val x = if (startX < 0) startX + w else startX
                
                drawLine(
                    color = dropColor,
                    start = Offset(x, startY),
                    end = Offset(
                        x = x + windAngleOffset,
                        y = startY + dropLength,
                    ),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }

        // ── Draw Snow ────────────────────────────────────────────────────────
        if (animatedSnow > 0.02f) {
            val snowColor = Color.White.copy(alpha = animatedSnow * 0.85f)
            val windOffset = (atmosphere.windSpeedKmh * 0.2f).coerceIn(-10f, 10f)

            snowflakes.forEach { (baseOffset, speedScale) ->
                val progress = (snowProgress * speedScale + baseOffset.y) % 1f
                val startY = progress * h
                
                // Horizontal sway (sine wave)
                val sway = kotlin.math.sin(progress * 2 * kotlin.math.PI.toFloat() * 2f + baseOffset.x * 10f) * 6f
                val startX = (baseOffset.x * w + startY * (windOffset / 40f) + sway) % w
                val x = if (startX < 0) startX + w else startX
                
                val radius = (2f + speedScale * 2f) * (0.8f + animatedSnow * 0.4f)
                
                drawCircle(
                    color = snowColor,
                    radius = radius,
                    center = Offset(x, startY),
                )
            }
        }
    }
}
