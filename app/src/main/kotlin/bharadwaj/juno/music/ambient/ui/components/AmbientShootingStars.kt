package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext

/**
 * Renders an extremely rare diagonal shooting star with a fading light trail.
 */
@Composable
fun AmbientShootingStars(
    seed: Long,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isReduceMotion = remember(context) {
        try {
            android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }

    if (isReduceMotion) return

    var progress by remember(seed) { mutableStateOf(0f) }

    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    LaunchedEffect(seed) {
        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        progress = 0f
        val anim = androidx.compose.animation.core.Animatable(0f)
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = LinearEasing)
        ) {
            progress = value
        }
    }

    val rand = remember(seed) { java.util.Random(seed) }
    // Start bounds in the upper half of the card
    val startX = remember(seed) { rand.nextFloat() * 0.55f * 1000f }
    val startY = remember(seed) { rand.nextFloat() * 0.22f * 400f }
    val angle = remember(seed) { 32f + rand.nextFloat() * 16f }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val flightX = startX + progress * w * 0.35f
        val flightY = startY + progress * h * 0.28f

        // Draw trail behind the current head
        val trailLength = 32f + (1f - progress) * 15f
        val trailStartX = flightX - trailLength
        val trailStartY = flightY - (trailLength * (angle / 45f))

        // Shooting star fades out as progress completes
        val alpha = (1f - progress).coerceIn(0f, 1f) * 0.75f

        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(trailStartX, trailStartY),
            end = Offset(flightX, flightY),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
    }
}
