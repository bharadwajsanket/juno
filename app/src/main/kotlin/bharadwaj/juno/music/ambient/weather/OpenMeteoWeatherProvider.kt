package bharadwaj.juno.music.ambient.weather

import bharadwaj.juno.music.ambient.model.AmbientWeather
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Weather provider backed by the [Open-Meteo](https://open-meteo.com) free API.
 *
 * Open-Meteo characteristics:
 *   - 100% free — no API key, no account, no rate limits for non-commercial use.
 *   - Uses WMO weather interpretation codes (mapped via [WeatherConditionMapper]).
 *   - Endpoint: https://api.open-meteo.com/v1/forecast
 *
 * The Ktor [HttpClient] is the same one already configured in the app's DI graph,
 * ensuring connection pooling and proxy settings are respected.
 */
@Singleton
class OpenMeteoWeatherProvider @Inject constructor(
    private val httpClient: HttpClient,
) : AmbientWeatherProvider {

    private companion object {
        const val TAG = "OpenMeteoWeather"
        const val BASE_URL = "https://api.open-meteo.com/v1/forecast"
    }

    override suspend fun fetchWeather(lat: Double, lon: Double): Result<AmbientWeather> {
        return try {
            val response = httpClient.get(BASE_URL) {
                parameter("latitude", lat)
                parameter("longitude", lon)
                parameter(
                    "current",
                    "temperature_2m,apparent_temperature,relative_humidity_2m," +
                        "wind_speed_10m,weather_code,cloud_cover,precipitation,rain,snowfall,visibility",
                )
                parameter("wind_speed_unit", "kmh")
                parameter("forecast_days", "1")
            }.body<OpenMeteoResponse>()

            val current = response.current
            val condition = WeatherConditionMapper.fromWmoCode(current.weatherCode)

            Result.success(
                AmbientWeather(
                    temperatureCelsius = current.temperature2m,
                    feelsLikeCelsius = current.apparentTemperature,
                    humidity = current.relativeHumidity2m,
                    condition = condition,
                    windSpeedKmh = current.windSpeed10m,
                    timestampMs = System.currentTimeMillis(),
                    isStale = false,
                    cloudCoverPercent = current.cloudCover,
                    precipitationMm = current.precipitation,
                    rainMm = current.rain,
                    snowfallCm = current.snowfall,
                    visibilityMeters = current.visibility,
                ),
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Weather fetch failed for ($lat, $lon)")
            Result.failure(e)
        }
    }

    // ─── Response DTOs ────────────────────────────────────────────────────────

    @Serializable
    private data class OpenMeteoResponse(
        val current: CurrentWeather,
    )

    @Serializable
    private data class CurrentWeather(
        @SerialName("temperature_2m") val temperature2m: Double,
        @SerialName("apparent_temperature") val apparentTemperature: Double,
        @SerialName("relative_humidity_2m") val relativeHumidity2m: Int,
        @SerialName("wind_speed_10m") val windSpeed10m: Double,
        @SerialName("weather_code") val weatherCode: Int,
        @SerialName("cloud_cover") val cloudCover: Int = 0,
        @SerialName("precipitation") val precipitation: Double = 0.0,
        @SerialName("rain") val rain: Double = 0.0,
        @SerialName("snowfall") val snowfall: Double = 0.0,
        @SerialName("visibility") val visibility: Double = 10000.0,
    )
}
