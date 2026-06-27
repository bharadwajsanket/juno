package bharadwaj.juno.music.ambient.weather

import bharadwaj.juno.music.ambient.model.AmbientWeather.Condition

/**
 * Maps WMO Weather Interpretation Codes (used by Open-Meteo) to the simplified
 * [Condition] enum used throughout the Ambient system.
 *
 * Reference: https://open-meteo.com/en/docs#weathervariables
 *
 *   0         Clear sky
 *   1, 2, 3   Mainly clear, partly cloudy, overcast
 *   45, 48    Foggy / depositing rime fog
 *   51–57     Drizzle (light / moderate / dense)
 *   61–67     Rain (slight / moderate / heavy; showers)
 *   71–77     Snow
 *   80–82     Showers (slight / moderate / violent)
 *   85–86     Snow showers
 *   95        Thunderstorm
 *   96, 99    Thunderstorm with hail
 */
object WeatherConditionMapper {

    fun fromWmoCode(code: Int): Condition = when (code) {
        0                    -> Condition.Clear
        1                    -> Condition.Clear
        2                    -> Condition.PartlyCloudy
        3                    -> Condition.Clouds
        45, 48               -> Condition.Fog
        51, 53               -> Condition.Drizzle
        55, 56, 57           -> Condition.Drizzle
        61, 80               -> Condition.Rain
        63, 81               -> Condition.Rain
        65, 67, 82           -> Condition.HeavyRain
        71, 73, 75, 77,
        85, 86               -> Condition.Snow
        95, 96, 99           -> Condition.Thunderstorm
        else                 -> Condition.Unknown
    }
}
