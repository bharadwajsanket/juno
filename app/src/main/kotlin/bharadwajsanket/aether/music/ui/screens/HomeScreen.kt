package bharadwajsanket.aether.music.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.request.ImageRequest
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem
import com.music.innertube.utils.completed
import com.music.innertube.utils.parseCookieString
import com.music.innertube.YouTube
import bharadwajsanket.aether.music.constants.GridItemSize
import bharadwajsanket.aether.music.constants.GridItemsSizeKey
import bharadwajsanket.aether.music.constants.GridThumbnailHeight
import bharadwajsanket.aether.music.constants.InnerTubeCookieKey
import bharadwajsanket.aether.music.constants.ListItemHeight
import bharadwajsanket.aether.music.constants.ListThumbnailSize
import bharadwajsanket.aether.music.constants.LocalProfileNameKey
import bharadwajsanket.aether.music.constants.RandomizeHomeOrderKey
import bharadwajsanket.aether.music.constants.ShowSpeedDialKey
import bharadwajsanket.aether.music.constants.SmallGridThumbnailHeight
import bharadwajsanket.aether.music.constants.ThumbnailCornerRadius
import bharadwajsanket.aether.music.ui.theme.AetherCorners
import bharadwajsanket.aether.music.db.entities.Album
import bharadwajsanket.aether.music.db.entities.Artist
import bharadwajsanket.aether.music.db.entities.LocalItem
import bharadwajsanket.aether.music.db.entities.Playlist
import bharadwajsanket.aether.music.db.entities.PlaylistEntity
import bharadwajsanket.aether.music.db.entities.PlaylistSongMap
import bharadwajsanket.aether.music.db.entities.Song
import bharadwajsanket.aether.music.extensions.toMediaItem
import bharadwajsanket.aether.music.LocalDatabase
import bharadwajsanket.aether.music.LocalPlayerAwareWindowInsets
import bharadwajsanket.aether.music.LocalPlayerConnection
import bharadwajsanket.aether.music.models.toMediaMetadata
import bharadwajsanket.aether.music.playback.queues.ListQueue
import bharadwajsanket.aether.music.playback.queues.LocalAlbumRadio
import bharadwajsanket.aether.music.playback.queues.YouTubeAlbumRadio
import bharadwajsanket.aether.music.playback.queues.YouTubeQueue
import bharadwajsanket.aether.music.R
import bharadwajsanket.aether.music.ui.component.AlbumGridItem
import bharadwajsanket.aether.music.ui.component.ArtistGridItem
import bharadwajsanket.aether.music.ui.component.HideOnScrollFAB
import bharadwajsanket.aether.music.ui.component.LocalBottomSheetPageState
import bharadwajsanket.aether.music.ui.component.LocalMenuState
import bharadwajsanket.aether.music.ui.component.NavigationTitle
import bharadwajsanket.aether.music.ui.component.RandomizeGridItem
import bharadwajsanket.aether.music.ui.component.shimmer.GridItemPlaceHolder
import bharadwajsanket.aether.music.ui.component.shimmer.ShimmerHost
import bharadwajsanket.aether.music.ui.component.shimmer.TextPlaceholder
import bharadwajsanket.aether.music.ui.component.SongGridItem
import bharadwajsanket.aether.music.ui.component.SongListItem
import bharadwajsanket.aether.music.ui.component.SpeedDialGridItem
import bharadwajsanket.aether.music.ui.component.YouTubeGridItem
import bharadwajsanket.aether.music.ui.component.YouTubeListItem
import bharadwajsanket.aether.music.ui.menu.AlbumMenu
import bharadwajsanket.aether.music.ui.menu.ArtistMenu
import bharadwajsanket.aether.music.ui.menu.SongMenu
import bharadwajsanket.aether.music.ui.menu.YouTubeAlbumMenu
import bharadwajsanket.aether.music.ui.menu.YouTubeArtistMenu
import bharadwajsanket.aether.music.ui.menu.YouTubePlaylistMenu
import bharadwajsanket.aether.music.ui.menu.YouTubeSongMenu
import bharadwajsanket.aether.music.ui.utils.SnapLayoutInfoProvider
import bharadwajsanket.aether.music.ui.utils.resize
import bharadwajsanket.aether.music.utils.listItemShape
import bharadwajsanket.aether.music.utils.rememberEnumPreference
import bharadwajsanket.aether.music.utils.rememberPreference
import bharadwajsanket.aether.music.viewmodels.CommunityPlaylistItem
import bharadwajsanket.aether.music.viewmodels.HomeViewModel
import kotlin.math.min
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import bharadwajsanket.aether.music.viewmodels.DailyDiscoverItem


sealed class HomeSection(val id: String, val baseWeight: Int) {
    data object ContinueListening : HomeSection("continue_listening", 1000)
    data object SpeedDial : HomeSection("speed_dial", 100)
    data object RecentlyPlayed : HomeSection("recently_played", 95)
    data object QuickPicks : HomeSection("quick_picks", 90)
    data object DailyDiscover : HomeSection("daily_discover", 80)
    data object KeepListening : HomeSection("keep_listening", 50)
    data object AccountPlaylists : HomeSection("account_playlists", 40)
    data object ForgottenFavorites : HomeSection("forgotten_favorites", 30)
    data object Favorites : HomeSection("favorites", 28)
    data object FromTheCommunity : HomeSection("from_the_community", 20)
    data class SimilarRecommendation(val index: Int) : HomeSection("similar_recommendation_$index", 10)
    data class HomePageSection(val index: Int) : HomeSection("home_page_section_$index", 10)
    data object MoodAndGenres : HomeSection("mood_and_genres", 5)
}

