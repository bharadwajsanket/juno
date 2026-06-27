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
import bharadwaj.juno.music.constants.LocalProfileNameKey
import bharadwaj.juno.music.ui.component.TextFieldDialog
import androidx.compose.ui.text.input.TextFieldValue

import androidx.hilt.navigation.compose.hiltViewModel
import bharadwaj.juno.music.viewmodels.HomeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import bharadwaj.juno.music.constants.InnerTubeCookieKey
import bharadwaj.juno.music.constants.AccountEmailKey
import bharadwaj.juno.music.constants.UseLoginForBrowse
import bharadwaj.juno.music.constants.YtmSyncKey
import bharadwaj.juno.music.utils.rememberPreference
import com.music.innertube.utils.parseCookieString
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import coil3.compose.AsyncImage
import bharadwaj.juno.music.ui.component.JunoSwitch as Switch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val scrollState = rememberScrollState()
    
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = hiltViewModel()
    val (innerTubeCookie, _) = rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = remember(innerTubeCookie) {
        innerTubeCookie.isNotEmpty() && "SAPISID" in parseCookieString(innerTubeCookie)
    }
    val (accountEmail, _) = rememberPreference(AccountEmailKey, "")
    val accountName by homeViewModel.accountName.collectAsState()
    val accountImageUrl by homeViewModel.accountImageUrl.collectAsState()

    val (useLoginForBrowse, onUseLoginForBrowseChange) = rememberPreference(UseLoginForBrowse, true)
    val (ytmSync, onYtmSyncChange) = rememberPreference(YtmSyncKey, true)
    val (localProfileName, onLocalProfileNameChange) = rememberPreference(LocalProfileNameKey, "")
    var showProfileNameDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val filteredResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val q = searchQuery.trim()
            settingsSearchItems.filter { item ->
                item.title.contains(q, ignoreCase = true) ||
                item.subtitle.contains(q, ignoreCase = true) ||
                item.category.contains(q, ignoreCase = true) ||
                item.keywords.any { it.contains(q, ignoreCase = true) } ||
                item.title.containsSubsequence(q) ||
                item.keywords.any { it.containsSubsequence(q) }
            }
        }
    }

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
            modifier = Modifier.padding(start = 8.dp, top = 24.dp, bottom = 8.dp)
        )

        // Settings Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search settings") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Clear search",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (searchQuery.isNotEmpty()) {
            if (filteredResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Material3SettingsGroup(
                    title = "Search Results",
                    items = filteredResults.map { result ->
                        Material3SettingsItem(
                            title = { Text(result.title) },
                            description = { Text(result.subtitle) },
                            icon = when (result.category) {
                                "Playback" -> painterResource(R.drawable.play)
                                "Downloads" -> painterResource(R.drawable.download)
                                "Appearance" -> painterResource(R.drawable.palette)
                                "Lyrics" -> painterResource(R.drawable.lyrics)
                                "Storage" -> painterResource(R.drawable.storage)
                                "Haptics" -> painterResource(R.drawable.graphic_eq)
                                "Updates" -> painterResource(R.drawable.deployed_app_update)
                                "About" -> painterResource(R.drawable.info)
                                "Account" -> painterResource(R.drawable.account)
                                else -> painterResource(R.drawable.settings)
                            },
                            onClick = {
                                navController.navigate(result.route)
                            }
                        )
                    }
                )
            }
        } else {
            Material3SettingsGroup(
                title = "Account & Sync",
                items = listOf(
                    Material3SettingsItem(
                        title = { Text(if (isLoggedIn) accountName else "Anonymous") },
                        description = { Text(if (isLoggedIn) accountEmail.ifEmpty { "Logged In" } else "Not Logged In") },
                        icon = painterResource(R.drawable.account),
                        trailingContent = if (isLoggedIn && !accountImageUrl.isNullOrBlank()) {
                            {
                                AsyncImage(
                                    model = accountImageUrl,
                                    contentDescription = "Profile Photo",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                            }
                        } else null,
                        onClick = { if (isLoggedIn) navController.navigate("settings/account") else navController.navigate("login") }
                    ),
                    if (isLoggedIn) {
                        Material3SettingsItem(
                            title = { Text("Use Account for Browsing") },
                            icon = painterResource(R.drawable.add_circle),
                            trailingContent = {
                                Switch(
                                    checked = useLoginForBrowse,
                                    onCheckedChange = {
                                        com.music.innertube.YouTube.useLoginForBrowse = it
                                        onUseLoginForBrowseChange(it)
                                    },
                                    modifier = Modifier.scale(0.8f)
                                )
                            },
                            onClick = {
                                val newVal = !useLoginForBrowse
                                com.music.innertube.YouTube.useLoginForBrowse = newVal
                                onUseLoginForBrowseChange(newVal)
                            }
                        )
                    } else null,
                    if (isLoggedIn) {
                        Material3SettingsItem(
                            title = { Text("YouTube Music Sync") },
                            icon = painterResource(R.drawable.cached),
                            trailingContent = {
                                Switch(
                                    checked = ytmSync,
                                    onCheckedChange = onYtmSyncChange,
                                    modifier = Modifier.scale(0.8f)
                                )
                            },
                            onClick = { onYtmSyncChange(!ytmSync) }
                        )
                    } else null
                ).filterNotNull()
            )

            Material3SettingsGroup(
                title = "Personalization",
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.person),
                        title = { Text(stringResource(R.string.display_name)) },
                        description = {
                            Text(
                                if (localProfileName.isNotEmpty()) localProfileName
                                else stringResource(R.string.display_name_desc)
                            )
                        },
                        onClick = { showProfileNameDialog = true }
                    )
                )
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
                        icon = painterResource(R.drawable.graphic_eq),
                        title = { Text("Haptics") },
                        description = { Text("Touch feedback and vibration settings") },
                        onClick = { navController.navigate("settings/haptics") }
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
                ).filterNotNull()
            )
        }
        
        if (showProfileNameDialog) {
            TextFieldDialog(
                title = { Text(stringResource(R.string.display_name)) },
                icon = { Icon(painterResource(R.drawable.person), null) },
                initialTextFieldValue = TextFieldValue(text = localProfileName),
                onDone = {
                    onLocalProfileNameChange(it.trim())
                    showProfileNameDialog = false
                },
                onDismiss = { showProfileNameDialog = false },
                isInputValid = { true }
            )
        }
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

