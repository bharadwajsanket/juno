package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.model.AmbientEnvironment
import bharadwaj.juno.music.ambient.model.AmbientWeather
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors

/**
 * Orchestrates deterministic, probability-driven atmospheric events (Birds, Rainbow, Shooting Stars, Fireflies).
 * Events are triggered deterministically based on weather parameters and time buckets.
 *
 * @param environment  Pre-resolved environment for the current scene (passed from [AmbientSceneCard]
 *                     to avoid duplicate [AmbientEnvironmentResolver.resolve] calls).
 * @param atmosphere   Base atmosphere snapshot (NOT the wind-gusted copy) so that the minute-level
 *                     seed remains stable across the continuous wind gust animation cycle.
 */
@Composable
fun AmbientLivingEvents(
    environment: AmbientEnvironment,
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    // Minute-level seed: keyed on stable weather/time properties only — NOT on windSpeedKmh,
    // which oscillates every animation frame and would cause the seed (and all probability
    // checks) to re-evaluate on every frame.
    val minuteSeed = remember(
        atmosphere.condition,
        atmosphere.isSunVisible,
        atmosphere.isMoonVisible,
    ) {
        System.currentTimeMillis() / 60_000L
    }

    // Initialize deterministic randomizer for this specific minute
    val rand = remember(minuteSeed) {
        java.util.Random(minuteSeed)
    }

    val isSun = atmosphere.isSunVisible
    val isMoon = atmosphere.isMoonVisible
    val clearSky = atmosphere.starVisibility > 0.6f

    // 1. Birds (Daytime only, 30% probability)
    val showBirds = isSun && rand.nextFloat() < 0.30f

    // 2. Shooting Stars (Night only, clear sky, 8% probability)
    val showShootingStar = isMoon && clearSky && rand.nextFloat() < 0.08f

    // 3. Fireflies (Night, calm wind < 15 km/h, Forest or Meadow environments only, 40% probability)
    val isForestOrMeadow = environment == AmbientEnvironment.Forest || environment == AmbientEnvironment.Meadow
    val isCalm = atmosphere.windSpeedKmh < 15f
    val showFireflies = isMoon && isCalm && isForestOrMeadow && rand.nextFloat() < 0.40f

    // 4. Rainbow: requires daytime AND actual precipitation (rain or drizzle).
    //    A rainbow physically requires sunlight + water droplets — it cannot appear
    //    on a dry clear-sky day regardless of cloud density.
    val hasPrecipitation = atmosphere.rainIntensity > 0.05f ||
        atmosphere.condition == AmbientWeather.Condition.Rain ||
        atmosphere.condition == AmbientWeather.Condition.Drizzle ||
        atmosphere.condition == AmbientWeather.Condition.HeavyRain
    val showRainbow = isSun && hasPrecipitation && atmosphere.cloudDensity in 0.05f..0.65f &&
        rand.nextFloat() < 0.15f

    Box(modifier = modifier) {
        if (showRainbow) {
            AmbientRainbow(modifier = Modifier.matchParentSize())
        }
        if (showShootingStar) {
            AmbientShootingStars(seed = minuteSeed, modifier = Modifier.matchParentSize())
        }
        if (showBirds) {
            AmbientBirds(seed = minuteSeed, modifier = Modifier.matchParentSize())
        }
        if (showFireflies) {
            AmbientFireflies(seed = minuteSeed, modifier = Modifier.matchParentSize())
        }
    }
}
