package bharadwaj.juno.music.ambient.engine

import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.model.AmbientState
import bharadwaj.juno.music.ambient.model.AmbientWeather.Condition
import kotlin.math.abs

/**
 * Pure, stateless calculator that transforms an [AmbientState.Active] + current
 * time into a continuous [AmbientAtmosphere].
 *
 * Design principles:
 *  - No I/O, no coroutines, no side effects — safe to call from any thread.
 *  - All math is local — no API calls, no system services.
 *  - All outputs are deterministic functions of inputs — same inputs always
 *    produce the same outputs, enabling testability and stable Compose Previews.
 *
 * Solar arc model:
 *   The sun travels from civil dawn (progress=0) to civil dusk (progress=1)
 *   along a parabolic vertical arc. Using civil twilight (not strict sunrise)
 *   gives a longer, smoother arc that includes the warm pre-dawn and post-sunset
 *   glow periods rather than snapping to a visible disc at the exact moment of
 *   sunrise.
 *
 * Lunar arc model:
 *   After civil dusk, the moon rises and sets before the next civil dawn.
 *   Progress 0 = civil dusk (moon at horizon), 0.5 = astronomical midnight
 *   (moon highest), 1.0 = next civil dawn (moon at horizon again).
 *
 * Glow model:
 *   Glow intensity is a "U-shaped" function of solar progress — highest at
 *   dawn and dusk (the warm-colored periods), lowest at solar noon and midnight.
 *   This drives horizon color warmth in [AmbientSky].
 */
object AmbientAtmosphereCalculator {

    /** One day in milliseconds. */
    private const val DAY_MS = 24L * 60L * 60L * 1_000L

    /**
     * Calculates [AmbientAtmosphere] for the given active state and epoch time.
     *
     * @param state    Must be [AmbientState.Active]. Returns [AmbientAtmosphere.Neutral] otherwise.
     * @param nowMs    Current time in epoch milliseconds (defaults to now).
     */
    fun calculate(
        state: AmbientState,
        nowMs: Long = System.currentTimeMillis(),
    ): AmbientAtmosphere {
        if (state !is AmbientState.Active) {
            // Fallback: Use current device local time to approximate day/night
            val calendar = java.util.Calendar.getInstance().apply { timeInMillis = nowMs }
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)

            // Assume day is roughly 6 AM to 6 PM (6:00 to 18:00)
            val isSunVisible = hour in 6..17
            val isMoonVisible = !isSunVisible

            val solarProgress: Float
            val lunarProgress: Float

            if (isSunVisible) {
                // Day duration: 12 hours (720 minutes). Starts at 6 AM.
                val minutesSinceDawn = (hour - 6) * 60 + minute
                solarProgress = (minutesSinceDawn.toFloat() / 720f).coerceIn(0f, 1f)
                lunarProgress = 0.5f
            } else {
                // Night duration: 12 hours (720 minutes). Starts at 6 PM.
                val minutesSinceDusk = if (hour >= 18) {
                    (hour - 18) * 60 + minute
                } else {
                    (hour + 6) * 60 + minute
                }
                solarProgress = 0.5f
                lunarProgress = (minutesSinceDusk.toFloat() / 720f).coerceIn(0f, 1f)
            }

            val glowIntensity = if (isSunVisible) {
                abs(2f * solarProgress - 1f)
            } else {
                0.08f
            }

            return AmbientAtmosphere(
                solarProgress = solarProgress,
                lunarProgress = lunarProgress,
                cloudDensity = 0.04f, // Clear sky for beautiful fallback rendering
                starVisibility = if (isMoonVisible) 1.0f else 0.0f,
                glowIntensity = glowIntensity,
                isSunVisible = isSunVisible,
                isMoonVisible = isMoonVisible,
                rainIntensity = 0.0f,
                snowIntensity = 0.0f,
                fogIntensity = 0.0f,
                windSpeedKmh = 5f,
                condition = Condition.Clear,
            )
        }

        val timeData = state.timeData
        val dawn  = timeData.civilTwilightStartMs
        val dusk  = timeData.civilTwilightEndMs
        val dayspan = (dusk - dawn).coerceAtLeast(1L)

        // ── Solar progress (0 = dawn, 1 = dusk) ──────────────────────────────
        val solarProgress = ((nowMs - dawn).toFloat() / dayspan).coerceIn(0f, 1f)
        val isSunVisible  = nowMs in dawn..dusk

        // ── Lunar progress (0 = dusk, 1 = next dawn) ─────────────────────────
        // Night spans from civil dusk today → civil dawn tomorrow.
        // If it's before dawn, we are in the tail of last night — offset backward by one day.
        val nightStart = dusk
        val nightEnd   = dawn + DAY_MS  // next day's civil dawn
        val nightspan  = (nightEnd - nightStart).coerceAtLeast(1L)

        val lunarProgress: Float
        val isMoonVisible: Boolean

        if (!isSunVisible) {
            val nowInNight = if (nowMs < dawn) {
                // Early morning — we are in the tail of last night.
                // Recalculate relative to yesterday's dusk.
                val yesterdayDusk = dusk - DAY_MS
                nowMs - yesterdayDusk
            } else {
                // After dusk — beginning of tonight's night.
                nowMs - nightStart
            }
            lunarProgress = (nowInNight.toFloat() / nightspan).coerceIn(0f, 1f)
            isMoonVisible = true
        } else {
            lunarProgress = 0.50f
            isMoonVisible = false
        }

