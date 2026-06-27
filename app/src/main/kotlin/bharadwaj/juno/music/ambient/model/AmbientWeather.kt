package bharadwaj.juno.music.ambient.model

import kotlinx.serialization.Serializable

/**
 * Normalized weather snapshot used by the Ambient system.
 *
 * @param temperatureCelsius  Current air temperature in °C.
 * @param feelsLikeCelsius    Apparent temperature in °C.
 * @param humidity            Relative humidity 0–100 %.
 * @param condition           Categorised weather condition.
 * @param windSpeedKmh        Wind speed at 10 m above ground in km/h.
 * @param timestampMs         Epoch milliseconds when this data was fetched.
 * @param isStale             True when the data comes from cache and is past its freshness window.
 */
@Serializable
data class AmbientWeather(
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val humidity: Int,
    val condition: Condition,
    val windSpeedKmh: Double,
    val timestampMs: Long = System.currentTimeMillis(),
    val isStale: Boolean = false,
    val cloudCoverPercent: Int = 0,
    val precipitationMm: Double = 0.0,
    val rainMm: Double = 0.0,
    val snowfallCm: Double = 0.0,
    val visibilityMeters: Double = 10000.0,
) {
    /**
     * High-level weather condition categories derived from WMO weather interpretation codes.
     * Keeps the decision engine simple regardless of the provider used.
     */
    enum class Condition {
        Clear,
        PartlyCloudy,
        Clouds,
        Fog,
        Drizzle,
        Rain,
        HeavyRain,
        Snow,
        Thunderstorm,
        Unknown,
    }

    /** True when temperature is in a comfortable range (10–28 °C). */
    val isComfortable: Boolean get() = temperatureCelsius in 10.0..28.0

    /** True when temperature is considered cold (below 5 °C). */
    val isCold: Boolean get() = temperatureCelsius < 5.0

    /** True when temperature is considered hot (above 33 °C). */
    val isHot: Boolean get() = temperatureCelsius > 33.0

    /** True when weather is precipitating (rain, snow, drizzle, etc.). */
    val isPrecipitating: Boolean
        get() = condition in setOf(
            Condition.Drizzle,
            Condition.Rain,
            Condition.HeavyRain,
            Condition.Snow,
            Condition.Thunderstorm,
        )
}
