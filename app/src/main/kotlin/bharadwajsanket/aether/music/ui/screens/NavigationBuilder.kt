

package bharadwajsanket.aether.music.ui.screens

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import bharadwajsanket.aether.music.constants.DarkModeKey
import bharadwajsanket.aether.music.constants.PureBlackKey
import bharadwajsanket.aether.music.ui.screens.artist.ArtistAlbumsScreen
import bharadwajsanket.aether.music.ui.screens.artist.ArtistItemsScreen
import bharadwajsanket.aether.music.ui.screens.artist.ArtistScreen
import bharadwajsanket.aether.music.ui.screens.artist.ArtistSongsScreen
import bharadwajsanket.aether.music.ui.screens.equalizer.EqScreen
import bharadwajsanket.aether.music.ui.screens.library.LibraryScreen
import bharadwajsanket.aether.music.ui.screens.library.LocalSongScreen
import bharadwajsanket.aether.music.ui.screens.playlist.AutoPlaylistScreen
import bharadwajsanket.aether.music.ui.screens.playlist.CachePlaylistScreen
import bharadwajsanket.aether.music.ui.screens.playlist.LocalPlaylistScreen
import bharadwajsanket.aether.music.ui.screens.playlist.OnlinePlaylistScreen
import bharadwajsanket.aether.music.ui.screens.playlist.TopPlaylistScreen
import bharadwajsanket.aether.music.ui.screens.search.OnlineSearchResult
import bharadwajsanket.aether.music.ui.screens.search.SearchScreen
import bharadwajsanket.aether.music.ui.screens.settings.AboutScreen
import bharadwajsanket.aether.music.ui.screens.settings.AppearanceSettings
import bharadwajsanket.aether.music.ui.screens.settings.BackupAndRestore
import bharadwajsanket.aether.music.ui.screens.settings.ContentSettings
import bharadwajsanket.aether.music.ui.screens.settings.DarkMode
import bharadwajsanket.aether.music.ui.screens.settings.PlayerSettings
import bharadwajsanket.aether.music.ui.screens.settings.PrivacySettings
import bharadwajsanket.aether.music.ui.screens.settings.HapticsSettings
import bharadwajsanket.aether.music.ui.screens.settings.RomanizationSettings
import bharadwajsanket.aether.music.ui.screens.settings.SettingsScreen
import bharadwajsanket.aether.music.ui.screens.settings.AccountSettingsScreen
import bharadwajsanket.aether.music.ui.screens.settings.StorageSettings
import bharadwajsanket.aether.music.ui.screens.settings.ThemeScreen
import bharadwajsanket.aether.music.ui.screens.settings.AiSettings
import bharadwajsanket.aether.music.ui.screens.settings.integrations.ListenTogetherSettings
import bharadwajsanket.aether.music.ui.screens.recognition.RecognitionScreen
import bharadwajsanket.aether.music.ui.screens.recognition.RecognitionHistoryScreen
import bharadwajsanket.aether.music.ui.screens.settings.UpdateSettings
import bharadwajsanket.aether.music.aethermusic.updater.UpdateScreen
import bharadwajsanket.aether.music.utils.rememberEnumPreference
import bharadwajsanket.aether.music.utils.rememberPreference
import bharadwajsanket.aether.music.aethermusic.changelog.ChangelogScreen
import bharadwajsanket.aether.music.aethermusic.commitscreen.CommitScreen
import bharadwajsanket.aether.music.ui.screens.equalizer.axion.AxionEqScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
    snackbarHostState: SnackbarHostState
) {
    composable(Screens.Home.route) {
        HomeScreen(navController = navController, snackbarHostState = snackbarHostState)
    }

    composable(Screens.Search.route) {
        val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
        val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }
        val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
            pureBlackEnabled && useDarkTheme
        }
        SearchScreen(
            navController = navController,
            pureBlack = pureBlack
        )
    }

    composable(Screens.Library.route) {
        LibraryScreen(navController)
    }

    composable(Screens.ListenTogether.route) {
        ListenTogetherScreen(navController, showTopBar = false)
    }

    composable(
        route = "listen_together_from_topbar",
    ) {
        ListenTogetherScreen(navController, showTopBar = true)
    }

    composable("listen_together/chat") {
        CommentTogetherScreen(navController)
    }

    composable("history") {
        HistoryScreen(navController)
    }

    composable("local_songs") {
        LocalSongScreen(navController)
    }

    composable("stats") {
        StatsScreen(navController)
    }

    composable("mood_and_genres") {
        MoodAndGenresScreen(navController, scrollBehavior)
    }

    composable("account") {
        AccountScreen(navController, scrollBehavior)
    }

    composable("new_release") {
        NewReleaseScreen(navController, scrollBehavior)
    }

    composable("charts_screen") {
        ChartsScreen(navController)
    }

    composable(
        route = "browse/{browseId}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
            }
        )
    ) {
        BrowseScreen(
            navController,
            scrollBehavior,
            it.arguments?.getString("browseId")
        )
    }

    composable(
        route = "search/{query}",
        arguments = listOf(
            navArgument("query") {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            fadeIn(tween(250))
        },
        exitTransition = {
            if (targetState.destination.route?.startsWith("search/") == true) {
                fadeOut(tween(200))
            } else {
                fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
            }
        },
        popEnterTransition = {
            if (initialState.destination.route?.startsWith("search/") == true) {
                fadeIn(tween(250))
            } else {
                fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
            }
        },
        popExitTransition = {
            fadeOut(tween(200))
        },
    ) {
        OnlineSearchResult(navController)
    }

    composable(
        route = "album/{albumId}",
        arguments = listOf(
            navArgument("albumId") {
                type = NavType.StringType
            },
        ),
    ) {
        AlbumScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/songs",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistSongsScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/albums",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            }
        )
    ) {
        ArtistAlbumsScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/items?browseId={browseId}?params={params}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        ArtistItemsScreen(navController, scrollBehavior)
    }

    composable(
        route = "online_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        OnlinePlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "local_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        LocalPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "auto_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        AutoPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "cache_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        CachePlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "top_playlist/{top}",
        arguments = listOf(
            navArgument("top") {
                type = NavType.StringType
            },
        ),
    ) {
        TopPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "youtube_browse/{browseId}?params={params}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        YouTubeBrowseScreen(navController)
    }

    composable("settings") {
        SettingsScreen(navController, scrollBehavior)
    }

    composable("settings/update") {
       UpdateSettings(navController, scrollBehavior)
    }

    composable("settings/account") {
        AccountSettingsScreen(navController, scrollBehavior)
    }

    composable("settings/appearance") {
        AppearanceSettings(navController, scrollBehavior, activity, snackbarHostState)
    }

    composable("settings/appearance/theme") {
        ThemeScreen(navController)
    }

    composable("settings/content") {
        ContentSettings(navController, scrollBehavior)
    }

    composable("settings/content/romanization") {
        RomanizationSettings(navController, scrollBehavior)
    }

    composable("settings/ai") {
        AiSettings(navController, scrollBehavior)
    }
    
    composable("settings/player") {
        PlayerSettings(navController, scrollBehavior)
    }

    composable(
        route = "settings/storage?autoOpenExportPicker={autoOpenExportPicker}",
        arguments = listOf(
            navArgument("autoOpenExportPicker") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { backStackEntry ->
        val autoOpenExportPicker =
            backStackEntry.arguments?.getBoolean("autoOpenExportPicker") ?: false
        StorageSettings(
            navController = navController,
            scrollBehavior = scrollBehavior,
            autoOpenExportPicker = autoOpenExportPicker,
        )
    }

    composable("settings/equalizer") {
        AxionEqScreen(onBackClick = { navController.navigateUp() })
    }

    composable("settings/privacy") {
        PrivacySettings(navController, scrollBehavior)
    }

    composable("settings/haptics") {
        HapticsSettings(navController, scrollBehavior)
    }

    composable("settings/backup_restore") {
        BackupAndRestore(navController, scrollBehavior)
    }

    composable("settings/spotify_import") {
        SpotifyImportScreen(navController)
    }

    composable(route = "settings/integrations/listen_together") {
        ListenTogetherSettings(navController, scrollBehavior)
    }

    composable("settings/about") {
        AboutScreen(navController, scrollBehavior)
    }

    composable("update") {
        UpdateScreen(navController)
    }

    composable("login") {
        LoginScreen(navController)
    }

    dialog("equalizer") {
        EqScreen(navController = navController)
    }

    composable("recognition") {
        RecognitionScreen(navController)
    }

    composable("recognition_history") {
        RecognitionHistoryScreen(navController)
    }
    composable("settings/changelog") {
        ChangelogScreen(navController,scrollBehavior)
    }
    composable("settings/commits") {
        CommitScreen(navController, scrollBehavior)
    }
}