        // ── Cloud density from weather condition & cloudCoverPercent ──────────
        val baseCloudDensity = cloudDensityFor(state.weather.condition)
        val cloudDensity = if (state.weather.cloudCoverPercent > 0) {
            (state.weather.cloudCoverPercent / 100f).coerceIn(0f, 1f)
        } else {
            baseCloudDensity
        }

        // ── Star visibility (Clear = many stars, Cloudy = few stars, Rain/Fog/Storm = 0 stars)
        val starVisibility = when (state.weather.condition) {
            Condition.Clear -> (1f - (cloudDensity / 0.3f)).coerceIn(0f, 1f)
            Condition.PartlyCloudy -> (0.5f - (cloudDensity / 0.5f)).coerceIn(0f, 1f)
            Condition.Clouds -> (0.2f - (cloudDensity / 0.8f)).coerceIn(0f, 1f)
            else -> 0f // No stars visible during precipitations, fog, or storm
        }

        // ── Rain intensity ──
        val rainIntensity = when (state.weather.condition) {
            Condition.Drizzle -> if (state.weather.rainMm > 0.0) (state.weather.rainMm / 2.0).toFloat().coerceIn(0.1f, 0.4f) else 0.25f
            Condition.Rain -> if (state.weather.rainMm > 0.0) (state.weather.rainMm / 8.0).toFloat().coerceIn(0.4f, 0.8f) else 0.60f
            Condition.HeavyRain -> if (state.weather.rainMm > 0.0) (state.weather.rainMm / 15.0).toFloat().coerceIn(0.8f, 1.0f) else 1.00f
            Condition.Thunderstorm -> if (state.weather.precipitationMm > 0.0) (state.weather.precipitationMm / 15.0).toFloat().coerceIn(0.7f, 1.0f) else 0.90f
            else -> 0.0f
        }

        // ── Snow intensity ──
        val snowIntensity = when (state.weather.condition) {
            Condition.Snow -> if (state.weather.snowfallCm > 0.0) (state.weather.snowfallCm / 5.0).toFloat().coerceIn(0.2f, 1.0f) else 0.50f
            else -> 0.0f
        }

        // ── Fog intensity ──
        val baseFog = when (state.weather.condition) {
            Condition.Fog -> if (state.weather.visibilityMeters < 10000.0) (1f - (state.weather.visibilityMeters.toFloat() / 5000f)).coerceIn(0.5f, 1.0f) else 0.80f
            Condition.Drizzle, Condition.Rain -> 0.15f
            Condition.HeavyRain, Condition.Thunderstorm -> 0.35f
            else -> if (state.weather.visibilityMeters < 8000.0) (1f - (state.weather.visibilityMeters.toFloat() / 8000f)).coerceIn(0f, 0.6f) else 0f
        }
        val isMorning = timeData.bucket == bharadwaj.juno.music.ambient.model.AmbientTimeBucket.Dawn || 
                         timeData.bucket == bharadwaj.juno.music.ambient.model.AmbientTimeBucket.Morning
        val morningMist = if (isMorning && state.weather.humidity > 85) 0.20f else 0.0f
        val fogIntensity = maxOf(baseFog, morningMist)

        // ── Wind Speed ──
        val windSpeed = state.weather.windSpeedKmh.toFloat()

        // ── Glow intensity (U-shaped, peaks at dawn/dusk) ─────────────────────
        // At noon (solarProgress=0.5): glow=0. At dawn/dusk (0.0 or 1.0): glow=1.
        // Use |2*p - 1| which maps 0→1, 0.5→0, 1→1.
        val glowIntensity = if (isSunVisible) {
            abs(2f * solarProgress - 1f)
        } else {
            // Deep night — no glow from sun. A faint fixed ambient keeps depth.
            0.08f
        }

        return AmbientAtmosphere(
            solarProgress  = solarProgress,
            lunarProgress  = lunarProgress,
            cloudDensity   = cloudDensity,
            starVisibility = starVisibility,
            glowIntensity  = glowIntensity,
            isSunVisible   = isSunVisible,
            isMoonVisible  = isMoonVisible,
            rainIntensity  = rainIntensity,
            snowIntensity  = snowIntensity,
            fogIntensity   = fogIntensity,
            windSpeedKmh   = windSpeed,
            condition      = state.weather.condition,
        )
    }

    // ─── Cloud density table ──────────────────────────────────────────────────

    private fun cloudDensityFor(condition: Condition): Float = when (condition) {
        Condition.Clear        -> 0.04f
        Condition.Unknown      -> 0.08f
        Condition.PartlyCloudy -> 0.32f
        Condition.Fog          -> 0.72f
        Condition.Clouds       -> 0.68f
        Condition.Drizzle      -> 0.78f
        Condition.Rain         -> 0.84f
        Condition.HeavyRain    -> 0.92f
        Condition.Snow         -> 0.86f
        Condition.Thunderstorm -> 0.96f
    }
}
