package bharadwajsanket.aether.music.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import bharadwajsanket.aether.music.ui.screens.Screens
import bharadwajsanket.aether.music.utils.HapticManager

@Composable
fun FloatingNavigationToolbar(
    items: List<Screens>,
    pureBlack: Boolean,
    modifier: Modifier = Modifier,
    isSelected: (Screens) -> Boolean,
    onItemClick: (Screens, Boolean) -> Unit,
) {
    val context = LocalContext.current
    val hapticManager = remember { HapticManager.getInstance(context) }
    val toolbarContainerColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
    val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(32.dp),
            color = toolbarContainerColor,
            border = BorderStroke(1.dp, outlineColor),
            shadowElevation = 4.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                items.forEach { screen ->
                    val selected = isSelected(screen)
                    FloatingNavigationToolbarItem(
                        screen = screen,
                        selected = selected,
                        pureBlack = pureBlack,
                        onClick = {
                            hapticManager.performTick()
                            onItemClick(screen, selected)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavigationToolbarItem(
    screen: Screens,
    selected: Boolean,
    pureBlack: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedContentColor = if (pureBlack) Color.White else MaterialTheme.colorScheme.primary
    val unselectedContentColor = if (pureBlack) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    val tintColor by animateColorAsState(
        targetValue = if (selected) selectedContentColor else unselectedContentColor,
        animationSpec = tween(durationMillis = 150),
        label = "tintColor"
    )

    val iconOpacity by animateFloatAsState(
        targetValue = if (selected) 1.0f else 0.6f,
        animationSpec = tween(durationMillis = 150),
        label = "iconOpacity"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 150),
        label = "glowAlpha"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Tab,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle glow behind the selected icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .graphicsLayer { alpha = glowAlpha }
                .background(
                    color = if (pureBlack) Color.White.copy(alpha = 0.08f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = CircleShape
                )
        )

        Icon(
            painter = painterResource(if (selected) screen.iconIdActive else screen.iconIdInactive),
            contentDescription = stringResource(screen.titleId),
            tint = tintColor,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer { alpha = iconOpacity }
        )
    }
}
