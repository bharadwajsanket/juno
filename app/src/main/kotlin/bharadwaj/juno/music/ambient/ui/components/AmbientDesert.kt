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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import kotlin.math.sin

/**
 * Procedural desert environment with rolling sand dunes, warm lighting gradients, heat haze, and parallax.
 */
@Composable
fun AmbientDesert(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "desert_motion")

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

    // Vertical heat haze cycle
    val hazeCycle by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "haze",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val fill = colors.mountainFill

        // Horizon sits at 68%
        val horizonY = h * 0.68f

        // Draw ground base
        drawRect(
            color = fill,
            topLeft = Offset(0f, horizonY),
        )

        // Warm desert tones based on mountainFill
        val warmSandColor = lerp(fill, Color(0xFFE65100), 0.28f) // warm sienna-orange tint
        val lightSandColor = lerp(fill, Color(0xFFFFB74D), 0.40f) // golden sand tint

        // ── 1. Far Dunes (includes subtle vertical heat haze translation) ─────
        val farDuneColor = lerp(warmSandColor, colors.skyBottom, 0.42f)
        val farDriftX = parallaxDrift * 0.25f
        val farHazeY = hazeCycle * 0.8f // vertical shimmer offset
        withTransform({
            translate(left = farDriftX, top = farHazeY)
        }) {
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.72f)
                quadraticTo(w * 0.30f, h * 0.61f, w * 0.60f, h * 0.73f)
                quadraticTo(w * 0.80f, h * 0.65f, w, h * 0.70f)
                lineTo(w, h)
                close()
            }
            drawPath(path, farDuneColor)
        }

        // ── 2. Mid Dunes (golden-sand gradients) ──────────────────────────────
        val midDuneColor = lerp(lightSandColor, colors.skyBottom, 0.18f)
        val midDriftX = parallaxDrift * 0.60f
        withTransform({
            translate(left = midDriftX)
        }) {
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.82f)
                quadraticTo(w * 0.20f, h * 0.70f, w * 0.50f, h * 0.81f)
                quadraticTo(w * 0.75f, h * 0.69f, w, h * 0.79f)
                lineTo(w, h)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(midDuneColor, warmSandColor.copy(alpha = 0.85f)),
                    startY = h * 0.69f,
                    endY = h * 0.82f,
                ),
            )
        }

        // ── 3. Near Dunes (foreground warm dune, full parallax) ───────────────
        val nearDriftX = parallaxDrift * 1.0f
        withTransform({
            translate(left = nearDriftX)
        }) {
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.89f)
                quadraticTo(w * 0.35f, h * 0.78f, w * 0.70f, h * 0.87f)
                quadraticTo(w * 0.85f, h * 0.82f, w, h * 0.86f)
                lineTo(w, h)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(lightSandColor, fill),
                    startY = h * 0.78f,
                    endY = h,
                ),
            )
        }
    }
}
