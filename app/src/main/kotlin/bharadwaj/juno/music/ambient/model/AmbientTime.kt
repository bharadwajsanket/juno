package bharadwaj.juno.music.ambient.model

/**
 * Discrete time-of-day buckets used by the Ambient decision engine.
 *
 * Boundaries are defined relative to sunrise/sunset so they shift naturally
 * with the season and the user's location.
 *
 * Approximate mapping (mid-latitude summer as reference):
 *
 *   Dawn        civil twilight start → sunrise
 *   Morning     sunrise → 10:00
 *   Noon        10:00 → 13:30
 *   Afternoon   13:30 → golden-hour start
 *   GoldenHour  golden-hour start → sunset − 30 min
 *   Sunset      sunset − 30 min → sunset + 30 min
 *   Evening     sunset + 30 min → 22:00
 *   Night       22:00 → 02:00
 *   Midnight    02:00 → civil twilight start
 */
enum class AmbientTimeBucket {
    Dawn,
    Morning,
    Noon,
    Afternoon,
    GoldenHour,
    Sunset,
    Evening,
    Night,
    Midnight,
}

/**
 * Full resolved time data for a single moment.
 *
 * @param bucket          The active [AmbientTimeBucket].
 * @param sunriseEpochMs  Epoch milliseconds for today's sunrise at the current location.
 * @param sunsetEpochMs   Epoch milliseconds for today's sunset at the current location.
 * @param goldenHourStartMs  Start of the golden hour window (epoch ms).
 * @param goldenHourEndMs    End of the golden hour window (epoch ms) — equals sunset − 30 min.
 * @param civilTwilightStartMs  Civil dawn start (sun is 6° below horizon, epoch ms).
 * @param civilTwilightEndMs    Civil dusk end (sun is 6° below horizon, epoch ms).
 * @param timezoneId      IANA timezone id of the location.
 * @param localHour       Local hour-of-day (0–23) at the moment this was resolved.
 * @param timestampMs     Epoch milliseconds when this snapshot was created.
 */
data class AmbientTimeData(
    val bucket: AmbientTimeBucket,
    val sunriseEpochMs: Long,
    val sunsetEpochMs: Long,
    val goldenHourStartMs: Long,
    val goldenHourEndMs: Long,
    val civilTwilightStartMs: Long,
    val civilTwilightEndMs: Long,
    val timezoneId: String,
    val localHour: Int,
    val timestampMs: Long = System.currentTimeMillis(),
)
