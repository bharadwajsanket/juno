package bharadwaj.juno.music.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import bharadwaj.juno.music.constants.ThumbnailCornerRadius

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RandomizeGridItem(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounceScale"
    )
    
    val dotOffsetMultiplier by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 600),
        label = "dotOffset"
    )

    val loadingAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "loadingAlpha"
    )

    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.08f),
                shape = shape
            )
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        
        val dotColor = MaterialTheme.colorScheme.onSecondaryContainer
        val dotSize = 14.dp
        val padding = 24.dp

        
        

        
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -padding * dotOffsetMultiplier, y = -padding * dotOffsetMultiplier)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = padding * dotOffsetMultiplier, y = -padding * dotOffsetMultiplier)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -padding * dotOffsetMultiplier, y = padding * dotOffsetMultiplier)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = padding * dotOffsetMultiplier, y = padding * dotOffsetMultiplier)
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        
        
        Box(modifier = Modifier.alpha(loadingAlpha)) {
            LoadingIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}
