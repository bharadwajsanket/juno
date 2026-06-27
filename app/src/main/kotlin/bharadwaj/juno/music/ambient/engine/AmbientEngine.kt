package bharadwaj.juno.music.ambient.engine

import bharadwaj.juno.music.ambient.model.AmbientLocation
import bharadwaj.juno.music.ambient.model.AmbientScene
import bharadwaj.juno.music.ambient.model.AmbientScene.*
import bharadwaj.juno.music.ambient.model.AmbientTimeData
import bharadwaj.juno.music.ambient.model.AmbientTimeBucket
import bharadwaj.juno.music.ambient.model.AmbientWeather
import bharadwaj.juno.music.ambient.model.AmbientWeather.Condition.*

/**
 * Pure, stateless decision engine that maps the triple
 * (AmbientLocation, AmbientWeather, AmbientTimeData) to an [AmbientScene].
 *
 * Design principles:
 *  - No I/O, no coroutines, no side effects — safe to call from any thread.
 *  - Snowfall overrides all other scenes (highest priority).
 *  - Thunderstorm overrides except for Snow.
 *  - Temperature extremes (hot/cold) take precedence over generic clear scenes.
 *  - [shouldTransition] enforces hysteresis to prevent scene thrashing on
 *    minor weather fluctuations.
 *
 * Note: [AmbientTimeBucket] entries are always qualified as [AmbientTimeBucket.X]
 * to disambiguate from [AmbientScene] entries that share names (e.g. GoldenHour).
 */
object AmbientEngine {

    // ─── Scene resolution ─────────────────────────────────────────────────────

    /**
     * Resolves the current [AmbientScene] from the three input sources.
     *
     * @param location  Current device location (used for future seasonal extensions).
     * @param weather   Current weather snapshot.
     * @param timeData  Resolved time data including the active bucket.
     */
    fun resolve(
        location: AmbientLocation,
        weather: AmbientWeather,
        timeData: AmbientTimeData,
    ): AmbientScene {
        val condition = weather.condition
        val bucket = timeData.bucket

        // ── Snow: highest priority override ──
        if (condition == Snow) return SnowScene

        // ── Thunderstorm: second priority ──
        if (condition == Thunderstorm) {
            return when (bucket) {
                AmbientTimeBucket.Dawn,
                AmbientTimeBucket.Morning,
                AmbientTimeBucket.Noon,
                AmbientTimeBucket.Afternoon -> StormyAfternoon
                AmbientTimeBucket.GoldenHour,
                AmbientTimeBucket.Sunset    -> RainySunset
                AmbientTimeBucket.Evening   -> RainyEvening
                AmbientTimeBucket.Night,
                AmbientTimeBucket.Midnight  -> StormyNight
            }
        }

        // ── Normal resolution matrix ──
        return when (bucket) {

            AmbientTimeBucket.Dawn -> SunriseGlow

            AmbientTimeBucket.Morning -> when {
                weather.isCold          -> WinterMorning
                condition.isPrecip()    -> RainyMorning
                condition == Clouds ||
                condition == PartlyCloudy ||
                condition == Fog        -> CloudyMorning
                else                    -> ClearMorning
            }

            AmbientTimeBucket.Noon -> when {
                condition.isPrecip()    -> CloudyNoon
                condition == Clouds ||
                condition == Fog        -> CloudyNoon
                else                    -> ClearNoon
            }

            AmbientTimeBucket.Afternoon -> when {
                condition.isPrecip()    -> RainyAfternoon
                weather.isHot &&
                condition == Clear      -> HotAfternoon
                condition == Clouds ||
                condition == PartlyCloudy ||
                condition == Fog        -> CloudyNoon
                else                    -> ClearAfternoon
            }

            AmbientTimeBucket.GoldenHour -> when {
                condition.isPrecip()    -> CloudyGoldenHour
                condition == Clouds ||
                condition == PartlyCloudy ||
                condition == Fog        -> CloudyGoldenHour
                else                    -> GoldenHour
            }

            AmbientTimeBucket.Sunset -> when {
                condition.isPrecip()    -> RainySunset
                condition == Clouds ||
                condition == PartlyCloudy -> CloudySunset
                else                    -> SunsetBlaze
            }

            AmbientTimeBucket.Evening -> when {
                condition.isPrecip()    -> RainyEvening
                condition == Clouds ||
                condition == Fog        -> CloudyEvening
                else                    -> ClearEvening
            }

            AmbientTimeBucket.Night -> when {
                condition.isPrecip()    -> RainyNight
                condition == Clouds ||
                condition == Fog        -> CloudyNight
                else                    -> StarryNight
            }

            AmbientTimeBucket.Midnight -> DeepMidnight
        }
    }

    // ─── Transition guard ─────────────────────────────────────────────────────

    /**
     * Returns true when a transition from [from] to [to] should be committed.
     *
     * Rules:
     *  - If the scenes are identical, never transition.
     *  - Snowfall/thunderstorm scene changes are always allowed (safety-first).
     *  - Minor stylistic variants within the same time bucket are suppressed to
     *    avoid flickering on borderline weather conditions.
     */
    fun shouldTransition(from: AmbientScene, to: AmbientScene): Boolean {
        if (from == to) return false

        // Always allow transitions involving special scenes (snow, storm)
        if (to == SnowScene || from == SnowScene) return true
        if (to == StormyNight || to == StormyAfternoon) return true
        if (from == StormyNight || from == StormyAfternoon) return true

        // Suppress transitions between close variants of the same time bucket
        val closeVariants = setOf(
            setOf(ClearMorning, CloudyMorning),
            setOf(ClearNoon, CloudyNoon),
            setOf(GoldenHour, CloudyGoldenHour),
            setOf(SunsetBlaze, CloudySunset),
            setOf(ClearEvening, CloudyEvening),
            setOf(StarryNight, CloudyNight),
        )

        for (pair in closeVariants) {
            if (from in pair && to in pair) return false
        }

        return true
    }

    // ─── Internal helpers ────────────────────────────────────────────────────

    private fun AmbientWeather.Condition.isPrecip(): Boolean =
        this in setOf(Drizzle, Rain, HeavyRain)
}
