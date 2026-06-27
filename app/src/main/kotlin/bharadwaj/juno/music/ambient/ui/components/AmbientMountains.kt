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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors

/**
 * Procedural Mountain landscape with layered depth ranges, parallax, atmospheric haze, and dynamic snow caps.
 */
@Composable
fun AmbientMountains(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mountain_motion")

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

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val fill = colors.mountainFill

        // Horizon line
        val horizonY = h * 0.58f

        // ── 1. Far Range (tall, light peaks, low parallax) ───────────────────
        val farDriftX = parallaxDrift * 0.25f
        val farColor = lerp(fill, colors.skyBottom, 0.40f)
        withTransform({
            translate(left = farDriftX)
        }) {
            val farPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.58f)
                quadraticTo(w * 0.10f, h * 0.20f, w * 0.20f, h * 0.52f)
                quadraticTo(w * 0.30f, h * 0.28f, w * 0.40f, h * 0.50f)
                quadraticTo(w * 0.50f, h * 0.18f, w * 0.60f, h * 0.46f)  // tallest peak
                quadraticTo(w * 0.70f, h * 0.30f, w * 0.80f, h * 0.48f)
                quadraticTo(w * 0.90f, h * 0.22f, w * 1.00f, h * 0.50f)
                lineTo(w, h)
                close()
            }
            drawPath(farPath, farColor)

            // Draw snow caps on far peaks during snowfall
            drawSnowCap(w * 0.10f, h * 0.36f, w * 0.08f, h * 0.05f, atmosphere.snowIntensity * 0.6f)
            drawSnowCap(w * 0.30f, h * 0.39f, w * 0.07f, h * 0.04f, atmosphere.snowIntensity * 0.6f)
            drawSnowCap(w * 0.50f, h * 0.32f, w * 0.10f, h * 0.06f, atmosphere.snowIntensity * 0.8f) // main peak cap
            drawSnowCap(w * 0.70f, h * 0.39f, w * 0.08f, h * 0.04f, atmosphere.snowIntensity * 0.6f)
            drawSnowCap(w * 0.90f, h * 0.36f, w * 0.08f, h * 0.05f, atmosphere.snowIntensity * 0.6f)
        }

        // ── 2. Distant Haze Overlay (between far and mid ranges) ─────────────
        val hazeColor = colors.skyBottom.copy(alpha = 0.20f)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, hazeColor, Color.Transparent),
                startY = h * 0.40f,
                endY = h * 0.75f,
            ),
        )

        // ── 3. Mid Range (medium peaks, moderate parallax) ───────────────────
        val midDriftX = parallaxDrift * 0.60f
        val midColor = lerp(fill, colors.skyBottom, 0.18f)
        withTransform({
            translate(left = midDriftX)
        }) {
            val midPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.74f)
                quadraticTo(w * 0.08f, h * 0.54f, w * 0.18f, h * 0.72f)
                quadraticTo(w * 0.28f, h * 0.38f, w * 0.40f, h * 0.68f)  // sharp peak
                quadraticTo(w * 0.50f, h * 0.52f, w * 0.60f, h * 0.70f)
                quadraticTo(w * 0.72f, h * 0.40f, w * 0.82f, h * 0.66f)
                quadraticTo(w * 0.92f, h * 0.58f, w * 1.00f, h * 0.70f)
                lineTo(w, h)
                close()
            }
            drawPath(midPath, midColor)

            drawSnowCap(w * 0.08f, h * 0.64f, w * 0.06f, h * 0.03f, atmosphere.snowIntensity * 0.7f)
            drawSnowCap(w * 0.28f, h * 0.53f, w * 0.09f, h * 0.05f, atmosphere.snowIntensity * 0.8f) // sharp peak cap
            drawSnowCap(w * 0.50f, h * 0.61f, w * 0.07f, h * 0.04f, atmosphere.snowIntensity * 0.7f)
            drawSnowCap(w * 0.72f, h * 0.53f, w * 0.08f, h * 0.05f, atmosphere.snowIntensity * 0.8f)
            drawSnowCap(w * 0.92f, h * 0.64f, w * 0.06f, h * 0.03f, atmosphere.snowIntensity * 0.7f)
        }

        // ── 4. Fore Haze Overlay (between mid and near ranges) ─────────────
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, hazeColor.copy(alpha = 0.12f), Color.Transparent),
                startY = h * 0.60f,
                endY = h * 0.85f,
            ),
        )

        // ── 5. Near Range (foreground hills, dark solid, full parallax) ──────
        val nearDriftX = parallaxDrift * 1.0f
        withTransform({
            translate(left = nearDriftX)
        }) {
            val nearPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.86f)
                quadraticTo(w * 0.10f, h * 0.70f, w * 0.22f, h * 0.84f)
                quadraticTo(w * 0.34f, h * 0.55f, w * 0.46f, h * 0.80f)  // main peak
                quadraticTo(w * 0.58f, h * 0.68f, w * 0.70f, h * 0.84f)
                quadraticTo(w * 0.82f, h * 0.58f, w * 0.92f, h * 0.80f)
                lineTo(w, h * 0.82f)
                lineTo(w, h)
                close()
            }
            drawPath(nearPath, fill)

            drawSnowCap(w * 0.10f, h * 0.78f, w * 0.06f, h * 0.03f, atmosphere.snowIntensity * 0.8f)
            drawSnowCap(w * 0.34f, h * 0.68f, w * 0.09f, h * 0.05f, atmosphere.snowIntensity * 0.9f) // main cap
            drawSnowCap(w * 0.58f, h * 0.76f, w * 0.07f, h * 0.04f, atmosphere.snowIntensity * 0.8f)
            drawSnowCap(w * 0.82f, h * 0.69f, w * 0.08f, h * 0.05f, atmosphere.snowIntensity * 0.9f)
        }
    }
}

private fun DrawScope.drawSnowCap(
    cx: Float,
    cy: Float,
    width: Float,
    height: Float,
    opacity: Float,
) {
    if (opacity <= 0.01f) return
    val path = Path().apply {
        moveTo(cx - width / 2f, cy + height)
        quadraticTo(cx, cy + height * 0.5f, cx, cy) // clean snow curve vertex
        quadraticTo(cx, cy + height * 0.5f, cx + width / 2f, cy + height)
        close()
    }
    drawPath(path, Color.White.copy(alpha = opacity))
}
