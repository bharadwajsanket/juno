package bharadwaj.juno.music.ambient.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere

/**
 * Atmospheric fog overlay that moves slowly and blends naturally.
 * Reduces distant landscape visibility and softens overall contrast.
 */
@Composable
fun AmbientFog(
    atmosphere: AmbientAtmosphere,
    modifier: Modifier = Modifier,
) {
    val animatedFogIntensity by animateFloatAsState(
        targetValue = atmosphere.fogIntensity,
        animationSpec = tween(durationMillis = 8000),
        label = "fog_intensity",
    )

    if (animatedFogIntensity <= 0.02f) return

    val infiniteTransition = rememberInfiniteTransition(label = "fog_drift")
    val driftX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "fog_drift_x",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Soft fog color: light grey-white
        val fogColor = Color(0xFFE5ECEF).copy(alpha = animatedFogIntensity * 0.42f)
        
        // Draw double layer of soft linear gradients moving horizontally
        val startX1 = w * (driftX - 0.5f)
        val endX1 = startX1 + w * 1.5f
        
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, fogColor, Color.Transparent),
                start = Offset(startX1, h * 0.4f),
                end = Offset(endX1, h * 0.9f),
            ),
        )

        val startX2 = w * (0.8f - driftX)
        val endX2 = startX2 + w * 1.5f
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, fogColor.copy(alpha = fogColor.alpha * 0.7f), Color.Transparent),
                start = Offset(startX2, h * 0.5f),
                end = Offset(endX2, h * 0.95f),
            ),
        )
    }
}