@Composable
fun CommunityPlaylistCard(
    item: CommunityPlaylistItem,
    onClick: () -> Unit,
    onSongClick: (SongItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    val containerColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val dbPlaylist by remember(item.playlist.id) { database.playlistByBrowseId(item.playlist.id) }.collectAsState(initial = null)
    val isBookmarked = dbPlaylist?.playlist?.bookmarkedAt != null

    Card(
        modifier = modifier
            .width(320.dp)
            .height(420.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(24.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = item.songs.getOrNull(0)?.thumbnail?.resize(544, 544),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                            AsyncImage(
                                model = item.songs.getOrNull(1)?.thumbnail?.resize(544, 544),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                        Row(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = item.songs.getOrNull(2)?.thumbnail?.resize(544, 544),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                            AsyncImage(
                                model = item.songs.getOrNull(3)?.thumbnail?.resize(544, 544),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.playlist.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.playlist.author?.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                item.songs.take(3).forEach { song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .combinedClickable(onClick = { onSongClick(song) }),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = song.thumbnail.resize(544, 544),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                text = song.artists.joinToString(", ") { it.name },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = {
                        item.playlist.playEndpoint?.let {
                            playerConnection?.playQueue(YouTubeQueue(it))
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_widget_play),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        item.playlist.radioEndpoint?.let {
                            playerConnection?.playQueue(YouTubeQueue(it))
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.radio),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            if (dbPlaylist?.playlist == null) {
                                database.transaction {
                                    val playlistEntity = PlaylistEntity(
                                        name = item.playlist.title,
                                        browseId = item.playlist.id,
                                        thumbnailUrl = item.playlist.thumbnail,
                                        remoteSongCount = item.playlist.songCountText?.split(" ")?.firstOrNull()?.toIntOrNull(),
                                        playEndpointParams = item.playlist.playEndpoint?.params,
                                        shuffleEndpointParams = item.playlist.shuffleEndpoint?.params,
                                        radioEndpointParams = item.playlist.radioEndpoint?.params
                                    ).toggleLike()
                                    insert(playlistEntity)
                                    scope.launch(Dispatchers.IO) {
                                        item.songs.ifEmpty {
                                            YouTube.playlist(item.playlist.id).completed()
                                                .getOrNull()?.songs.orEmpty()
                                        }.map { it.toMediaMetadata() }
                                            .onEach(::insert)
                                            .mapIndexed { index, song ->
                                                PlaylistSongMap(
                                                    songId = song.id,
                                                    playlistId = playlistEntity.id,
                                                    position = index,
                                                    setVideoId = song.setVideoId
                                                )
                                            }
                                            .forEach(::insert)
                                    }
                                }
                            } else {
                                database.transaction {
                                    val currentPlaylist = dbPlaylist!!.playlist
                                    update(currentPlaylist.toggleLike())
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(if (isBookmarked) R.drawable.library_add_check else R.drawable.library_add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DailyDiscoverCard(
    dailyDiscover: bharadwajsanket.aether.music.viewmodels.DailyDiscoverItem,
    onClick: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val database = LocalDatabase.current
    val playCount by remember(dailyDiscover.recommendation.id) { database.getLifetimePlayCount(dailyDiscover.recommendation.id) }.collectAsState(initial = 0)
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current

    val song = dailyDiscover.recommendation as? SongItem
    val playsString = stringResource(R.string.plays)

    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .clip(AetherCorners.xl)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (song != null) {
                        menuState.show {
                            YouTubeSongMenu(
                                song = song,
                                navController = navController,
                                onDismiss = { menuState.dismiss() }
                            )
                        }
                    }
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        shape = AetherCorners.xl
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(dailyDiscover.recommendation.thumbnail?.resize(1200, 1200))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )

            if (maxWidth > 200.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = dailyDiscover.recommendation.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = buildString {
                                append((dailyDiscover.recommendation as? SongItem)?.artists?.joinToString(", ") { it.name } ?: "")
                                if (playCount > 0) {
                                    append(" • $playCount $playsString")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    val messages = listOf(
                        R.string.daily_discover_sounds_like,
                        R.string.daily_discover_because_you_listen_to,
                        R.string.daily_discover_similar_to,
                        R.string.daily_discover_based_on,
                        R.string.daily_discover_for_fans_of
                    )
                    val messageRes = remember(dailyDiscover.seed.id) {
                        messages[kotlin.math.abs(dailyDiscover.seed.id.hashCode()) % messages.size]
                    }

                    Text(
                        text = stringResource(messageRes, "${dailyDiscover.seed.title} • ${dailyDiscover.seed.artists.joinToString(", ") { it.name }}"),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}




@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val haptic = LocalHapticFeedback.current

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val quickPicks by viewModel.quickPicks.collectAsState()
    val forgottenFavorites by viewModel.forgottenFavorites.collectAsState()
    val keepListening by viewModel.keepListening.collectAsState()
    val similarRecommendations by viewModel.similarRecommendations.collectAsState()
    val accountPlaylists by viewModel.accountPlaylists.collectAsState()
    val homePage by viewModel.homePage.collectAsState()
    val explorePage by viewModel.explorePage.collectAsState()
    val dailyDiscover by viewModel.dailyDiscover.collectAsState()
    val communityPlaylists by viewModel.communityPlaylists.collectAsState()
    // v3.5.0: new home sections
    val recentlyPlayedSongs by viewModel.recentlyPlayedSongs.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()

    val allLocalItems by viewModel.allLocalItems.collectAsState()
    val allYtItems by viewModel.allYtItems.collectAsState()
    val speedDialItems by viewModel.speedDialItems.collectAsState()
    val selectedChip by viewModel.selectedChip.collectAsState()

    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val isMoodAndGenresLoading = isLoading && explorePage?.moodAndGenres == null
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isRandomizing by viewModel.isRandomizing.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    val quickPicksLazyGridState = rememberLazyGridState()
    val forgottenFavoritesLazyGridState = rememberLazyGridState()

    val accountName by viewModel.accountName.collectAsState()
    val accountImageUrl by viewModel.accountImageUrl.collectAsState()
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val (randomizeHomeOrder) = rememberPreference(RandomizeHomeOrderKey, true)
    val (showSpeedDial) = rememberPreference(ShowSpeedDialKey, true)
    val (localProfileName) = rememberPreference(LocalProfileNameKey, "")
    val greeting = remember(localProfileName) {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greetingText = when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
        if (localProfileName.isNotBlank()) {
            "$greetingText, $localProfileName"
        } else {
            greetingText
        }
    }


    val isLoggedIn = remember(innerTubeCookie) {
        "SAPISID" in parseCookieString(innerTubeCookie)
    }
    val url = if (isLoggedIn) accountImageUrl else null

    val scope = rememberCoroutineScope()
    
    var randomizeJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    val lazylistState = rememberLazyListState()
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)
    val currentGridHeight = if (gridItemSize == GridItemSize.BIG) GridThumbnailHeight else SmallGridThumbnailHeight
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop =
        backStackEntry?.savedStateHandle?.getStateFlow("scrollToTop", false)?.collectAsState()


    var randomSeed by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            randomSeed = System.currentTimeMillis()
        }
    }

    val foundInSettings = stringResource(R.string.found_in_settings_content)

    LaunchedEffect(scrollToTop?.value) {
        if (scrollToTop?.value == true) {
            lazylistState.animateScrollToItem(0)
            backStackEntry?.savedStateHandle?.set("scrollToTop", false)
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { lazylistState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val len = lazylistState.layoutInfo.totalItemsCount
                if (lastVisibleIndex != null && lastVisibleIndex >= len - 3) {
                    viewModel.loadMoreYouTubeItems(homePage?.continuation)
                }
            }
    }

    NetworkReload(
        onReload = viewModel::refresh
    )

    if (selectedChip != null) {
        BackHandler {
            
            viewModel.toggleChip(selectedChip)
        }
    }

    val localGridItem: @Composable (LocalItem) -> Unit = {
        when (it) {
            is Song -> SongGridItem(
                song = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            if (it.id == mediaMetadata?.id) {
                                playerConnection.togglePlayPause()
                            } else {
                                playerConnection.playQueue(
                                    YouTubeQueue.radio(it.toMediaMetadata()),
                                )
                            }
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(
                                HapticFeedbackType.LongPress,
                            )
                            menuState.show {
                                SongMenu(
                                    originalSong = it,
                                    navController = navController,
                                    onDismiss = menuState::dismiss,
                                )
                            }
                        },
                    ),
                isActive = it.id == mediaMetadata?.id,
                isPlaying = isPlaying,
            )

            is Album -> AlbumGridItem(
                album = it,
                isActive = it.id == mediaMetadata?.album?.id,
                isPlaying = isPlaying,
                coroutineScope = scope,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            navController.navigate("album/${it.id}")
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            menuState.show {
                                AlbumMenu(
                                    originalAlbum = it,
                                    navController = navController,
                                    onDismiss = menuState::dismiss
                                )
                            }
                        }
                    )
            )

            is Artist -> ArtistGridItem(
                artist = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            navController.navigate("artist/${it.id}")
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(
                                HapticFeedbackType.LongPress,
                            )
                            menuState.show {
                                ArtistMenu(
                                    originalArtist = it,
                                    coroutineScope = scope,
                                    onDismiss = menuState::dismiss,
                                )
                            }
                        },
                    ),
            )

            is Playlist -> {}
        }
    }

    val ytGridItem: @Composable (YTItem) -> Unit = { item ->
        YouTubeGridItem(
            item = item,
            isActive = item.id in listOf(mediaMetadata?.album?.id, mediaMetadata?.id),
            isPlaying = isPlaying,
            coroutineScope = scope,
            thumbnailRatio = 1f,
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        when (item) {
                            is SongItem -> playerConnection.playQueue(
                                YouTubeQueue(
                                    item.endpoint ?: WatchEndpoint(
                                        videoId = item.id
                                    ), item.toMediaMetadata()
                                )
                            )

                            is AlbumItem -> navController.navigate("album/${item.id}")
                            is ArtistItem -> navController.navigate("artist/${item.id}")
                            is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                        }
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuState.show {
                            when (item) {
                                is SongItem -> YouTubeSongMenu(
                                    song = item,
                                    navController = navController,
                                    onDismiss = menuState::dismiss
                                )

                                is AlbumItem -> YouTubeAlbumMenu(
                                    albumItem = item,
                                    navController = navController,
                                    onDismiss = menuState::dismiss
                                )

                                is ArtistItem -> YouTubeArtistMenu(
                                    artist = item,
                                    onDismiss = menuState::dismiss
                                )

                                is PlaylistItem -> YouTubePlaylistMenu(
                                    playlist = item,
                                    coroutineScope = scope,
                                    onDismiss = menuState::dismiss
                                )
                            }
                        }
                    }
                )
        )
    }

    val homeSections = remember(
        randomizeHomeOrder,
        randomSeed,
        speedDialItems,
        quickPicks,
        dailyDiscover,
        keepListening,
        recentlyPlayedSongs,
        favoriteSongs,
        accountPlaylists,
        forgottenFavorites,
        communityPlaylists,
        similarRecommendations,
        homePage?.sections,
        explorePage?.moodAndGenres
    ) {
        val list = mutableListOf<HomeSection>()

        // v3.5.4: SpeedDial is first — appears directly under greeting, no gap
        if (showSpeedDial && speedDialItems.isNotEmpty()) list.add(HomeSection.SpeedDial)
        if (keepListening?.isNotEmpty() == true) list.add(HomeSection.ContinueListening)
        if (recentlyPlayedSongs.isNotEmpty()) list.add(HomeSection.RecentlyPlayed)
        if (accountPlaylists?.isNotEmpty() == true) list.add(HomeSection.AccountPlaylists)
        if (favoriteSongs.isNotEmpty()) list.add(HomeSection.Favorites)

        // Secondary/network sections
        if (quickPicks?.isNotEmpty() == true) list.add(HomeSection.QuickPicks)
        if (communityPlaylists?.isNotEmpty() == true) list.add(HomeSection.FromTheCommunity)
        if (dailyDiscover?.isNotEmpty() == true) list.add(HomeSection.DailyDiscover)
        if (forgottenFavorites?.isNotEmpty() == true) list.add(HomeSection.ForgottenFavorites)

        similarRecommendations?.indices?.forEach { i ->
            list.add(HomeSection.SimilarRecommendation(i))
        }

        homePage?.sections?.indices?.forEach { i ->
            list.add(HomeSection.HomePageSection(i))
        }

        if (explorePage?.moodAndGenres != null) list.add(HomeSection.MoodAndGenres)

        // v3.5.0: Priority sections are always first (added at top of list above).
        // Only randomize/sort the secondary sections.
        val prioritySections = list.filter { section ->
            section is HomeSection.SpeedDial ||
            section is HomeSection.ContinueListening ||
            section is HomeSection.RecentlyPlayed ||
            section is HomeSection.AccountPlaylists ||
            section is HomeSection.Favorites
        }
        val secondarySections = list.filter { section -> section !in prioritySections }

        val sortedSecondary = if (randomizeHomeOrder) {
            secondarySections.sortedByDescending { section ->
                val sectionRandom = Random(randomSeed + section.id.hashCode())
                val base = when (section) {
                    HomeSection.QuickPicks -> 500
                    HomeSection.DailyDiscover -> 450
                    HomeSection.ForgottenFavorites, HomeSection.FromTheCommunity -> 300
                    else -> 100
                }
                val modifier = when (section) {
                    HomeSection.QuickPicks -> 0
                    else -> sectionRandom.nextInt(-100, 200)
                }
                base + modifier
            }
        } else {
            val defaultOrder = mapOf(
                HomeSection.DailyDiscover to 600,
                HomeSection.QuickPicks to 500,
                HomeSection.ForgottenFavorites to 400,
                HomeSection.FromTheCommunity to 350,
                HomeSection.KeepListening to 300,
                HomeSection.MoodAndGenres to 100
            )
            secondarySections.sortedByDescending { section ->
                when (section) {
                    is HomeSection.SimilarRecommendation -> 200 - section.index
                    is HomeSection.HomePageSection -> 150 - section.index
                    else -> defaultOrder[section] ?: 0
                }
            }
        }

        prioritySections + sortedSecondary
    }

    LaunchedEffect(quickPicks) {
        quickPicksLazyGridState.scrollToItem(0)
    }

    LaunchedEffect(forgottenFavorites) {
        forgottenFavoritesLazyGridState.scrollToItem(0)
    }

    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                state = pullRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(LocalPlayerAwareWindowInsets.current.asPaddingValues()),
            )
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            val horizontalLazyGridItemWidthFactor = if (maxWidth * 0.475f >= 320.dp) 0.475f else 0.9f
            val horizontalLazyGridItemWidth = maxWidth * horizontalLazyGridItemWidthFactor
            val quickPicksSnapLayoutInfoProvider = remember(quickPicksLazyGridState) {
                SnapLayoutInfoProvider(
                    lazyGridState = quickPicksLazyGridState,
                    positionInLayout = { layoutSize, itemSize ->
                        (layoutSize * horizontalLazyGridItemWidthFactor / 2f - itemSize / 2f)
                    }
                )
            }
            val forgottenFavoritesSnapLayoutInfoProvider = remember(forgottenFavoritesLazyGridState) {
                SnapLayoutInfoProvider(
                    lazyGridState = forgottenFavoritesLazyGridState,
                    positionInLayout = { layoutSize, itemSize ->
                        (layoutSize * horizontalLazyGridItemWidthFactor / 2f - itemSize / 2f)
                    }
                )
            }

            LazyColumn(
                state = lazylistState,
                contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
            ) {
                item(key = "greeting_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }


                homeSections.forEach { section ->
                    when (section) {

                        // ── v3.5.0 NEW: Continue Listening (hero section) ──────────────────
                        HomeSection.ContinueListening -> {
                            keepListening?.takeIf { it.isNotEmpty() }?.let { items ->
                                item(key = "continue_listening_title") {
                                    NavigationTitle(
                                        title = "Continue Listening",
                                        modifier = Modifier.animateItem()
                                    )
                                }
                                item(key = "continue_listening_list") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.animateItem()
                                    ) {
                                        items(items, key = { it.id }) { item ->
                                            Box(
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .height(160.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .combinedClickable(
                                                        onClick = {
                                                            when (item) {
                                                                is Song -> {
                                                                    if (item.id == mediaMetadata?.id) {
                                                                        playerConnection.togglePlayPause()
                                                                    } else {
                                                                        playerConnection.playQueue(YouTubeQueue.radio(item.toMediaMetadata()))
                                                                    }
                                                                }
                                                                is Album -> navController.navigate("album/${item.id}")
                                                                is Artist -> navController.navigate("artist/${item.id}")
                                                                is Playlist -> {}
                                                            }
                                                        },
                                                        onLongClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.show {
                                                                when (item) {
                                                                    is Song -> SongMenu(originalSong = item, navController = navController, onDismiss = menuState::dismiss)
                                                                    is Album -> AlbumMenu(originalAlbum = item, navController = navController, onDismiss = menuState::dismiss)
                                                                    is Artist -> ArtistMenu(originalArtist = item, coroutineScope = scope, onDismiss = menuState::dismiss)
                                                                    is Playlist -> {}
                                                                }
                                                            }
                                                        }
                                                    )
                                            ) {
                                                // Artwork
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(item.thumbnailUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                // Gradient overlay
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Brush.verticalGradient(
                                                                colors = listOf(
                                                                    Color.Transparent,
                                                                    Color.Black.copy(alpha = 0.15f),
                                                                    Color.Black.copy(alpha = 0.7f),
                                                                    Color.Black.copy(alpha = 0.88f)
                                                                )
                                                            )
                                                        )
                                                )
                                                // Playing indicator
                                                val itemIsActive = item.id == mediaMetadata?.id
                                                if (itemIsActive && isPlaying) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .padding(8.dp)
                                                            .size(26.dp)
                                                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.volume_up),
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.onPrimary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                                // Text info
                                                Column(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                                ) {
                                                    Text(
                                                        text = item.title,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.White,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    val subtitle = when (item) {
                                                        is Song -> item.artists.joinToString { it.name }
                                                        is Album -> item.artists.joinToString { it.name }
                                                        is Artist -> "Artist"
                                                        is Playlist -> item.playlist.name
                                                    }
                                                    Text(
                                                        text = subtitle,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.White.copy(alpha = 0.72f),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── v3.5.0 NEW: Recently Played ───────────────────────────────────
                        HomeSection.RecentlyPlayed -> {
                            recentlyPlayedSongs.takeIf { it.isNotEmpty() }?.let { songs ->
                                item(key = "recently_played_title") {
                                    val rp = "Recently Played"
                                    NavigationTitle(
                                        title = rp,
                                        modifier = Modifier.animateItem(),
                                        onPlayAllClick = {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = rp,
                                                    items = songs.map { it.toMediaMetadata().toMediaItem() }
                                                )
                                            )
                                        }
                                    )
                                }
                                item(key = "recently_played_list") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.animateItem()
                                    ) {
                                        items(songs, key = { it.id }) { song ->
                                            SongGridItem(
                                                song = song,
                                                isActive = song.id == mediaMetadata?.id,
                                                isPlaying = isPlaying,
                                                modifier = Modifier
                                                    .width(currentGridHeight)
                                                    .combinedClickable(
                                                        onClick = {
                                                            if (song.id == mediaMetadata?.id) {
                                                                playerConnection.togglePlayPause()
                                                            } else {
                                                                playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                                                            }
                                                        },
                                                        onLongClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.show {
                                                                SongMenu(originalSong = song, navController = navController, onDismiss = menuState::dismiss)
                                                            }
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── v3.5.0 NEW: Favorites ─────────────────────────────────────────
                        HomeSection.Favorites -> {
                            favoriteSongs.takeIf { it.isNotEmpty() }?.let { songs ->
                                item(key = "favorites_title") {
                                    val favTitle = "Your Favorites"
                                    NavigationTitle(
                                        title = favTitle,
                                        modifier = Modifier.animateItem(),
                                        onPlayAllClick = {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = favTitle,
                                                    items = songs.map { it.toMediaMetadata().toMediaItem() }
                                                )
                                            )
                                        }
                                    )
                                }
                                item(key = "favorites_list") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.animateItem()
                                    ) {
                                        items(songs, key = { it.id }) { song ->
                                            SongGridItem(
                                                song = song,
                                                isActive = song.id == mediaMetadata?.id,
                                                isPlaying = isPlaying,
                                                modifier = Modifier
                                                    .width(currentGridHeight)
                                                    .combinedClickable(
                                                        onClick = {
                                                            if (song.id == mediaMetadata?.id) {
                                                                playerConnection.togglePlayPause()
                                                            } else {
                                                                playerConnection.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                                                            }
                                                        },
                                                        onLongClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.show {
                                                                SongMenu(originalSong = song, navController = navController, onDismiss = menuState::dismiss)
                                                            }
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HomeSection.SpeedDial -> {
                            speedDialItems.takeIf { it.isNotEmpty() }?.let { items ->
                                item(key = "speed_dial_title") {
                                    NavigationTitle(
                                        title = stringResource(R.string.speed_dial),
                                        modifier = Modifier.animateItem()
                                    )
                                }

                                item(key = "speed_dial_list") {
                                    val targetItemSize = 160.dp
                                    val availableWidth = maxWidth - 32.dp
                                    val columns = (availableWidth / targetItemSize).toInt().coerceAtLeast(3)
                                    val rows = if (columns >= 6) 1 else if (columns >= 4) 2 else 3
                                    val itemsPerPage = columns * rows
                                    val itemWidth = availableWidth / columns

                                    val pagerState = rememberPagerState(pageCount = { (items.size + itemsPerPage - 1) / itemsPerPage })

                                    Column(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .animateItem(),
                                    ) {
                                        HorizontalPager(
                                            state = pagerState,
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            pageSpacing = 16.dp,
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(itemWidth * rows),
                                        ) { page ->
                                            val pageStartIndex = page * itemsPerPage
                                            val pageItems = items.drop(pageStartIndex).take(itemsPerPage)

                                            Column(modifier = Modifier.fillMaxSize()) {
                                                for (row in 0 until rows) {
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        for (col in 0 until columns) {
                                                            val itemIndex = row * columns + col

                                                            val isRandomizeSlot = (page == 0 && itemIndex == itemsPerPage - 1)

                                                            if (isRandomizeSlot) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .width(itemWidth)
                                                                        .height(itemWidth)
                                                                        .padding(6.dp)
                                                                ) {
                                                                    RandomizeGridItem(
                                                                        isLoading = isRandomizing,
                                                                        onClick = {
                                                                            if (isRandomizing) {
                                                                                randomizeJob?.cancel()
                                                                            } else {
                                                                                randomizeJob = scope.launch {
                                                                                    val randomItem = viewModel.getRandomItem()
                                                                                    if (randomItem != null) {
                                                                                        when (randomItem) {
                                                                                            is SongItem -> playerConnection.playQueue(
                                                                                                YouTubeQueue(
                                                                                                    randomItem.endpoint ?: WatchEndpoint(videoId = randomItem.id),
                                                                                                    randomItem.toMediaMetadata()
                                                                                                )
                                                                                            )
                                                                                            is AlbumItem -> navController.navigate("album/${randomItem.id}")
                                                                                            is ArtistItem -> navController.navigate("artist/${randomItem.id}")
                                                                                            is PlaylistItem -> navController.navigate("online_playlist/${randomItem.id}") 
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    )
                                                                }
                                                            } else if (itemIndex < pageItems.size) {
                                                                val item = pageItems[itemIndex]
                                                                val isPinned by remember(item.id) { database.speedDialDao.isPinned(item.id) }.collectAsState(initial = false)

                                                                Box(
                                                                    modifier = Modifier
                                                                        .width(itemWidth)
                                                                        .height(itemWidth)
                                                                        .padding(6.dp)
                                                                ) {
                                                                    SpeedDialGridItem(
                                                                        item = item,
                                                                        isPinned = isPinned,
                                                                        isActive = item.id in listOf(mediaMetadata?.album?.id, mediaMetadata?.id),
                                                                        isPlaying = isPlaying,
                                                                        modifier = Modifier
                                                                            .fillMaxSize()
                                                                            .combinedClickable(
                                                                                onClick = {
                                                                                    when (item) {
                                                                                        is SongItem -> playerConnection.playQueue(
                                                                                            YouTubeQueue(
                                                                                                item.endpoint ?: WatchEndpoint(videoId = item.id),
                                                                                                item.toMediaMetadata()
                                                                                            )
                                                                                        )
                                                                                        is AlbumItem -> navController.navigate("album/${item.id}")
                                                                                        is ArtistItem -> navController.navigate("artist/${item.id}")

                                                                                        is PlaylistItem -> navController.navigate("online_playlist/${item.id}") 
                                                                                    }
                                                                                },
                                                                                onLongClick = {
                                                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                                    menuState.show {
                                                                                        when (item) {
                                                                                            is SongItem -> YouTubeSongMenu(
                                                                                                song = item,
                                                                                                navController = navController,
                                                                                                onDismiss = menuState::dismiss
                                                                                            )
                                                                                            is AlbumItem -> YouTubeAlbumMenu(
                                                                                                albumItem = item,
                                                                                                navController = navController,
                                                                                                onDismiss = menuState::dismiss
                                                                                            )
                                                                                            is ArtistItem -> YouTubeArtistMenu(
                                                                                                artist = item,
                                                                                                onDismiss = menuState::dismiss
                                                                                            )
                                                                                            is PlaylistItem -> YouTubePlaylistMenu(
                                                                                                playlist = item,
                                                                                                coroutineScope = scope,
                                                                                                onDismiss = menuState::dismiss
                                                                                            )
                                                                                        }
                                                                                    }
                                                                                }
                                                                            )
                                                                    )
                                                                }
                                                            } else {
                                                                Spacer(modifier = Modifier.width(itemWidth))
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (pagerState.pageCount > 1) {
                                            Row(
                                                modifier = Modifier
                                                    .height(24.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                repeat(pagerState.pageCount) { iteration ->
                                                    val color = if (pagerState.currentPage == iteration)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(4.dp)
                                                            .clip(CircleShape)
                                                            .background(color)
                                                            .size(8.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        HomeSection.QuickPicks -> {
                            quickPicks?.takeIf { it.isNotEmpty() }?.let { picks ->
                                item(key = "quick_picks_title") {
                                    NavigationTitle(
                                        title = stringResource(R.string.quick_picks),
                                        modifier = Modifier.animateItem(),
                                        onPlayAllClick = {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = "Quick Picks",
                                                    items = picks.distinctBy { it.id }.map { it.toMediaMetadata().toMediaItem() }
                                                )
                                            )
                                        }
                                    )
                                }
                                item(key = "quick_picks_list") {
                                    val distinctQuickPicks = picks.distinctBy { it.id }
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.animateItem()
                                    ) {
                                        items(distinctQuickPicks, key = { it.id }) { originalSong ->
                                            val song by remember(originalSong.id) { database.song(originalSong.id) }.collectAsState(initial = originalSong)
                                            SongGridItem(
                                                song = song!!,
                                                isActive = song!!.id == mediaMetadata?.id,
                                                isPlaying = isPlaying,
                                                modifier = Modifier
                                                    .width(currentGridHeight)
                                                    .combinedClickable(
                                                        onClick = {
                                                            if (song!!.id == mediaMetadata?.id) {
                                                                playerConnection.togglePlayPause()
                                                            } else {
                                                                playerConnection.playQueue(YouTubeQueue.radio(song!!.toMediaMetadata()))
                                                            }
                                                        },
                                                        onLongClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.show {
                                                                SongMenu(originalSong = song!!, navController = navController, onDismiss = menuState::dismiss)
                                                            }
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HomeSection.FromTheCommunity -> {
                            communityPlaylists?.takeIf { it.isNotEmpty() }?.let { playlists ->
                                item(key = "community_playlists_title") {
                                    NavigationTitle(
                                        title = stringResource(R.string.from_the_community),
                                        modifier = Modifier.animateItem()
                                    )
                                }

                                item(key = "community_playlists_content") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.animateItem()
                                    ) {
                                        items(playlists, key = { it.playlist.id }) { item ->
                                            CommunityPlaylistCard(
                                                item = item,
                                                onClick = {
                                                    navController.navigate("online_playlist/${item.playlist.id.removePrefix("VL")}")
                                                },
                                                onSongClick = { song ->
                                                    playerConnection.playQueue(
                                                        YouTubeQueue(
                                                            song.endpoint ?: WatchEndpoint(videoId = song.id),
                                                            song.toMediaMetadata()
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        HomeSection.DailyDiscover -> {
                            dailyDiscover?.takeIf { it.isNotEmpty() }?.let { discoverList ->
                                
                                item(key = "daily_discover_title") {
                                    val title = stringResource(R.string.your_daily_discover)
                                    NavigationTitle(
                                        title = title,
                                        onPlayAllClick = {
                                            val queueItems = discoverList.mapNotNull {
                                                (it.recommendation as? SongItem)?.toMediaMetadata()
                                            }

                                            if (queueItems.isNotEmpty()) {
                                                playerConnection.playQueue(
                                                    ListQueue(
                                                        title = title,
                                                        items = queueItems.map { it.toMediaItem() }
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                                item(key = "daily_discover_content") {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        items(discoverList) { item ->
                                            DailyDiscoverCard(
                                                dailyDiscover = item,
                                                onClick = {
                                                    val song = item.recommendation as? SongItem
                                                    val mediaMetadata = song?.toMediaMetadata()
                                                    if (mediaMetadata != null) {
                                                        playerConnection.playQueue(
                                                            YouTubeQueue(
                                                                song.endpoint ?: WatchEndpoint(videoId = song.id),
                                                                mediaMetadata
                                                            )
                                                        )
                                                    }
                                                },
                                                navController = navController
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        HomeSection.KeepListening -> {
                            keepListening?.takeIf { it.isNotEmpty() }?.let { keepListening ->
                                item(key = "keep_listening_title") {
                                    NavigationTitle(
                                        title = stringResource(R.string.keep_listening),
                                        modifier = Modifier.animateItem()
                                    )
                                }

                                item(key = "keep_listening_list") {
                                    val rows = if (keepListening.size > 6) 2 else 1
                                    LazyHorizontalGrid(
                                        state = rememberLazyGridState(),
                                        rows = GridCells.Fixed(rows),
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height((currentGridHeight + with(LocalDensity.current) {
                                                MaterialTheme.typography.bodyLarge.lineHeight.toDp() * 2 +
                                                        MaterialTheme.typography.bodyMedium.lineHeight.toDp() * 2
                                            }) * rows)
                                            .animateItem()
                                    ) {
                                        items(keepListening, key = { it.id }) {
                                            localGridItem(it)
                                        }
                                    }
                                }
                            }
                        }
                        HomeSection.AccountPlaylists -> {
                            accountPlaylists?.takeIf { it.isNotEmpty() }?.let { accountPlaylists ->
                                item(key = "account_playlists_title") {
                                    NavigationTitle(
                                        label = stringResource(R.string.your_youtube_playlists),
                                        title = accountName,
                                        thumbnail = {
                                            if (url != null) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(url)
                                                        .diskCachePolicy(CachePolicy.ENABLED)
                                                        .diskCacheKey(url)
                                                        .crossfade(false)
                                                        .build(),
                                                    placeholder = painterResource(id = R.drawable.person),
                                                    error = painterResource(id = R.drawable.person),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(ListThumbnailSize)
                                                        .clip(CircleShape)
                                                )
                                            } else {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.person),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(ListThumbnailSize)
                                                )
                                            }
                                        },
                                        onClick = {
                                            navController.navigate("account")
                                        },
                                        modifier = Modifier.animateItem()
                                    )
                                }

                                item(key = "account_playlists_list") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.animateItem()
                                    ) {
                                        items(
                                            items = accountPlaylists.distinctBy { it.id },
                                            key = { it.id },
                                        ) { item ->
                                            ytGridItem(item)
                                        }
                                    }
                                }
                            }
                        }
                        HomeSection.ForgottenFavorites -> {
                            forgottenFavorites?.takeIf { it.isNotEmpty() }?.let { forgottenFavorites ->
                                item(key = "forgotten_favorites_title") {
                                    val forgottenFavoritesTitle = stringResource(R.string.forgotten_favorites)
                                    NavigationTitle(
                                        title = forgottenFavoritesTitle,
                                        modifier = Modifier.animateItem(),
                                        onPlayAllClick = {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = forgottenFavoritesTitle,
                                                    items = forgottenFavorites.distinctBy { it.id }.map { it.toMediaItem() }
                                                )
                                            )
                                        }
                                    )
                                }

                                item(key = "forgotten_favorites_list") {
                                    
                                    val rows = min(4, forgottenFavorites.size)
                                    LazyHorizontalGrid(
                                        state = forgottenFavoritesLazyGridState,
                                        rows = GridCells.Fixed(rows),
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        flingBehavior = rememberSnapFlingBehavior(
                                            forgottenFavoritesSnapLayoutInfoProvider
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(ListItemHeight * rows)
                                            .animateItem()
                                    ) {
                                        itemsIndexed(
                                            items = forgottenFavorites.distinctBy { it.id },
                                            key = { _, it -> it.id }
                                        ) { index, originalSong ->
                                            val song by remember(originalSong.id) { database.song(originalSong.id) }.collectAsState(initial = originalSong)

                                            SongListItem(
                                                song = song!!,
                                                showInLibraryIcon = true,
                                                isActive = song!!.id == mediaMetadata?.id,
                                                isPlaying = isPlaying,
                                                isSwipeable = false,
                                                shape = listItemShape(index = index % rows, count = rows),
                                                trailingContent = {
                                                    IconButton(
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.show {
                                                                SongMenu(
                                                                    originalSong = song!!,
                                                                    navController = navController,
                                                                    onDismiss = menuState::dismiss
                                                                )
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.more_vert),
                                                            contentDescription = null
                                                        )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .width(horizontalLazyGridItemWidth)
                                                    .combinedClickable(
                                                        onClick = {
                                                            if (song!!.id == mediaMetadata?.id) {
                                                                playerConnection.togglePlayPause()
                                                            } else {
                                                                playerConnection.playQueue(
                                                                    YouTubeQueue.radio(
                                                                        song!!.toMediaMetadata()
                                                                    )
                                                                )
                                                            }
                                                        },
                                                        onLongClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.show {
                                                                SongMenu(
                                                                    originalSong = song!!,
                                                                    navController = navController,
                                                                    onDismiss = menuState::dismiss
                                                                )
                                                            }
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        is HomeSection.SimilarRecommendation -> {
                            val recommendation = similarRecommendations?.getOrNull(section.index)
                            recommendation?.let {
                                item(key = "similar_to_title_${section.index}") {
                                    NavigationTitle(
                                        label = stringResource(R.string.similar_to),
                                        title = recommendation.title.title,
                                        thumbnail = recommendation.title.thumbnailUrl?.let { thumbnailUrl ->
                                            {
                                                val shape =
                                                    if (recommendation.title is Artist) CircleShape else RoundedCornerShape(
                                                        ThumbnailCornerRadius
                                                    )
                                                AsyncImage(
                                                    model = thumbnailUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(ListThumbnailSize)
                                                        .clip(shape)
                                                )
                                            }
                                        },
                                        onClick = {
                                            when (recommendation.title) {
                                                is Song -> navController.navigate("album/${recommendation.title.album!!.id}")
                                                is Album -> navController.navigate("album/${recommendation.title.id}")
                                                is Artist -> navController.navigate("artist/${recommendation.title.id}")
                                                is Playlist -> {}
                                            }
                                        },
                                        modifier = Modifier.animateItem()
                                    )
                                }

                                item(key = "similar_to_list_${section.index}") {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.animateItem()
                                    ) {
                                        items(recommendation.items, key = { it.id }) { item ->
                                            ytGridItem(item)
                                        }
                                    }
                                }
                            }
                        }
                        is HomeSection.HomePageSection -> {
                            val sectionData = homePage?.sections?.getOrNull(section.index)
                            sectionData?.let {
                                
                                val sectionSongs = sectionData.items.filterIsInstance<SongItem>()
                                val hasPlayableSongs = sectionSongs.isNotEmpty()
                                
                                val isSongsOnlySection = sectionData.items.isNotEmpty() &&
                                        sectionData.items.all { it is SongItem }

                                item(key = "home_section_title_${section.index}") {
                                    NavigationTitle(
                                        title = sectionData.title,
                                        label = sectionData.label,
                                        thumbnail = sectionData.thumbnail?.let { thumbnailUrl ->
                                            {
                                                val shape =
                                                    if (sectionData.endpoint?.isArtistEndpoint == true) CircleShape else RoundedCornerShape(
                                                        ThumbnailCornerRadius
                                                    )
                                                AsyncImage(
                                                    model = thumbnailUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(ListThumbnailSize)
                                                        .clip(shape)
                                                )
                                            }
                                        },
                                        onClick = sectionData.endpoint?.let { endpoint ->
                                            {
                                                when {
                                                    endpoint.browseId == "FEmusic_moods_and_genres" ->
                                                        navController.navigate("mood_and_genres")
                                                    endpoint.params != null ->
                                                        navController.navigate("youtube_browse/${endpoint.browseId}?params=${endpoint.params}")
                                                    else ->
                                                        navController.navigate("browse/${endpoint.browseId}")
                                                }
                                            }
                                        },
                                        onPlayAllClick = if (hasPlayableSongs) {
                                            {
                                                playerConnection.playQueue(
                                                    ListQueue(
                                                        title = sectionData.title,
                                                        items = sectionSongs.map { it.toMediaMetadata().toMediaItem() }
                                                    )
                                                )
                                            }
                                        } else null,
                                        modifier = Modifier.animateItem()
                                    )
                                }

                                if (isSongsOnlySection) {
                                    
                                    item(key = "home_section_list_${section.index}") {
                                        LazyHorizontalGrid(
                                            state = rememberLazyGridState(),
                                            rows = GridCells.Fixed(4),
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(ListItemHeight * 4)
                                                .animateItem()
                                        ) {
                                            itemsIndexed(
                                                items = sectionSongs.distinctBy { it.id },
                                                key = { _, it -> it.id }
                                            ) { index, song ->
                                                YouTubeListItem(
                                                    item = song,
                                                    isActive = song.id == mediaMetadata?.id,
                                                    isPlaying = isPlaying,
                                                    isSwipeable = false,
                                                    shape = listItemShape(index = index % 4, count = 4),
                                                    trailingContent = {
                                                        IconButton(
                                                            onClick = {
                                                                menuState.show {
                                                                    YouTubeSongMenu(
                                                                        song = song,
                                                                        navController = navController,
                                                                        onDismiss = menuState::dismiss
                                                                    )
                                                                }
                                                            }
                                                        ) {
                                                            Icon(
                                                                painter = painterResource(R.drawable.more_vert),
                                                                contentDescription = null
                                                            )
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .width(horizontalLazyGridItemWidth)
                                                        .combinedClickable(
                                                            onClick = {
                                                                if (song.id == mediaMetadata?.id) {
                                                                    playerConnection.togglePlayPause()
                                                                } else {
                                                                    playerConnection.playQueue(
                                                                        YouTubeQueue.radio(song.toMediaMetadata())
                                                                    )
                                                                }
                                                            },
                                                            onLongClick = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                menuState.show {
                                                                    YouTubeSongMenu(
                                                                        song = song,
                                                                        navController = navController,
                                                                        onDismiss = menuState::dismiss
                                                                    )
                                                                }
                                                            }
                                                        )
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    
                                    item(key = "home_section_list_${section.index}") {
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.animateItem()
                                        ) {
                                            items(sectionData.items, key = { it.id }) { item ->
                                                ytGridItem(item)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        HomeSection.MoodAndGenres -> {
                            explorePage?.moodAndGenres?.let { moodAndGenres ->
                                item(key = "mood_and_genres_title") {
                                    NavigationTitle(
                                        title = stringResource(R.string.mood_and_genres),
                                        onClick = {
                                            navController.navigate("mood_and_genres")
                                        },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                                item(key = "mood_and_genres_list") {
                                    LazyHorizontalGrid(
                                        rows = GridCells.Fixed(4),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height((MoodAndGenresButtonHeight + 12.dp) * 4 + 12.dp)
                                            .animateItem()
                                    ) {
                                        items(moodAndGenres, key = { it.title }) {
                                            MoodAndGenresButton(
                                                title = it.title,
                                                onClick = {
                                                    navController.navigate("youtube_browse/${it.endpoint.browseId}?params=${it.endpoint.params}")
                                                },
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .width(180.dp)
                                            )
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                if (isLoading || homePage?.continuation != null && homePage?.sections?.isNotEmpty() == true) {
                    item(key = "loading_shimmer") {
                        ShimmerHost(
                            modifier = Modifier.animateItem()
                        ) {
                            repeat(2) {
                                TextPlaceholder(
                                    height = 36.dp,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .width(250.dp),
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    items(4) {
                                        GridItemPlaceHolder()
                                    }
                                }
                            }

                            TextPlaceholder(
                                height = 36.dp,
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 12.dp)
                                    .width(250.dp),
                            )
                            repeat(4) {
                                Row {
                                    repeat(2) {
                                        TextPlaceholder(
                                            height = MoodAndGenresButtonHeight,
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .padding(horizontal = 12.dp)
                                                .width(200.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

        }
    }
}