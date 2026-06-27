package bharadwaj.juno.music.ambient.time

import java.util.TimeZone
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Calculates sunrise, sunset, golden hour, and civil twilight times for a given
 * location and date using the NOAA solar equations.
 *
 * Implementation is a pure Kotlin adaptation of the NOAA Solar Calculator
 * spreadsheet (https://gml.noaa.gov/grad/solcalc/calcdetails.html).
 * No external dependency required.
 *
 * All output times are epoch milliseconds in UTC, suitable for direct comparison
 * with [System.currentTimeMillis].
 */
object SunriseSunsetCalculator {

    /**
     * Calculated solar events for a single day at a given location.
     *
     * @param sunriseMs          Epoch ms of sunrise (sun upper limb crosses horizon).
     * @param sunsetMs           Epoch ms of sunset.
     * @param goldenHourStartMs  Start of the golden hour — sun altitude ~6° above horizon.
     * @param goldenHourEndMs    End of golden hour — typically sunset − 30 min.
     * @param civilTwilightDawnMs  Civil dawn — sun is 6° below horizon (start of usable light).
     * @param civilTwilightDuskMs  Civil dusk — sun is 6° below horizon (end of usable light).
     */
    data class SolarEvents(
        val sunriseMs: Long,
        val sunsetMs: Long,
        val goldenHourStartMs: Long,
        val goldenHourEndMs: Long,
        val civilTwilightDawnMs: Long,
        val civilTwilightDuskMs: Long,
    )

    /**
     * Calculates solar events for today at [latitude]/[longitude].
     *
     * @param latitude   WGS84 latitude in decimal degrees.
     * @param longitude  WGS84 longitude in decimal degrees.
     * @param timezone   Timezone used to anchor the calculation to local midnight.
     *                   Defaults to the device's current default timezone.
     * @param epochMs    Epoch ms representing the moment of interest. Defaults to now.
     */
    fun calculate(
        latitude: Double,
        longitude: Double,
        timezone: TimeZone = TimeZone.getDefault(),
        epochMs: Long = System.currentTimeMillis(),
    ): SolarEvents {
        // Julian date for the given moment
        val jd = toJulianDate(epochMs)

        // Fractional Julian century from J2000.0
        val t = (jd - 2_451_545.0) / 36_525.0

        // Solar geometry
        val geomMeanLongSun = geomMeanLongSun(t) // degrees
        val geomMeanAnomSun = geomMeanAnomSun(t) // degrees
        val eccentEarthOrbit = eccentEarthOrbit(t)
        val sunEqOfCtr = sunEqOfCtr(geomMeanAnomSun, t)
        val sunTrueLong = geomMeanLongSun + sunEqOfCtr // degrees
        val sunAppLong = sunTrueLong - 0.00569 - 0.00478 * sin(toRad(125.04 - 1934.136 * t))
        val meanObliqEcliptic = 23.0 + (26.0 + (21.448 - t * (46.815 + t * (0.00059 - t * 0.001813))) / 60.0) / 60.0
        val obliqCorr = meanObliqEcliptic + 0.00256 * cos(toRad(125.04 - 1934.136 * t))
        val sunDeclin = toDeg(asin(sin(toRad(obliqCorr)) * sin(toRad(sunAppLong)))) // degrees

        // Equation of time (minutes)
        val varY = tan(toRad(obliqCorr / 2.0)).let { it * it }
        val eqOfTime = 4.0 * toDeg(
            varY * sin(2.0 * toRad(geomMeanLongSun))
                - 2.0 * eccentEarthOrbit * sin(toRad(geomMeanAnomSun))
                + 4.0 * eccentEarthOrbit * varY * sin(toRad(geomMeanAnomSun)) * cos(2.0 * toRad(geomMeanLongSun))
                - 0.5 * varY * varY * sin(4.0 * toRad(geomMeanLongSun))
                - 1.25 * eccentEarthOrbit * eccentEarthOrbit * sin(2.0 * toRad(geomMeanAnomSun)),
        )

        // Solar noon in fractional days from local midnight
        val tzOffsetHours = timezone.getOffset(epochMs) / 3_600_000.0
        val solarNoonFrac = (720.0 - 4.0 * longitude - eqOfTime + tzOffsetHours * 60.0) / 1440.0

        // Hour angles for sunrise/sunset and civil twilight
        val haSunrise = haForAltitude(latitude, sunDeclin, -0.833)
        val haCivilTwilight = haForAltitude(latitude, sunDeclin, -6.0)
        // Golden hour: sun altitude ~6° above horizon (i.e. when sun rises above 6°)
        val haGoldenHour = haForAltitude(latitude, sunDeclin, 6.0)

        // Convert fractional day offsets to epoch ms
        val localMidnightMs = localMidnightEpochMs(epochMs, timezone)

        fun fracDayToEpochMs(noonFrac: Double, haMinutes: Double, isSunrise: Boolean): Long {
            val offset = if (isSunrise) -haMinutes else haMinutes
            return localMidnightMs + ((noonFrac + offset / 1440.0) * 86_400_000.0).toLong()
        }

        val sunriseMs = fracDayToEpochMs(solarNoonFrac, haSunrise, true)
        val sunsetMs = fracDayToEpochMs(solarNoonFrac, haSunrise, false)
        val civilDawnMs = fracDayToEpochMs(solarNoonFrac, haCivilTwilight, true)
        val civilDuskMs = fracDayToEpochMs(solarNoonFrac, haCivilTwilight, false)

        // Golden hour: starts when sun hits 6° altitude (descending), ends 30 min before sunset
        val goldenHourStartMs = fracDayToEpochMs(solarNoonFrac, haGoldenHour, false)
        val goldenHourEndMs = sunsetMs - 30L * 60L * 1_000L

        return SolarEvents(
            sunriseMs = sunriseMs,
            sunsetMs = sunsetMs,
            goldenHourStartMs = goldenHourStartMs.coerceAtMost(goldenHourEndMs),
            goldenHourEndMs = goldenHourEndMs,
            civilTwilightDawnMs = civilDawnMs,
            civilTwilightDuskMs = civilDuskMs,
        )
    }

    // ─── NOAA helper functions ─────────────────────────────────────────────────

    private fun toRad(deg: Double) = Math.toRadians(deg)
    private fun toDeg(rad: Double) = Math.toDegrees(rad)

    private fun toJulianDate(epochMs: Long): Double =
        epochMs / 86_400_000.0 + 2_440_587.5

    private fun geomMeanLongSun(t: Double): Double =
        (280.46646 + t * (36000.76983 + t * 0.0003032)) % 360.0

    private fun geomMeanAnomSun(t: Double): Double =
        357.52911 + t * (35999.05029 - 0.0001537 * t)

    private fun eccentEarthOrbit(t: Double): Double =
        0.016708634 - t * (0.000042037 + 0.0000001267 * t)

    private fun sunEqOfCtr(m: Double, t: Double): Double {
        val mRad = toRad(m)
        return sin(mRad) * (1.914602 - t * (0.004817 + 0.000014 * t)) +
            sin(2.0 * mRad) * (0.019993 - 0.000101 * t) +
            sin(3.0 * mRad) * 0.000289
    }

    /**
     * Calculates the hour angle (in minutes) for the given solar altitude.
     * [altitudeDeg] is negative for sunrise/sunset (−0.833), more negative for twilight.
     */
    private fun haForAltitude(lat: Double, declin: Double, altitudeDeg: Double): Double {
        val cosHa = (cos(toRad(90.0 - altitudeDeg)) /
            (cos(toRad(lat)) * cos(toRad(declin)))) -
            tan(toRad(lat)) * tan(toRad(declin))
        // Clamp to [-1, 1] to avoid NaN at polar extremes
        val clamped = cosHa.coerceIn(-1.0, 1.0)
        return toDeg(acos(clamped)) * 4.0 // convert degrees to minutes
    }

    /** Returns the epoch ms for local midnight on the day containing [epochMs]. */
    private fun localMidnightEpochMs(epochMs: Long, timezone: TimeZone): Long {
        val cal = java.util.Calendar.getInstance(timezone).apply {
            timeInMillis = epochMs
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
