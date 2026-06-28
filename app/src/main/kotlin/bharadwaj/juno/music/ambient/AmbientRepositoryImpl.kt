package bharadwaj.juno.music.ambient

import android.content.Context
import bharadwaj.juno.music.ambient.engine.AmbientEngine
import bharadwaj.juno.music.ambient.location.AmbientLocationCache
import bharadwaj.juno.music.ambient.location.AmbientLocationProvider
import bharadwaj.juno.music.ambient.location.LocationPermissionHelper
import bharadwaj.juno.music.ambient.model.AmbientLocation
import bharadwaj.juno.music.ambient.model.AmbientScene
import bharadwaj.juno.music.ambient.model.AmbientState
import bharadwaj.juno.music.ambient.model.AmbientWeather
import bharadwaj.juno.music.ambient.time.AmbientTimeBucketResolver
import bharadwaj.juno.music.ambient.weather.AmbientWeatherCache
import bharadwaj.juno.music.ambient.weather.AmbientWeatherProvider
import bharadwaj.juno.music.constants.AmbientDebugForcedSceneKey
import bharadwaj.juno.music.di.ApplicationScope
import bharadwaj.juno.music.utils.NetworkConnectivityObserver
import bharadwaj.juno.music.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [AmbientRepository].
 *
 * Lifecycle:
 *  1. Starts in [AmbientState.Initializing].
 *  2. Checks location permission → transitions to [AmbientState.LocationPermissionRequired] if missing.
 *  3. On permission available: reads cache immediately so there is no blank state on cold start.
 *  4. Subscribes to live location updates; on each fix: fetches weather (cache-first), resolves
 *     time, runs [AmbientEngine], emits [AmbientState.Active].
 *  5. [forceScene] overlays a debug scene without disrupting the live data pipeline.
 *  6. [refresh] cancels and restarts the location subscription.
 */
