package bharadwaj.juno.music.ambient.model

/**
 * Continuous atmospheric values derived from [AmbientState.Active] + current time.
 *
 * Unlike [AmbientScene], which is a discrete label updated by the repository,
 * [AmbientAtmosphere] contains real-valued quantities that change continuously
 * over time — updated every 60 seconds by the UI layer while the app is visible.
 *
 * All float fields are in the range [0.0, 1.0] unless documented otherwise.
 *
 * @param solarProgress    0.0 = civil dawn, 0.5 = solar noon, 1.0 = civil dusk.
 *                         Valid only during daytime ([isSunVisible] == true).
 * @param lunarProgress    0.0 = sunset, 0.5 = astronomical midnight, 1.0 = next sunrise.
 *                         Valid only during night ([isMoonVisible] == true).
 * @param cloudDensity     0.0 = perfectly clear, 1.0 = fully overcast. Derived from
 *                         [AmbientWeather.Condition].
 * @param starVisibility   0.0 = none visible, 1.0 = full clear-sky field. Decreases
 *                         sharply as [cloudDensity] exceeds ~0.3.
 * @param glowIntensity    Horizon/sky glow strength. Peaks at dawn and dusk (values
 *                         near 0 or 1 of [solarProgress]), falls at noon and midnight.
 * @param isSunVisible     True during daylight hours (civil dawn → civil dusk).
 * @param isMoonVisible    True during nighttime (civil dusk → next civil dawn).
 */
data class AmbientAtmosphere(
    val solarProgress: Float,
    val lunarProgress: Float,
    val cloudDensity: Float,
    val starVisibility: Float,
    val glowIntensity: Float,
    val isSunVisible: Boolean,
    val isMoonVisible: Boolean,
    val rainIntensity: Float = 0f,
    val snowIntensity: Float = 0f,
    val fogIntensity: Float = 0f,
    val windSpeedKmh: Float = 0f,
    val condition: AmbientWeather.Condition = AmbientWeather.Condition.Unknown,
) {
    companion object {
        /**
         * Neutral fallback used before any real data is available.
         * Renders a calm afternoon sky with no glow and moderate clarity.
         */
        val Neutral = AmbientAtmosphere(
            solarProgress    = 0.50f,
            lunarProgress    = 0.50f,
            cloudDensity     = 0.10f,
            starVisibility   = 0.00f,
            glowIntensity    = 0.00f,
            isSunVisible     = true,
            isMoonVisible    = false,
            rainIntensity    = 0.00f,
            snowIntensity    = 0.00f,
            fogIntensity     = 0.00f,
            windSpeedKmh     = 0.00f,
            condition        = AmbientWeather.Condition.Clear,
        )
    }
}
