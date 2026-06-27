package bharadwaj.juno.music.ambient.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.model.AmbientScene
import bharadwaj.juno.music.ambient.model.AmbientWeather

/**
 * Per-scene color palette consumed by every rendering layer.
 *
 * All alpha values are baked in so layers don't need to guess — they draw
 * elements at full specified alpha and rely on these tokens for the mood.
 *
 * Sky gradients follow a natural color-temperature arc:
 *   Deep night (cool blue-black) → Dawn (indigo → peach) → Morning (azure → pale sky)
 *   → Noon (rich blue) → Afternoon (vivid blue) → Golden Hour (amber → deep crimson)
 *   → Sunset (magenta → burnt orange) → Evening (indigo) → Night (near-black)
 */
data class AmbientSceneColors(
    /** Topmost sky color (used for vertical gradient top stop). */
    val skyTop: Color,
    /** Bottommost sky color (horizon line). */
    val skyBottom: Color,
    /** Optional mid-point stop for three-color gradients. */
    val skyMid: Color? = null,
    /** Primary greeting text color. */
    val textPrimary: Color,
    /** Subtitle / secondary text color. */
    val textSecondary: Color,
    /** Sun or moon disc base color. */
    val celestialBody: Color,
    /** Outer glow / halo color around sun or moon. */
    val celestialGlow: Color,
    /** Mountain silhouette fill color. */
    val mountainFill: Color,
    /** Cloud shape fill color. */
    val cloudFill: Color,
    /** Whether the primary text should cast a subtle shadow for legibility. */
    val needsTextShadow: Boolean = false,
)

// ─── Palette ──────────────────────────────────────────────────────────────────

object AmbientSceneColorPalette {

    // ── Dawn / Sunrise ────────────────────────────────────────────────────────
    // Pre-dawn indigo transitions through a warm peach band to a bright
    // golden horizon — softened from the previous harsh orange jump.
    private val Sunrise = AmbientSceneColors(
        skyTop    = Color(0xFF16082A),       // Deep pre-dawn indigo
        skyMid    = Color(0xFFBD5A38),       // Warm coppery band (replaces harsh FF7043)
        skyBottom = Color(0xFFFFBF70),       // Soft amber horizon
        textPrimary   = Color(0xFFFFF3E0),
        textSecondary = Color(0xFFFFCC88),
        celestialBody = Color(0xFFFFAB40),
        celestialGlow = Color(0xFFFF8C00).copy(alpha = 0.28f),
        mountainFill  = Color(0xFF16082A).copy(alpha = 0.88f),
        cloudFill     = Color(0xFFFF8A65).copy(alpha = 0.28f),
        needsTextShadow = true,
    )

    // ── Clear Morning ─────────────────────────────────────────────────────────
    // Bright sky-blue top fading to a light airy azure — fresh and open.
    private val ClearMorning = AmbientSceneColors(
        skyTop    = Color(0xFF3572C6),
        skyBottom = Color(0xFF93CCE8),
        textPrimary   = Color(0xFF0D1B6E),
        textSecondary = Color(0xFF2D45A8),
        celestialBody = Color(0xFFFFF59D),
        celestialGlow = Color(0xFFFFF9C4).copy(alpha = 0.38f),
        mountainFill  = Color(0xFF607D8B).copy(alpha = 0.50f),
        cloudFill     = Color.White.copy(alpha = 0.70f),
        needsTextShadow = false,
    )

    // ── Cloudy Morning ────────────────────────────────────────────────────────
    private val CloudyMorning = AmbientSceneColors(
        skyTop    = Color(0xFF607D8B),
        skyBottom = Color(0xFFB0BEC5),
        textPrimary   = Color(0xFF1A2E36),
        textSecondary = Color(0xFF37474F),
        celestialBody = Color(0xFFB0BEC5),
        celestialGlow = Color(0xFFECEFF1).copy(alpha = 0.18f),
        mountainFill  = Color(0xFF546E7A).copy(alpha = 0.55f),
        cloudFill     = Color.White.copy(alpha = 0.55f),
    )

    // ── Clear Noon ────────────────────────────────────────────────────────────
    // Vibrant cerulean — the sun is high, sky is rich and saturated.
    private val ClearNoon = AmbientSceneColors(
        skyTop    = Color(0xFF1255A8),       // Brighter than previous 0D47A1
        skyBottom = Color(0xFF5AB4F0),       // Lighter horizon for an open feel
        textPrimary   = Color(0xFFFAFAFA),
        textSecondary = Color(0xFFDCEEFD),
        celestialBody = Color(0xFFFFFDE7),
        celestialGlow = Color(0xFFFFF59D).copy(alpha = 0.32f),
        mountainFill  = Color(0xFF1565C0).copy(alpha = 0.42f),
        cloudFill     = Color.White.copy(alpha = 0.72f),
        needsTextShadow = true,
    )

