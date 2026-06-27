package bharadwaj.juno.music.ambient.weather

import android.content.Context
import androidx.datastore.preferences.core.edit
import bharadwaj.juno.music.ambient.model.AmbientWeather
import bharadwaj.juno.music.constants.AmbientCachedWeatherKey
import bharadwaj.juno.music.constants.AmbientLastFetchEpochKey
import bharadwaj.juno.music.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the last fetched [AmbientWeather] to DataStore.
 *
 * Cache policy:
 *   - Fresh window: 1 hour. Within this window [read] returns the cached value
 *     and [isStale] is false.
 *   - Stale window: data older than 1 hour is still returned but with
 *     [AmbientWeather.isStale] = true so consumers can show an indicator.
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
     * Returns true if cached data exists AND is within the fresh window.
     * Use this to decide whether a live fetch can be skipped.
     */
    suspend fun isFresh(): Boolean {
        return try {
            val prefs = context.dataStore.data
                .map { it[AmbientLastFetchEpochKey] }
                .firstOrNull()
            prefs != null && (System.currentTimeMillis() - prefs) < FRESH_WINDOW_MS
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Serialises and stores [weather] along with the current epoch timestamp.
     */
    suspend fun write(weather: AmbientWeather) {
        try {
            val raw = json.encodeToString(AmbientWeather.serializer(), weather)
            context.dataStore.edit { prefs ->
                prefs[AmbientCachedWeatherKey] = raw
                prefs[AmbientLastFetchEpochKey] = System.currentTimeMillis()
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
        }
    }
}
