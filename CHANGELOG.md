# JUNO Changelog

---

## ⚠️ Playback Compatibility Notice — August 19, 2026

YouTube-sourced playback is currently unavailable in JUNO.

The failure has been reproduced on both the current v6.5.4 build and the
previously working v5.5.4 release. This indicates that the issue is not
specific to the Nature Reborn player redesign.

Current investigation is focused on the YouTube/Innertube playback extraction
and client compatibility layer.

Users do not need to reinstall JUNO or modify device settings because of this
known issue.

This is an investigation notice, not a claim about an official YouTube outage
or policy change.

---

## v6.5.4 — Nature Reborn
*Released: August 2026*

### ✨ New

- **Redesigned landscape player** — CarPlay-inspired split-panel layout with album artwork on the left and a tabbed control/lyrics/queue panel on the right. The previous rotated-portrait design is gone.
- **Landscape tab persistence** — The selected landscape panel (Controls, Lyrics, Queue) is now preserved when rotating the screen.
- **Nature Reborn visual system** — Depth-based navigation transitions (scale + fade), refined artwork shadow rendering, and palette-driven dynamic theming across the full app.
- **Haptic feedback system** — Distinct haptic patterns for navigation, seek, playback toggle, EQ interaction, and error states.
- **New share format** — All share links now use `https://juno.music/watch?v={id}` and are verified App Links.
- **Unified action dock in landscape** — Single action row under artwork with Lyrics, Sleep Timer, EQ, Repeat, Shuffle, and More. No duplicate controls.

### 🔧 Improved

- **Equalizer stability** — Audio processor now validates format before applying EQ profiles, eliminating a class of ExoPlayer playback errors that occurred when switching tracks.
- **Dynamic theme extraction** — Coil image requests for color sampling are now capped at 200×200px, reducing memory usage on every track change.
- **Playback state flows** — Switched from `SharingStarted.Lazily` to `SharingStarted.WhileSubscribed(5000)` for correct upstream lifecycle management.
- **Search behavior** — Single tap opens search screen. Double tap activates the search bar and opens the keyboard. No more random keyboard appearances.
- **Manifest permissions** — Permissions are organized by category with clear documentation. Unused permissions removed.

### 🐛 Fixed

- Audio clipping when using EQ profiles with high gain bands
- Landscape player showing duplicate action buttons
- Landscape player tab resetting to Controls on every screen rotation
- Search keyboard appearing on single tap without user action
- Portrait queue bottom sheet briefly appearing during landscape player load
- Share links using the old `share.junomusic.fun` domain

### 🔒 Security

- Release builds no longer emit internal logs (session state, media IDs, error details) to logcat
- Removed `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions that were declared but not used

---

## v5.5.4 — Motion & Polish
*Previous stable release*

- Unified `JUNOMotion` animation token system
- Spring-based artwork crossfades in Mini Player and full Player
- `.bounceClick` touch feedback modifier applied throughout
- Corner shapes, spacing, and shimmer loading states unified
- Optimized lazy list recomposition performance
- Navigation transitions standardized

---

## v4.x — Living Environment
*Legacy*

- Living Sky procedural rendering engine
- Real-time weather-responsive now-playing visuals
- Local media library foundations

---

*Full commit history available on [GitHub](https://github.com/bharadwajsanket/juno).*
