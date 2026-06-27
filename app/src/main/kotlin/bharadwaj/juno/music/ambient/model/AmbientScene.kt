package bharadwaj.juno.music.ambient.model

/**
 * A discrete ambient scene representing a specific combination of time-of-day,
 * weather conditions, and seasonal context.
 *
 * Each scene maps to a unique visual/audio atmosphere that the rendering layer
 * will use to drive UI animations and theming (implemented in a later phase).
 *
 * Scenes are resolved by [bharadwaj.juno.music.ambient.engine.AmbientEngine]
 * from the inputs: [AmbientLocation], [AmbientWeather], and [AmbientTimeData].
 */
enum class AmbientScene {

    // ── Dawn ──────────────────────────────────────────────────────────────────
    /** First light — clear sky, cool air, low horizontal light. */
    SunriseGlow,

    // ── Morning ───────────────────────────────────────────────────────────────
    /** Clear, crisp morning sky. */
    ClearMorning,
    /** Overcast or partly cloudy morning. */
    CloudyMorning,
    /** Rain or drizzle in the morning. */
    RainyMorning,
    /** Sub-zero or near-freezing morning (temp < 5 °C). */
    WinterMorning,

    // ── Midday ────────────────────────────────────────────────────────────────
    /** Bright, sunny midday sky. */
    ClearNoon,
    /** Diffuse light through clouds at noon. */
    CloudyNoon,

    // ── Afternoon ─────────────────────────────────────────────────────────────
    /** Intense heat, clear sky (temp > 33 °C). */
    HotAfternoon,
    /** Thunder and storm activity in the afternoon. */
    StormyAfternoon,
    /** Rain or heavy overcast in the afternoon. */
    RainyAfternoon,
    /** Clear, comfortable afternoon. */
    ClearAfternoon,

    // ── Golden Hour ───────────────────────────────────────────────────────────
    /** Warm golden light in the hour before sunset — clear sky. */
    GoldenHour,
    /** Golden hour obscured by clouds. */
    CloudyGoldenHour,

    // ── Sunset ────────────────────────────────────────────────────────────────
    /** Vivid orange/red sunset, clear horizon. */
    SunsetBlaze,
    /** Muted sunset behind cloud layer. */
    CloudySunset,
    /** Sunset with rain or drizzle. */
    RainySunset,

    // ── Evening ───────────────────────────────────────────────────────────────
    /** Clear sky just after sunset — blue hour. */
    ClearEvening,
    /** Overcast evening. */
    CloudyEvening,
    /** Rain during the evening. */
    RainyEvening,

    // ── Night ─────────────────────────────────────────────────────────────────
    /** Clear, starry night sky. */
    StarryNight,
    /** Overcast night — no visible stars. */
    CloudyNight,
    /** Rainy night. */
    RainyNight,
    /** Thunderstorm at night. */
    StormyNight,

    // ── Midnight ──────────────────────────────────────────────────────────────
    /** Deepest part of the night — still and dark. */
    DeepMidnight,

    // ── Special ───────────────────────────────────────────────────────────────
    /** Snow on the ground or falling snow — any time of day. */
    SnowScene,
}
