package bharadwaj.juno.music.ambient.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import bharadwaj.juno.music.ambient.AmbientRepository
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.model.AmbientScene
import bharadwaj.juno.music.ambient.model.AmbientState
import bharadwaj.juno.music.ambient.model.AmbientEnvironment
import bharadwaj.juno.music.ambient.model.AmbientEnvironmentResolver
import bharadwaj.juno.music.ambient.ui.components.AmbientSky
import bharadwaj.juno.music.ambient.ui.components.AmbientSun
import bharadwaj.juno.music.ambient.ui.components.AmbientMoon
import bharadwaj.juno.music.ambient.ui.components.AmbientStars
import bharadwaj.juno.music.ambient.ui.components.AmbientClouds
import bharadwaj.juno.music.ambient.ui.components.AmbientLivingEvents
import bharadwaj.juno.music.ambient.ui.components.AmbientOcean
import bharadwaj.juno.music.ambient.ui.components.AmbientForest
import bharadwaj.juno.music.ambient.ui.components.AmbientMountains
import bharadwaj.juno.music.ambient.ui.components.AmbientMeadow
import bharadwaj.juno.music.ambient.ui.components.AmbientDesert
import bharadwaj.juno.music.ambient.ui.components.AmbientFog
import bharadwaj.juno.music.ambient.ui.components.AmbientPrecipitation
import bharadwaj.juno.music.ambient.ui.components.AmbientLightning
import bharadwaj.juno.music.ambient.ui.layers.AmbientGreetingLayer
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import bharadwaj.juno.music.ui.component.ActionPromptDialog
import kotlinx.coroutines.launch

/**
 * Top-level ambient scene composable. Renders a living sky that quietly
 * reflects the user's real-world time, weather, and location.
 *
 * Layers (bottom to top):
 *   1. [AmbientSkyLayer]        — sky gradient + sun/moon on real orbital arcs
 *   2. [AmbientBackgroundLayer] — mountains + weather-aware star field
 *   3. [AmbientForegroundLayer] — weather-aware cloud layers
 *   4. [AmbientGreetingLayer]   — greeting text + subtitle
 *
 * Data flow:
 *   [AmbientRepository] emits [AmbientState] on location/weather updates.
 *   [rememberAmbientAtmosphere] ticks every 60 s to derive continuous
 *   [AmbientAtmosphere] values (solar/lunar progress, cloud density, stars).
 *   All four layers receive both the discrete [AmbientScene] and the
 *   continuous [AmbientAtmosphere] so they can render correctly at any
 *   moment without waiting for a scene change.
 *
 * Lifecycle:
 *   [DisposableEffect] on [lifecycleOwner] fires [AmbientRepository.refresh]
 *   on every ON_RESUME so the scene is always current after backgrounding.
 *   The atmosphere producer's coroutine is cancelled automatically when the
 *   composable leaves the tree (backgrounded, navigated away).
 *
 * Permission:
 *   A Compose permission launcher fires when state is [AmbientState.LocationPermissionRequired].
 *   Grant → immediate [AmbientRepository.refresh].
 *
 * Preview / debug:
 *   Pass [previewScene] + [previewAtmosphere] to bypass the repository entirely.
 *
 * @param displayName        First name shown in the greeting.
 * @param modifier           Applied to the card container.
 * @param previewScene       Optional scene override for Compose Previews.
 * @param previewAtmosphere  Optional atmosphere override for Compose Previews.
 * @param ambientRepository  Optional repository injection override (for testing).
 */
@Composable
fun AmbientSceneHost(
    displayName: String,
    modifier: Modifier = Modifier,
    previewScene: AmbientScene? = null,
    previewAtmosphere: AmbientAtmosphere? = null,
    ambientRepository: AmbientRepository? = null,
) {
    if (previewScene != null) {
        // ── Preview / debug override path ─────────────────────────────────────
        val previewGreeting = remember(previewScene, displayName) {
            AmbientSceneGreeting.resolve(previewScene, displayName)
        }
        AmbientSceneCard(
            scene = previewScene,
            atmosphere = previewAtmosphere ?: AmbientAtmosphere.Neutral,
            greeting = previewGreeting,
            modifier = modifier,
        )
        return
    }

    // ── Production path ───────────────────────────────────────────────────────
    val repo = ambientRepository ?: rememberAmbientRepository()
    val state by repo.state.collectAsState()

    // Trigger a subtle haptic only on transition to Active (successful activation)
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    var wasActive by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        if (state is AmbientState.Active) {
            if (!wasActive) {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                wasActive = true
            }
        } else {
            wasActive = false
        }
    }

    // Discrete scene — changes on repository updates (every 15+ min or on refresh)
    val scene: AmbientScene by remember(state) {
        derivedStateOf {
            when (val s = state) {
                is AmbientState.Active -> s.effectiveScene
                else -> fallbackSceneForHour()
            }
        }
    }

    // Continuous atmosphere — ticks every 60 s + immediately on state change
    val atmosphere by rememberAmbientAtmosphere(state)

    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()


    // ── Lifecycle: refresh on ON_RESUME, stop on ON_PAUSE ──────────────────────
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    scope.launch { repo.refresh() }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    repo.stop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            // stop() is already called on ON_PAUSE — only remove the observer here
            // to avoid a redundant double-stop on every composition teardown.
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val greeting = remember(scene, displayName) {
        AmbientSceneGreeting.resolve(scene, displayName)
    }

    AmbientSceneCard(
        scene = scene,
        atmosphere = atmosphere,
        greeting = greeting,
        modifier = modifier,
    )
}

