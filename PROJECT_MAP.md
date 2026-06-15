# Aether Music 2.0 Project Mapping

This document maps the architectural composition, modules, packages, major features, navigation structure, and system elements of Aether Music 2.0.

---

## 1. Modules Map

The project contains 14 Gradle sub-modules in addition to the root configuration:

| Module Name | Purpose & Description |
| :--- | :--- |
| `:app` | **Core Application**: Jetpack Compose UI, ViewModels, Room DB layers, services, update systems, and DI bindings. |
| `:aethermusiccanvas` | **Aether Canvas**: Custom mesh visualizer, glow canvas effects, and dynamic media background animations. |
| `:applecanvas` | **Apple Canvas**: Apple Music-inspired animated canvas and background blur shader systems. |
| `:artistvideo` | **Artist Video**: Background rendering framework for artist banner playback videos. |
| `:betterlyrics` | **Better Lyrics**: Advanced parser and renderer for TTML and standard synced LRC lyric sheets. |
| `:canvas` | **Base Canvas**: Core abstracts and shared assets for artwork-based background visualizers. |
| `:innertube` | **InnerTube**: Direct interface client for streaming audio, searching tracks, and scraping YouTube Music metadata. |
| `:jiosaavn` | **JioSaavn**: Integration API client for Saavn high-quality local language music metadata and streams. |
| `:kugou` | **Kugou**: Search provider integration for downloading synced lyrics from Kugou servers. |
| `:lrclib` | **LrcLib**: Synced lyrics fetcher utilizing the open-source LrcLib API. |
| `:paxsenixlyrics` | **Paxsenix**: Secondary lyrics search provider integration. |
| `:shazamkit` | **ShazamKit**: Integrated song recognition via Shazam API interfaces. |
| `:simpmusic` | **SimpMusic**: Downstream lyric scraper and metadata integration client. |
| `:youlyplus` | **YoulyPlus**: Alternative stream/lyric metadata query client. |

---

## 2. Package Structure (`:app`)

The core application code resides in the root package namespace `bharadwajsanket.aether.music`, subdivided as follows:

- **`.playback`**: Service logic, ExoPlayer integrations, media notification providers, download service, and queue sequencers.
- **`.ui`**: View components, themes, and screen composables.
  - **`.ui.theme`**: Font styling (Inter), color schemes, corner tokens, and typography weights.
  - **`.ui.component`**: Reusable widgets (player controls, list views, dialog drawers, settings cells).
  - **`.ui.screens`**: Screen destinations (Home, Library, Search, ListenTogether).
- **`.db`**: Persistent Room database, DAO structures, entity tables (History, Downloads, Cache, Playlists), and migrations.
- **`.viewmodels`**: UI state managers bridging database models and API calls to Composes.
- **`.aethermusic`**: Fork enhancements including updater, commits tracker, and changelog viewers.
- **`.utils`**: Helper classes (HapticManager, IconUtils, NetworkConfig, Cipher systems).
- **`.constants`**: Theme sizes, layout tokens, and SharedPreferences storage keys.

---

## 3. Major Features

1. **Definitive Resonance Orbits Logo**: Monoline vector-based icon system scaling flawlessly across density ranges and device dimensions.
2. **Centralized Premium Haptics**: Native vibration feedback personalizable for slider seek scrubs, settings changes, and button clicks.
3. **Smart Lyrics Sync**: Karaoke-style animation rendering with translation support (using DeepL or OpenRouter models) and romanization engines.
4. **Offline Playback**: Secure database local storage for cached streaming tracks and persistent offline download suites.
5. **Spotify Import**: Local scraper capable of importing playlists from Spotify databases via Web APIs.

---

## 4. Navigation Routes

All routes are registered inside [NavigationBuilder.kt](file:///Users/sanketbharadwaj/Developer/Echo-Music/app/src/main/kotlin/com/music/echo/ui/screens/NavigationBuilder.kt):

- **Main Navigation Destinations**:
  - `home`: Root main screen.
  - `search_input`: Search text input field.
  - `library`: User local catalog / playlists.
  - `listen_together`: Disabled placeholder fallback screen.
- **Sub-pages / Nested Paths**:
  - `history`: List of recently played songs.
  - `local_songs`: Scanned local media tracks.
  - `stats`: User playback frequency statistics.
  - `mood_and_genres`: Category browse screen.
  - `account`: Login / Profile configurations.
  - `browse/{browseId}`: YouTube item browse details.
  - `search/{query}`: Online search results listing.
  - `album/{albumId}`: Album contents listing.
  - `artist/{artistId}`: Artist details page.
  - `online_playlist/{playlistId}`: Online playlist tracklist.
  - `local_playlist/{playlistId}`: Local SQLite playlist.
  - `settings`: Main settings entry page.

---

## 5. Settings Screens

Settings pages are nested under the `settings` path:

1. `settings/update`: Check update channels and configure nightly feeds.
2. `settings/account`: Log in with YouTube Music / Google accounts.
3. `settings/appearance`: Font configurations, theme selectors, scale sliders, and icon switchers.
4. `settings/appearance/theme`: Deep color seed customizations.
5. `settings/content`: Search scopes, regional filtering, and lyrics providers.
6. `settings/content/romanization`: Sub-language translation filters.
7. `settings/ai`: OpenRouter and DeepL API keys for lyric translations.
8. `settings/player`: Playback quality, codecs, crossfade, and UI layout controls.
9. `settings/storage`: File exports, cache sizes, and memory thresholds.
10. `settings/equalizer`: Axion software equalizer controls.
11. `settings/privacy`: History pausing and screenshot prevention.
12. `settings/haptics`: Vibration strength and haptic triggers.
13. `settings/backup_restore`: Local database exports and sync files.
14. `settings/integrations`: Discord RPC hooks and scrobble clients.
15. `settings/spotify_import`: Spotify SP_DC login credentials.

---

## 6. Services & Workers

### Services (Android Manifest Registered)
1. `MusicService` (`:app:playback`): Media3 Session media playback backend managing the audio pipeline.
2. `ExoDownloadService` (`:app:playback`): Media3 Download Manager running background stream downloads.
3. `AudioExportService` (`:app:playback`): Background task converter rendering cached tracks as raw MP3 exports.

### Workers (WorkManager Scheduled)
1. `UpdateDownloadWorker` (`:app:aethermusic:updater`): Periodic update check and nightly APK downloader.

---

## 7. Native Libraries

No pre-compiled static native binary libraries (`.so` files) are checked directly into source control. All architectures (`armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`) pull native components dynamically at build-time through Gradle dependency trees:
- `libffmpeg` & `libffmpegkit`: Loaded via FFmpegKit for transcoding audio formats.
- `libpython` & `libaria2c`: Pulled by extractor tools for media streaming.
- `libandroidx.graphics.path`: Path rendering utilities.

---

## 8. Assets

- **Fonts**: Inter font-family family weights reside under `app/src/main/res/font/`:
  - `inter_light.ttf`
  - `inter_regular.ttf`
  - `inter_medium.ttf`
  - `inter_semibold.ttf`
  - `inter_bold.ttf`
- **Vectors**: All custom vectors reside in `app/src/main/res/drawable/`.

---

## 9. Launcher Aliases

Android launcher entries are mapped in `AndroidManifest.xml` via three activity-aliases pointing to `.MainActivity`:
- `.MainActivityAlias` (Default Adaptive Vector Icon - Resonance Orbits).
- `.MainActivityLegacy` (Violet Background Vector Icon).
- `.MainActivityStatic` (Fallback Static Icon).
