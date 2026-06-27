package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

/**
 * Fireflies floating overlay for meadows and forests on calm evenings.
 */
@Composable
fun AmbientFireflies(
    seed: Long,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "firefly_twinkle")
    val twinklePulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart,
        ),
        label = "twinkle",
    )

    val rand = remember(seed) { java.util.Random(seed) }
    
    // Draw 8 tiny fireflies
    val fireflies = remember(seed) {
        List(8) {
            Triple(
                Offset(rand.nextFloat(), 0.35f + rand.nextFloat() * 0.3f), // relative position
                3f + rand.nextFloat() * 2f, // radius
                rand.nextFloat() * 2f * kotlin.math.PI.toFloat() // random phase offset
            )
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        fireflies.forEach { (relOffset, radius, phase) ->
            // Flickering alpha
            val alpha = (sin(twinklePulse + phase) * 0.4f + 0.5f).coerceIn(0.1f, 0.9f)
            
            // Subtle floating drift
            val dx = sin(twinklePulse + phase) * 6f
            val dy = sin(twinklePulse * 1.5f + phase) * 4f
            
            val cx = relOffset.x * w + dx
            val cy = relOffset.y * h + dy

            // Draw soft yellow-green glow circle
            drawCircle(
                color = Color(0xFFD4E157).copy(alpha = alpha * 0.55f),
                radius = radius * 2.2f,
                center = Offset(cx, cy)
            )
            // Core
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = radius * 0.8f,
                center = Offset(cx, cy)
            )
        }
    }
}
