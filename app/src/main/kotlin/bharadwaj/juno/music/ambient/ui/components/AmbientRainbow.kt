package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.EaseInOutSine
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Procedural atmospheric rainbow.
 * Appears with low opacity and soft, desaturated colors after rain events.
 */
@Composable
fun AmbientRainbow(
    modifier: Modifier = Modifier,
) {
    var alphaProgress by remember { mutableStateOf(0f) }

    // Rainbow slowly fades in over 4 seconds, lingers, and fades out when event clears
    LaunchedEffect(Unit) {
        val anim = androidx.compose.animation.core.Animatable(0f)
        anim.animateTo(
            targetValue = 0.12f,
            animationSpec = tween(durationMillis = 4500, easing = EaseInOutSine)
        )
        alphaProgress = anim.value
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val rainbowColors = listOf(
            Color(0xAAFF1744), // desaturated red
            Color(0xAAFF9100), // desaturated orange
            Color(0xAAFFEA00), // desaturated yellow
            Color(0xAA00E676), // desaturated green
            Color(0xAA2979FF), // desaturated blue
            Color(0xAA651FFF)  // desaturated violet
        )

        val strokeWidth = 2.5f
        val baseRadius = h * 0.46f

        // Draw soft partial arc in the sky background
        rainbowColors.forEachIndexed { index, color ->
            val r = baseRadius + index * strokeWidth
            drawArc(
                color = color.copy(alpha = color.alpha * alphaProgress),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.12f - index * strokeWidth, h * 0.40f - index * strokeWidth),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
