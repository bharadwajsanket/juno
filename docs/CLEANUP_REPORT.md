# AETHER Project Cleanup Report & Audit

This report contains details of the unused files and resources found during the static analysis audit of AETHER v3.5.4.

---

## 🎨 Unused Drawables & Mipmaps
**Confidence Level: 100%**  
*Rationale: None of these resource names are referenced anywhere in the source code (`.kt`), XML layouts/menus, application manifests, or build files.*

| Path | Description / Type | Confidence |
| :--- | :--- | :--- |
| `app/src/main/res/drawable/arrow_upward.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_google.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/sparks.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_ringtone.png` | Raster PNG asset | 100% |
| `app/src/main/res/drawable/apk_install.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/apple_lyrics.png` | Raster PNG asset | 100% |
| `app/src/main/res/drawable/telegram.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/avg_time_oldplayer.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/speaker_apple.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/collab.jpg` | Raster JPEG asset | 100% |
| `app/src/main/res/drawable/apple_queue.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/music_history.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/connect_people.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/headset_applemusic.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/contrast.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/arrow_downward.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/dev.jpg` | Raster JPEG asset | 100% |
| `app/src/main/res/drawable/auto_play.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_drive.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/web_link.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/biotech.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_x_new.xml` | Vector drawable (legacy Twitter icon) | 100% |
| `app/src/main/res/drawable/ic_discord_new.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/globe.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/no_volume.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/integration.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/github_main.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_instagram_new.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/apple_music_me.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/star.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/network_update.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_patreon_new.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/stream_old_player.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/spatial_tracking_apple.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_telegram_new.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_ten_sided_cookie.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/dock_to_top.xml` | Vector drawable | 100% |
| `app/src/main/res/drawable/ic_launcher_background_v31.xml` | Vector drawable (redundant copy) | 100% |
| `app/src/main/res/drawable-v31/ic_launcher_background_v31.xml` | Vector drawable (redundant copy) | 100% |
| `app/src/main/res/drawable-mdpi/ic_stat_name.png` | Notification status icon (Unreferenced) | 100% |
| `app/src/main/res/drawable-hdpi/ic_stat_name.png` | Notification status icon (Unreferenced) | 100% |
| `app/src/main/res/drawable-xhdpi/ic_stat_name.png` | Notification status icon (Unreferenced) | 100% |
| `app/src/main/res/drawable-xxhdpi/ic_stat_name.png` | Notification status icon (Unreferenced) | 100% |
| `app/src/main/res/drawable-xxxhdpi/ic_stat_name.png` | Notification status icon (Unreferenced) | 100% |

---

## 📄 Unused XML Resource Files
**Confidence Level: 100%**  
*Rationale: The recognizer widget and the font-family configuration are completely unreferenced in both the manifests and the Kotlin code.*

| Path | Description | Confidence |
| :--- | :--- | :--- |
| `app/src/main/res/xml/recognizer_widget_info.xml` | Widget Info XML Configuration | 100% |
| `app/src/main/res/xml-v31/recognizer_widget_info.xml` | Widget Info XML Configuration (v31 version) | 100% |
| `app/src/main/res/font/bbh_bartle.xml` | Font family wrapper (loaded via direct TTF in code) | 100% |

---

## 🔤 Unused Fonts
**Confidence Level: 100%**  
*Rationale: Checked all source code for use of these font files.*

| Path | Description | Confidence |
| :--- | :--- | :--- |
| `app/src/main/res/font/google_sans_flex.ttf` | TrueType Font | 100% |
| `app/src/main/res/font/sans_flex.ttf` | TrueType Font | 100% |

---

## 📁 Unused Assets
**Confidence Level: 100%**  
*None.*

---

## ⚙️ Unused Kotlin Files
**Confidence Level: Low/Medium (Static Name Match)**  
*Rationale: Static analysis matches filenames to usages. In Kotlin, files can contain file-level functions, extensions, and classes named differently from the filename (e.g. `aetherMusicUpdater.kt` contains the used `checkForUpdate` function). Therefore, these files must **NOT** be deleted without manual verification.*