    // ── Clear Afternoon ───────────────────────────────────────────────────────
    private val ClearAfternoon = AmbientSceneColors(
        skyTop    = Color(0xFF1565C0),
        skyBottom = Color(0xFF64B5F6),
        textPrimary   = Color(0xFFFAFAFA),
        textSecondary = Color(0xFFDCEEFD),
        celestialBody = Color(0xFFFFF176),
        celestialGlow = Color(0xFFFFF9C4).copy(alpha = 0.28f),
        mountainFill  = Color(0xFF0D47A1).copy(alpha = 0.45f),
        cloudFill     = Color.White.copy(alpha = 0.62f),
        needsTextShadow = true,
    )

    // ── Hot Afternoon ─────────────────────────────────────────────────────────
    private val HotAfternoon = AmbientSceneColors(
        skyTop    = Color(0xFF0D47A1),
        skyMid    = Color(0xFF1E88E5),
        skyBottom = Color(0xFFFF8F00),
        textPrimary   = Color(0xFFFAFAFA),
        textSecondary = Color(0xFFFFE082),
        celestialBody = Color(0xFFFFFFFF),
        celestialGlow = Color(0xFFFFF9C4).copy(alpha = 0.42f),
        mountainFill  = Color(0xFF0D47A1).copy(alpha = 0.55f),
        cloudFill     = Color.White.copy(alpha = 0.65f),
        needsTextShadow = true,
    )

    // ── Golden Hour ───────────────────────────────────────────────────────────
    // Warm amber-gold top, tapering through a muted burnt sienna to gold horizon.
    // Softened from the previous harsh E65100 red-orange mid.
    private val GoldenHour = AmbientSceneColors(
        skyTop    = Color(0xFF180A28),
        skyMid    = Color(0xFFCC5500),       // Burnt amber (softened from E65100)
        skyBottom = Color(0xFFFFAB00),
        textPrimary   = Color(0xFFFFF8E1),
        textSecondary = Color(0xFFFFCC40),
        celestialBody = Color(0xFFFFAB40),
        celestialGlow = Color(0xFFFF8C00).copy(alpha = 0.32f),
        mountainFill  = Color(0xFF180A28).copy(alpha = 0.82f),
        cloudFill     = Color(0xFFFF8A65).copy(alpha = 0.38f),
        needsTextShadow = true,
    )

    // ── Sunset Blaze ─────────────────────────────────────────────────────────
    // Routes through deep purple-magenta rather than pure red, giving a more
    // natural sunset spectrum (violet → crimson → orange).
    private val SunsetBlaze = AmbientSceneColors(
        skyTop    = Color(0xFF100320),
        skyMid    = Color(0xFFB5215A),       // Deep magenta-crimson (replaces E53935)
        skyBottom = Color(0xFFFF6030),       // Burnt orange horizon
        textPrimary   = Color(0xFFFFF3E0),
        textSecondary = Color(0xFFFFAB80),
        celestialBody = Color(0xFFFF7043),
        celestialGlow = Color(0xFFE53935).copy(alpha = 0.32f),
        mountainFill  = Color(0xFF100320).copy(alpha = 0.92f),
        cloudFill     = Color(0xFFDD3377).copy(alpha = 0.28f),
        needsTextShadow = true,
    )

    // ── Cloudy Sunset ─────────────────────────────────────────────────────────
    private val CloudySunset = AmbientSceneColors(
        skyTop    = Color(0xFF2E3F4C),
        skyMid    = Color(0xFF6E7F8A),
        skyBottom = Color(0xFFAABBC5),
        textPrimary   = Color(0xFFFAFAFA),
        textSecondary = Color(0xFFCFD8DC),
        celestialBody = Color(0xFFFFCC80),
        celestialGlow = Color(0xFFFFE082).copy(alpha = 0.20f),
        mountainFill  = Color(0xFF1E2D38).copy(alpha = 0.78f),
        cloudFill     = Color(0xFFB0BEC5).copy(alpha = 0.55f),
        needsTextShadow = true,
    )

