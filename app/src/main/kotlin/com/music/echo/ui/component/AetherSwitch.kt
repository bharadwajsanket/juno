package bharadwajsanket.aether.music.ui.component

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import bharadwajsanket.aether.music.utils.HapticManager

@Composable
fun AetherSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = getAetherSwitchColors()
) {
    val context = LocalContext.current
    val hapticManager = remember { HapticManager.getInstance(context) }

    Switch(
        checked = checked,
        onCheckedChange = { newValue ->
            hapticManager.performMediumClick()
            onCheckedChange?.invoke(newValue)
        },
        modifier = modifier,
        thumbContent = thumbContent,
        enabled = enabled,
        colors = colors
    )
}

@Composable
fun getAetherSwitchColors(): SwitchColors {
    val isLight = !MaterialTheme.colorScheme.background.let { it.luminance() < 0.5f }
    val isPureBlack = MaterialTheme.colorScheme.background == Color.Black

    return SwitchDefaults.colors(
        // ON colors
        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        checkedBorderColor = Color.Transparent,
        checkedIconColor = MaterialTheme.colorScheme.primary,

        // OFF colors
        uncheckedThumbColor = when {
            isPureBlack -> Color(0xFFCCCCCC)
            isLight -> MaterialTheme.colorScheme.onSurfaceVariant
            else -> Color(0xFFC0C0C0)
        },
        uncheckedTrackColor = when {
            isPureBlack -> Color(0xFF222222)
            isLight -> MaterialTheme.colorScheme.surfaceVariant
            else -> Color(0xFF333333)
        },
        uncheckedBorderColor = when {
            isPureBlack -> Color(0xFF444444)
            isLight -> MaterialTheme.colorScheme.outline
            else -> Color(0xFF555555)
        },
        uncheckedIconColor = when {
            isPureBlack -> Color.Black
            isLight -> MaterialTheme.colorScheme.surface
            else -> Color.Black
        }
    )
}
