package bharadwaj.juno.music.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bharadwaj.juno.music.ui.screens.Screens
import bharadwaj.juno.music.utils.HapticManager
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

@Composable
fun FloatingNavigationToolbar(
    items: NavigationItemsList,
    pureBlack: Boolean,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    isSelected: (Screens) -> Boolean,
    onItemClick: (Screens, Boolean) -> Unit,
) {
    val context = LocalContext.current
    val hapticManager = remember { HapticManager.getInstance(context) }

    val baseBgColor = if (pureBlack) {
        Color.Black.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.65f)
    }

    val borderColor = if (pureBlack) {
        Color.White.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

    val themeSurface = MaterialTheme.colorScheme.surface
    val hazeStyle = remember(pureBlack, themeSurface) {
        HazeStyle(
            backgroundColor = if (pureBlack) Color.Black else themeSurface,
            blurRadius = 24.dp,
            tint = HazeTint(if (pureBlack) Color.Black.copy(alpha = 0.6f) else Color.Transparent)
        )
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    clip = false
                )
                .hazeChild(state = hazeState, shape = RoundedCornerShape(28.dp), style = hazeStyle)
                .background(
                    color = baseBgColor,
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                items.items.forEach { screen ->
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
    val activeColor = if (pureBlack) Color.White else MaterialTheme.colorScheme.primary
    val inactiveColor = if (pureBlack) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)

    val tintColor by animateColorAsState(
        targetValue = if (selected) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 150),
        label = "tintColor"
    )

    val labelAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "labelAlpha"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default material ripple to use custom spring selection effect
                role = Role.Tab,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(36.dp)
                    .width(
                        animateDpAsState(
                            targetValue = if (selected) 64.dp else 40.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "capsuleWidth"
                        ).value
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        color = if (selected) {
                            if (pureBlack) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        } else {
                            Color.Transparent
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(if (selected) screen.iconIdActive else screen.iconIdInactive),
                    contentDescription = stringResource(screen.titleId),
                    tint = tintColor,
                    modifier = Modifier
                        .size(
                            animateDpAsState(
                                targetValue = if (selected) 24.dp else 21.dp,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
                                label = "iconSize"
                            ).value
                        )
                )
            }

            Box(
                modifier = Modifier
                    .height(16.dp)
                    .graphicsLayer {
                        alpha = labelAlpha
                        translationY = (1f - labelAlpha) * 4.dp.toPx()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(screen.titleId),
                    color = tintColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
