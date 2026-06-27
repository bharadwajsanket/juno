package bharadwaj.juno.music.ambient.weather

import bharadwaj.juno.music.ambient.model.AmbientWeather

/**
 * Contract for obtaining weather data in the Ambient system.
 *
 * Implementations are expected to:
 *   - Be safe to call from any coroutine context.
 *   - Return a [Result] so callers can handle network/API errors gracefully
 *     without requiring a try-catch at every call site.
 *   - Never cache internally — caching is the responsibility of
 *     [AmbientWeatherCache].
 */
interface AmbientWeatherProvider {

    /**
     * Fetches current weather conditions for the given coordinates.
     *
     * @param lat  Latitude in decimal degrees (WGS84).
     * @param lon  Longitude in decimal degrees (WGS84).
     * @return [Result.success] with a fresh [AmbientWeather] snapshot,
     *         or [Result.failure] with the underlying exception.
     */
    suspend fun fetchWeather(lat: Double, lon: Double): Result<AmbientWeather>
}
