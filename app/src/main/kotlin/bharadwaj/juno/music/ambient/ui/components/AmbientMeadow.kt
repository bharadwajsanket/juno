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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural meadow environment with rolling green/amber hills, parallax, and wind-blown grass.
 */
@Composable
fun AmbientMeadow(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meadow_motion")

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

    // Base wind oscillator cycle
    val windCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wind_cycle",
    )

    // Grass sway amount based on wind speed
    val windSway = sin(windCycle) * (atmosphere.windSpeedKmh / 15f).coerceIn(0.1f, 3.5f)

    // Deterministic grass placement coordinates
    val grassPlacements = remember {
        List(25) {
            val rx = Random.nextFloat()
            val rLen = 8f + Random.nextFloat() * 10f
            Pair(rx, rLen)
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val fill = colors.mountainFill

        // Horizon base sits around 68% of height
        val horizonY = h * 0.68f

        // Draw ground fill
        drawRect(
            color = fill,
            topLeft = Offset(0f, horizonY),
        )

        // ── 1. Far Range Hills (softest green, lowest parallax) ───────────────
        val farHillColor = lerp(fill, Color(0xFF4CAF50), 0.15f) // subtle green tint
        val farDriftX = parallaxDrift * 0.25f
        withTransform({
            translate(left = farDriftX)
        }) {
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.72f)
                quadraticTo(w * 0.25f, h * 0.63f, w * 0.50f, h * 0.70f)
                quadraticTo(w * 0.75f, h * 0.61f, w, h * 0.69f)
                lineTo(w, h)
                close()
            }
            drawPath(path, lerp(farHillColor, colors.skyBottom, 0.40f))
        }

        // ── 2. Mid Range Hills (green, moderate parallax) ─────────────────────
        val midHillColor = lerp(fill, Color(0xFF388E3C), 0.25f)
        val midDriftX = parallaxDrift * 0.60f
        withTransform({
            translate(left = midDriftX)
        }) {
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.81f)
                quadraticTo(w * 0.35f, h * 0.73f, w * 0.65f, h * 0.80f)
                quadraticTo(w * 0.85f, h * 0.71f, w, h * 0.78f)
                lineTo(w, h)
                close()
            }
            drawPath(path, lerp(midHillColor, colors.skyBottom, 0.18f))
        }

        // ── 3. Near Range Hills & Grass (darkest/foreground layer, full parallax) ──
        val nearHillColor = fill
        val nearDriftX = parallaxDrift * 1.0f
        withTransform({
            translate(left = nearDriftX)
        }) {
            // Draw foreground rolling hill path
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.89f)
                quadraticTo(w * 0.20f, h * 0.82f, w * 0.45f, h * 0.88f)
                quadraticTo(w * 0.75f, h * 0.78f, w, h * 0.87f)
                lineTo(w, h)
                close()
            }
            drawPath(path, nearHillColor)

            // Draw procedural grass blades blowing in the wind along the curve of the near hill
            grassPlacements.forEach { (relX, length) ->
                val startX = relX * w
                
                // Approximate Near Hill height Y at startX to snap grass to ground
                val progress = startX / w
                val startY = if (progress < 0.45f) {
                    val pLocal = progress / 0.45f
                    // quadratic curve interpolation
                    val p1 = h * 0.89f
                    val p2 = h * 0.82f
                    val p3 = h * 0.88f
                    val y = (1f - pLocal) * (1f - pLocal) * p1 + 2f * (1f - pLocal) * pLocal * p2 + pLocal * pLocal * p3
                    y
                } else {
                    val pLocal = (progress - 0.45f) / 0.55f
                    val p1 = h * 0.88f
                    val p2 = h * 0.78f
                    val p3 = h * 0.87f
                    val y = (1f - pLocal) * (1f - pLocal) * p1 + 2f * (1f - pLocal) * pLocal * p2 + pLocal * pLocal * p3
                    y
                }

                // Wind sway adds displacement to blade tips
                val bladeSway = windSway * (length / 8f) * 4f
                drawGrassBlade(startX, startY + 1f, length, nearHillColor, bladeSway)
            }
        }
    }
}

private fun DrawScope.drawGrassBlade(
    startX: Float,
    startY: Float,
    length: Float,
    baseColor: Color,
    windSway: Float,
) {
    val path = Path().apply {
        moveTo(startX - 1.5f, startY)
        // Curve to tip
        quadraticTo(
            startX + windSway * 0.4f, startY - length * 0.6f,
            startX + windSway, startY - length,
        )
        // Outline back to root
        quadraticTo(
            startX + windSway * 0.4f - 0.5f, startY - length * 0.6f,
            startX + 1.5f, startY,
        )
        close()
    }
    drawPath(path, baseColor)
}
