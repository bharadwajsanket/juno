package bharadwajsanket.aether.music.ui.screens.settings

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
import bharadwajsanket.aether.music.ui.component.AetherSwitch as Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bharadwajsanket.aether.music.LocalPlayerAwareWindowInsets
import bharadwajsanket.aether.music.R
import bharadwajsanket.aether.music.constants.EnableHapticsKey
import bharadwajsanket.aether.music.ui.component.IconButton
import bharadwajsanket.aether.music.ui.component.Material3SettingsGroup
import bharadwajsanket.aether.music.ui.component.Material3SettingsItem
import bharadwajsanket.aether.music.ui.utils.backToMain
import bharadwajsanket.aether.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HapticsSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (enableHaptics, onEnableHapticsChange) = rememberPreference(
        key = EnableHapticsKey,
        defaultValue = true
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
