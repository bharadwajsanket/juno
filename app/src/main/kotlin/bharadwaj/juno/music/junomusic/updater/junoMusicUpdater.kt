package bharadwaj.juno.music.junomusic.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import bharadwaj.juno.music.BuildConfig
import bharadwaj.juno.music.R
import coil3.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import bharadwaj.juno.music.junomusic.updater.downloadmanager.UpdateDownloadWorker
import bharadwaj.juno.music.junomusic.updater.downloadmanager.DownloadNotificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import bharadwaj.juno.music.ui.component.ChangelogItem
import bharadwaj.juno.music.ui.component.leadingItemShape
import bharadwaj.juno.music.ui.component.middleItemShape
import bharadwaj.juno.music.ui.component.endItemShape
import bharadwaj.juno.music.ui.component.detachedItemShape
import bharadwaj.juno.music.ui.component.parseMarkdown
import bharadwaj.juno.music.ui.component.AnimatedActionButton
import bharadwaj.juno.music.ui.component.ExpressiveIconButton
import bharadwaj.juno.music.ui.component.ErrorSnackbar
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.ClickableText
import timber.log.Timber

data class ChangelogSection(val title: String, val items: List<String>)

sealed class JUNOUpdateStatus {
    object Idle : JUNOUpdateStatus()
    object Checking : JUNOUpdateStatus()
    data class Available(
        val version: String,
        val changelog: List<ChangelogSection>,
        val size: String,
        val releaseDate: String,
        val description: String?,
        val imageUrl: String?,
        val apkUrl: String?,
        val isFromCache: Boolean = false
    ) : JUNOUpdateStatus()

    data class NoUpdate(val version: String, val isFromCache: Boolean = false) : JUNOUpdateStatus()
    data class Error(val message: String) : JUNOUpdateStatus()
}

fun isSafeApkFile(context: Context, file: File): Boolean {
    return try {
        val expectedDir = getDownloadedApksDir(context).canonicalPath
        val fileCanonical = file.canonicalPath
        fileCanonical.startsWith(expectedDir) && file.isFile && file.name.endsWith(".apk", ignoreCase = true)
    } catch (e: Exception) {
        false
    }
}

fun verifyApkIntegrity(context: Context, file: File): Boolean {
    return try {
        if (!file.exists() || !file.isFile) return false
        val pm = context.packageManager
        val info = pm.getPackageArchiveInfo(file.absolutePath, 0)
        info != null
    } catch (e: Exception) {
        Timber.e(e, "APK integrity check failed for: ${file.absolutePath}")
        false
    }
}