*   `app/src/gms/kotlin/com/music/echo/cast/CastManager.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/EchoMusicLyrics.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/AppNavigation.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/DraggableScrollBarOverlay.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/AutoResizeText.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/BigSeekBar.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/ThumbnailCornerRadiusSelector.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/LyricsV2.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/NavigationTile.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/PlayerSlider.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/Preference.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/GridMenu.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/AlbumGradient.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/NewMenuComponents.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/UpdaterComponents.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/shimmer/ListItemPlaceholder.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/component/shimmer/GridItemPlaceholder.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/NavControllerUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/UriUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/FadingEdge.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/ItemWrapper.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/ShapeUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/AppBar.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/LazyGridSnapLayoutInfoProvider.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/ScrollUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/YouTubeUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/KeyUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/utils/StringUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/screens/CommentTogether.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/screens/NavigationBuilder.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/screens/CanvasAlbum.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/screens/ExploreScreen.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/screens/search/suggestions/TabNewsSuggestion.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/screens/search/suggestions/SuggestionModels.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/menu/SelectionSongsMenu.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/menu/PlaylistScreenMenus.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/ui/player/ThumbnailSnapUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/viewmodels/LibraryViewModels.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/viewmodels/ThemeViewModel.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/di/AppModule.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/di/NetworkModule.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/di/Qualifiers.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/constants/PreferenceKeys.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/spotify/models/SpotifyToken.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/aethermusic/BluetoothUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/aethermusic/changelog/changelogscreen.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/aethermusic/updater/aetherMusicUpdater.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/aethermusic/updater/UpdateStorageUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/aethermusic/updater/downloadmanager/AetherNotificationProvider.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/aethermusic/updater/downloadmanager/downloadnotificationmanager.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/LocalYouTubeDownloader.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/Utils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/ComposeDebugUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/WebAuthSessionCleaner.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/LocalFileDownloader.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/LocalMediaIntents.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/SuperProperties.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/NetworkUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/ShapesCurve.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/StringUtils.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/utils/potoken/JavaScriptUtil.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/MediaItemExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/CoroutineExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/QueueExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/StringExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/ListExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/FileExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/ContextExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/UtilExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/PlayerExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/extensions/ModifierExt.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/spotifyimport/SpotifyImportModels.kt`
*   `app/src/main/kotlin/bharadwajsanket/aether/music/playback/queues/LocalMixQueue.kt`
*   `innertube/src/test/kotlin/com/music/innertube/MainTest.kt`
*   `innertube/src/test/kotlin/com/music/innertube/SearchVideoTest.kt`
*   `innertube/src/test/kotlin/com/music/innertube/models/RunsTest.kt`
*   `innertube/src/main/kotlin/com/music/innertube/NetworkConfig.kt`
*   `innertube/src/main/kotlin/com/music/innertube/utils/Utils.kt`
*   `innertube/src/main/kotlin/com/music/innertube/models/MusicMultiRowImageItemRenderer.kt`
*   `innertube/src/main/kotlin/com/music/innertube/models/response/ContinuationResponse.kt`
*   `innertube/src/main/kotlin/com/music/innertube/models/response/AddItemYouTubePlaylistResponse.kt`
*   `innertube/src/main/kotlin/com/music/innertube/pages/LibraryAlbumsPage.kt`
*   `aethermusiccanvas/src/main/kotlin/com/music/echo/aethermusiccanvas/AetherMusicCanvasProvider.kt`
*   `canvas/src/test/kotlin/com/music/echo/canvas/MonochromeApiCanvasTest.kt`
*   `shazamkit/src/main/kotlin/com/music/shazamkit/models/ShazamModels.kt`
*   `paxsenixlyrics/src/main/kotlin/com/music/paxsenix/models/PaxsenixModels.kt`
