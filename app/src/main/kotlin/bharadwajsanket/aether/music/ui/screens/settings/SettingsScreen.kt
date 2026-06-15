

package bharadwajsanket.aether.music.ui.screens.settings

import bharadwajsanket.aether.music.R
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import bharadwajsanket.aether.music.BuildConfig
import bharadwajsanket.aether.music.LocalPlayerAwareWindowInsets
import bharadwajsanket.aether.music.ui.component.IconButton
import bharadwajsanket.aether.music.ui.component.Material3SettingsGroup
import bharadwajsanket.aether.music.ui.component.Material3SettingsItem
import bharadwajsanket.aether.music.ui.screens.Screens
import bharadwajsanket.aether.music.ui.utils.backToMain
import bharadwajsanket.aether.music.constants.ENABLE_LISTEN_TOGETHER
import bharadwajsanket.aether.music.ui.theme.AetherSpacing
import bharadwajsanket.aether.music.aethermusic.updater.getUpdateAvailableState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val isAndroid12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val isUpdateAvailable = getUpdateAvailableState(context) && bharadwajsanket.aether.music.aethermusic.updater.getAutoUpdateCheckSetting(context)

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
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp, top = 24.dp, bottom = 16.dp)
        )

        
        // 1. Playback
        Material3SettingsGroup(
            title = stringResource(R.string.settings_group_playback),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.play),
                    title = { Text(stringResource(R.string.player_and_audio)) },
                    description = { Text(stringResource(R.string.settings_player_and_audio_desc)) },
                    onClick = { navController.navigate("settings/player") }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Player
        Material3SettingsGroup(
            title = stringResource(R.string.settings_group_player),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.appearance)) },
                    description = { Text(stringResource(R.string.settings_appearance_desc)) },
                    onClick = { navController.navigate("settings/appearance") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.translate),
                    title = { Text(stringResource(R.string.ai_lyrics_translation)) },
                    description = { Text(stringResource(R.string.settings_ai_lyrics_translation_desc)) },
                    onClick = { navController.navigate("settings/ai") }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Library
        Material3SettingsGroup(
            title = stringResource(R.string.settings_group_library),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.language),
                    title = { Text(stringResource(R.string.content)) },
                    description = { Text(stringResource(R.string.settings_content_desc)) },
                    onClick = { navController.navigate("settings/content") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.storage),
                    title = { Text(stringResource(R.string.storage)) },
                    description = { Text(stringResource(R.string.settings_storage_desc)) },
                    onClick = { navController.navigate("settings/storage") }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Personalization
        Material3SettingsGroup(
            title = stringResource(R.string.settings_group_personalization),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.account),
                    title = { Text(stringResource(R.string.account)) },
                    description = { Text(stringResource(R.string.settings_account_desc)) },
                    onClick = { navController.navigate("settings/account") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.graphic_eq),
                    title = { Text(stringResource(R.string.haptics)) },
                    description = { Text(stringResource(R.string.settings_haptics_desc)) },
                    onClick = { navController.navigate("settings/haptics") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.security),
                    title = { Text(stringResource(R.string.privacy)) },
                    description = { Text(stringResource(R.string.settings_privacy_desc)) },
                    onClick = { navController.navigate("settings/privacy") }
                )
            )
        )

        if (ENABLE_LISTEN_TOGETHER) {
            Spacer(modifier = Modifier.height(16.dp))

            // 5. Connectivity
            Material3SettingsGroup(
                title = stringResource(R.string.settings_group_connectivity),
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.group),
                        title = { Text(stringResource(R.string.listen_together)) },
                        description = { Text(stringResource(R.string.settings_listen_together_desc)) },
                        onClick = { navController.navigate(Screens.ListenTogether.route) }
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Advanced
        Material3SettingsGroup(
            title = stringResource(R.string.settings_group_advanced),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.restore),
                    title = { Text(stringResource(R.string.backup_restore)) },
                    description = { Text(stringResource(R.string.settings_backup_restore_desc)) },
                    onClick = { navController.navigate("settings/backup_restore") }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 7. About
        Material3SettingsGroup(
            title = stringResource(R.string.settings_group_about),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(if (isUpdateAvailable) R.drawable.ic_launcher_nobg else R.drawable.update),
                    title = { Text(stringResource(R.string.system_update)) },
                    description = if (isUpdateAvailable) {
                        {
                            Text(
                                text = stringResource(R.string.update_available),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        { Text(stringResource(R.string.settings_system_update_desc)) }
                    },
                    onClick = { navController.navigate("settings/update") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = { Text(stringResource(R.string.about)) },
                    description = { Text(stringResource(R.string.settings_about_desc)) },
                    onClick = { navController.navigate("settings/about") }
                )
            )
        )

        Spacer(modifier = Modifier.height(AetherSpacing.xxl))
    }

    TopAppBar(
        title = {
            androidx.compose.animation.AnimatedVisibility(
                visible = scrollState.value > 100,
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut()
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null
                )
            }
        }
    )
}
