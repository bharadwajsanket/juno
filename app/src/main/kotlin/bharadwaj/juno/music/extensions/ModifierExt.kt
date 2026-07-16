

package bharadwaj.juno.music.extensions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import bharadwaj.juno.music.ui.theme.JUNOMotion
import kotlin.math.abs

fun Modifier.SwipeGesture(
    enabled: Boolean = true,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    swipeThreshold: Float = 100f
): Modifier = if (enabled) {
    this.pointerInput(Unit) {
        var totalDrag = 0f

        detectHorizontalDragGestures(
            onDragStart = {
                totalDrag = 0f
            },
            onDragEnd = {
                if (abs(totalDrag) > swipeThreshold) {
                    if (totalDrag > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
                totalDrag = 0f
            },
            onDragCancel = {
                totalDrag = 0f
            },
            onHorizontalDrag = { change, dragAmount ->
                change.consume()
                totalDrag += dragAmount
            }
        )
    }
} else {
    this
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.bounceClick(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = JUNOMotion.EmphasizedSpring,
        label = "bounceScale"
    )
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
            onLongClick = onLongClick
        )
}

