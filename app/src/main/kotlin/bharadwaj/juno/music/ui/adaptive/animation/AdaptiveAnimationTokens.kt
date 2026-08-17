package bharadwaj.juno.music.ui.adaptive.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standardized animation tokens for adaptive transitions and pane state changes.
 */
@Immutable
object AdaptiveAnimationTokens {
    /**
     * Standard animation duration specs in milliseconds.
     */
    const val FastDurationMs: Int = 150
    const val NormalDurationMs: Int = 300
    const val SlowDurationMs: Int = 500

    /**
     * Spring specifications for smooth pane resizing and layout shifts.
     */
    val DefaultSpringSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val DpSpringSpec: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val CrossfadeSpec: AnimationSpec<Float> = tween(
        durationMillis = NormalDurationMs
    )

    val SlideSpec: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
}
