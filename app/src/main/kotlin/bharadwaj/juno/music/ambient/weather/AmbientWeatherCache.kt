package bharadwaj.juno.music.ambient.weather

import android.content.Context
import androidx.datastore.preferences.core.edit
import bharadwaj.juno.music.ambient.model.AmbientWeather
import bharadwaj.juno.music.constants.AmbientCachedWeatherKey
import bharadwaj.juno.music.constants.AmbientCachedWeatherLatKey
import bharadwaj.juno.music.constants.AmbientCachedWeatherLonKey
import bharadwaj.juno.music.constants.AmbientLastFetchEpochKey
import bharadwaj.juno.music.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

/**
 * Persists the last fetched [AmbientWeather] to DataStore.
 *
 * Cache policy:
 *   - Fresh window: 1 hour AND location within 50 km of the cached fetch location.
 *     Both conditions must hold for [isFresh] to return true.
 *   - Stale: data older than 1 hour OR user has moved more than 50 km is still
 *     returned by [read] but with [AmbientWeather.isStale] = true so consumers
 *     can show an indicator.
 *   - The repository decides whether to attempt a live fetch before falling
 *     back to the cache.
 */
@Singleton
class AmbientWeatherCache @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private companion object {
        const val TAG = "AmbientWeatherCache"

        /** Weather is considered fresh for this duration. */
        const val FRESH_WINDOW_MS = 60L * 60L * 1_000L // 1 hour

        /**
         * Maximum distance (km) from the cached fetch location before the cache is
         * considered stale regardless of age. Prevents serving Mumbai weather in Delhi.
         */
        const val FRESH_RADIUS_KM = 50.0
    }

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Reads the cached weather, marking it as stale if it is older than [FRESH_WINDOW_MS].
     * Returns null if nothing has been cached yet.
     */
    suspend fun read(): AmbientWeather? {
        return try {
            val prefs = context.dataStore.data.firstOrNull() ?: return null
            val raw = prefs[AmbientCachedWeatherKey] ?: return null
            val lastFetch = prefs[AmbientLastFetchEpochKey] ?: 0L

            val isStale = (System.currentTimeMillis() - lastFetch) > FRESH_WINDOW_MS
            json.decodeFromString<AmbientWeather>(raw).copy(isStale = isStale)
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to read cached weather")
            null
        }
    }

    /**
     * Returns true if cached data exists, is within the fresh time window, AND the provided
     * coordinates are within [FRESH_RADIUS_KM] of the location used for the last fetch.
     *
     * Use this to decide whether a live fetch can be skipped. When the user has moved
     * significantly (e.g., travelled to another city), the cache is treated as stale even
     * if it was fetched recently.
     *
     * @param lat  Current latitude in decimal degrees.
     * @param lon  Current longitude in decimal degrees.
     */
    suspend fun isFresh(lat: Double, lon: Double): Boolean {
        return try {
            val prefs = context.dataStore.data.firstOrNull() ?: return false
            val lastFetch = prefs[AmbientLastFetchEpochKey] ?: return false

            val timeOk = (System.currentTimeMillis() - lastFetch) < FRESH_WINDOW_MS
            if (!timeOk) return false

            val cachedLat = prefs[AmbientCachedWeatherLatKey] ?: return false
            val cachedLon = prefs[AmbientCachedWeatherLonKey] ?: return false

            isWithinRadius(lat, lon, cachedLat, cachedLon)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Serialises and stores [weather] along with the current epoch timestamp and the
     * coordinates of the location that was used to fetch it.
     *
     * @param weather  The weather snapshot to cache.
     * @param lat      Latitude of the location used for the fetch.
     * @param lon      Longitude of the location used for the fetch.
     */
    suspend fun write(weather: AmbientWeather, lat: Double = 0.0, lon: Double = 0.0) {
        try {
            val raw = json.encodeToString(AmbientWeather.serializer(), weather)
            context.dataStore.edit { prefs ->
                prefs[AmbientCachedWeatherKey] = raw
                prefs[AmbientLastFetchEpochKey] = System.currentTimeMillis()
                prefs[AmbientCachedWeatherLatKey] = lat
                prefs[AmbientCachedWeatherLonKey] = lon
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to cache weather")
        }
    }

    /** Removes the cached weather entry. */
    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(AmbientCachedWeatherKey)
            prefs.remove(AmbientLastFetchEpochKey)
            prefs.remove(AmbientCachedWeatherLatKey)
            prefs.remove(AmbientCachedWeatherLonKey)
        }
    }

    // ─── Internal helpers ─────────────────────────────────────────────────────

    /**
     * Returns true when (lat1, lon1) and (lat2, lon2) are within [FRESH_RADIUS_KM].
     *
     * Uses the equirectangular approximation — accurate to within ~1% for distances
     * under 200 km, which is more than sufficient for a 50 km threshold check.
     */
    private fun isWithinRadius(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
    ): Boolean {
        val dLat = lat1 - lat2
        val midLat = Math.toRadians((lat1 + lat2) / 2.0)
        val dLon = (lon1 - lon2) * cos(midLat)
        val distKm = 111.0 * sqrt(dLat * dLat + dLon * dLon)
        return distKm <= FRESH_RADIUS_KM
    }
}
