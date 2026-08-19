package bharadwaj.juno.music.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import bharadwaj.juno.music.ui.component.JunoSwitch as Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bharadwaj.juno.music.LocalPlayerAwareWindowInsets
import bharadwaj.juno.music.R
import bharadwaj.juno.music.constants.EnableHapticsKey
import bharadwaj.juno.music.constants.HapticIntensityKey
import bharadwaj.juno.music.ui.component.IconButton
import bharadwaj.juno.music.ui.component.Material3SettingsGroup
import bharadwaj.juno.music.ui.component.Material3SettingsItem
import bharadwaj.juno.music.ui.utils.backToMain
import bharadwaj.juno.music.utils.rememberPreference
import androidx.compose.material3.Slider
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import bharadwaj.juno.music.utils.HapticManager
import bharadwaj.juno.music.utils.HapticType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HapticsSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val (enableHaptics, onEnableHapticsChange) = rememberPreference(
        key = EnableHapticsKey,
        defaultValue = true
    )
    val (hapticIntensity, onHapticIntensityChange) = rememberPreference(
        key = HapticIntensityKey,
        defaultValue = 0.6f
    )

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Top
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Material3SettingsGroup(
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.graphic_eq),
                    title = { Text(stringResource(R.string.enable_haptics)) },
                    description = { Text(stringResource(R.string.enable_haptics_desc)) },
                    trailingContent = {
                        Switch(
                            checked = enableHaptics,
                            onCheckedChange = onEnableHapticsChange,
                            thumbContent = {
                                Icon(
                                    painter = painterResource(
                                        id = if (enableHaptics) R.drawable.check else R.drawable.close
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(androidx.compose.material3.SwitchDefaults.IconSize)
                                )
                            }
                        )
                    },
                    onClick = { onEnableHapticsChange(!enableHaptics) }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        val currentLevel = remember(enableHaptics, hapticIntensity) {
            if (!enableHaptics) 0f
            else {
                when {
                    hapticIntensity <= 0.35f -> 1f
                    hapticIntensity <= 0.65f -> 2f
                    hapticIntensity <= 0.85f -> 3f
                    else -> 4f
                }
            }
        }

        val levelName = remember(currentLevel) {
            when (currentLevel) {
                0f -> "Off"
                1f -> "Light"
                2f -> "Medium"
                3f -> "Strong"
                else -> "Immersive"
            }
        }

        Text(
            text = "Haptic Level: $levelName",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Off",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = currentLevel,
                onValueChange = { level ->
                    when (level) {
                        0f -> {
                            onEnableHapticsChange(false)
                            onHapticIntensityChange(0.0f)
                        }
                        1f -> {
                            onEnableHapticsChange(true)
                            onHapticIntensityChange(0.25f)
                        }
                        2f -> {
                            onEnableHapticsChange(true)
                            onHapticIntensityChange(0.55f)
                        }
                        3f -> {
                            onEnableHapticsChange(true)
                            onHapticIntensityChange(0.8f)
                        }
                        4f -> {
                            onEnableHapticsChange(true)
                            onHapticIntensityChange(1.0f)
                        }
                    }
                },
                valueRange = 0f..4f,
                steps = 3,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                onValueChangeFinished = {
                    when (currentLevel) {
                        0f -> {}
                        1f -> HapticManager.getInstance(context).performHaptic(HapticType.LIGHT)
                        2f -> HapticManager.getInstance(context).performHaptic(HapticType.MEDIUM)
                        3f -> HapticManager.getInstance(context).performHaptic(HapticType.HEAVY)
                        4f -> HapticManager.getInstance(context).performHaptic(HapticType.BOUNDARY)
                    }
                }
            )
            Text(
                text = "Immersive",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.haptics)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        }
    )
}