fun shareApkFile(context: Context, file: File) {
    try {
        if (!isSafeApkFile(context, file)) {
            Timber.e("Unsafe file requested for sharing: ${file.absolutePath}")
            return
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share JUNO Update APK"))
    } catch (e: Exception) {
        Timber.e(e, "Error sharing APK")
    }
}

fun installApkFile(context: Context, file: File) {
    try {
        if (!isSafeApkFile(context, file)) {
            Timber.e("Unsafe file requested for installation: ${file.absolutePath}")
            return
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ContextCompat.startActivity(context, installIntent, null)
    } catch (e: Exception) {
        Timber.e(e, "Error launching package installer")
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpdateScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<JUNOUpdateStatus>(JUNOUpdateStatus.NoUpdate(BuildConfig.VERSION_NAME)) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var isDownloadComplete by remember { mutableStateOf(false) }
    var downloadedFile by remember { mutableStateOf<File?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentVersion = BuildConfig.VERSION_NAME
    val autoUpdateCheckEnabled = getAutoUpdateCheckSetting(context)

    LaunchedEffect(Unit) {
        DownloadNotificationManager.initialize(context)
    }

    val installLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context.packageManager.canRequestPackageInstalls()) {
                val file = downloadedFile
                if (file != null && file.exists() && verifyApkIntegrity(context, file)) {
                    installApkFile(context, file)
                }
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Unknown sources permission is required to install updates.")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkFlow("update_download")
            .collect { workInfos ->
                val workInfo = workInfos.firstOrNull() ?: return@collect

                when (workInfo.state) {
                    WorkInfo.State.RUNNING -> {
                        isDownloading = true
                        downloadProgress = workInfo.progress.getFloat("progress", 0f)
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        isDownloading = false
                        isDownloadComplete = true
                        val filePath = workInfo.outputData.getString("file_path")
                        if (filePath != null) {
                            val f = File(filePath)
                            if (f.exists()) {
                                downloadedFile = f
                            } else {
                                isDownloadComplete = false
                                downloadedFile = null
                                downloadProgress = 0f
                            }
                        }
                    }
                    WorkInfo.State.FAILED -> {
                        isDownloading = false
                        downloadProgress = 0f
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.download_failed))
                        }
                    }
                    WorkInfo.State.CANCELLED -> {
                        isDownloading = false
                        downloadProgress = 0f
                    }
                    else -> {}
                }
            }
    }

    LaunchedEffect(isDownloadComplete, downloadedFile) {
        if (isDownloadComplete && downloadedFile != null) {
            if (!downloadedFile!!.exists()) {
                isDownloadComplete = false
                downloadedFile = null
                downloadProgress = 0f
            }
        }
    }

    fun triggerUpdateCheck() {
        status = JUNOUpdateStatus.Checking
        scope.launch {
            delay(1000L)
            checkForUpdate(
                context = context,
                onSuccess = { tag, isAvailable, changelog, size, date, description, imageUrl, apkUrl, isFromCache ->
                    saveLastCheckedTime(context, LocalDateTime.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy, h:mm a")))
                    saveUpdateAvailableState(context, isAvailable)
                    status = if (isAvailable) {
                        JUNOUpdateStatus.Available(
                            version = tag,
                            changelog = changelog,
                            size = size,
                            releaseDate = date,
                            description = description,
                            imageUrl = imageUrl,
                            apkUrl = apkUrl,
                            isFromCache = isFromCache
                        )
                    } else {
                        JUNOUpdateStatus.NoUpdate(tag, isFromCache = isFromCache)
                    }
                },
                onError = {
                    status = JUNOUpdateStatus.Error(context.getString(R.string.cant_check_updates))
                },
                force = true
            )
        }
    }

    LaunchedEffect(Unit) {
        if (autoUpdateCheckEnabled) {
            triggerUpdateCheck()
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    val titleText = if (status is JUNOUpdateStatus.Available) {
                        buildAnnotatedString {
                            append(stringResource(R.string.new_update) + " ")
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append((status as JUNOUpdateStatus.Available).version)
                            }
                        }
                    } else {
                        AnnotatedString(stringResource(R.string.settings_check_updates_title))
                    }
                    Text(text = titleText, maxLines = 1)
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        ExpressiveIconButton(
                            onClick = { navController.navigateUp() },
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.cancel),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (val currentStatus = status) {
                        is JUNOUpdateStatus.Idle, is JUNOUpdateStatus.Checking, is JUNOUpdateStatus.NoUpdate, is JUNOUpdateStatus.Error -> {
                            AnimatedActionButton(
                                text = stringResource(R.string.check_for_update),
                                onClick = { triggerUpdateCheck() },
                                enabled = currentStatus !is JUNOUpdateStatus.Checking && !isDownloading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is JUNOUpdateStatus.Available -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AnimatedActionButton(
                                    text = stringResource(R.string.later),
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier.weight(1f),
                                    isOutlined = true,
                                    enabled = !isDownloading
                                )
                                AnimatedActionButton(
                                    text = if (isDownloading) "${(downloadProgress * 100).toInt()}%" else if (isDownloadComplete) stringResource(R.string.install) else stringResource(R.string.update_available),
                                    onClick = {
                                        if (isDownloadComplete) {
                                            val file = downloadedFile
                                            if (file == null || !file.exists() || !verifyApkIntegrity(context, file)) {
                                                if (file != null && file.exists()) {
                                                    file.delete()
                                                }
                                                isDownloadComplete = false
                                                downloadedFile = null
                                                downloadProgress = 0f
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Downloaded APK is corrupted or invalid. Please download again.")
                                                }
                                                return@AnimatedActionButton
                                            }
                                            file.let { f ->
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    if (!context.packageManager.canRequestPackageInstalls()) {
                                                        val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                                            data = Uri.parse("package:${context.packageName}")
                                                        }
                                                        installLauncher.launch(intent)
                                                        return@let
                                                    }
                                                }
                                                installApkFile(context, f)
                                            }
                                        } else {
                                            val urlToDownload = currentStatus.apkUrl
                                            if (urlToDownload.isNullOrBlank()) {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(context.getString(R.string.cant_check_updates))
                                                }
                                            } else {
                                                val downloadRequest = OneTimeWorkRequestBuilder<UpdateDownloadWorker>()
                                                    .setInputData(workDataOf("apk_url" to urlToDownload, "version" to currentStatus.version, "file_size" to currentStatus.size))
                                                    .addTag("update_download")
                                                    .build()
                                                WorkManager.getInstance(context).enqueueUniqueWork("update_download", ExistingWorkPolicy.KEEP, downloadRequest)
                                                isDownloading = true
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isDownloading || isDownloadComplete
                                )
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { ErrorSnackbar(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val contentModifier = if (status is JUNOUpdateStatus.Available) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.fillParentMaxSize()
                    }

                    Box(
                        modifier = contentModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        when (val currentStatus = status) {
                            is JUNOUpdateStatus.Checking -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = stringResource(R.string.checking_for_updates),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            is JUNOUpdateStatus.NoUpdate -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.deployed_app_update),
                                        contentDescription = null,
                                        modifier = Modifier.size(120.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = stringResource(R.string.on_latest_version),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.current_version_v, currentStatus.version),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    val lastChecked = getLastCheckedTime(context)
                                    if (lastChecked.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.last_checked, lastChecked),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            is JUNOUpdateStatus.Error -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.error),
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = currentStatus.message,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.check_internet_connection),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    AnimatedActionButton(
                                        text = stringResource(R.string.check_for_update),
                                        onClick = { triggerUpdateCheck() },
                                        modifier = Modifier.widthIn(min = 150.dp)
                                    )
                                }
                            }

                            is JUNOUpdateStatus.Available -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    if (currentStatus.isFromCache) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.error),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "Offline - Showing cached release info",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = stringResource(R.string.release_date_v, currentStatus.releaseDate),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = stringResource(R.string.update_size_v, currentStatus.size),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    if (!currentStatus.imageUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = currentStatus.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(24.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                    if (!currentStatus.description.isNullOrBlank()) {
                                        val annotatedText = currentStatus.description.parseMarkdown()

                                        ClickableText(
                                            text = annotatedText,
                                            onClick = { offset ->
                                                annotatedText.getStringAnnotations("URL", offset, offset).firstOrNull()?.let {
                                                    ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(it.item)), null)
                                                }
                                            },
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 20.sp
                                            ),
                                            modifier = Modifier.padding(bottom = 24.dp)
                                        )
                                    }
                                    
                                    currentStatus.changelog.forEach { section ->
                                        Text(
                                            text = section.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                        )
                                        section.items.forEachIndexed { index, item ->
                                            val shape = when {
                                                section.items.size == 1 -> detachedItemShape()
                                                index == 0 -> leadingItemShape()
                                                index == section.items.size - 1 -> endItemShape()
                                                else -> middleItemShape()
                                            }
                                            ChangelogItem(text = item, shape = shape)
                                            if (index != section.items.size - 1) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                            }
                                        }
                                    }
                                    
                                    if (currentStatus.changelog.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }

                                    if (isDownloading) {
                                        if (downloadProgress > 0f) {
                                            androidx.compose.material3.LinearProgressIndicator(
                                                progress = downloadProgress,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                            )
                                        } else {
                                            androidx.compose.material3.LinearProgressIndicator(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }

                                    if (isDownloadComplete && downloadedFile != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextButton(
                                            onClick = { shareApkFile(context, downloadedFile!!) },
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Share,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Share APK / Install Manually")
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

const val PREFS_NAME = "settings"
const val KEY_AUTO_UPDATE_CHECK = "auto_update_check"
const val KEY_LAST_CHECKED_TIME = "last_checked_time"
const val KEY_BETA_UPDATES = "beta_updates"
const val KEY_UPDATE_AVAILABLE = "update_available"

fun getUpdateAvailableState(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(KEY_UPDATE_AVAILABLE, false)
}

fun saveUpdateAvailableState(context: Context, available: Boolean) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean(KEY_UPDATE_AVAILABLE, available).apply()
}

fun getAutoUpdateCheckSetting(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(KEY_AUTO_UPDATE_CHECK, true)
}

fun saveAutoUpdateCheckSetting(context: Context, enabled: Boolean) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean(KEY_AUTO_UPDATE_CHECK, enabled).apply()
}

const val KEY_UPDATE_NOTIFICATIONS = "update_notifications"

fun getUpdateNotificationsSetting(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(KEY_UPDATE_NOTIFICATIONS, true)
}

fun saveUpdateNotificationsSetting(context: Context, enabled: Boolean) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean(KEY_UPDATE_NOTIFICATIONS, enabled).apply()
}

fun saveLastCheckedTime(context: Context, timestamp: String) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPrefs.edit().putString(KEY_LAST_CHECKED_TIME, timestamp).apply()
}

