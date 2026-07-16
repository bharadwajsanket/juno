

package bharadwaj.juno.music.ui.component.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmer
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.ShimmerBounds

@Composable
fun ShimmerHost(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View, theme = ShimmerTheme)
    Column(
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        modifier =
        modifier
            .shimmer(shimmer)
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent)),
                    blendMode = BlendMode.DstIn,
                )
            },
        content = content,
    )
}

val ShimmerTheme =
    defaultShimmerTheme.copy(
        animationSpec =
        infiniteRepeatable(
            animation =
            tween(
                durationMillis = 1200,
                easing = LinearEasing,
                delayMillis = 400,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        shaderColors =
        listOf(
            Color.Unspecified.copy(alpha = 0.12f),
            Color.Unspecified.copy(alpha = 0.28f),
            Color.Unspecified.copy(alpha = 0.12f),
        ),
    )