    // ── Clear Evening (blue hour) ─────────────────────────────────────────────
    // Deep indigo sky — the blue hour after sunset, before full night.
    private val ClearEvening = AmbientSceneColors(
        skyTop    = Color(0xFF090118),
        skyBottom = Color(0xFF18227A),
        textPrimary   = Color(0xFFECEFF1),
        textSecondary = Color(0xFF90CAF9),
        celestialBody = Color(0xFFE8EAF6),
        celestialGlow = Color(0xFFB3BEFE).copy(alpha = 0.20f),
        mountainFill  = Color(0xFF090118).copy(alpha = 0.92f),
        cloudFill     = Color(0xFF3F51B5).copy(alpha = 0.28f),
        needsTextShadow = true,
    )

    // ── Starry Night ──────────────────────────────────────────────────────────
    // Very dark with a subtle deep-navy horizon glow — cold and clear.
    private val StarryNight = AmbientSceneColors(
        skyTop    = Color(0xFF010008),
        skyBottom = Color(0xFF0C1A3C),
        textPrimary   = Color(0xFFECEFF1),
        textSecondary = Color(0xFF90CAF9),
        celestialBody = Color(0xFFFFFDE7),
        celestialGlow = Color(0xFFFFF176).copy(alpha = 0.16f),
        mountainFill  = Color(0xFF010008).copy(alpha = 0.96f),
        cloudFill     = Color(0xFF1A237E).copy(alpha = 0.22f),
        needsTextShadow = true,
    )

    // ── Cloudy Night ──────────────────────────────────────────────────────────
    private val CloudyNight = AmbientSceneColors(
        skyTop    = Color(0xFF080808),
        skyBottom = Color(0xFF1A1A2C),
        textPrimary   = Color(0xFFECEFF1),
        textSecondary = Color(0xFFB0BEC5),
        celestialBody = Color(0xFFCFD8DC),
        celestialGlow = Color(0xFFECEFF1).copy(alpha = 0.10f),
        mountainFill  = Color(0xFF080808).copy(alpha = 0.96f),
        cloudFill     = Color(0xFF37474F).copy(alpha = 0.48f),
        needsTextShadow = true,
    )

    // ── Deep Midnight ─────────────────────────────────────────────────────────
    // Near-pitch-black — the deepest, most restful part of night.
    private val DeepMidnight = AmbientSceneColors(
        skyTop    = Color(0xFF000000),
        skyBottom = Color(0xFF080818),
        textPrimary   = Color(0xFFECEFF1),
        textSecondary = Color(0xFF78909C),
        celestialBody = Color(0xFFB0BEC5),
        celestialGlow = Color(0xFFECEFF1).copy(alpha = 0.07f),
        mountainFill  = Color(0xFF000000).copy(alpha = 0.99f),
        cloudFill     = Color(0xFF1A1A2C).copy(alpha = 0.38f),
        needsTextShadow = true,
    )

    // ── Snow ──────────────────────────────────────────────────────────────────
    private val Snow = AmbientSceneColors(
        skyTop    = Color(0xFFAABCC5),
        skyBottom = Color(0xFFE8EFF2),
        textPrimary   = Color(0xFF1A237E),
        textSecondary = Color(0xFF3949AB),
        celestialBody = Color(0xFFFAFAFA),
        celestialGlow = Color.White.copy(alpha = 0.30f),
        mountainFill  = Color(0xFFB0BEC5).copy(alpha = 0.50f),
        cloudFill     = Color.White.copy(alpha = 0.80f),
    )

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Returns the [AmbientSceneColors] for a given [AmbientScene], with
     * sensible fallbacks for scenes not yet fully implemented.
     */
    fun forScene(scene: AmbientScene): AmbientSceneColors = when (scene) {
        AmbientScene.SunriseGlow      -> Sunrise
        AmbientScene.ClearMorning     -> ClearMorning
        AmbientScene.CloudyMorning    -> CloudyMorning
        AmbientScene.RainyMorning     -> CloudyMorning
        AmbientScene.WinterMorning    -> CloudyMorning
        AmbientScene.ClearNoon        -> ClearNoon
        AmbientScene.CloudyNoon       -> CloudyMorning
        AmbientScene.ClearAfternoon   -> ClearAfternoon
        AmbientScene.HotAfternoon     -> HotAfternoon
        AmbientScene.StormyAfternoon  -> CloudySunset
        AmbientScene.RainyAfternoon   -> CloudyMorning
        AmbientScene.GoldenHour       -> GoldenHour
        AmbientScene.CloudyGoldenHour -> CloudySunset
        AmbientScene.SunsetBlaze      -> SunsetBlaze
        AmbientScene.CloudySunset     -> CloudySunset
        AmbientScene.RainySunset      -> CloudySunset
        AmbientScene.ClearEvening     -> ClearEvening
        AmbientScene.CloudyEvening    -> CloudyNight
        AmbientScene.RainyEvening     -> CloudyNight
        AmbientScene.StarryNight      -> StarryNight
        AmbientScene.CloudyNight      -> CloudyNight
        AmbientScene.RainyNight       -> CloudyNight
        AmbientScene.StormyNight      -> CloudyNight
        AmbientScene.DeepMidnight     -> DeepMidnight
        AmbientScene.SnowScene        -> Snow
    }
}

