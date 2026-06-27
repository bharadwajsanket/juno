package bharadwaj.juno.music.ambient.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import bharadwaj.juno.music.ambient.model.AmbientLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Low-power location provider backed by the Android [LocationManager].
 *
 * Provider priority (most accurate to least):
 *   1. GPS (only when ACCESS_FINE_LOCATION is granted)
 *   2. Network (cell/Wi-Fi triangulation — sufficient for weather)
 *   3. Passive (uses fixes obtained by other apps — zero battery cost)
 *
 * Update interval: minimum 15 minutes / 1 km to keep battery usage negligible.
 * The provider gracefully degrades if a higher-priority provider is unavailable.
 *
 * Compatible with both FOSS and GMS flavours — no Play Services dependency.
 */
@Singleton
class FusedAmbientLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : AmbientLocationProvider {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    companion object {
        private const val TAG = "AmbientLocation"

        /** Minimum time between location updates (ms). */
        private const val MIN_INTERVAL_MS = 30L * 1_000L // 30 seconds

        /** Minimum distance between location updates (metres). */
        private const val MIN_DISTANCE_M = 100f // 100 metres
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): AmbientLocation? {
        if (!LocationPermissionHelper.hasAnyLocationPermission(context)) return null

        val providers = buildList {
            if (LocationPermissionHelper.hasFinePermission(context) &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ) add(LocationManager.GPS_PROVIDER)
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                add(LocationManager.NETWORK_PROVIDER)
            }
            add(LocationManager.PASSIVE_PROVIDER)
        }

        val best: Location? = providers
            .mapNotNull { provider ->
                try {
                    locationManager.getLastKnownLocation(provider)
                } catch (e: Exception) {
                    Timber.tag(TAG).w(e, "getLastKnownLocation failed for provider $provider")
                    null
                }
            }
            .minByOrNull { if (it.hasAccuracy()) it.accuracy else Float.MAX_VALUE }

        return best?.toAmbientLocation()
    }

    @SuppressLint("MissingPermission")
    override fun locationUpdates(): Flow<AmbientLocation> = callbackFlow {
        if (!LocationPermissionHelper.hasAnyLocationPermission(context)) {
            Timber.tag(TAG).w("Location permission not granted — updates flow is empty")
            close()
            return@callbackFlow
        }

        val listener = LocationListener { location ->
            trySend(location.toAmbientLocation())
        }

        val providers = buildList {
            if (LocationPermissionHelper.hasFinePermission(context) &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ) add(LocationManager.GPS_PROVIDER)
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                add(LocationManager.NETWORK_PROVIDER)
            }
            // Always register passive so we piggyback on other apps' fixes for free.
            add(LocationManager.PASSIVE_PROVIDER)
        }

        providers.forEach { provider ->
            try {
                locationManager.requestLocationUpdates(
                    provider,
                    MIN_INTERVAL_MS,
                    MIN_DISTANCE_M,
                    listener,
                    Looper.getMainLooper(),
                )
                Timber.tag(TAG).d("Registered location updates on provider: $provider")
            } catch (e: Exception) {
                Timber.tag(TAG).w(e, "Failed to register provider: $provider")
            }
        }

        // Emit the last known fix immediately so there is no cold-start delay.
        getLastKnownLocation()?.let { trySend(it) }

        awaitClose {
            try {
                locationManager.removeUpdates(listener)
                Timber.tag(TAG).d("Location updates unregistered")
            } catch (e: Exception) {
                Timber.tag(TAG).w(e, "Failed to unregister location listener")
            }
        }
    }.distinctUntilChanged { old, new ->
        // Suppress updates that moved less than ~100 m to avoid unnecessary resolves.
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            old.latitude, old.longitude,
            new.latitude, new.longitude,
            results,
        )
        results[0] < 100f
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun Location.toAmbientLocation(): AmbientLocation {
        val tz = TimeZone.getDefault().id
        return AmbientLocation(
            latitude = latitude,
            longitude = longitude,
            timezoneId = tz,
            accuracyMeters = if (hasAccuracy()) accuracy else null,
            timestampMs = time,
        )
    }
}
