package bharadwaj.juno.music.ui.screens.settings

import bharadwaj.juno.music.R
import android.os.Build
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bharadwaj.juno.music.LocalPlayerAwareWindowInsets
import bharadwaj.juno.music.ui.component.IconButton
import bharadwaj.juno.music.ui.component.Material3SettingsGroup
import bharadwaj.juno.music.ui.component.Material3SettingsItem
import bharadwaj.juno.music.ui.utils.backToMain
import bharadwaj.juno.music.ui.theme.JUNOSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
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

        Material3SettingsGroup(
            title = "JUNO Settings",
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.play),
                    title = { Text("Playback") },
                    description = { Text("Audio quality, normalization, equalizer, and player behavior") },
                    onClick = { navController.navigate("settings/player") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.download),
                    title = { Text("Downloads") },
                    description = { Text("Download quality, auto-download on like, and storage management") },
                    onClick = { navController.navigate("settings/downloads") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text("Appearance") },
                    description = { Text("Dark mode, player themes, and visual settings") },
                    onClick = { navController.navigate("settings/appearance") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.lyrics),
                    title = { Text("Lyrics") },
                    description = { Text("Lyrics provider, styling, and text size") },
                    onClick = { navController.navigate("settings/ai") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.storage),
                    title = { Text("Storage") },
                    description = { Text("Song cache size, image cache size, and data settings") },
                    onClick = { navController.navigate("settings/storage") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.deployed_app_update),
                    title = { Text("Updates") },
                    description = { Text("Check for updates and settings") },
                    onClick = { navController.navigate("settings/update") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = { Text("About") },
                    description = { Text("App version, changelog, and legal details") },
                    onClick = { navController.navigate("settings/about") }
                )
            )
        )

        Spacer(modifier = Modifier.height(JUNOSpacing.xxl))
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
