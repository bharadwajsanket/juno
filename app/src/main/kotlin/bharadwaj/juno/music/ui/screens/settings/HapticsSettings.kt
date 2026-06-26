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

        Text(
            text = "Intensity",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (enableHaptics) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Light",
                style = MaterialTheme.typography.bodyMedium,
                color = if (enableHaptics) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Slider(
                value = hapticIntensity,
                onValueChange = onHapticIntensityChange,
                valueRange = 0.2f..1.0f,
                enabled = enableHaptics,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                onValueChangeFinished = {
                    HapticManager.getInstance(context).performHaptic(HapticType.MEDIUM)
                }
            )
            Text(
                text = "Strong",
                style = MaterialTheme.typography.bodyMedium,
                color = if (enableHaptics) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
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
