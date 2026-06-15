# Changelog

All notable changes to the Aether Music project will be documented in this file. The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [2.0.0] - 2026-06-15

This release establishes Aether Music 2.0 as a standalone product with a clean monogrammed brand identity, unified vector iconography, stable navigation, and a production-grade custom haptic engine.

### Added
*   **Monoline Geometric Brand Identity**: Fully original monogrammed "A" logo system designed for launchers, adaptive launcher layouts, themed icons, and setting configurations.
*   **Premium Haptic Engine**: Native haptic clicks for player playback actions, settings switches, and volume seek bar drags. Configurable via Settings -> Personalization -> Haptics.
*   **Adaptive Launcher Vectorization**: Converted all main icons (`ic_launcher.xml` and `icon.xml`) from static raster images to dynamically compiled sharp XML vector structures.
*   **OLED Pure Black Mode**: Integrated a dedicated white-alpha overlay (`Color.White.copy(alpha = 0.06f)`) for settings cards to ensure premium visual contrast on OLED displays.

### Changed
*   **Home Navigation Stabilization**: Tapping the bottom bar Home icon from any nested sub-screen now pops the Compose backstack cleanly rather than pushing duplicates or causing routing loops.
*   **Cleaned Package Directories**: Renamed package subfolders to `aethermusic` to match internal namespace specs.
*   **Listen Together State**: Master toggle `ENABLE_LISTEN_TOGETHER` set to `false`, isolating websocket/server config references and showing a clean upgrade notice screen when accessed.

### Removed
*   **Unused Repositories & Assets**: Deleted over 3.3 MB of obsolete templates, migration helpers, outdated notebooks, and old branding resources.

---

## [1.0.0] - 2026-06-10

### Added
*   Initial release of Aether Music.
*   Introduced modern dark design styling inspired by Apple Music × Nothing OS layouts.
