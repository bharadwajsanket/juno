

package bharadwajsanket.aether.music.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import bharadwajsanket.aether.music.LocalPlayerAwareWindowInsets
import bharadwajsanket.aether.music.LocalPlayerConnection
import bharadwajsanket.aether.music.R
import bharadwajsanket.aether.music.constants.GridItemSize
import bharadwajsanket.aether.music.constants.GridItemsSizeKey
import bharadwajsanket.aether.music.constants.GridThumbnailHeight
import bharadwajsanket.aether.music.models.toMediaMetadata
import bharadwajsanket.aether.music.playback.queues.YouTubeQueue
import bharadwajsanket.aether.music.ui.component.IconButton
import bharadwajsanket.aether.music.ui.component.LocalMenuState
import bharadwajsanket.aether.music.ui.component.YouTubeGridItem
import bharadwajsanket.aether.music.ui.component.shimmer.GridItemPlaceHolder
import bharadwajsanket.aether.music.ui.component.shimmer.ShimmerHost
import bharadwajsanket.aether.music.ui.menu.YouTubeAlbumMenu
import bharadwajsanket.aether.music.ui.menu.YouTubeArtistMenu
import bharadwajsanket.aether.music.ui.menu.YouTubePlaylistMenu
import bharadwajsanket.aether.music.ui.menu.YouTubeSongMenu
import bharadwajsanket.aether.music.ui.utils.backToMain
import bharadwajsanket.aether.music.utils.rememberEnumPreference
import bharadwajsanket.aether.music.viewmodels.YouTubeBrowseViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun YouTubeBrowseScreen(
    navController: NavController,
    viewModel: YouTubeBrowseViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val browseResult by viewModel.result.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    val allItems = browseResult?.items?.flatMap { it.items } ?: emptyList()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = GridThumbnailHeight + if (gridItemSize == GridItemSize.BIG) 24.dp else (-24).dp),
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
    ) {
        if (browseResult == null) {
            items(8) {
                ShimmerHost {
                    GridItemPlaceHolder(fillMaxWidth = true)
                }
            }
        }

        items(
            items = allItems.distinctBy { it.id },
            key = { it.id }
        ) { item ->
            YouTubeGridItem(
                item = item,
                isActive = when (item) {
                    is SongItem -> mediaMetadata?.id == item.id
                    is AlbumItem -> mediaMetadata?.album?.id == item.id
                    else -> false
                },
                isPlaying = isPlaying,
                fillMaxWidth = true,
                coroutineScope = coroutineScope,
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            when (item) {
                                is SongItem -> {
                                    if (item.id == mediaMetadata?.id) {
                                        playerConnection.togglePlayPause()
                                    } else {
                                        playerConnection.playQueue(
                                            YouTubeQueue.radio(item.toMediaMetadata())
                                        )
                                    }
                                }
                                is AlbumItem -> navController.navigate("album/${item.id}")
                                is ArtistItem -> navController.navigate("artist/${item.id}")
                                is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                            }
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            menuState.show {
                                when (item) {
                                    is SongItem ->
                                        YouTubeSongMenu(
                                            song = item,
                                            navController = navController,
                                            onDismiss = menuState::dismiss,
                                        )
                                    is AlbumItem ->
                                        YouTubeAlbumMenu(
                                            albumItem = item,
                                            navController = navController,
                                            onDismiss = menuState::dismiss,
                                        )
                                    is ArtistItem ->
                                        YouTubeArtistMenu(
                                            artist = item,
                                            onDismiss = menuState::dismiss,
                                        )
                                    is PlaylistItem ->
                                        YouTubePlaylistMenu(
                                            playlist = item,
                                            coroutineScope = coroutineScope,
                                            onDismiss = menuState::dismiss,
                                        )
                                }
                            }
                        }
                    )
                    .animateItem()
            )
        }
    }

    TopAppBar(
        title = { Text(browseResult?.title.orEmpty()) },
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