fun getLastCheckedTime(context: Context): String {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPrefs.getString(KEY_LAST_CHECKED_TIME, "") ?: ""
}

fun getBetaUpdatesSetting(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(KEY_BETA_UPDATES, false)
}

fun saveBetaUpdatesSetting(context: Context, enabled: Boolean) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean(KEY_BETA_UPDATES, enabled).apply()
}

private fun formatGitHubDate(githubDate: String): String = try {
    val githubFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val displayFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, h:mm a")
    val dateTime = LocalDateTime.parse(githubDate, githubFormatter)
    dateTime.format(displayFormatter)
} catch (e: Exception) {
    githubDate
}

fun cleanVersionString(version: String): String {
    var clean = version.trim()
    val firstDigitOrDotIdx = clean.indexOfFirst { it.isDigit() || it == '.' }
    if (firstDigitOrDotIdx > 0) {
        clean = clean.substring(firstDigitOrDotIdx)
    }
    return clean
}

fun isNewerVersion(latestVersion: String, currentVersion: String): Boolean {
    val latestClean = cleanVersionString(latestVersion)
    val currentClean = cleanVersionString(currentVersion)

    val latestParts = latestClean.split(".").map { segment ->
        segment.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
    }
    val currentParts = currentClean.split(".").map { segment ->
        segment.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
    }

    for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
        val latest = latestParts.getOrElse(i) { 0 }
        val current = currentParts.getOrElse(i) { 0 }
        when {
            latest > current -> return true
            latest < current -> return false
        }
    }

    return false
}

