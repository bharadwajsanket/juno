

package bharadwajsanket.aether.music
import bharadwajsanket.aether.music.ui.screens.settings.RingtoneViewModel
import bharadwajsanket.aether.music.ui.component.RingtoneTrimmerDialog
import bharadwajsanket.aether.music.ui.component.RingtoneProgressDialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.widget.Toast
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import bharadwajsanket.aether.music.constants.AppBarHeight
import bharadwajsanket.aether.music.constants.AppLanguageKey
import bharadwajsanket.aether.music.constants.DarkModeKey
import bharadwajsanket.aether.music.constants.DefaultOpenTabKey
import bharadwajsanket.aether.music.constants.DisableScreenshotKey
import bharadwajsanket.aether.music.constants.DynamicThemeKey
import bharadwajsanket.aether.music.constants.OnboardingCompletedKey
import bharadwajsanket.aether.music.constants.EnableHighRefreshRateKey
import bharadwajsanket.aether.music.constants.FloatingToolbarBottomPadding
import bharadwajsanket.aether.music.constants.FloatingToolbarHorizontalPadding
import bharadwajsanket.aether.music.constants.ListenTogetherInTopBarKey
import bharadwajsanket.aether.music.constants.ListenTogetherUsernameKey
import bharadwajsanket.aether.music.constants.ENABLE_LISTEN_TOGETHER
import bharadwajsanket.aether.music.constants.MiniPlayerBottomSpacing
import bharadwajsanket.aether.music.constants.MiniPlayerHeight
import bharadwajsanket.aether.music.constants.NavigationBarAnimationSpec
import bharadwajsanket.aether.music.constants.NavigationBarHeight
import bharadwajsanket.aether.music.aethermusic.updater.checkForUpdate
import bharadwajsanket.aether.music.aethermusic.updater.getAutoUpdateCheckSetting
import bharadwajsanket.aether.music.aethermusic.updater.isNewerVersion
import bharadwajsanket.aether.music.aethermusic.updater.saveUpdateAvailableState
import bharadwajsanket.aether.music.aethermusic.updater.getUpdateNotificationsSetting
import bharadwajsanket.aether.music.aethermusic.UpdateNotificationHelper
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import bharadwajsanket.aether.music.constants.PauseListenHistoryKey
import bharadwajsanket.aether.music.constants.PauseSearchHistoryKey
import bharadwajsanket.aether.music.constants.PureBlackKey
import bharadwajsanket.aether.music.constants.SYSTEM_DEFAULT
import bharadwajsanket.aether.music.constants.SelectedThemeColorKey
import bharadwajsanket.aether.music.constants.StopMusicOnTaskClearKey
import bharadwajsanket.aether.music.constants.UseNewMiniPlayerDesignKey
import bharadwajsanket.aether.music.db.MusicDatabase
import bharadwajsanket.aether.music.db.entities.SearchHistory
import bharadwajsanket.aether.music.extensions.toEnum
import bharadwajsanket.aether.music.models.toMediaMetadata
import bharadwajsanket.aether.music.playback.DownloadUtil
import bharadwajsanket.aether.music.playback.MusicService
import bharadwajsanket.aether.music.playback.MusicService.MusicBinder
import bharadwajsanket.aether.music.playback.PlayerConnection
import bharadwajsanket.aether.music.playback.queues.YouTubeQueue
import bharadwajsanket.aether.music.ui.component.AppNavigationRail
import bharadwajsanket.aether.music.ui.component.BottomSheetMenu
import bharadwajsanket.aether.music.ui.component.BottomSheetPage
import bharadwajsanket.aether.music.ui.component.FloatingNavigationToolbar
import bharadwajsanket.aether.music.ui.component.LocalBottomSheetPageState
import bharadwajsanket.aether.music.ui.component.LocalMenuState
import bharadwajsanket.aether.music.ui.component.rememberBottomSheetState
import bharadwajsanket.aether.music.ui.component.shimmer.ShimmerTheme
import bharadwajsanket.aether.music.ui.menu.YouTubeSongMenu
import bharadwajsanket.aether.music.ui.player.BottomSheetPlayer
import bharadwajsanket.aether.music.ui.screens.Screens
import bharadwajsanket.aether.music.ui.screens.SettingDialoge
import bharadwajsanket.aether.music.ui.screens.WelcomeDialog
import bharadwajsanket.aether.music.ui.screens.navigationBuilder
import bharadwajsanket.aether.music.ui.screens.settings.DarkMode
import bharadwajsanket.aether.music.ui.screens.settings.NavigationTab
import bharadwajsanket.aether.music.ui.theme.ColorSaver
import bharadwajsanket.aether.music.ui.theme.DefaultThemeColor
import bharadwajsanket.aether.music.ui.theme.aetherMusicTheme
import bharadwajsanket.aether.music.ui.theme.extractThemeColor
import bharadwajsanket.aether.music.ui.utils.appBarScrollBehavior
import bharadwajsanket.aether.music.ui.utils.resetHeightOffset
import bharadwajsanket.aether.music.utils.SyncUtils
import bharadwajsanket.aether.music.utils.dataStore
import bharadwajsanket.aether.music.utils.get
import bharadwajsanket.aether.music.utils.rememberEnumPreference
import bharadwajsanket.aether.music.utils.rememberPreference
import bharadwajsanket.aether.music.utils.reportException
import bharadwajsanket.aether.music.utils.setAppLocale
import bharadwajsanket.aether.music.viewmodels.HomeViewModel
import com.valentinilk.shimmer.LocalShimmerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val ACTION_SEARCH = "bharadwajsanket.aether.music.action.SEARCH"
        private const val ACTION_LIBRARY = "bharadwajsanket.aether.music.action.LIBRARY"
    }

    @Inject
    lateinit var database: MusicDatabase

    @Inject
    lateinit var downloadUtil: DownloadUtil

    @Inject
    lateinit var syncUtils: SyncUtils

    @Inject
    lateinit var listenTogetherManager: bharadwajsanket.aether.music.listentogether.ListenTogetherManager

    private lateinit var navController: NavHostController
    private var pendingIntent: Intent? = null

    private var playerConnection by mutableStateOf<PlayerConnection?>(null)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is MusicBinder) {
                try {
                    playerConnection = PlayerConnection(this@MainActivity, service, database, lifecycleScope)
                    Timber.tag("MainActivity").d("PlayerConnection created successfully")
                    
                    listenTogetherManager.setPlayerConnection(playerConnection)
                } catch (e: Exception) {
                    Timber.tag("MainActivity").e(e, "Failed to create PlayerConnection")
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            
            listenTogetherManager.setPlayerConnection(null)
            playerConnection?.dispose()
            playerConnection = null
        }
    }

    override fun onStart() {
        super.onStart()

        
        
        
        bindService(
            Intent(this, MusicService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dataStore.get(StopMusicOnTaskClearKey, false) &&
            playerConnection?.isPlaying?.value == true &&
            isFinishing
        ) {
            stopService(Intent(this, MusicService::class.java))
            unbindService(serviceConnection)
            playerConnection = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::navController.isInitialized) {
            handleDeepLinkIntent(intent, navController)
        } else {
            pendingIntent = intent
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        WindowCompat.setDecorFitsSystemWindows(window, false)

        
        listenTogetherManager.initialize()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val locale = dataStore[AppLanguageKey]
                ?.takeUnless { it == SYSTEM_DEFAULT }
                ?.let { Locale.forLanguageTag(it) }
                ?: Locale.getDefault()
            setAppLocale(this, locale)
        }

        lifecycleScope.launch {
            dataStore.data
                .map { it[DisableScreenshotKey] ?: false }
                .distinctUntilChanged()
                .collectLatest {
                    if (it) {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE,
                        )
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
        }

        setContent {
            aethermusicApp(
                playerConnection = playerConnection,
                database = database,
                downloadUtil = downloadUtil,
                syncUtils = syncUtils,
            )
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun aethermusicApp(
        playerConnection: PlayerConnection?,
        database: MusicDatabase,
        downloadUtil: DownloadUtil,
        syncUtils: SyncUtils,
    ) {
        val enableDynamicTheme by rememberPreference(DynamicThemeKey, defaultValue = true)
        val enableHighRefreshRate by rememberPreference(EnableHighRefreshRateKey, defaultValue = true)
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            if (getAutoUpdateCheckSetting(context)) {
                
                delay(2000L)
                checkForUpdate(
                    context = context,
                    onSuccess = { latestVersion, isAvailable, _, _, _, _, _, _ ->
                        val currentVersion = BuildConfig.VERSION_NAME
                        Log.d("UpdateCheck", "Startup check success. Latest: $latestVersion, Current: $currentVersion, isAvailable: $isAvailable")
                        saveUpdateAvailableState(context, isAvailable)
                        
                        if (isAvailable && getUpdateNotificationsSetting(context)) {
                            Log.d("UpdateCheck", "Posting update notification for $latestVersion")
                            UpdateNotificationHelper.showUpdateNotification(context, latestVersion)
                        }
                    },
                    onError = {
                        Log.e("UpdateCheck", "Startup check failed")
                        
                    }
                )
            }
        }

        LaunchedEffect(enableHighRefreshRate) {
            val window = this@MainActivity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val layoutParams = window.attributes
                if (enableHighRefreshRate) {
                    layoutParams.preferredDisplayModeId = 0
                } else {
                    val modes = window.windowManager.defaultDisplay.supportedModes
                    val mode60 = modes.firstOrNull { kotlin.math.abs(it.refreshRate - 60f) < 1f }
                        ?: modes.minByOrNull { kotlin.math.abs(it.refreshRate - 60f) }

                    if (mode60 != null) {
                        layoutParams.preferredDisplayModeId = mode60.modeId
                    }
                }
                window.attributes = layoutParams
            } else {
                val params = window.attributes
                if (enableHighRefreshRate) {
                    params.preferredRefreshRate = 0f
                } else {
                    params.preferredRefreshRate = 60f
                }
                window.attributes = params
            }
        }

        val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }

        LaunchedEffect(useDarkTheme) {
            setSystemBarAppearance(useDarkTheme)
        }

        val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
        val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
            pureBlackEnabled && useDarkTheme
        }

        val (selectedThemeColorInt) = rememberPreference(SelectedThemeColorKey, defaultValue = DefaultThemeColor.toArgb())
        val selectedThemeColor = Color(selectedThemeColorInt)

        var themeColor by rememberSaveable(stateSaver = ColorSaver) {
            mutableStateOf(selectedThemeColor)
        }

        LaunchedEffect(selectedThemeColor) {
            if (!enableDynamicTheme) {
                themeColor = selectedThemeColor
            }
        }

        LaunchedEffect(playerConnection, enableDynamicTheme, selectedThemeColor) {
            val playerConnection = playerConnection
            if (!enableDynamicTheme || playerConnection == null) {
                themeColor = selectedThemeColor
                return@LaunchedEffect
            }

            playerConnection.service.currentMediaMetadata.collectLatest { song ->
                if (song?.thumbnailUrl != null) {
                    withContext(Dispatchers.IO) {
                        try {
                            val result = imageLoader.execute(
                                ImageRequest.Builder(this@MainActivity)
                                    .data(song.thumbnailUrl)
                                    .allowHardware(false)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .networkCachePolicy(CachePolicy.ENABLED)
                                    .crossfade(false)
                                    .build()
                            )
                            themeColor = result.image?.toBitmap()?.extractThemeColor() ?: selectedThemeColor
                        } catch (e: Exception) {
                            
                            themeColor = selectedThemeColor
                        }
                    }
                } else {
                    themeColor = selectedThemeColor
                }
            }
        }

        aetherMusicTheme(
            darkTheme = useDarkTheme,
            pureBlack = pureBlack,
            themeColor = themeColor,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (pureBlack) Color.Black else MaterialTheme.colorScheme.surface)
            ) {
                val focusManager = LocalFocusManager.current
                val density = LocalDensity.current
                val configuration = LocalWindowInfo.current
                val cutoutInsets = WindowInsets.displayCutout
                val windowsInsets = WindowInsets.systemBars
                val bottomInset = with(density) { windowsInsets.getBottom(density).toDp() }
                val bottomInsetDp = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

                val navController = rememberNavController()
                androidx.compose.runtime.DisposableEffect(navController) {
                    val hapticManager = bharadwajsanket.aether.music.utils.HapticManager.getInstance(applicationContext)
                    val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, _, _ ->
                        hapticManager.performTick()
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }
                val homeViewModel: HomeViewModel = hiltViewModel()
                val accountImageUrl by homeViewModel.accountImageUrl.collectAsState()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val (previousTab, setPreviousTab) = rememberSaveable { mutableStateOf("home") }

                val (listenTogetherInTopBar) = rememberPreference(ListenTogetherInTopBarKey, defaultValue = true)
                val navigationItems = remember { 
                    bharadwajsanket.aether.music.ui.component.NavigationItemsList(
                        listOf(
                            Screens.Home,
                            Screens.Search,
                            Screens.Library,
                            Screens.Settings
                        )
                    )
                }
                val (useNewMiniPlayerDesign) = rememberPreference(UseNewMiniPlayerDesignKey, defaultValue = true)
                val defaultOpenTab = remember {
                    dataStore[DefaultOpenTabKey].toEnum(defaultValue = NavigationTab.HOME)
                }
                val tabOpenedFromShortcut = remember {
                    when (intent?.action) {
                        ACTION_SEARCH -> NavigationTab.LIBRARY
                        ACTION_LIBRARY -> NavigationTab.SEARCH
                        else -> null
                    }
                }

                val topLevelScreens = remember {
                    listOf(
                        Screens.Home.route,
                        Screens.Library.route,
                        Screens.ListenTogether.route,
                        "settings",
                    )
                }

                val (query, onQueryChange) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
                    mutableStateOf(TextFieldValue())
                }

                val onSearch: (String) -> Unit = remember {
                    { searchQuery ->
                        if (searchQuery.isNotEmpty()) {
                            navController.navigate("search/${URLEncoder.encode(searchQuery, "UTF-8")}")

                            if (dataStore[PauseSearchHistoryKey] != true) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    database.query {
                                        insert(SearchHistory(query = searchQuery))
                                    }
                                }
                            }
                        }
                    }
                }

                
                val currentRoute by remember {
                    derivedStateOf { navBackStackEntry?.destination?.route }
                }

                val inSearchScreen by remember {
                    derivedStateOf { currentRoute?.startsWith("search/") == true }
                }
                val navigationItemRoutes = remember(navigationItems) {
                    navigationItems.items.map { it.route }.toSet()
                }

                val shouldShowNavigationBar = remember(currentRoute, navigationItemRoutes) {
                    currentRoute == null ||
                        navigationItemRoutes.contains(currentRoute) ||
                        currentRoute!!.startsWith("search/")
                }

                val isLandscape = configuration.containerDpSize.width > configuration.containerDpSize.height

                val showRail = isLandscape && !inSearchScreen

                val navPadding = if (shouldShowNavigationBar && !showRail) {
                    NavigationBarHeight + FloatingToolbarBottomPadding
                } else {
                    0.dp
                }

                val collapsedBound = remember(bottomInset, showRail, shouldShowNavigationBar, navPadding, useNewMiniPlayerDesign, maxHeight) {
                    bottomInset +
                        (if (!showRail && shouldShowNavigationBar) navPadding else 0.dp) +
                        (if (useNewMiniPlayerDesign) MiniPlayerBottomSpacing else 0.dp) +
                        MiniPlayerHeight
                }

                val playerBottomSheetState = rememberBottomSheetState(
                    dismissedBound = 0.dp,
                    collapsedBound = collapsedBound,
                    expandedBound = maxHeight,
                )

                val onShuffleClick: (() -> Unit)? = remember(playerConnection, playerBottomSheetState) {
                    playerConnection?.let { connection ->
                        {
                            if (playerBottomSheetState.isExpanded) {
                                playerBottomSheetState.collapseSoft()
                            }
                            connection.player.shuffleModeEnabled = !connection.player.shuffleModeEnabled
                        }
                    }
                }

                val onMusicRecognitionClick: (() -> Unit) = remember(navController, playerBottomSheetState) {
                    {
                        if (playerBottomSheetState.isExpanded) {
                            playerBottomSheetState.collapseSoft()
                        }
                        navController.navigate("recognition") {
                            launchSingleTop = true
                        }
                    }
                }

                val playerAwareWindowInsets = remember(
                    bottomInset,
                    shouldShowNavigationBar,
                    playerBottomSheetState.isDismissed,
                    showRail,
                ) {
                    var bottom = bottomInset
                    if (shouldShowNavigationBar && !showRail) {
                        bottom += NavigationBarHeight
                    }
                    if (!playerBottomSheetState.isDismissed) bottom += MiniPlayerHeight
                    windowsInsets
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .add(WindowInsets(top = AppBarHeight, bottom = bottom))
                }
                appBarScrollBehavior(
                    canScroll = {
                        !inSearchScreen &&
                            (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                    }
                )

                val topAppBarScrollBehavior = appBarScrollBehavior(
                    canScroll = {
                        !inSearchScreen &&
                            (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                    },
                )

                
                LaunchedEffect(navBackStackEntry) {
                    if (inSearchScreen) {
                        val searchQuery = withContext(Dispatchers.IO) {
                            val rawQuery = navBackStackEntry?.arguments?.getString("query")!!
                            try {
                                URLDecoder.decode(rawQuery, "UTF-8")
                            } catch (e: IllegalArgumentException) {
                                rawQuery
                            }
                        }
                        onQueryChange(
                            TextFieldValue(
                                searchQuery,
                                TextRange(searchQuery.length)
                            )
                        )
                    } else if (navigationItems.items.fastAny { it.route == navBackStackEntry?.destination?.route }) {
                        onQueryChange(TextFieldValue())
                    }

                    
                    if (navigationItems.items.fastAny { it.route == navBackStackEntry?.destination?.route }) {
                        if (navigationItems.items.fastAny { it.route == previousTab }) {
                            topAppBarScrollBehavior.state.resetHeightOffset()
                        }
                    }

                    topAppBarScrollBehavior.state.resetHeightOffset()

                    
                    navController.currentBackStackEntry?.destination?.route?.let {
                        setPreviousTab(it)
                    }
                }

                LaunchedEffect(playerConnection) {
                    val player = playerConnection?.player ?: return@LaunchedEffect
                    if (player.currentMediaItem == null) {
                        if (!playerBottomSheetState.isDismissed) {
                            playerBottomSheetState.dismiss()
                        }
                    } else {
                        if (playerBottomSheetState.isDismissed) {
                            playerBottomSheetState.collapseSoft()
                        }
                    }
                }

                DisposableEffect(playerConnection, playerBottomSheetState) {
                    val player = playerConnection?.player ?: return@DisposableEffect onDispose { }
                    val listener = object : Player.Listener {
                        override fun onMediaItemTransition(
                            mediaItem: MediaItem?,
                            reason: Int,
                        ) {
                            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED &&
                                mediaItem != null &&
                                playerBottomSheetState.isDismissed
                            ) {
                                playerBottomSheetState.collapseSoft()
                            }
                        }
                    }
                    player.addListener(listener)
                    onDispose {
                        player.removeListener(listener)
                    }
                }

                var shouldShowTopBar by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(navBackStackEntry, listenTogetherInTopBar) {
                    val currentRoute = navBackStackEntry?.destination?.route
                    val isListenTogetherScreen = currentRoute == Screens.ListenTogether.route || 
                        currentRoute == "listen_together_from_topbar"
                    shouldShowTopBar = currentRoute in topLevelScreens &&
                        currentRoute != "settings" &&
                        !(isListenTogetherScreen && listenTogetherInTopBar)
                }

                val coroutineScope = rememberCoroutineScope()
                var sharedSong: SongItem? by remember {
                    mutableStateOf(null)
                }
                val snackbarHostState = remember { SnackbarHostState() }
                var showSettingDialoge by remember { mutableStateOf(false) }

                val (_, setLastOpenedVersionCode) = rememberPreference(bharadwajsanket.aether.music.constants.LastOpenedVersionCodeKey, -1)
                val (_, setOnboardingCompleted) = rememberPreference(OnboardingCompletedKey, false)
                var showWelcomeDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    // Read BOTH flags synchronously from DataStore on the IO thread.
                    // This is the only safe way to avoid the race where rememberPreference
                    // starts at defaultValue=false/−1 before DataStore emits the real value,
                    // which caused the dialog to flash on every launch.
                    val storedOnboardingCompleted = withContext(Dispatchers.IO) {
                        applicationContext.dataStore.get(OnboardingCompletedKey, false)
                    }
                    val storedVersionCode = withContext(Dispatchers.IO) {
                        applicationContext.dataStore.get(bharadwajsanket.aether.music.constants.LastOpenedVersionCodeKey, -1)
                    }

                    when {
                        storedOnboardingCompleted -> {
                            // Already completed — never show again. Just update version code.
                        }
                        storedVersionCode > 0 -> {
                            // Existing install (update from pre-3.5.4) — auto-complete onboarding.
                            setOnboardingCompleted(true)
                        }
                        else -> {
                            // True fresh install (no version code ever stored).
                            showWelcomeDialog = true
                        }
                    }

                    // Always update version code on launch.
                    if (storedVersionCode < BuildConfig.VERSION_CODE) {
                        setLastOpenedVersionCode(BuildConfig.VERSION_CODE)
                    }
                }

                LaunchedEffect(Unit) {
                    if (pendingIntent != null) {
                        handleDeepLinkIntent(pendingIntent!!, navController)
                        pendingIntent = null
                    } else {
                        handleDeepLinkIntent(intent, navController)
                    }
                }

                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { intent ->
                        handleDeepLinkIntent(intent, navController)
                    }

                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }

                val currentTitle = when (navBackStackEntry?.destination?.route) {
                    Screens.Home.route -> "AETHER"
                    Screens.Search.route -> stringResource(R.string.search)
                    Screens.Library.route -> stringResource(R.string.filter_library)
                    Screens.ListenTogether.route -> stringResource(R.string.together)
                    else -> ""
                }



                val pauseListenHistory by rememberPreference(PauseListenHistoryKey, defaultValue = false)
                val eventCount by database.eventCount().collectAsState(initial = 0)
                val showHistoryButton = remember(pauseListenHistory, eventCount) {
                    !(pauseListenHistory && eventCount == 0)
                }

                val baseBg = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer

                val ringtoneViewModel: RingtoneViewModel = viewModel()
                val ringtoneUiState by ringtoneViewModel.uiState.collectAsState()

                CompositionLocalProvider(
                    LocalRingtoneViewModel provides ringtoneViewModel,
                    LocalDatabase provides database,
                    LocalContentColor provides if (pureBlack) Color.White else contentColorFor(MaterialTheme.colorScheme.surface),
                    LocalPlayerConnection provides playerConnection,
                    LocalPlayerAwareWindowInsets provides playerAwareWindowInsets,
                    LocalDownloadUtil provides downloadUtil,
                    LocalShimmerTheme provides ShimmerTheme,
                    LocalSyncUtils provides syncUtils,
                    LocalListenTogetherManager provides listenTogetherManager,
                ) {

                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            AnimatedVisibility(
                                visible = shouldShowTopBar,
                                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 200))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .background(if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer)
                                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                                        .windowInsetsPadding(
                                            if (showRail) {
                                                WindowInsets(left = NavigationBarHeight)
                                                    .add(cutoutInsets.only(WindowInsetsSides.Start))
                                            } else {
                                                cutoutInsets.only(WindowInsetsSides.Start + WindowInsetsSides.End)
                                            }
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = currentTitle,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = if (currentTitle == "AETHER") FontWeight.ExtraBold else FontWeight.Bold,
                                            fontSize = if (currentTitle == "AETHER") 22.sp else 20.sp,
                                            letterSpacing = if (currentTitle == "AETHER") 1.2.sp else 0.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        // 1. Listen Together / Community
                                        if (ENABLE_LISTEN_TOGETHER) {
                                            IconButton(
                                                onClick = { navController.navigate("listen_together_from_topbar") },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.group_outlined),
                                                    contentDescription = stringResource(R.string.together),
                                                    modifier = Modifier.size(20.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        // 2. Sync
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    syncUtils.performFullSync()
                                                    snackbarHostState.showSnackbar("Library sync started in background")
                                                }
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.refresh),
                                                contentDescription = "Sync",
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        // 3. Settings / Profile
                                        IconButton(
                                            onClick = { showSettingDialoge = true },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            if (accountImageUrl != null) {
                                                AsyncImage(
                                                    model = accountImageUrl,
                                                    contentDescription = stringResource(R.string.account),
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                )
                                            } else {
                                                Icon(
                                                    painter = painterResource(R.drawable.settings),
                                                    contentDescription = stringResource(R.string.account),
                                                    modifier = Modifier.size(20.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            val onNavItemClick: (Screens, Boolean) -> Unit = remember(navController, coroutineScope, topAppBarScrollBehavior, playerBottomSheetState) {
                                { screen: Screens, isSelected: Boolean ->
                                    if (playerBottomSheetState.isExpanded) {
                                        playerBottomSheetState.collapseSoft()
                                    }

                                    if (isSelected) {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                        coroutineScope.launch {
                                            topAppBarScrollBehavior.state.resetHeightOffset()
                                        }
                                    } else {
                                        if (screen == Screens.Home) {
                                            val popped = navController.popBackStack(Screens.Home.route, false)
                                            if (!popped) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        } else {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                            }

                            if (!showRail && currentRoute != "update" && currentRoute != "listen_together/chat") {
                                val navigationBarHeight by animateDpAsState(
                                    targetValue = if (shouldShowNavigationBar && !showRail) NavigationBarHeight else 0.dp,
                                    animationSpec = NavigationBarAnimationSpec,
                                    label = "navBarHeight",
                                )
                                Box {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )

                                    val navSlideDistance = bottomInset + FloatingToolbarBottomPadding + NavigationBarHeight

                                    val navOffsetY = if (navigationBarHeight == 0.dp) {
                                        navSlideDistance
                                    } else {
                                        val slideOffset =
                                            navSlideDistance * playerBottomSheetState.progress.coerceIn(0f, 1f)
                                        val hideOffset =
                                            navSlideDistance * (1 - navigationBarHeight.coerceAtMost(NavigationBarHeight) / NavigationBarHeight)
                                        slideOffset + hideOffset
                                    }

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .height(navSlideDistance)
                                            .graphicsLayer {
                                                translationY = navOffsetY.toPx()
                                            },
                                    ) {
                                        FloatingNavigationToolbar(
                                            items = navigationItems,
                                            pureBlack = pureBlack,
                                            isSelected = { screen ->
                                                currentRoute == screen.route || currentRoute?.startsWith("${screen.route}/") == true
                                            },
                                            onItemClick = onNavItemClick,
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(
                                                    start = FloatingToolbarHorizontalPadding,
                                                    end = FloatingToolbarHorizontalPadding,
                                                    bottom = bottomInset + FloatingToolbarBottomPadding,
                                                )
                                                .height(NavigationBarHeight)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .height(bottomInsetDp)
                                            
                                            .graphicsLayer {
                                                val progress = playerBottomSheetState.progress
                                                alpha = if (progress > 0f || (useNewMiniPlayerDesign && !shouldShowNavigationBar)) 0f else 1f
                                            }
                                            .background(baseBg)
                                    )
                                }
                            } else {
                                if (currentRoute != "update" && currentRoute != "listen_together/chat") {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .height(bottomInsetDp)
                                        
                                        .graphicsLayer {
                                            val progress = playerBottomSheetState.progress
                                            alpha = if (progress > 0f || (useNewMiniPlayerDesign && !shouldShowNavigationBar)) 0f else 1f
                                        }
                                        .background(baseBg)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                    ) {
                        Row(Modifier.fillMaxSize()) {
                            val onRailItemClick: (Screens, Boolean) -> Unit = remember(navController, coroutineScope, topAppBarScrollBehavior, playerBottomSheetState) {
                                { screen: Screens, isSelected: Boolean ->
                                    if (playerBottomSheetState.isExpanded) {
                                        playerBottomSheetState.collapseSoft()
                                    }

                                    if (isSelected) {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                        coroutineScope.launch {
                                            topAppBarScrollBehavior.state.resetHeightOffset()
                                        }
                                    } else {
                                        if (screen == Screens.Home) {
                                            val popped = navController.popBackStack(Screens.Home.route, false)
                                            if (!popped) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        } else {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                            }

                            val onRailSearchLongClick: () -> Unit = remember(navController) {
                                {
                                    navController.navigate("recognition") {
                                        launchSingleTop = true
                                    }
                                }
                            }

                            if (showRail && currentRoute != "update") {
                                AppNavigationRail(
                                    navigationItems = navigationItems,
                                    currentRoute = currentRoute,
                                    onItemClick = onRailItemClick,
                                    pureBlack = pureBlack,
                                    onSearchLongClick = onRailSearchLongClick
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                
                                NavHost(
                                    navController = navController,
                                    startDestination = when (tabOpenedFromShortcut ?: defaultOpenTab) {
                                        NavigationTab.HOME -> Screens.Home
                                        NavigationTab.LIBRARY -> Screens.Library
                                        else -> Screens.Home
                                    }.route,
                                    
                                    enterTransition = {
                                        val currentRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == targetState.destination.route
                                        }
                                        val previousRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == initialState.destination.route
                                        }

                                        if (currentRouteIndex == -1 || currentRouteIndex > previousRouteIndex)
                                            slideInHorizontally { it / 8 } + fadeIn(tween(200))
                                        else
                                            slideInHorizontally { -it / 8 } + fadeIn(tween(200))
                                    },
                                    
                                    exitTransition = {
                                        val currentRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == initialState.destination.route
                                        }
                                        val targetRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == targetState.destination.route
                                        }

                                        if (targetRouteIndex == -1 || targetRouteIndex > currentRouteIndex)
                                            slideOutHorizontally { -it / 8 } + fadeOut(tween(200))
                                        else
                                            slideOutHorizontally { it / 8 } + fadeOut(tween(200))
                                    },
                                    
                                    popEnterTransition = {
                                        val currentRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == targetState.destination.route
                                        }
                                        val previousRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == initialState.destination.route
                                        }

                                        if (previousRouteIndex != -1 && previousRouteIndex < currentRouteIndex)
                                            slideInHorizontally { it / 8 } + fadeIn(tween(200))
                                        else
                                            slideInHorizontally { -it / 8 } + fadeIn(tween(200))
                                    },
                                    
                                    popExitTransition = {
                                        val currentRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == initialState.destination.route
                                        }
                                        val targetRouteIndex = navigationItems.items.indexOfFirst {
                                            it.route == targetState.destination.route
                                        }

                                        if (currentRouteIndex != -1 && currentRouteIndex < targetRouteIndex)
                                            slideOutHorizontally { -it / 8 } + fadeOut(tween(200))
                                        else
                                            slideOutHorizontally { it / 8 } + fadeOut(tween(200))
                                    },
                                    modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                                ) {
                                    navigationBuilder(
                                        navController = navController,
                                        scrollBehavior = topAppBarScrollBehavior,
                                        activity = this@MainActivity,
                                        snackbarHostState = snackbarHostState
                                    )
                                }
                            }
                        }
                    }

                    BottomSheetMenu(
                        state = LocalMenuState.current,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )

                    BottomSheetPage(
                        state = LocalBottomSheetPageState.current,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )



                    sharedSong?.let { song ->
                        playerConnection?.let {
                            Dialog(
                                onDismissRequest = { sharedSong = null },
                                properties = DialogProperties(usePlatformDefaultWidth = false),
                            ) {
                                Surface(
                                    modifier = Modifier.padding(24.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = AlertDialogDefaults.containerColor,
                                    tonalElevation = AlertDialogDefaults.TonalElevation,
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        YouTubeSongMenu(
                                            song = song,
                                            navController = navController,
                                            onDismiss = { sharedSong = null },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val ringtoneUiState by ringtoneViewModel.uiState.collectAsState()
                    RingtoneTrimmerDialog(
                        isVisible = ringtoneUiState.showTrimmer,
                        songId = ringtoneUiState.targetSongId,
                        songTitle = ringtoneUiState.targetSongTitle,
                        duration = ringtoneUiState.targetSongDuration,
                        onDismiss = { ringtoneViewModel.hideTrimmer() },
                        onResolveStreamUrl = { ringtoneViewModel.getStreamUrl(this@MainActivity, it) },
                        onConfirm = { start, end -> ringtoneViewModel.setAsRingtone(this@MainActivity, start, end) }
                    )

                    if (ringtoneUiState.showProgress) {
                        RingtoneProgressDialog(
                            isVisible = ringtoneUiState.showProgress,
                            progress = ringtoneUiState.progress,
                            statusMessage = ringtoneUiState.statusMessage,
                            isComplete = ringtoneUiState.isComplete,
                            isSuccess = ringtoneUiState.isSuccess,
                            onDismiss = { ringtoneViewModel.dismissProgress() },
                            onOpenSettings = { ringtoneViewModel.openRingtoneSettings(this@MainActivity) }
                        )
                    }

                    if (showSettingDialoge) {
                        SettingDialoge(
                            onDismissRequest = { showSettingDialoge = false },
                            onNavigate = { route ->
                                showSettingDialoge = false
                                navController.navigate(route)
                            },
                            homeViewModel = homeViewModel
                        )
                    }

                    if (showWelcomeDialog) {
                        WelcomeDialog(
                            onDismissRequest = {
                                showWelcomeDialog = false
                            },
                            onComplete = {
                                // Persist the completion flag so onboarding never shows again
                                setOnboardingCompleted(true)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun handleDeepLinkIntent(intent: Intent, navController: NavHostController) {
        if (intent.getBooleanExtra("open_update_screen", false)) {
            intent.removeExtra("open_update_screen")
            navController.navigate("update")
            return
        }
        val uri = intent.data ?: intent.extras?.getString(Intent.EXTRA_TEXT)?.toUri() ?: return
        intent.data = null
        intent.removeExtra(Intent.EXTRA_TEXT)
        val coroutineScope = lifecycle.coroutineScope

        val listenCode = uri.getQueryParameter("code")
            ?: uri.getQueryParameter("room")
            ?: uri.pathSegments.getOrNull(1)
        val isListenLink = uri.pathSegments.firstOrNull() == "listen" || uri.host?.equals("listen", ignoreCase = true) == true
        if (!listenCode.isNullOrBlank() && isListenLink) {
            if (ENABLE_LISTEN_TOGETHER) {
                val username = dataStore.get(ListenTogetherUsernameKey, "").ifBlank { "Guest" }
                listenTogetherManager.joinRoom(listenCode, username)
            } else {
                Toast.makeText(this, "Listen Together is being upgraded and will return in a future update.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        when (val path = uri.pathSegments.firstOrNull()) {
            "playlist" -> uri.getQueryParameter("list")?.let { playlistId ->
                if (playlistId.startsWith("OLAK5uy_")) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.albumSongs(playlistId).onSuccess { songs ->
                            songs.firstOrNull()?.album?.id?.let { browseId ->
                                withContext(Dispatchers.Main) {
                                    navController.navigate("album/$browseId")
                                }
                            }
                        }.onFailure { reportException(it) }
                    }
                } else {
                    navController.navigate("online_playlist/$playlistId")
                }
            }

            "browse" -> uri.lastPathSegment?.let { browseId ->
                navController.navigate("album/$browseId")
            }

            "channel", "c" -> uri.lastPathSegment?.let { artistId ->
                navController.navigate("artist/$artistId")
            }

            "search" -> {
                uri.getQueryParameter("q")?.let {
                    navController.navigate("search/${URLEncoder.encode(it, "UTF-8")}")
                }
            }

            else -> {
                val videoId = when {
                    path == "watch" -> uri.getQueryParameter("v")
                    uri.host == "youtu.be" -> uri.pathSegments.firstOrNull()
                    else -> null
                }

                val playlistId = uri.getQueryParameter("list")

                if (videoId != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.queue(listOf(videoId), playlistId).onSuccess { queue ->
                            withContext(Dispatchers.Main) {
                                playerConnection?.playQueue(
                                    YouTubeQueue(
                                        WatchEndpoint(videoId = queue.firstOrNull()?.id, playlistId = playlistId),
                                        queue.firstOrNull()?.toMediaMetadata()
                                    )
                                )
                            }
                        }.onFailure {
                            reportException(it)
                        }
                    }
                } else if (playlistId != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.queue(null, playlistId).onSuccess { queue ->
                            val firstItem = queue.firstOrNull()
                            withContext(Dispatchers.Main) {
                                playerConnection?.playQueue(
                                    YouTubeQueue(
                                        WatchEndpoint(videoId = firstItem?.id, playlistId = playlistId),
                                        firstItem?.toMediaMetadata()
                                    )
                                )
                            }
                        }.onFailure {
                            reportException(it)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setSystemBarAppearance(isDark: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView.rootView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor = (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
    }
}

val LocalDatabase = staticCompositionLocalOf<MusicDatabase> { error("No database provided") }
val LocalRingtoneViewModel = compositionLocalOf<RingtoneViewModel> { error("No RingtoneViewModel provided") }

val LocalPlayerConnection = staticCompositionLocalOf<PlayerConnection?> { error("No PlayerConnection provided") }
val LocalPlayerAwareWindowInsets = compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }
val LocalDownloadUtil = staticCompositionLocalOf<DownloadUtil> { error("No DownloadUtil provided") }
val LocalSyncUtils = staticCompositionLocalOf<SyncUtils> { error("No SyncUtils provided") }
val LocalListenTogetherManager = staticCompositionLocalOf<bharadwajsanket.aether.music.listentogether.ListenTogetherManager?> { null }
val LocalIsPlayerExpanded = compositionLocalOf { false }
