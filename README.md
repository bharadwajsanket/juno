<div align="center">
  <h1>Aether Music</h1>

  <p><strong>A premium, atmospheric, and minimal Android music application built for users who value control, personalization, privacy, and performance.</strong></p>

  [![License](https://img.shields.io/github/license/bharadwajsanket/Aether-Music?style=for-the-badge&color=28a745)](LICENSE)
</div>

---

## 1. Overview

**Aether Music** is a premium, modern music player experience for Android. It features a spacious layout, atmospheric dark color schemes, consistent corner shapes, and premium transition animations. Aether Music transforms the user experience through a completely fresh design language inspired by Apple Music × Nothing OS, while preserving robust local library playback, downloading, network caching, synchronized lyrics, and database/sync mechanics.

### Brand Identity & Design Concept
Aether Music's identity is anchored in **flowing geometry and atmospheric resonance**. The official launcher icon is designed as a pure vector representation of intersecting resonance orbits (orbital energy rings) with a floating horizontal bridge. When viewed vertically, this minimalist layout subtly forms a monogram "A" for *Aether*, representing wave propagation, acoustic resonance, and premium hardware aesthetics without relying on generic music notes, triangles, or headphones.

---

## 2. Features

*   **Atmospheric Resonance Brand Identity**: Fully adaptive, pure vector-based launcher icon system representing overlapping soundwaves and orbital geometry.
*   **Premium Design System**: Structured around a harmony-focused dark theme (backgrounds at `#090909`, surface cards at `#151515`) with a low-saturation atmospheric violet accent (`#9E9BE3`).
*   **Centralized Premium Haptics**: Native tactile feedback for player button presses, settings switches, volume, and playback seek bar scrubbing. Includes custom configuration controls.
*   **Spacious Layouts**: Increased whitespace, polished paddings, enlarged artwork dimensions, and a streamlined layout.
*   **Smart Lyrics**: Synchronized, word-by-word scrollable lyrics with support for AI translation and translation providers (such as OpenRouter).
*   **Offline Mode**: Search, stream, and cache/download audio locally for completely network-free playback.
*   **Spotify Integration**: Search and import playlists directly into local databases.
*   **Pure Black Mode**: A dedicated high-contrast settings skin for OLED screens.

---

## 3. Screenshots

*Screenshots demonstrating the spacious UI, custom player, haptics personalization, and lyrics visualizers will be hosted in the upcoming GitHub release media assets.*

---

## 4. Installation

Official releases can be found under the **Releases** tab of the GitHub repository. 
1. Go to the [Releases](https://github.com/bharadwajsanket/Aether-Music/releases) page.
2. Download the latest `universal-foss-debug.apk` or GMS variant.
3. Install the APK on your Android device (enabling installation from unknown sources if required).

---

## 5. Build Instructions

### Prerequisites
*   Android Studio (latest version recommended)
*   Android SDK (API level 26+)
*   JDK 21
*   Git

### Step-by-Step Build
1.  **Clone the Repository**
    ```bash
    git clone https://github.com/bharadwajsanket/Aether-Music.git
    cd Aether-Music
    ```
2.  **Configure Local Properties**
    Create a `local.properties` file in the root directory:
    ```properties
    sdk.dir=/path/to/your/android/sdk
    ```
3.  **Compile via Gradle**
    *   To build the FOSS Debug variant:
        ```bash
        ./gradlew assembleUniversalFossDebug
        ```
    *   To build the GMS Debug variant:
        ```bash
        ./gradlew assembleUniversalGmsDebug
        ```

---

## 6. Architecture

Aether Music is built following clean architecture guidelines:
*   **Jetpack Compose**: 100% Kotlin-based declarative UI design.
*   **MVVM Pattern**: Separates state and view presentation logic cleanly.
*   **Centralized HapticManager**: Unified engine interacting with Android's `Vibrator` systems.
*   **Ktor Client**: Highly performant asynchronous HTTP request execution for metadata, artwork, and streaming.
*   **Room Database**: Local persistence for cache, downloads, history, and playlists.

---

## 7. Roadmap

*   [ ] Centralized cross-device backup syncing support.
*   [ ] Refactored Listen Together server network sync protocols.
*   [ ] Advanced equalizer audio enhancement presets.

---

## 8. Credits

This project includes work derived from open-source software.

Thanks to the original contributors and maintainers.

---

## 9. License

This project is licensed under the **GNU General Public License v3.0** (GPL-3.0) - see the [LICENSE](LICENSE) file for details.
