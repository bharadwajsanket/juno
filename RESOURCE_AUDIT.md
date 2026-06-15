# Aether Music 2.0 Resource Audit Report

This report outlines the audit of resources located in `drawable/`, `mipmap/`, and `assets/` folders, checking for duplications, orphans, and optimization limits.

---

## 1. Directory Maps

### 1.1 `app/src/main/res/drawable/`
Contains UI icons, vector icons, custom widget shape shapes, and a few required interface rasters:
- **Core Adaptive Vector Brand Elements**:
  - `ic_launcher_foreground.xml`: Standard white orbits lineart.
  - `ic_launcher_monochrome.xml`: Standard themed orbits lineart.
  - `legacy_icon_foreground.xml`: Violet themed orbits lineart.
  - `legacy_icon_monochrome.xml`: Violet themed monochrome orbits lineart.
  - `ic_launcher_nobg.xml`: Centered vector logo without background card.
  - `icon.xml`: Full app logo card (Resonance Orbits inside `#090909` circle background).
- **Interface Rasters (PNG/WebP/JPG)**:
  - `apple_headset.png` (Used in device connections layout)
  - `apple_airpods.png` (Used in audio connection details settings)
  - `speaker_applemusic.png` (Used in player speaker controls layout)
  - `apple_lyrics.png` (Used in lyrics setup screen)
  - `ic_ringtone.png` (Used in ringtone trimmer controls)
  - `weeknd.webp` (Used as fallback/test placeholder in specific views)
  - `dev.jpg` (Developer about avatar)

### 1.2 `app/src/main/res/mipmap-*/`
Houses the fallback raster launcher icons for devices running APIs below Android 8.0 (Oreo):
- `ic_launcher.png` (fallback primary adaptive launcher card)
- `ic_launcher_round.png` (fallback primary round launcher card)
- `legacy_icon.png` (fallback legacy violet card)
- `legacy_icon_round.png` (fallback legacy violet round card)
- `tv_banner.png` (Android TV launcher card asset, located in `mipmap-xhdpi/`)

### 1.3 `app/src/main/assets/`
Contains client-side YouTube signature deciphering JavaScript routines:
- `solver/yt.solver.core.js`
- `solver/meriyah.js`
- `solver/astring.js`

---

## 2. Unused / Orphaned Resources Identified

The audit identified the following orphaned XML drawables that are never referenced in either Kotlin sources or secondary XML resource files:

1. **`echo_music_library_circle.xml`** (`drawable/`): Legacy circle overlay vector left over from upstream library screen styling. Can be safely deleted.
2. **`license_echo.xml`** (`drawable/`): Legacy icon vector originally used on developer dialogs that have since been fully restructured/rebranded. Can be safely deleted.

---

## 3. Duplicate and Redundant Assets Audit

- **Raster Cleanup Verification**: All density-specific PNG layouts (`ic_launcher_foreground.png`, `ic_launcher_monochrome.png`, `legacy_icon_foreground.png`, `legacy_icon_monochrome.png`, `ic_launcher_static.png`) have been successfully deleted from all density mipmap buckets (`mipmap-mdpi/`, `mipmap-hdpi/`, `mipmap-xhdpi/`, `mipmap-xxhdpi/`, `mipmap-xxxhdpi/`), leaving only standard fallback `.png` card layers. No duplicate assets remain.
- **Sharp Vectors Only**: Modern device themes (API 26+) resolve vector icons exclusively, avoiding scaling blur or scaling layout calculation overhead.
