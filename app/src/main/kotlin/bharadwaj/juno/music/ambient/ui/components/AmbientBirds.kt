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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

/**
 * Renders small groups of birds flying along a curved path in the daytime.
 */
@Composable
fun AmbientBirds(
    seed: Long,
    modifier: Modifier = Modifier,
) {
    var progress by remember(seed) { mutableStateOf(0f) }
    
    // Flight duration of 8.5 seconds
    LaunchedEffect(seed) {
        progress = 0f
        val anim = androidx.compose.animation.core.Animatable(0f)
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 8500, easing = LinearEasing)
        ) {
            progress = value
        }
    }

    val rand = remember(seed) { java.util.Random(seed) }
    
    // Group size between 1 and 4
    val groupSize = remember(seed) { rand.nextInt(3) + 1 }
    
    // Determine flight direction (default left-to-right)
    val flyLeftToRight = remember(seed) { rand.nextBoolean() }
    
    // Deterministic starting heights and offsets for each bird in the group
    val birdOffsets = remember(seed) {
        List(groupSize) {
            Offset(
                x = rand.nextFloat() * 40f - 20f,
                y = rand.nextFloat() * 30f - 15f
            )
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val startX = if (flyLeftToRight) -50f else w + 50f
        val endX = if (flyLeftToRight) w + 50f else -50f

        // Flight coordinates along a parabolic path
        val cx = startX + (endX - startX) * progress
        val cy = h * 0.25f + sin(progress * kotlin.math.PI.toFloat()) * -h * 0.12f

        val birdColor = Color(0x3E000000) // tiny dark silhouette

        birdOffsets.forEachIndexed { index, offset ->
            val bx = cx + offset.x
            val by = cy + offset.y

            // Wing flap oscillation (very fast)
            val flapTime = System.currentTimeMillis() / 80f + index * 3
            val wingSway = sin(flapTime) * 3.5f

            // Draw a stylized v-shape wing path
            val path = Path().apply {
                moveTo(bx - 6f, by + wingSway)
                quadraticTo(bx - 3f, by - 2f, bx, by + 1f)
                quadraticTo(bx + 3f, by - 2f, bx + 6f, by + wingSway)
            }
            drawPath(
                path = path,
                color = birdColor,
                style = Stroke(width = 1.5f)
            )
        }
    }
}
