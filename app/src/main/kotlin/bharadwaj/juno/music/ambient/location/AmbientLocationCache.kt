package bharadwaj.juno.music.ambient.location

import android.content.Context
import androidx.datastore.preferences.core.edit
import bharadwaj.juno.music.ambient.model.AmbientLocation
import bharadwaj.juno.music.constants.AmbientCachedLocationKey
import bharadwaj.juno.music.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the last known [AmbientLocation] to DataStore so the Ambient system
 * can start in an offline-friendly state without waiting for a live GPS fix.
 *
 * Uses [kotlinx.serialization] JSON — consistent with other serialised state
 * in the app. The stored value is a single JSON string keyed by
 * [AmbientCachedLocationKey].
 */
@Singleton
class AmbientLocationCache @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private companion object {
        const val TAG = "AmbientLocationCache"
    }

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Reads and deserialises the last cached location.
     * Returns null if no location has been cached yet or if the cache is corrupt.
     */
    suspend fun read(): AmbientLocation? {
        return try {
            context.dataStore.data
                .map { prefs -> prefs[AmbientCachedLocationKey] }
                .firstOrNull()
                ?.let { raw -> json.decodeFromString<AmbientLocation>(raw) }
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to read cached location")
            null
        }
    }

    /**
     * Serialises and writes [location] to DataStore.
     */
    suspend fun write(location: AmbientLocation) {
        try {
            val raw = json.encodeToString(AmbientLocation.serializer(), location)
            context.dataStore.edit { prefs ->
                prefs[AmbientCachedLocationKey] = raw
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to cache location")
        }
    }

    /**
     * Removes the cached location entry.
     */
    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(AmbientCachedLocationKey)
        }
    }
}
