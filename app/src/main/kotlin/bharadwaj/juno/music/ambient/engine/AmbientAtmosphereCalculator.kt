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
        val zoneId = try {
            if (state is AmbientState.Active) {
                java.time.ZoneId.of(state.timeData.timezoneId)
            } else {
                java.time.ZoneId.systemDefault()
            }
        } catch (e: Exception) {
            java.time.ZoneId.systemDefault()
        }

        val sunrise = if (state is AmbientState.Active && state.timeData.sunriseEpochMs > 0L) {
            state.timeData.sunriseEpochMs
        } else {
            val localDate = java.time.Instant.ofEpochMilli(nowMs).atZone(zoneId).toLocalDate()
            localDate.atTime(6, 0).atZone(zoneId).toInstant().toEpochMilli()
        }

        val sunset = if (state is AmbientState.Active && state.timeData.sunsetEpochMs > 0L) {
            state.timeData.sunsetEpochMs
        } else {
            val localDate = java.time.Instant.ofEpochMilli(nowMs).atZone(zoneId).toLocalDate()
            localDate.atTime(18, 0).atZone(zoneId).toInstant().toEpochMilli()
        }

        val isSunVisible = nowMs in sunrise..sunset
        val isMoonVisible = !isSunVisible

        val solarProgress: Float
        val lunarProgress: Float

        if (isSunVisible) {
            solarProgress = ((nowMs - sunrise).toFloat() / (sunset - sunrise).coerceAtLeast(1L)).coerceIn(0f, 1f)
            lunarProgress = 0.5f
        } else {
            solarProgress = 0.5f
            val nextSunrise = sunrise + 24L * 60L * 60L * 1000L
            val yesterdaySunset = sunset - 24L * 60L * 60L * 1000L
            val (nightStart, nightEnd) = if (nowMs < sunrise) {
                yesterdaySunset to sunrise
            } else {
                sunset to nextSunrise
            }
            lunarProgress = ((nowMs - nightStart).toFloat() / (nightEnd - nightStart).coerceAtLeast(1L)).coerceIn(0f, 1f)
        }

        val glowIntensity = if (isSunVisible) {
            abs(2f * solarProgress - 1f)
        } else {
            0.08f
        }

        if (state !is AmbientState.Active) {
            return AmbientAtmosphere(
                solarProgress = solarProgress,
                lunarProgress = lunarProgress,
                cloudDensity = 0.04f,
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
