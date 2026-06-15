

package bharadwajsanket.aether.music.ui.screens.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import bharadwajsanket.aether.music.R
import bharadwajsanket.aether.music.constants.ChipSortTypeKey
import bharadwajsanket.aether.music.constants.LibraryFilter
import bharadwajsanket.aether.music.ui.component.ChipsRow
import bharadwajsanket.aether.music.utils.rememberEnumPreference
import androidx.activity.compose.BackHandler

@Composable
fun LibraryScreen(navController: NavController) {
    var filterType by rememberEnumPreference(ChipSortTypeKey, LibraryFilter.LIBRARY)

    BackHandler(enabled = filterType != LibraryFilter.LIBRARY) {
        filterType = LibraryFilter.LIBRARY
    }

    val filterContent = @Composable {
        Row {
            ChipsRow(
                chips =
                listOf(
                    LibraryFilter.PLAYLISTS to stringResource(R.string.filter_playlists),
                    LibraryFilter.SONGS to stringResource(R.string.filter_songs),
                    LibraryFilter.ALBUMS to stringResource(R.string.filter_albums),
                    LibraryFilter.ARTISTS to stringResource(R.string.filter_artists),
                    LibraryFilter.LOCAL to stringResource(R.string.filter_local),
                ),
                currentValue = filterType,
                onValueUpdate = {
                    filterType =
                        if (filterType == it) {
                            LibraryFilter.LIBRARY
                        } else {
                            it
                        }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (filterType) {
            LibraryFilter.LIBRARY -> LibraryMixScreen(navController, filterContent)
            LibraryFilter.PLAYLISTS -> LibraryPlaylistsScreen(navController, filterContent)
            LibraryFilter.SONGS -> LibrarySongsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY })

            LibraryFilter.ALBUMS -> LibraryAlbumsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY })

            LibraryFilter.ARTISTS -> LibraryArtistsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY })

            LibraryFilter.LOCAL -> LocalSongScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY },
                isEmbedded = true
            )
        }
    }
}
