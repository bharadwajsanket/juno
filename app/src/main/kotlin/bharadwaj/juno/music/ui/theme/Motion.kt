package bharadwaj.juno.music.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.ui.unit.Dp

/**
 * JUNO Motion System
 * Centralized animations, durations, easings, and transitions to ensure UI consistency.
 */
object JUNOMotion {
    // Easing Curves (Standard Material 3 easing curves)
    val Emphasized = CubicBezierEasing(0.2f, 0.0f, 0f, 1.0f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val Standard = CubicBezierEasing(0.2f, 0.0f, 0f, 1.0f)
    val StandardDecelerate = CubicBezierEasing(0f, 0f, 0.2f, 1f)
    val StandardAccelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)

    // Duration Tokens (in milliseconds)
    const val DurationFast = 150
    const val DurationNormal = 300
    const val DurationSlow = 500

    // Animation Specs (Reusable Tween specs)
    val FastFloatSpec: FiniteAnimationSpec<Float> = tween(durationMillis = DurationFast, easing = FastOutSlowInEasing)
    val NormalFloatSpec: FiniteAnimationSpec<Float> = tween(durationMillis = DurationNormal, easing = FastOutSlowInEasing)
    val SlowFloatSpec: FiniteAnimationSpec<Float> = tween(durationMillis = DurationSlow, easing = FastOutSlowInEasing)

    // Spring Constants
    val SpringDefault: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val CrispSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val CrispSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val TouchSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val TouchSpringDp: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val EmphasizedSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val MorphSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.6f,
        stiffness = 500f
    )

    val LyricsScrollSpec: AnimationSpec<Float> = tween(
        durationMillis = 600,
        easing = EmphasizedDecelerate
    )

    // Specialized timing / shared transition durations
    val SharedTiming: AnimationSpec<Float> = tween(durationMillis = DurationNormal, easing = EmphasizedDecelerate)

    // Standard Enter/Exit Transitions (Fade & Scale)
    val FadeIn: EnterTransition = fadeIn(animationSpec = tween(durationMillis = DurationNormal, easing = LinearEasing))
    val FadeOut: ExitTransition = fadeOut(animationSpec = tween(durationMillis = DurationNormal, easing = LinearEasing))

    val ScaleIn: EnterTransition = scaleIn(animationSpec = tween(durationMillis = DurationNormal, easing = EmphasizedDecelerate), initialScale = 0.95f)
    val ScaleOut: ExitTransition = scaleOut(animationSpec = tween(durationMillis = DurationNormal, easing = EmphasizedAccelerate), targetScale = 0.95f)
}