// ─── Scene card ───────────────────────────────────────────────────────────────

@Composable
private fun AmbientSceneCard(
    scene: AmbientScene,
    atmosphere: AmbientAtmosphere,
    greeting: AmbientGreeting,
    modifier: Modifier = Modifier,
) {
    val colors = remember(scene) { AmbientSceneColorPalette.forScene(scene) }

    val borderColor by animateColorAsState(
        targetValue = colors.textPrimary.copy(alpha = 0.10f),
        animationSpec = tween(durationMillis = SCENE_CROSSFADE_MS),
        label = "ambient_border",
    )

    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CARD_HEIGHT)
            .shadow(elevation = 4.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .border(width = 1.dp, color = borderColor, shape = cardShape),
    ) {
        // Scene cross-fades when the discrete scene changes
        Crossfade(
            targetState = scene,
            animationSpec = tween(durationMillis = SCENE_CROSSFADE_MS),
            label = "ambient_scene",
        ) { activeScene ->
            val baseColors = remember(activeScene) { AmbientSceneColorPalette.forScene(activeScene) }
            val targetColors = remember(baseColors, atmosphere) { baseColors.adjustForWeather(atmosphere) }

            val animatedSkyTop by animateColorAsState(
                targetValue = targetColors.skyTop,
                animationSpec = tween(durationMillis = 8000),
                label = "sky_top",
            )
            val animatedSkyBottom by animateColorAsState(
                targetValue = targetColors.skyBottom,
                animationSpec = tween(durationMillis = 8000),
                label = "sky_bottom",
            )
            val animatedSkyMid = targetColors.skyMid?.let {
                animateColorAsState(
                    targetValue = it,
                    animationSpec = tween(durationMillis = 8000),
                    label = "sky_mid",
                ).value
            }
            val animatedMountainFill by animateColorAsState(
                targetValue = targetColors.mountainFill,
                animationSpec = tween(durationMillis = 8000),
                label = "mountain_fill",
            )
            val animatedCloudFill by animateColorAsState(
                targetValue = targetColors.cloudFill,
                animationSpec = tween(durationMillis = 8000),
                label = "cloud_fill",
            )

            val activeColors = remember(
                animatedSkyTop,
                animatedSkyBottom,
                animatedSkyMid,
                animatedMountainFill,
                animatedCloudFill,
                targetColors,
            ) {
                targetColors.copy(
                    skyTop = animatedSkyTop,
                    skyBottom = animatedSkyBottom,
                    skyMid = animatedSkyMid,
                    mountainFill = animatedMountainFill,
                    cloudFill = animatedCloudFill,
                )
            }

            // Shared wind gusts: oscillates wind speed dynamically up to 2.4x
            val windGustCycle by rememberInfiniteTransition(label = "wind_gust").animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 24_000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "gust_cycle",
            )

            val windGustMultiplier = remember(windGustCycle) {
                val dist = Math.abs(windGustCycle - 0.5f)
                if (dist < 0.18f) {
                    val progress = (0.18f - dist) / 0.18f
                    1.0f + progress * 1.4f
                } else {
                    1.0f
                }
            }

            val activeAtmosphere = remember(atmosphere, windGustMultiplier) {
                atmosphere.copy(
                    windSpeedKmh = atmosphere.windSpeedKmh * windGustMultiplier
                )
            }

            val environment = remember(activeScene) {
                AmbientEnvironmentResolver.resolve(activeScene)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // 1. Sky Gradient
                AmbientSky(
                    colors = activeColors,
                    atmosphere = activeAtmosphere,
                    modifier = Modifier.fillMaxSize(),
                )

                // 2. Celestial objects (Sun or Moon).
                // Scene-specific scenes use static sets; SnowScene uses the live atmosphere
                // because snow can fall at any hour — the celestial body must match the
                // actual time of day rather than being unconditionally pinned to the sun.
                when {
                    showsSun(activeScene) -> AmbientSun(
                        atmosphere = activeAtmosphere,
                        colors = activeColors,
                        modifier = Modifier.fillMaxSize(),
                    )
                    showsMoon(activeScene) -> AmbientMoon(
                        atmosphere = activeAtmosphere,
                        colors = activeColors,
                        modifier = Modifier.fillMaxSize(),
                    )
                    activeScene == AmbientScene.SnowScene && activeAtmosphere.isSunVisible -> AmbientSun(
                        atmosphere = activeAtmosphere,
                        colors = activeColors,
                        modifier = Modifier.fillMaxSize(),
                    )
                    activeScene == AmbientScene.SnowScene && activeAtmosphere.isMoonVisible -> AmbientMoon(
                        atmosphere = activeAtmosphere,
                        colors = activeColors,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // 3. Stars (weather-aware twinkling stars)
                if (activeAtmosphere.isMoonVisible && activeAtmosphere.starVisibility > 0.02f) {
                    AmbientStars(
                        colors = activeColors,
                        atmosphere = activeAtmosphere,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // 4. Clouds (with dynamic shadows)
                AmbientClouds(
                    colors = activeColors,
                    atmosphere = activeAtmosphere,
                    modifier = Modifier.fillMaxSize(),
                )

                // 5. Living Events (Rainbow, Birds, Shooting Stars, Fireflies)
                // Pass the base atmosphere (not the wind-gusted copy) so that the
                // minute-level seed and isCalm threshold remain stable across the
                // continuous wind gust animation cycle.
                AmbientLivingEvents(
                    environment = environment,
                    colors = activeColors,
                    atmosphere = atmosphere,
                    modifier = Modifier.fillMaxSize(),
                )

                // 6. Environment (Ocean, Forest, Mountains, Meadow, Desert) with 3s Crossfade
                Crossfade(
                    targetState = environment,
                    animationSpec = tween(durationMillis = 3000),
                    label = "ambient_environment",
                    modifier = Modifier.fillMaxSize(),
                ) { activeEnv ->
                    when (activeEnv) {
                        AmbientEnvironment.Ocean -> AmbientOcean(
                            colors = activeColors,
                            atmosphere = activeAtmosphere,
                        )
                        AmbientEnvironment.Forest -> AmbientForest(
                            colors = activeColors,
                            atmosphere = activeAtmosphere,
                        )
                        AmbientEnvironment.Mountains -> AmbientMountains(
                            colors = activeColors,
                            atmosphere = activeAtmosphere,
                        )
                        AmbientEnvironment.Meadow -> AmbientMeadow(
                            colors = activeColors,
                            atmosphere = activeAtmosphere,
                        )
                        AmbientEnvironment.Desert -> AmbientDesert(
                            colors = activeColors,
                            atmosphere = activeAtmosphere,
                        )
                    }
                }

                // 7. Weather overlays (Fog, Rain/Snow particles, Lightning)
                AmbientFog(
                    atmosphere = activeAtmosphere,
                    modifier = Modifier.fillMaxSize(),
                )
                AmbientPrecipitation(
                    atmosphere = activeAtmosphere,
                    modifier = Modifier.fillMaxSize(),
                )
                AmbientLightning(
                    atmosphere = activeAtmosphere,
                    modifier = Modifier.fillMaxSize(),
                )

                // 8. Greeting overlay card
                AmbientGreetingLayer(
                    greeting = greeting,
                    colors = activeColors,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private val CARD_HEIGHT = 132.dp
private const val SCENE_CROSSFADE_MS = 900

private fun fallbackSceneForHour(): AmbientScene {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..6   -> AmbientScene.SunriseGlow
        in 7..10  -> AmbientScene.ClearMorning
        in 11..13 -> AmbientScene.ClearNoon
        in 14..16 -> AmbientScene.ClearAfternoon
        in 17..18 -> AmbientScene.GoldenHour
        in 19..20 -> AmbientScene.SunsetBlaze
        in 21..22 -> AmbientScene.ClearEvening
        else      -> AmbientScene.StarryNight
    }
}

@Composable
private fun rememberAmbientRepository(): AmbientRepository {
    val vm = hiltViewModel<AmbientSceneHostViewModel>()
    return vm.repository
}

private fun showsSun(scene: AmbientScene): Boolean = scene in setOf(
    AmbientScene.SunriseGlow,
    AmbientScene.ClearMorning,
    AmbientScene.CloudyMorning,
    AmbientScene.RainyMorning,
    AmbientScene.WinterMorning,
    AmbientScene.ClearNoon,
    AmbientScene.CloudyNoon,
    AmbientScene.ClearAfternoon,
    AmbientScene.HotAfternoon,
    AmbientScene.RainyAfternoon,
    AmbientScene.StormyAfternoon,
    AmbientScene.GoldenHour,
    AmbientScene.CloudyGoldenHour,
    AmbientScene.SunsetBlaze,
    AmbientScene.CloudySunset,
    AmbientScene.RainySunset,
    // Note: SnowScene is intentionally excluded — celestial body is driven by atmosphere
    // (isSunVisible / isMoonVisible) so it correctly reflects day vs. night snowfall.
)

private fun showsMoon(scene: AmbientScene): Boolean = scene in setOf(
    AmbientScene.ClearEvening,
    AmbientScene.CloudyEvening,
    AmbientScene.RainyEvening,
    AmbientScene.StarryNight,
    AmbientScene.CloudyNight,
    AmbientScene.RainyNight,
    AmbientScene.StormyNight,
    AmbientScene.DeepMidnight,
)
