<div align="center">

# JUNO

### A premium music player for Android. Local-first. Distraction-free.

🔴 **YouTube Playback Temporarily Unavailable — Investigation Ongoing**

<p>
  <em>Built for people who still care about how music feels.</em>
</p>

<p>
  <a href="https://android.com"><img src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Compose-1.7.5-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"></a>
  <a href="LICENSE"><img src="https://img.shields.io/github/license/bharadwajsanket/juno?style=for-the-badge&color=28a745" alt="License"></a>
  <a href="https://github.com/bharadwajsanket/juno/releases"><img src="https://img.shields.io/badge/Release-v6.5.4-blue?style=for-the-badge" alt="Release"></a>
</p>

<br>

[Overview](#overview) · [What's New](#whats-new-in-v654---nature-reborn) · [Features](#features) · [Architecture](#architecture) · [Tech Stack](#tech-stack) · [Build](#build-instructions) · [Contributing](#contributing) · [License](#license)

</div>

<br>

---

> [!WARNING]
> ## ⚠️ JUNO PLAYBACK CURRENTLY UNAVAILABLE
>
> **JUNO is temporarily unable to play YouTube-sourced music.**
>
> We are currently investigating a playback compatibility issue in the YouTube/Innertube
> extraction layer. This issue is **not specific to JUNO 6.5.4** — the same playback
> failure is also reproducible on the previously working **v5.5.4** release.
>
> ### What is affected?
> - ▶️ YouTube-sourced music playback
> - 🎵 Streaming from YouTube
> - ⏱️ Playback may remain at `0:00`
> - ❌ `Playback failed — Video unavailable`
> - ❌ `IO_UNSPECIFIED (2000)`
>
> ### What is NOT necessarily affected?
> - Your local music library
> - Playlists and library data
> - UI and navigation
> - Lyrics and other non-streaming features
> - Downloaded/local media playback
>
> **You do not need to reinstall JUNO or change your device settings.**
> If you are seeing the playback error, it is a known issue currently under investigation.
>
> We will update this notice when playback compatibility is restored.

### 🛠️ Current Status

**Status:** 🔴 Investigating  
**Affected:** YouTube/Innertube streaming  
**JUNO versions tested:** v6.5.4 and previously working v5.5.4  
**Last verified:** August 19, 2026

Our diagnostics show that some YouTube InnerTube clients still return playable
formats while others return `UNPLAYABLE` / `Video unavailable`. We are
investigating the client/extraction compatibility path.

<br>

---

## Overview

JUNO is an Android music player built with Jetpack Compose, ExoPlayer/Media3, and Material 3. It streams from YouTube Music, plays your local library, downloads for offline listening, and shows real-time synchronized lyrics — all wrapped in an interface built around the music, not around itself.

JUNO is **not** a YouTube client. It uses YouTube Music as a backend source while delivering a purpose-built music player experience with a unique visual identity, precision audio controls, and thoughtful interaction design.

> **Current release:** v6.5.4 — Nature Reborn

<br>

---

## What's New in v6.5.4 — Nature Reborn

**6.5.4 is the most polished JUNO release to date.** It introduces a completely redesigned landscape player, a production-grade haptic system, significant equalizer improvements, and a full suite of stability fixes.

### Landscape Player — Redesigned From Scratch

The landscape player now follows a CarPlay-inspired split-panel layout:

- **Left**: Large album artwork with a unified action dock (Lyrics, Sleep Timer, EQ, Repeat, Shuffle, More)
- **Right**: Tabbed panel switching between Controls, Lyrics, and Queue
- Tab state is preserved across screen rotations
- Portrait player is completely untouched

### Nature Reborn Visual System

- Dynamic album palette extraction powers the entire app's color theme in real time
- Artwork shadow rendering refined — no bleeding artifacts
- Depth-based navigation transitions: scale + fade instead of lateral slide
- All motion uses the centralized `JUNOMotion` token system

### Haptic Feedback System

- `HapticManager` singleton with distinct haptic patterns for navigation, seek, playback toggle, EQ adjustment, and error states
- Haptics fire on every nav destination change
- Seek bar triggers start/end haptics at seek boundaries

### Equalizer Improvements

- Custom parametric biquad filter chain — no system EQ dependency
- Dynamic preamp headroom calculation prevents clipping at high gain settings
- Fixed: audio processor now validates format before applying profiles, eliminating ExoPlayer initialization crashes

### Share & Deep Links

- All share links use `https://juno.music/watch?v={id}`
- Deep links verified with `android:autoVerify="true"` for true App Links behavior

<br>

---

## Features

### Playback
- Stream from YouTube Music (no account required for browsing)
- Local library playback from device storage
- Crossfade between tracks (configurable duration)
- Gapless playback
- Background playback via Media3 `MediaLibraryService`
- Cast to Google Cast / Chromecast (GMS flavor)
- Sleep timer, Repeat modes, Shuffle

### Audio Quality
- Parametric equalizer — 10 bands + preamp with dynamic headroom management
- Custom biquad filter chain (independent of Android system EQ)
- Format display: codec, bitrate, sample rate, loudness
- High-quality stream selection (configurable)

### Library
- Full offline library with Room database
- Listen history, play counts, and listening statistics
- Liked songs, bookmarked artists and playlists
- Download manager for offline listening
- Import playlists from Spotify
- Local file scanning (MP3, FLAC, M4A, OGG, WAV, etc.)

### Lyrics
- Real-time synchronized lyrics with auto-scroll
- Multiple providers: LRCLib, BetterLyrics, KuGou, Paxsenix, SimP Music, YouLyPlus
- Inline lyrics view in player
- Lyrics-to-story sharing with customizable visual templates

### Discovery
- Home feed with personalized recommendations
- Browse: charts, new releases, mood & genre mixes
- Artist pages, album pages, YouTube Music playlists
- Online search with suggestions
- Music recognition (ShazamKit integration)

### UI & Experience
- Material 3 Expressive design with Inter typeface
- Dynamic color theming from album artwork
- CarPlay-class landscape player
- Pure black mode for OLED displays
- Adaptive layout for phones and tablets
- Home screen widget
- Deep links: `juno.music/watch?v={id}`, `juno.music/playlist?list={id}`

<br>

---

## Screenshots

> Screenshots for v6.5.4 — Nature Reborn coming soon.

<br>

---

## Architecture

JUNO follows standard Android MVVM with Jetpack Compose as the UI layer.

```
┌─────────────────────────────────────────────────┐
│  UI Layer (Jetpack Compose)                     │
│  Screens / Components / Player / Menus          │
│  ViewModels (Hilt) — one per screen             │
├─────────────────────────────────────────────────┤
│  PlayerConnection                               │
│  Reactive bridge between MusicService and UI    │
│  Exposes StateFlows for all playback state      │
├─────────────────────────────────────────────────┤
│  MusicService (MediaLibraryService / Media3)    │
│  ExoPlayer • MediaSession • Audio Focus         │
│  Crossfade • Cast • EQ • Error Recovery         │
├─────────────────────────────────────────────────┤
│  Data Layer                                     │
│  Room (MusicDatabase) • DataStore (Prefs)       │
│  Coil 3 (Image Loading) • OkHttp (Networking)   │
├─────────────────────────────────────────────────┤
│  API Modules (separate Gradle modules)          │
│  innertube • kugou • lrclib • betterlyrics      │
│  jiosaavn • shazamkit • canvas                  │
└─────────────────────────────────────────────────┘
```

### Key Design Decisions

- **Single Activity**: `MainActivity` hosts all Compose navigation via `NavHost`
- **Service binding**: `MusicService` bound in `onStart`/`onStop` for correct multi-window behavior
- **PlayerConnection**: Single source of truth for UI playback state, derived from `Player.Listener` events exposed as `StateFlow`
- **Hilt DI**: All ViewModels, repositories, and services use constructor injection throughout
- **Modular API layer**: Each third-party data source is an independent Gradle module

<br>

---

## Tech Stack

| Layer | Library / Tool |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 Expressive |
| Navigation | Compose Navigation |
| Dependency Injection | Hilt (Dagger) |
| Playback | Media3 ExoPlayer + MediaLibraryService |
| Database | Room (SQLite) |
| Preferences | DataStore |
| Image Loading | Coil 3 |
| Networking | OkHttp + Ktor |
| Async | Kotlin Coroutines + Flow |
| Typography | Inter (open source) |
| Build | Gradle KTS + KSP |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 |

<br>

---

## Build Instructions

### Prerequisites

- Android Studio Ladybug or newer
- JDK 21
- Android SDK API 36

### Clone

```bash
git clone https://github.com/bharadwajsanket/juno.git
cd juno
```

### Build Variants

JUNO has two flavor dimensions:

| Dimension | Values | Description |
|---|---|---|
| `variant` | `foss` (default), `gms` | FOSS = F-Droid compatible, no Google Play Services. GMS = includes Google Cast. |
| `abi` | `arm64`, `armeabi`, `x86`, `x86_64`, `universal` | Target CPU architecture |

### Debug Build

```bash
./gradlew assembleArm64FossDebug
```

### Release Build

```bash
cp keystore.properties.template keystore.properties
# Fill in your signing config in keystore.properties
./gradlew assembleArm64GmsRelease
```

### Optional: API Keys

```bash
cp local.properties.template local.properties
# Add LASTFM_API_KEY and LASTFM_SECRET if needed
```

<br>

---

## Contributing

Contributions are welcome. Before opening a pull request:

1. **Open an issue first** for any significant change or new feature
2. Follow existing code conventions: Kotlin idioms, Compose best practices
3. Use the design token system: `JUNOMotion`, `JUNOSpacing`, `JUNOCorners`, `AppTypography`
4. Test all UI changes in both portrait and landscape orientations
5. Do not add new permissions without a documented reason in the PR description
6. Do not modify the `innertube` module's core implementation without prior discussion

### What Not To Submit

- New backend-dependent features or social features
- Large architecture rewrites (these need coordination)
- New third-party service integrations without prior discussion

<br>

---

## License

JUNO is released under the **GNU General Public License v3.0**.

```
Copyright (C) 2024-2026 Sanket Bharadwaj

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
```

See [LICENSE](LICENSE) for the full license text.

This project builds on work from the open-source community. See the About screen in the app for credits and attributions.

---

<div align="center">
<sub>Built by <a href="https://github.com/bharadwajsanket">Sanket Bharadwaj</a></sub>
</div>