fun selectBestApk(assets: JSONArray, currentVariant: String, currentAbi: String): JSONObject? {
    val candidates = mutableListOf<Pair<JSONObject, Int>>()
    for (i in 0 until assets.length()) {
        val asset = assets.getJSONObject(i)
        val name = asset.getString("name")
        if (!name.endsWith(".apk", ignoreCase = true)) continue
        if (name.contains("debug", ignoreCase = true)) continue

        var score = 100 // Base score

        val hasGms = name.contains("gms", ignoreCase = true)
        val hasFoss = name.contains("foss", ignoreCase = true)

        if (currentVariant.equals("gms", ignoreCase = true)) {
            if (hasGms) score += 20
            if (hasFoss) score -= 50
        } else if (currentVariant.equals("foss", ignoreCase = true)) {
            if (hasFoss) score += 20
            if (hasGms) score -= 50
        }

        val nameLower = name.lowercase()
        val abis = listOf("arm64", "armeabi", "x86", "x86_64")
        var assetAbi: String? = null
        for (abi in abis) {
            if (nameLower.contains(abi)) {
                assetAbi = abi
                break
            }
        }

        if (assetAbi != null) {
            if (assetAbi == currentAbi.lowercase()) {
                score += 10
            } else {
                score -= 80 // Incompatible architecture
            }
        } else {
            if (nameLower.contains("universal")) {
                score += 5
            }
        }
        candidates.add(asset to score)
    }

    return candidates.sortedByDescending { it.second }.firstOrNull()?.first
}

