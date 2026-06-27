package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import bharadwaj.juno.music.ambient.ui.motion.AmbientMotion

/**
 * Crescent moon with real orbital arc and soft glow.
 *
 * Position is driven by [AmbientAtmosphere.lunarProgress]:
 *
 *   progress = 0.0  → bottom-right (dusk, moon just rising on the west)
 *   progress = 0.5  → top-center   (astronomical midnight, highest point)
 *   progress = 1.0  → bottom-left  (dawn, moon setting on the east)
 *
 * The moon travels the opposite east→west direction to the sun.
 *
 *   cx = lerp(0.84·w, 0.16·w, lunarProgress)   ← right to left
 *   arcFactor = 4 · p · (1 − p)
 *   cy = lerp(h·0.88, h·0.18, arcFactor)
 *
 * The crescent is formed by painting a full disc then overlaying a shadow
 * circle at a slight upper-right offset, leaving a crescent on the left.
 *
 * The [AmbientMotion.float] animation gives a calm, breathing vertical drift
 * on top of the mathematically correct orbital position.
 */
@Composable
fun AmbientMoon(
    atmosphere: AmbientAtmosphere,
    colors: AmbientSceneColors,
    modifier: Modifier = Modifier,
) {
    val floatY by AmbientMotion.float(label = "moon_float", periodMs = 9_000, amplitude = 4f)
    val glowScale by AmbientMotion.breathe(label = "moon_glow", periodMs = 8_000, maxScale = 1.05f)

    Canvas(modifier = modifier) {
        // Dim/hide moon under cloud cover (completely hidden during storms > 0.8)
        val visibility = (1f - (atmosphere.cloudDensity * 1.25f)).coerceIn(0f, 1f)
        if (visibility <= 0.01f) return@Canvas

        val w = size.width
        val h = size.height
        val p = atmosphere.lunarProgress

        // ── Orbital position (right → left arc) ───────────────────────────────
        val cx = lerp(w * 0.84f, w * 0.16f, p)

        // Inverted parabola: peaks at p=0.5 (midnight)
        val arcFactor = 4f * p * (1f - p)
        val cyRaw = lerp(h * 0.88f, h * 0.18f, arcFactor)
        val cy = cyRaw + floatY

        val moonR = lerp(h * 0.14f, h * 0.10f, arcFactor)

        // ── Moon Halo (subtle wide scattering ring) ──────────────────────────
        // Visible only with thin cloud coverage (density between 0.08 and 0.55)
        val hasHalo = atmosphere.cloudDensity in 0.08f..0.55f
        if (hasHalo) {
            drawCircle(
                color = Color.White.copy(alpha = 0.06f * visibility),
                radius = moonR * 3.8f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5f),
            )
        }

        // ── Outer glow halo ───────────────────────────────────────────────────
        val glowR = moonR * 2.4f * glowScale
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.celestialGlow.copy(alpha = 0.14f * visibility),
                    Color.Transparent,
                ),
                center = Offset(cx, cy),
                radius = glowR,
            ),
            radius = glowR,
            center = Offset(cx, cy),
        )

        // ── Moon disc (Crescent Path subtraction) ─────────────────────────────
        // We construct a clean path that represents only the crescent shape.
        // This is safe at any transparency level and blends correctly on any background.
        val path = Path().apply {
            val path1 = Path().apply { addOval(Rect(cx - moonR, cy - moonR, cx + moonR, cy + moonR)) }
            val path2 = Path().apply {
                val r2 = moonR * 0.82f
                val cx2 = cx + moonR * 0.38f
                val cy2 = cy - moonR * 0.10f
                addOval(Rect(cx2 - r2, cy2 - r2, cx2 + r2, cy2 + r2))
            }
            op(path1, path2, PathOperation.Difference)
        }

        drawPath(
            path = path,
            color = colors.celestialBody.copy(alpha = 0.90f * visibility),
        )
    }
}

// ── lerp helper ───────────────────────────────────────────────────────────────

private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)
