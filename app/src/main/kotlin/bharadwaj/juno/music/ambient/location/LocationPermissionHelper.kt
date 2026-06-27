package bharadwaj.juno.music.ambient.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Stateless helper for checking location permission status.
 *
 * This class intentionally does NOT request permissions — permission requests
 * must be initiated from a UI component (Activity / Fragment) in a later phase.
 */
object LocationPermissionHelper {

    /**
     * Returns true if at least ACCESS_COARSE_LOCATION has been granted.
     * This is the minimum required for weather-based ambient features.
     */
    fun hasCoarsePermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Returns true if ACCESS_FINE_LOCATION has been granted.
     * Fine permission enables more accurate location tracking (GPS),
     * but the system falls back to coarse if only coarse is available.
     */
    fun hasFinePermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Returns true if at least one of coarse or fine location is available.
     */
    fun hasAnyLocationPermission(context: Context): Boolean =
        hasCoarsePermission(context) || hasFinePermission(context)
}