fun parseReleaseBodyToChangelog(body: String, context: Context): List<ChangelogSection> {
    val sections = mutableListOf<ChangelogSection>()
    var currentTitle = context.getString(R.string.changelog)
    var currentItems = mutableListOf<String>()

    val lines = body.split("\n")
    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue

        if (trimmed.startsWith("#")) {
            if (currentItems.isNotEmpty()) {
                sections.add(ChangelogSection(currentTitle, currentItems.toList()))
                currentItems.clear()
            }
            var title = trimmed
            while (title.startsWith("#")) {
                title = title.substring(1)
            }
            currentTitle = title.trim()
            if (currentTitle.isEmpty()) {
                currentTitle = context.getString(R.string.changelog)
            }
        } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("• ")) {
            val cleanItem = trimmed.substring(2).trim()
            if (cleanItem.isNotEmpty()) {
                currentItems.add(cleanItem)
            }
        } else if (trimmed.startsWith("-") || trimmed.startsWith("*")) {
            val cleanItem = trimmed.substring(1).trim()
            if (cleanItem.isNotEmpty()) {
                currentItems.add(cleanItem)
            }
        } else {
            currentItems.add(trimmed)
        }
    }

    if (currentItems.isNotEmpty()) {
        sections.add(ChangelogSection(currentTitle, currentItems.toList()))
    }

    return sections.ifEmpty {
        listOf(ChangelogSection(context.getString(R.string.changelog), listOf(body.trim())))
    }
}

fun saveLatestReleaseToCache(context: Context, jsonResponse: String) {
    try {
        val cacheData = JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
            put("response", jsonResponse)
        }
        context.openFileOutput("latest_release_cache.json", Context.MODE_PRIVATE).use {
            it.write(cacheData.toString().toByteArray())
        }
    } catch (e: Exception) {
        Timber.e(e, "Error saving latest release cache")
    }
}

fun loadLatestReleaseFromCache(context: Context): Pair<Long, String>? {
    return try {
        val cacheFile = File(context.filesDir, "latest_release_cache.json")
        if (!cacheFile.exists()) return null
        val cacheData = JSONObject(context.openFileInput("latest_release_cache.json").use { it.bufferedReader().readText() })
        val timestamp = cacheData.getLong("timestamp")
        val response = cacheData.getString("response")
        Pair(timestamp, response)
    } catch (e: Exception) {
        // Delete corrupt cache file
        try {
            context.deleteFile("latest_release_cache.json")
        } catch (delEx: Exception) {
            // ignore
        }
        null
    }
}

