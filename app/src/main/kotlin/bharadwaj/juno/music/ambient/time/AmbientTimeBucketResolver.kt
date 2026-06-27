package bharadwaj.juno.music.ambient.time

import bharadwaj.juno.music.ambient.model.AmbientTimeData
import bharadwaj.juno.music.ambient.model.AmbientTimeBucket
import java.util.Calendar
import java.util.TimeZone

/**
 * Resolves the current moment into an [AmbientTimeBucket] and builds a full
 * [AmbientTimeData] snapshot.
 *
 * Bucket boundaries (all relative to today's solar events at the device location):
 *
 *   Dawn        civil twilight dawn  →  sunrise
 *   Morning     sunrise              →  10:00 local
 *   Noon        10:00                →  13:30 local
 *   Afternoon   13:30                →  golden hour start
 *   GoldenHour  golden hour start    →  sunset − 30 min
 *   Sunset      sunset − 30 min      →  sunset + 30 min
 *   Evening     sunset + 30 min      →  22:00 local
 *   Night       22:00                →  02:00 local
 *   Midnight    02:00                →  civil twilight dawn (next day)
 */
object AmbientTimeBucketResolver {

    /**
     * Resolves [epochMs] (defaults to now) into an [AmbientTimeData] for the given
     * [latitude] / [longitude] / [timezoneId].
     */
    fun resolve(
        latitude: Double,
        longitude: Double,
        timezoneId: String,
        epochMs: Long = System.currentTimeMillis(),
    ): AmbientTimeData {
        val timezone = try {
            val systemTz = TimeZone.getTimeZone(timezoneId)
            val systemOffsetHours = systemTz.getOffset(epochMs) / 3600000.0
            val longOffsetHours = longitude / 15.0
            if (Math.abs(systemOffsetHours - longOffsetHours) > 3.0) {
                val offsetHours = Math.round(longOffsetHours).toInt().coerceIn(-12, 14)
                val offsetMs = offsetHours * 3600 * 1000
                java.util.SimpleTimeZone(offsetMs, "GMT${if (offsetHours >= 0) "+" else ""}$offsetHours")
            } else {
                systemTz
            }
        } catch (e: Exception) {
            val offsetHours = Math.round(longitude / 15.0).toInt().coerceIn(-12, 14)
            val offsetMs = offsetHours * 3600 * 1000
            java.util.SimpleTimeZone(offsetMs, "GMT${if (offsetHours >= 0) "+" else ""}$offsetHours")
        }

        val solar = SunriseSunsetCalculator.calculate(latitude, longitude, timezone, epochMs)

        val localCal = Calendar.getInstance(timezone).apply { timeInMillis = epochMs }
        val localHour = localCal.get(Calendar.HOUR_OF_DAY)
        val localMinute = localCal.get(Calendar.MINUTE)
        val localTimeMinutes = localHour * 60 + localMinute

        val bucket = resolveBucket(epochMs, solar, localTimeMinutes)

        return AmbientTimeData(
            bucket = bucket,
            sunriseEpochMs = solar.sunriseMs,
            sunsetEpochMs = solar.sunsetMs,
            goldenHourStartMs = solar.goldenHourStartMs,
            goldenHourEndMs = solar.goldenHourEndMs,
            civilTwilightStartMs = solar.civilTwilightDawnMs,
            civilTwilightEndMs = solar.civilTwilightDuskMs,
            timezoneId = timezone.id,
            localHour = localHour,
            timestampMs = epochMs,
        )
    }

    // ─── Internal ─────────────────────────────────────────────────────────────

    private fun resolveBucket(
        nowMs: Long,
        solar: SunriseSunsetCalculator.SolarEvents,
        localTimeMinutes: Int,
    ): AmbientTimeBucket {
        val noonMinutes = 10 * 60        // 10:00
        val afternoonMinutes = 13 * 60 + 30  // 13:30
        val eveningEndMinutes = 22 * 60  // 22:00
        val nightEndMinutes = 2 * 60     // 02:00

        return when {
            // Snow / extreme conditions are handled by the engine, not here

            // Midnight: 02:00 → civil twilight dawn
            localTimeMinutes < nightEndMinutes ->
                AmbientTimeBucket.Midnight

            // Dawn: civil twilight dawn → sunrise
            nowMs in solar.civilTwilightDawnMs until solar.sunriseMs ->
                AmbientTimeBucket.Dawn

            // Morning: sunrise → 10:00
            nowMs >= solar.sunriseMs && localTimeMinutes < noonMinutes ->
                AmbientTimeBucket.Morning

            // Noon: 10:00 → 13:30
            localTimeMinutes in noonMinutes until afternoonMinutes ->
                AmbientTimeBucket.Noon

            // GoldenHour: golden hour start → sunset − 30 min (golden hour end)
            nowMs in solar.goldenHourStartMs until solar.goldenHourEndMs ->
                AmbientTimeBucket.GoldenHour

            // Afternoon: 13:30 → golden hour start
            localTimeMinutes >= afternoonMinutes && nowMs < solar.goldenHourStartMs ->
                AmbientTimeBucket.Afternoon

            // Sunset: sunset − 30 min → sunset + 30 min
            nowMs in solar.goldenHourEndMs until (solar.sunsetMs + 30L * 60_000L) ->
                AmbientTimeBucket.Sunset

            // Evening: sunset + 30 min → 22:00
            nowMs >= (solar.sunsetMs + 30L * 60_000L) && localTimeMinutes < eveningEndMinutes ->
                AmbientTimeBucket.Evening

            // Night: 22:00 → 02:00
            localTimeMinutes >= eveningEndMinutes ->
                AmbientTimeBucket.Night

            // Midnight: 02:00 → civil twilight dawn (already covered above, but safety fallback)
            else -> AmbientTimeBucket.Midnight
        }
    }
}
