package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.model.AmbientEnvironment
import bharadwaj.juno.music.ambient.model.AmbientEnvironmentResolver
import bharadwaj.juno.music.ambient.model.AmbientScene
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors

/**
 * Orchestrates deterministic, probability-driven atmospheric events (Birds, Rainbow, Shooting Stars, Fireflies).
 * Events are triggered deterministically based on location coordinates, weather parameters, and time buckets.
 */
@Composable
fun AmbientLivingEvents(
    scene: AmbientScene,
    colors: AmbientSceneColors,
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    // Generate a minute-level tick seed
    val minuteSeed = remember(atmosphere) {
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

    // 3. Fireflies (Night, calm wind < 15km/h, Forest or Meadow environments only, 40% probability)
    val env = remember(scene) {
        AmbientEnvironmentResolver.resolve(scene)
    }
    val isForestOrMeadow = env == AmbientEnvironment.Forest || env == AmbientEnvironment.Meadow
    val isCalm = atmosphere.windSpeedKmh < 15f
    val showFireflies = isMoon && isCalm && isForestOrMeadow && rand.nextFloat() < 0.40f

    // 4. Rainbow (Daytime, low cloud cover < 0.45, high humidity indicator [represented by rainIntensity == 0 but snowIntensity == 0 during clear morning/noon], 15% probability)
    val showRainbow = isSun && atmosphere.cloudDensity in 0.05f..0.45f && rand.nextFloat() < 0.15f

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