suspend fun checkForUpdate(
    context: Context,
    onSuccess: (tag: String, isAvailable: Boolean, changelog: List<ChangelogSection>, size: String, date: String, description: String?, imageUrl: String?, apkUrl: String?, isFromCache: Boolean) -> Unit,
    onError: () -> Unit,
    force: Boolean = false,
) {
    withContext(Dispatchers.IO) {
        val currentVersion = BuildConfig.VERSION_NAME
        val isBetaEnabled = getBetaUpdatesSetting(context)

        if (!force) {
            val cached = loadLatestReleaseFromCache(context)
            if (cached != null) {
                val age = System.currentTimeMillis() - cached.first
                if (age < 12 * 60 * 60 * 1000L) { // 12 hours freshness
                    try {
                        val json = cached.second
                        val targetRelease = if (json.trim().startsWith("[")) {
                            val array = JSONArray(json)
                            var found: JSONObject? = null
                            for (i in 0 until array.length()) {
                                val r = array.getJSONObject(i)
                                val isDraft = r.optBoolean("draft", false)
                                val isPrerelease = r.optBoolean("prerelease", false)
                                if (isDraft) continue
                                if (isPrerelease && !isBetaEnabled) continue
                                found = r
                                break
                            }
                            found
                        } else {
                            JSONObject(json)
                        }

                        if (targetRelease != null) {
                            val targetTagName = targetRelease.getString("tag_name")
                            val currentVersion = BuildConfig.VERSION_NAME
                            val shouldShow = isNewerVersion(targetTagName, currentVersion)

                            if (shouldShow) {
                                val tagWithPrefix = targetRelease.getString("tag_name")
                                val body = targetRelease.optString("body", "")
                                val changelogList = parseReleaseBodyToChangelog(body, context)

                                val publishedAt = targetRelease.getString("published_at")
                                val formattedReleaseDate = formatGitHubDate(publishedAt)
                                val assets = targetRelease.getJSONArray("assets")
                                val bestAsset = selectBestApk(assets, BuildConfig.FLAVOR_variant, BuildConfig.FLAVOR_abi)

                                if (bestAsset != null) {
                                    val apkSizeInBytes = bestAsset.getLong("size")
                                    val apkSizeInMB = String.format("%.1f", apkSizeInBytes / (1024.0 * 1024.0))
                                    val apkDownloadUrl = bestAsset.getString("browser_download_url")
                                    withContext(Dispatchers.Main) {
                                        onSuccess(tagWithPrefix, true, changelogList, apkSizeInMB, formattedReleaseDate, null, null, apkDownloadUrl, true)
                                    }
                                    return@withContext
                                }
                            }
                            withContext(Dispatchers.Main) {
                                onSuccess(currentVersion, false, emptyList(), "", "", null, null, null, true)
                            }
                            return@withContext
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing cached update")
                    }
                }
            }
        }

        try {
            val urlString = "https://api.github.com/repos/bharadwajsanket/juno/releases"
            val url = URL(urlString)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.setRequestProperty("User-Agent", "junomusic-Update-App")
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            if (connection.responseCode == 200) {
                val json = connection.inputStream.bufferedReader().use { it.readText() }
                saveLatestReleaseToCache(context, json)

                val array = JSONArray(json)
                var targetRelease: JSONObject? = null
                for (i in 0 until array.length()) {
                    val r = array.getJSONObject(i)
                    val isDraft = r.optBoolean("draft", false)
                    val isPrerelease = r.optBoolean("prerelease", false)
                    if (isDraft) continue
                    if (isPrerelease && !isBetaEnabled) continue
                    targetRelease = r
                    break
                }

                if (targetRelease != null) {
                    val currentVersion = BuildConfig.VERSION_NAME
                    val targetTagName = targetRelease.getString("tag_name")
                    val shouldShow = isNewerVersion(targetTagName, currentVersion)

                    if (shouldShow) {
                        val tagWithPrefix = targetRelease.getString("tag_name")
                        val displayTag = tagWithPrefix

                        val changelogList = mutableListOf<ChangelogSection>()
                        var description: String? = null
                        var imageUrl: String? = null
                        try {
                            val changelogUrl =
                                URL("https://github.com/bharadwajsanket/juno/releases/download/$tagWithPrefix/changelog.json")
                            val changelogConnection = changelogUrl.openConnection() as java.net.HttpURLConnection
                            changelogConnection.connectTimeout = 8000
                            changelogConnection.readTimeout = 8000
                            val changelogJson = changelogConnection.inputStream.bufferedReader().use { it.readText() }
                            val changelogData = JSONObject(changelogJson)

                            description = changelogData.optString("description").takeIf { it.isNotEmpty() }
                            imageUrl = changelogData.optString("image").takeIf { it.isNotEmpty() }

                            val changelogArray = changelogData.getJSONArray("changelog")
                            for (j in 0 until changelogArray.length()) {
                                val sectionObj = changelogArray.getJSONObject(j)
                                val title = sectionObj.getString("title")
                                val itemsArray = sectionObj.getJSONArray("items")
                                val itemsList = mutableListOf<String>()
                                for (k in 0 until itemsArray.length()) {
                                    itemsList.add(itemsArray.getString(k))
                                }
                                changelogList.add(ChangelogSection(title, itemsList))
                            }
                        } catch (e: Exception) {
                            val body = targetRelease.optString("body", "")
                            changelogList.addAll(parseReleaseBodyToChangelog(body, context))
                        }

                        val publishedAt = targetRelease.getString("published_at")
                        val formattedReleaseDate = formatGitHubDate(publishedAt)
                        val assets = targetRelease.getJSONArray("assets")

                        val bestAsset = selectBestApk(assets, BuildConfig.FLAVOR_variant, BuildConfig.FLAVOR_abi)
                        if (bestAsset != null) {
                            val apkSizeInBytes = bestAsset.getLong("size")
                            val apkSizeInMB = String.format("%.1f", apkSizeInBytes / (1024.0 * 1024.0))
                            val apkDownloadUrl = bestAsset.getString("browser_download_url")

                            withContext(Dispatchers.Main) {
                                onSuccess(displayTag, true, changelogList, apkSizeInMB, formattedReleaseDate, description, imageUrl, apkDownloadUrl, false)
                            }
                            return@withContext
                        }
                    }

                    withContext(Dispatchers.Main) {
                        onSuccess(currentVersion, false, emptyList(), "", "", null, null, null, false)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onSuccess(currentVersion, false, emptyList(), "", "", null, null, null, false)
                    }
                }
            } else {
                throw java.io.IOException("HTTP Error ${connection.responseCode}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking for updates: ${e.message}")
            val cached = loadLatestReleaseFromCache(context)
            if (cached != null) {
                try {
                    val json = cached.second
                    val targetRelease = if (json.trim().startsWith("[")) {
                        val array = JSONArray(json)
                        var found: JSONObject? = null
                        for (i in 0 until array.length()) {
                            val r = array.getJSONObject(i)
                            val isDraft = r.optBoolean("draft", false)
                            val isPrerelease = r.optBoolean("prerelease", false)
                            if (isDraft) continue
                            if (isPrerelease && !isBetaEnabled) continue
                            found = r
                            break
                        }
                        found
                    } else {
                        JSONObject(json)
                    }

                    if (targetRelease != null) {
                        val targetTagName = targetRelease.getString("tag_name")
                        val currentVersion = BuildConfig.VERSION_NAME
                        val shouldShow = isNewerVersion(targetTagName, currentVersion)
                        if (shouldShow) {
                            val tagWithPrefix = targetRelease.getString("tag_name")
                            val body = targetRelease.optString("body", "")
                            val changelogList = parseReleaseBodyToChangelog(body, context)

                            val publishedAt = targetRelease.getString("published_at")
                            val formattedReleaseDate = formatGitHubDate(publishedAt)
                            val assets = targetRelease.getJSONArray("assets")
                            val bestAsset = selectBestApk(assets, BuildConfig.FLAVOR_variant, BuildConfig.FLAVOR_abi)

                            if (bestAsset != null) {
                                val apkSizeInBytes = bestAsset.getLong("size")
                                val apkSizeInMB = String.format("%.1f", apkSizeInBytes / (1024.0 * 1024.0))
                                val apkDownloadUrl = bestAsset.getString("browser_download_url")
                                withContext(Dispatchers.Main) {
                                    onSuccess(tagWithPrefix, true, changelogList, apkSizeInMB, formattedReleaseDate, null, null, apkDownloadUrl, true)
                                }
                                return@withContext
                            }
                        }
                    }
                } catch (parseEx: Exception) {
                    Timber.e(parseEx, "Failed to parse fallback cache")
                }
            }

            withContext(Dispatchers.Main) {
                onError()
            }
        }
    }
}

fun String.extractUrls(): List<Pair<IntRange, String>> {
    val urlPattern = Pattern.compile(
        "(?:^|[\\s])((https?://|www\\.|pic\\.)[\\w-]+(\\.[\\w-]+)+([/?].*)?)"
    )
    val matcher = urlPattern.matcher(this)
    val urlList = mutableListOf<Pair<IntRange, String>>()

    while (matcher.find()) {
        val url = matcher.group(1)?.trim() ?: continue
        val range = IntRange(matcher.start(1), matcher.end(1) - 1)

        val fullUrl = if (url.startsWith("http")) url else "https://$url"
        urlList.add(range to fullUrl)
    }

    return urlList
}
