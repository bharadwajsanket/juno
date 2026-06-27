package bharadwaj.juno.music.ambient.ui.motion

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Reusable ambient motion primitives.
 *
 * All animations are infinite, looping, and use only Compose's built-in
 * [rememberInfiniteTransition] — zero external dependencies.
 *
 * Design principle: all defaults are tuned for subtlety. Motion should be
 * barely perceptible — it adds life without drawing attention to itself.
 *
 * Naming convention:
 *   drift   → horizontal (X) oscillation
 *   float   → vertical (Y) oscillation
 *   breathe → scale pulse
 *   twinkle → alpha flicker
 */
object AmbientMotion {

    /**
     * Sinusoidal horizontal drift.
     *
     * Uses [EaseInOutSine] for a smooth, wave-like feel (as opposed to the
     * more abrupt FastOutSlowIn which can feel mechanical at slow speeds).
     *
     * @param label     Unique label for tooling (avoids transition conflicts).
     * @param periodMs  Full oscillation period in milliseconds.
     * @param amplitude Max offset in either direction (pixels).
     */
    @Composable
    fun drift(
        label: String = "drift",
        periodMs: Int = 14_000,
        amplitude: Float = 10f,
    ): State<Float> {
        val transition = rememberInfiniteTransition(label = label)
        return transition.animateFloat(
            initialValue = -amplitude,
            targetValue = amplitude,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = periodMs, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "${label}_x",
        )
    }

    /**
     * Sinusoidal vertical float.
     *
     * @param label     Unique label for tooling.
     * @param periodMs  Full oscillation period in milliseconds.
     * @param amplitude Max offset (pixels).
     */
    @Composable
    fun float(
        label: String = "float",
        periodMs: Int = 7_000,
        amplitude: Float = 5f,
    ): State<Float> {
        val transition = rememberInfiniteTransition(label = label)
        return transition.animateFloat(
            initialValue = -amplitude,
            targetValue = amplitude,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = periodMs, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "${label}_y",
        )
    }

    /**
     * Slow scale breath — pulses between 1.0 and [maxScale].
     *
     * Default [maxScale] of 1.04f is intentionally subtle — just enough
     * to suggest life without causing visible size changes.
     *
     * @param label     Unique label for tooling.
     * @param periodMs  Duration of one full breath cycle (in + out).
     * @param maxScale  Upper scale factor.
     */
    @Composable
    fun breathe(
        label: String = "breathe",
        periodMs: Int = 6_000,
        maxScale: Float = 1.04f,
    ): State<Float> {
        val transition = rememberInfiniteTransition(label = label)
        return transition.animateFloat(
            initialValue = 1.0f,
            targetValue = maxScale,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = periodMs, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "${label}_scale",
        )
    }

    /**
     * Alpha twinkle — oscillates between [minAlpha] and 1.0.
     *
     * Uses [LinearEasing] for a natural shimmer (sine-wave alpha from
     * linear interpolation reads as organic, not mechanical).
     *
     * @param label          Unique label for tooling.
     * @param periodMs       Duration of a full twinkle cycle.
     * @param phaseOffsetMs  Starting phase offset to desync multiple stars.
     * @param minAlpha       Minimum alpha during the dim phase.
     */
    @Composable
    fun twinkle(
        label: String = "twinkle",
        periodMs: Int = 2_800,
        phaseOffsetMs: Int = 0,
        minAlpha: Float = 0.25f,
    ): State<Float> {
        val transition = rememberInfiniteTransition(label = label)
        return transition.animateFloat(
            initialValue = minAlpha,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = periodMs,
                    delayMillis = phaseOffsetMs % periodMs,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "${label}_alpha",
        )
    }
}