@Singleton
class AmbientRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope,
    private val locationProvider: AmbientLocationProvider,
    private val locationCache: AmbientLocationCache,
    private val weatherProvider: AmbientWeatherProvider,
    private val weatherCache: AmbientWeatherCache,
    private val networkObserver: NetworkConnectivityObserver,
) : AmbientRepository {

    private companion object {
        const val TAG = "AmbientRepository"
    }

    private val _state = MutableStateFlow<AmbientState>(AmbientState.Initializing)
    override val state: StateFlow<AmbientState> = _state.asStateFlow()

    /** Tracks the current debug override independently of the live pipeline. */
    private var debugForcedScene: AmbientScene? = null

    /** Job holding the active location subscription; replaced on [refresh]. */
    @Volatile
    private var locationJob: Job? = null

    init {
        start()
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    override fun forceScene(scene: AmbientScene?) {
        debugForcedScene = scene

        // Update the current Active state in-place if one exists, or log the intent
        val current = _state.value
        if (current is AmbientState.Active) {
            _state.update { current.copy(debugForcedScene = scene) }
        }

        // Persist debug scene preference so it survives process death during dev
        scope.launch {
            context.dataStore.edit { prefs ->
                if (scene != null) {
                    prefs[AmbientDebugForcedSceneKey] = scene.name
                } else {
                    prefs.remove(AmbientDebugForcedSceneKey)
                }
            }
        }

        Timber.tag(TAG).d("Debug scene forced: $scene")
    }

    override suspend fun refresh() {
        val previous = locationJob
        if (previous != null && previous.isActive) {
            previous.cancel()
        }
        start()
    }

    override fun stop() {
        Timber.tag(TAG).d("stop() called - pausing location updates")
        locationJob?.cancel()
        locationJob = null
    }

    // ─── Internal ─────────────────────────────────────────────────────────────

    private fun start() {
        if (!LocationPermissionHelper.hasAnyLocationPermission(context)) {
            Timber.tag(TAG).d("Location permission not granted")
            _state.value = AmbientState.LocationPermissionRequired
            return
        }

        _state.value = AmbientState.Initializing

        locationJob = scope.launch {
            // 1. Serve from cache immediately for instant first-frame content
            warmFromCache()

            // 2. Start an 8-second timeout fallback in case GPS/Location Services is disabled/fails
            val timeoutJob = launch {
                delay(8_000)
                if (_state.value is AmbientState.Initializing) {
                    Timber.tag(TAG).w("Location updates timed out. Resolving with fallback location.")
                    val fallbackLoc = locationCache.read() ?: AmbientLocation(
                        latitude = 40.7128,
                        longitude = -74.0060,
                        city = "Default",
                        country = "US",
                        timezoneId = java.util.TimeZone.getDefault().id
                    )
                    resolveAndEmit(fallbackLoc)
                }
            }

            // 3. Subscribe to live location updates
            try {
                locationProvider.locationUpdates().collect { location ->
                    timeoutJob.cancel()
                    locationCache.write(location)
                    resolveAndEmit(location)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error collecting location updates, checking fallback")
                timeoutJob.cancel()
                if (_state.value is AmbientState.Initializing) {
                    val fallbackLoc = locationCache.read() ?: AmbientLocation(
                        latitude = 40.7128,
                        longitude = -74.0060,
                        city = "Default",
                        country = "US",
                        timezoneId = java.util.TimeZone.getDefault().id
                    )
                    resolveAndEmit(fallbackLoc)
                }
            }
        }
    }

    /**
     * Attempts to build an [AmbientState.Active] from cached location + cached weather.
     * Emits nothing if either cache is empty.
     */
    private suspend fun warmFromCache() {
        val cachedLocation = locationProvider.getLastKnownLocation()
            ?: locationCache.read()
            ?: return

        Timber.tag(TAG).d("Warming from cache: $cachedLocation")
        resolveAndEmit(cachedLocation)
    }

    /**
     * Resolves weather, time, and scene for the given [location] and emits a new
     * [AmbientState.Active] state.
     */
    private suspend fun resolveAndEmit(location: AmbientLocation) {
        val isOnline = networkObserver.isCurrentlyConnected()

        val weather: AmbientWeather = when {
            isOnline && !weatherCache.isFresh(location.latitude, location.longitude) -> {
                weatherProvider.fetchWeather(location.latitude, location.longitude)
                    .onSuccess { weatherCache.write(it, location.latitude, location.longitude) }
                    .getOrElse {
                        Timber.tag(TAG).w(it, "Live weather fetch failed, falling back to cache")
                        weatherCache.read() ?: buildDefaultWeather()
                    }
            }
            else -> {
                weatherCache.read() ?: if (isOnline) {
                    weatherProvider.fetchWeather(location.latitude, location.longitude)
                        .onSuccess { weatherCache.write(it, location.latitude, location.longitude) }
                        .getOrElse { buildDefaultWeather() }
                } else {
                    buildDefaultWeather()
                }
            }
        }

        val timeData = AmbientTimeBucketResolver.resolve(
            latitude = location.latitude,
            longitude = location.longitude,
            timezoneId = location.timezoneId,
        )

        val resolvedScene = AmbientEngine.resolve(location, weather, timeData)

        // Only transition if the engine recommends it
        val currentScene = (_state.value as? AmbientState.Active)?.scene
        if (currentScene != null && !AmbientEngine.shouldTransition(currentScene, resolvedScene)) {
            Timber.tag(TAG).d("Transition suppressed: $currentScene → $resolvedScene")
            // Update non-scene fields (location, weather, time) without changing scene
            _state.update { current ->
                if (current is AmbientState.Active) {
                    current.copy(
                        location = location,
                        weather = weather,
                        timeData = timeData,
                        isOffline = !isOnline,
                    )
                } else current
            }
            return
        }

        _state.value = AmbientState.Active(
            scene = resolvedScene,
            location = location,
            weather = weather,
            timeData = timeData,
            isOffline = !isOnline,
            debugForcedScene = debugForcedScene,
        )

        Timber.tag(TAG).d(
            "Scene resolved: $resolvedScene (bucket=${timeData.bucket}, " +
                "condition=${weather.condition}, offline=${!isOnline})",
        )
    }

    /**
     * Returns a minimal default [AmbientWeather] used when no data is available
     * at all (e.g. first launch, no connectivity, empty cache).
     */
    private fun buildDefaultWeather() = AmbientWeather(
        temperatureCelsius = 20.0,
        feelsLikeCelsius = 20.0,
        humidity = 50,
        condition = AmbientWeather.Condition.Unknown,
        windSpeedKmh = 0.0,
        isStale = true,
    )
}