private data class SettingsSearchItem(
    val title: String,
    val subtitle: String,
    val category: String,
    val keywords: List<String>,
    val route: String
)

private fun String.containsSubsequence(query: String): Boolean {
    if (query.isEmpty()) return true
    var queryIdx = 0
    val target = this.lowercase()
    val q = query.lowercase()
    for (char in target) {
        if (char == q[queryIdx]) {
            queryIdx++
            if (queryIdx == q.length) return true
        }
    }
    return false
}

private val settingsSearchItems = listOf(
    SettingsSearchItem(
        title = "Playback",
        subtitle = "Audio quality, normalization, equalizer, and player behavior",
        category = "Playback",
        keywords = listOf("playback", "play", "audio", "quality", "sound", "volume", "player", "equalizer"),
        route = "settings/player"
    ),
    SettingsSearchItem(
        title = "Equalizer",
        subtitle = "Equalizer and audio effects",
        category = "Playback",
        keywords = listOf("equalizer", "eq", "sound", "audio", "tone", "bass", "treble"),
        route = "settings/player"
    ),
    SettingsSearchItem(
        title = "Audio Normalization",
        subtitle = "Adjust volume levels across songs",
        category = "Playback",
        keywords = listOf("normalization", "volume", "loudness", "audio", "playback"),
        route = "settings/player"
    ),
    SettingsSearchItem(
        title = "Downloads",
        subtitle = "Download quality, auto-download on like, and storage management",
        category = "Downloads",
        keywords = listOf("downloads", "download", "offline", "quality", "auto-download"),
        route = "settings/downloads"
    ),
    SettingsSearchItem(
        title = "Auto-Download on Like",
        subtitle = "Automatically download liked songs for offline play",
        category = "Downloads",
        keywords = listOf("auto-download", "download", "like", "favorites", "offline"),
        route = "settings/downloads"
    ),
    SettingsSearchItem(
        title = "Appearance",
        subtitle = "Dark mode, player themes, and visual settings",
        category = "Appearance",
        keywords = listOf("appearance", "theme", "dark mode", "light mode", "look", "feel", "colors", "style"),
        route = "settings/appearance"
    ),
    SettingsSearchItem(
        title = "Dark Mode",
        subtitle = "Toggle dark mode and theme settings",
        category = "Appearance",
        keywords = listOf("dark mode", "light mode", "theme", "colors", "appearance"),
        route = "settings/appearance"
    ),
    SettingsSearchItem(
        title = "Display Name",
        subtitle = "Set your local display name for personalized greetings",
        category = "Personalization",
        keywords = listOf("display name", "personalization", "name", "nickname", "greeting"),
        route = "settings"
    ),
    SettingsSearchItem(
        title = "Romanization",
        subtitle = "Romanize non-Latin song titles and lyrics",
        category = "Lyrics",
        keywords = listOf("romanization", "romanize", "lyrics", "content", "language"),
        route = "settings/content/romanization"
    ),
    SettingsSearchItem(
        title = "Storage",
        subtitle = "Song cache size, image cache size, and data settings",
        category = "Storage",
        keywords = listOf("storage", "stor", "cache", "data", "clear", "size", "disk"),
        route = "settings/storage"
    ),
    SettingsSearchItem(
        title = "Clear Cache",
        subtitle = "Clear cached songs, images, or databases",
        category = "Storage",
        keywords = listOf("clear", "cache", "storage", "delete", "free space"),
        route = "settings/storage"
    ),
    SettingsSearchItem(
        title = "Haptics",
        subtitle = "Enable or disable touch haptic feedback",
        category = "Haptics",
        keywords = listOf("haptics", "haptic", "vibration", "vib", "touch", "feedback"),
        route = "settings/haptics"
    ),
    SettingsSearchItem(
        title = "Vibration",
        subtitle = "Enable touch vibration feedback",
        category = "Haptics",
        keywords = listOf("vibration", "vib", "haptics", "haptic", "feedback"),
        route = "settings/haptics"
    ),
    SettingsSearchItem(
        title = "Intensity",
        subtitle = "Adjust haptic vibration strength (Light to Strong)",
        category = "Haptics",
        keywords = listOf("intensity", "strength", "vibration", "haptics", "haptic"),
        route = "settings/haptics"
    ),
    SettingsSearchItem(
        title = "Updates",
        subtitle = "Check for updates and settings",
        category = "Updates",
        keywords = listOf("updates", "update", "version", "check", "new"),
        route = "settings/update"
    ),
    SettingsSearchItem(
        title = "About",
        subtitle = "App version, changelog, and legal details",
        category = "About",
        keywords = listOf("about", "version", "changelog", "legal", "credits"),
        route = "settings/about"
    ),
    SettingsSearchItem(
        title = "Account",
        subtitle = "Manage your connected Google / YouTube Music account",
        category = "Account",
        keywords = listOf("account", "profile", "google", "youtube", "login", "logout"),
        route = "settings/account"
    ),
    SettingsSearchItem(
        title = "Backup & Restore",
        subtitle = "Backup your settings and library, or restore from a file",
        category = "Storage",
        keywords = listOf("backup", "restore", "export", "import", "save", "load"),
        route = "settings/backup_restore"
    ),
    SettingsSearchItem(
        title = "Spotify Import",
        subtitle = "Import your playlists from Spotify",
        category = "Account",
        keywords = listOf("spotify", "import", "playlists", "transfer", "migrate"),
        route = "settings/spotify_import"
    )
)
