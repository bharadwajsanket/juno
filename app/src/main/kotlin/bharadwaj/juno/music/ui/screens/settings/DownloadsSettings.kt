package bharadwaj.juno.music.ui.screens.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bharadwaj.juno.music.LocalPlayerAwareWindowInsets
import bharadwaj.juno.music.LocalPlayerConnection
import bharadwaj.juno.music.R
import bharadwaj.juno.music.constants.AutoDownloadOnLikeKey
import bharadwaj.juno.music.constants.DownloadQuality
import bharadwaj.juno.music.constants.DownloadQualityKey
import bharadwaj.juno.music.constants.ExportDirectoryUriKey
import bharadwaj.juno.music.ui.component.JunoSwitch as Switch
import bharadwaj.juno.music.ui.component.EnumDialog
import bharadwaj.juno.music.ui.component.IconButton
import bharadwaj.juno.music.ui.component.Material3SettingsGroup
import bharadwaj.juno.music.ui.component.Material3SettingsItem
import bharadwaj.juno.music.ui.utils.backToMain
import bharadwaj.juno.music.utils.rememberEnumPreference
import bharadwaj.juno.music.utils.rememberPreference
import bharadwaj.juno.music.extensions.tryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val downloadCache = LocalPlayerConnection.current?.service?.downloadCache
    val coroutineScope = rememberCoroutineScope()

    val (downloadQuality, onDownloadQualityChange) = rememberEnumPreference(
        DownloadQualityKey,
        defaultValue = DownloadQuality.YOUTUBE
    )
    val (autoDownloadOnLike, onAutoDownloadOnLikeChange) = rememberPreference(
        AutoDownloadOnLikeKey,
        defaultValue = false
    )
    val (exportDirectoryUri, onExportDirectoryUriChange) = rememberPreference(
        key = ExportDirectoryUriKey,
        defaultValue = "",
    )

    val exportDirectoryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
                onExportDirectoryUriChange(uri.toString())
            }
        }

    var showDownloadQualityDialog by remember { mutableStateOf(false) }

    var downloadCacheSize by remember {
        mutableStateOf(tryOrNull { downloadCache?.cacheSpace } ?: 0L)
    }

    if (showDownloadQualityDialog) {
        EnumDialog(
            onDismiss = { showDownloadQualityDialog = false },
            onSelect = {
                onDownloadQualityChange(it)
                showDownloadQualityDialog = false
            },
            title = stringResource(R.string.download_quality_title),
            current = downloadQuality,
            values = DownloadQuality.values().toList(),
            valueText = {
                when (it) {
                    DownloadQuality.YOUTUBE -> "YouTube Music (AAC/Default)"
                    DownloadQuality.SAAVN -> "Saavn (320kbps)"
                    DownloadQuality.LOSSLESS -> "Qobuz (Lossless)"
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Downloads") },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Material3SettingsGroup(
                title = "Preferences",
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.download),
                        title = { Text(stringResource(R.string.download_quality_title)) },
                        description = {
                            Text(
                                when (downloadQuality) {
                                    DownloadQuality.YOUTUBE -> "YouTube Music (AAC/Default)"
                                    DownloadQuality.SAAVN -> "Saavn (320kbps)"
                                    DownloadQuality.LOSSLESS -> "Qobuz (Lossless)"
                                }
                            )
                        },
                        onClick = { showDownloadQualityDialog = true }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.favorite),
                        title = { Text(stringResource(R.string.auto_download_on_like)) },
                        description = { Text(stringResource(R.string.auto_download_on_like_desc)) },
                        trailingContent = {
                            Switch(
                                checked = autoDownloadOnLike,
                                onCheckedChange = onAutoDownloadOnLikeChange,
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (autoDownloadOnLike) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        },
                        onClick = { onAutoDownloadOnLikeChange(!autoDownloadOnLike) }
                    )
                )
            )

            Material3SettingsGroup(
                title = "Storage & Location",
                items = listOf(
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.folder_managed),
                        title = { Text("Export directory") },
                        description = {
                            Text(
                                if (exportDirectoryUri.isEmpty()) "Not set" else exportDirectoryUri
                            )
                        },
                        onClick = { exportDirectoryLauncher.launch(null) }
                    ),
                    Material3SettingsItem(
                        icon = painterResource(R.drawable.delete),
                        title = { Text("Clear downloads") },
                        description = {
                            val formattedSize = remember(downloadCacheSize) {
                                val mb = downloadCacheSize.toDouble() / (1024 * 1024)
                                if (mb < 1024) String.format("%.1f MB", mb)
                                else String.format("%.2f GB", mb / 1024)
                            }
                            Text("Remove all downloaded music files. Current size: $formattedSize")
                        },
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                tryOrNull {
                                    downloadCache?.keys?.forEach { key ->
                                        downloadCache.removeResource(key)
                                    }
                                }
                                downloadCacheSize = tryOrNull { downloadCache?.cacheSpace } ?: 0L
                            }
                        }
                    )
                )
            )
        }
    }
}
