package bharadwaj.juno.music.ambient.model

import kotlinx.serialization.Serializable

/**
 * Normalized location snapshot used by the Ambient system.
 *
 * @param latitude  WGS84 latitude in degrees.
 * @param longitude WGS84 longitude in degrees.
 * @param city      Resolved city name, or null if not available.
 * @param country   ISO 3166-1 alpha-2 country code, or null if not available.
 * @param timezoneId IANA timezone id, e.g. "Asia/Kolkata".
 * @param accuracyMeters  Horizontal accuracy radius in metres, or null if unknown.
 * @param timestampMs  Epoch milliseconds when this location was sampled.
 */
@Serializable
data class AmbientLocation(
    val latitude: Double,
    val longitude: Double,
    val city: String? = null,
    val country: String? = null,
    val timezoneId: String = "UTC",
    val accuracyMeters: Float? = null,
    val timestampMs: Long = System.currentTimeMillis(),
)
