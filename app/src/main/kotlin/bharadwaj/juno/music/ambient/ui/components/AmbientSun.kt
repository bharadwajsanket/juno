package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import bharadwaj.juno.music.ambient.ui.motion.AmbientMotion

/**
 * Sun disc with real orbital arc and animated glow.
 *
 * Position is driven by [AmbientAtmosphere.solarProgress]:
 *
 *   progress = 0.0  → bottom-left  (dawn, just clearing the horizon)
 *   progress = 0.5  → top-center   (solar noon, highest point)
 *   progress = 1.0  → bottom-right (dusk, descending to horizon)
 *
 * X follows a linear left-to-right sweep:
 *   cx = lerp(0.18·w, 0.88·w, solarProgress)
 *
 * Y follows an inverted parabola (highest at progress=0.5):
 *   arcFactor = 4 · p · (1 − p)       ← 0 at endpoints, 1.0 at noon
 *   cy = lerp(h·0.90, h·0.16, arcFactor)
 *
 * At the horizon (progress ≈ 0 or ≈ 1), the disc is partially clipped by
 * a top-semicircle arc so it appears to rise from / sink below the scene.
 * The clipping threshold is when the disc's bottom edge would exit the card.
 *
 * Disc size also scales with altitude — larger and warmer near the horizon
 * (simulating atmospheric refraction), smaller and brighter overhead.
 *
 * The ambient [AmbientMotion.float] breathes the disc gently upward and
 * downward independent of the arc — imperceptible unless watched.
 */
@Composable
fun AmbientSun(
    atmosphere: AmbientAtmosphere,
    colors: AmbientSceneColors,
    modifier: Modifier = Modifier,
) {
    val floatY by AmbientMotion.float(label = "sun_float", periodMs = 8_000, amplitude = 3f)
    val glowScale by AmbientMotion.breathe(label = "sun_glow", periodMs = 7_000, maxScale = 1.05f)

    Canvas(modifier = modifier) {
        // Dim sun under cloud cover (completely hidden when overcast > 0.8)
        val visibility = (1f - (atmosphere.cloudDensity * 1.25f)).coerceIn(0f, 1f)
        if (visibility <= 0.01f) return@Canvas

        val w = size.width
        val h = size.height
        val p = atmosphere.solarProgress

        // ── Orbital position ─────────────────────────────────────────────────
        val cx = lerp(w * 0.18f, w * 0.88f, p)

        // Inverted parabola: peaks at p=0.5
        val arcFactor = 4f * p * (1f - p)
        val cyRaw = lerp(h * 0.90f, h * 0.16f, arcFactor)
        val cy = cyRaw + floatY

        // ── Disc radius — larger near horizon, smaller overhead ───────────────
        // At arcFactor=0 (horizon): radius = h * 0.17
        // At arcFactor=1 (noon):    radius = h * 0.11
        val bodyRadius = lerp(h * 0.17f, h * 0.11f, arcFactor)

        // ── Glow — wide and warm near horizon, tight and white overhead ───────
        // glowIntensity drives how warm/wide the glow is
        val gi = atmosphere.glowIntensity
        val outerGlowR = bodyRadius * lerp(3.2f, 2.4f, arcFactor) * glowScale
        val midGlowR   = bodyRadius * lerp(1.80f, 1.50f, arcFactor) * glowScale

        val outerGlowColor = lerp(colors.celestialGlow, Color(0x22FF8C00), gi).copy(alpha = 0.16f * visibility)
        val midGlowColor   = lerp(colors.celestialGlow, Color(0x44FF8C00), gi).copy(alpha = 0.28f * visibility)

        // Outer halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(outerGlowColor, Color.Transparent),
                center = Offset(cx, cy),
                radius = outerGlowR,
            ),
            radius = outerGlowR,
            center = Offset(cx, cy),
        )

        // Mid glow ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(midGlowColor, Color.Transparent),
                center = Offset(cx, cy),
                radius = midGlowR,
            ),
            radius = midGlowR,
            center = Offset(cx, cy),
        )

        // ── Disc — horizon clip when bottom edge exits card ───────────────────
        val isAtHorizon = cy + bodyRadius > h
        val discColors = listOf(
            colors.celestialBody.copy(alpha = colors.celestialBody.alpha * visibility),
            colors.celestialGlow.copy(alpha = 0.48f * visibility)
        )

        if (isAtHorizon) {
            // Draw only the top semicircle (startAngle=180° → sweeps upward in Canvas coords)
            drawArc(
                brush = Brush.radialGradient(
                    colors = discColors,
                    center = Offset(cx, cy),
                    radius = bodyRadius,
                ),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - bodyRadius, cy - bodyRadius),
                size = Size(bodyRadius * 2f, bodyRadius * 2f),
            )
        } else {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = discColors,
                    center = Offset(cx, cy),
                    radius = bodyRadius,
                ),
                radius = bodyRadius,
                center = Offset(cx, cy),
            )
        }
    }
}

// ── lerp helpers for Float ────────────────────────────────────────────────────

/** Linear interpolation between two floats. */
private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)
