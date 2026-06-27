package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors
import bharadwaj.juno.music.ambient.ui.motion.AmbientMotion

/**
 * A fixed field of 16 stars at pre-computed stable positions.
 *
 * Star visibility is driven by [AmbientAtmosphere.starVisibility]:
 *   1.0 = full clear-night sky (all stars at full alpha)
 *   0.5 = partly cloudy (stars dimmed and reduced)
 *   0.0 = overcast/rain/storm (no stars visible — composable skips draw)
 *
 * Stars are confined to the upper 50% of the card so they remain visible
 * above the mountain silhouettes (which start at ~55% height) and don't
 * bleed into the greeting area.
 *
 * 4 twinkle groups run asynchronously for a natural, desynchronised shimmer.
 * Twinkle alphas are multiplied by [starVisibility] so they fade smoothly
 * with increasing cloud cover.
 */
@Composable
fun AmbientStars(
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val visibility = atmosphere.starVisibility

    // Skip all draw and animation work when stars are invisible
    if (visibility <= 0.02f) return

    // 4 twinkle groups — staggered phase for natural desync
    val alpha0 by AmbientMotion.twinkle(label = "star_a", periodMs = 3_200, phaseOffsetMs = 0,   minAlpha = 0.22f)
    val alpha1 by AmbientMotion.twinkle(label = "star_b", periodMs = 2_600, phaseOffsetMs = 700,  minAlpha = 0.18f)
    val alpha2 by AmbientMotion.twinkle(label = "star_c", periodMs = 3_800, phaseOffsetMs = 1400, minAlpha = 0.28f)
    val alpha3 by AmbientMotion.twinkle(label = "star_d", periodMs = 2_200, phaseOffsetMs = 350,  minAlpha = 0.15f)

    // All Y positions (ry) ≤ 0.50 — stars live in the upper half of the card
    // (relX, relY, radiusFactor) — all relative to canvas dimensions
    val groups: List<Pair<Float, List<Triple<Float, Float, Float>>>> = listOf(
        alpha0 to listOf(
            Triple(0.08f, 0.06f, 1.8f),
            Triple(0.35f, 0.10f, 1.3f),
            Triple(0.55f, 0.05f, 2.0f),
            Triple(0.82f, 0.12f, 1.4f),
        ),
        alpha1 to listOf(
            Triple(0.18f, 0.20f, 1.2f),
            Triple(0.45f, 0.24f, 2.2f),
            Triple(0.68f, 0.18f, 1.0f),
            Triple(0.92f, 0.22f, 1.6f),
        ),
        alpha2 to listOf(
            Triple(0.10f, 0.36f, 1.5f),
            Triple(0.30f, 0.32f, 1.0f),
            Triple(0.60f, 0.38f, 2.0f),
            Triple(0.85f, 0.34f, 1.2f),
        ),
        alpha3 to listOf(
            Triple(0.22f, 0.46f, 1.4f),
            Triple(0.50f, 0.44f, 1.0f),
            Triple(0.72f, 0.48f, 1.8f),
            Triple(0.95f, 0.42f, 1.2f),
        ),
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val baseColor = colors.textPrimary

        for ((alpha, stars) in groups) {
            for ((rx, ry, rf) in stars) {
                // Horizon fade: stars closer to the horizon (ry close to 0.50) are faded down
                val horizonFade = (1f - (ry / 0.50f) * 0.50f).coerceIn(0.2f, 1.0f)

                // Combine twinkle alpha, global weather visibility, and horizon fade
                val effectiveAlpha = alpha * visibility * horizonFade * 0.75f

                // Size variation: stars pulse slightly in sync with their twinkling intensity
                val sizePulse = 0.85f + alpha * 0.15f
                val radius = rf * density * sizePulse

                drawCircle(
                    color  = baseColor.copy(alpha = effectiveAlpha),
                    radius = radius,
                    center = Offset(w * rx, h * ry),
                )
            }
        }
    }
}