fun AmbientSceneColors.adjustForWeather(atmosphere: AmbientAtmosphere): AmbientSceneColors {
    val cloudDensity = atmosphere.cloudDensity
    val rain = atmosphere.rainIntensity
    val snow = atmosphere.snowIntensity
    val fog = atmosphere.fogIntensity
    val isStorm = atmosphere.condition == AmbientWeather.Condition.Thunderstorm

    val adjustedSkyTop = adjustColorForWeather(skyTop, cloudDensity, rain, snow, fog, isStorm)
    val adjustedSkyBottom = adjustColorForWeather(skyBottom, cloudDensity, rain, snow, fog, isStorm)
    val adjustedSkyMid = skyMid?.let { adjustColorForWeather(it, cloudDensity, rain, snow, fog, isStorm) }
    
    val adjustedMountain = adjustColorForWeather(mountainFill, cloudDensity, rain, snow, fog, isStorm)
    val adjustedCloud = adjustCloudColorForWeather(cloudFill, atmosphere.condition, cloudDensity)

    return this.copy(
        skyTop = adjustedSkyTop,
        skyBottom = adjustedSkyBottom,
        skyMid = adjustedSkyMid,
        mountainFill = adjustedMountain,
        cloudFill = adjustedCloud,
    )
}

private fun adjustColorForWeather(
    color: Color,
    cloudDensity: Float,
    rainIntensity: Float,
    snowIntensity: Float,
    fogIntensity: Float,
    isThunderstorm: Boolean,
): Color {
    val colorInt = color.toArgb()
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(colorInt, hsl)

    if (cloudDensity > 0f) {
        hsl[1] = hsl[1] * (1f - cloudDensity * 0.55f)
        hsl[2] = hsl[2] * (1f - cloudDensity * 0.40f)
    }

    if (rainIntensity > 0f) {
        hsl[1] = hsl[1] * (1f - rainIntensity * 0.25f)
        hsl[2] = hsl[2] * (1f - rainIntensity * 0.20f)
    }
    if (snowIntensity > 0f) {
        hsl[1] = hsl[1] * (1f - snowIntensity * 0.35f)
        hsl[2] = (hsl[2] + snowIntensity * 0.12f).coerceIn(0f, 1f)
    }

    if (fogIntensity > 0f) {
        hsl[1] = hsl[1] * (1f - fogIntensity * 0.75f)
        hsl[2] = hsl[2] * (1f - fogIntensity * 0.40f) + (0.7f * fogIntensity * 0.40f)
    }

    if (isThunderstorm) {
        hsl[1] = hsl[1] * 0.35f
        hsl[2] = hsl[2] * 0.30f
        hsl[0] = if (hsl[0] in 0f..70f) hsl[0] + 15f else hsl[0]
    }

    return Color(ColorUtils.HSLToColor(hsl))
}

private fun adjustCloudColorForWeather(
    color: Color,
    condition: AmbientWeather.Condition,
    density: Float,
): Color {
    val colorInt = color.toArgb()
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(colorInt, hsl)

    when (condition) {
        AmbientWeather.Condition.Thunderstorm -> {
            hsl[0] = 260f
            hsl[1] = 0.12f
            hsl[2] = 0.28f
        }
        AmbientWeather.Condition.HeavyRain, AmbientWeather.Condition.Rain -> {
            hsl[0] = 210f
            hsl[1] = 0.10f
            hsl[2] = 0.42f
        }
        AmbientWeather.Condition.Drizzle -> {
            hsl[0] = 205f
            hsl[1] = 0.08f
            hsl[2] = 0.58f
        }
        AmbientWeather.Condition.Snow -> {
            hsl[0] = 215f
            hsl[1] = 0.12f
            hsl[2] = 0.88f
        }
        AmbientWeather.Condition.Fog -> {
            hsl[0] = 0f
            hsl[1] = 0f
            hsl[2] = 0.80f
        }
        else -> {
            hsl[1] = hsl[1] * (1f - density * 0.35f)
            hsl[2] = (hsl[2] - density * 0.12f).coerceAtLeast(0.45f)
        }
    }
    return Color(ColorUtils.HSLToColor(hsl)).copy(alpha = color.alpha)
}
