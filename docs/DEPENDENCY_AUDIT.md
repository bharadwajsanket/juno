# AETHER Dependency Audit Report

This report audits the dependencies in `gradle/libs.versions.toml` and `app/build.gradle.kts` for AETHER v3.5.4.

---

## 🔍 Unused Dependencies
*These dependencies are included in the build files but are never imported or referenced in any source files.*

| Dependency | Library Coordinates | Purpose | Recommendation |
| :--- | :--- | :--- | :--- |
| `haze` | `dev.chrisbanes.haze:haze` | Glassmorphic / blur layout effects for Jetpack Compose | **Remove** — confirmed completely unreferenced in the codebase. |
| `concurrent-futures` | `androidx.concurrent:concurrent-futures-ktx` | Kotlin extensions for Android ListenableFuture | **Remove** — confirmed completely unreferenced in the codebase (replaced by native Kotlin Coroutines integration). |
| `jsoup` | `org.jsoup:jsoup` | HTML parsing and manipulation | **Remove** — confirmed completely unreferenced in the codebase. |

---

## 📦 Required Dependencies (Keep)
*These dependencies are confirmed to be actively used or required by platform APIs.*

| Dependency | Library Coordinates | Active Usage / Purpose | Recommendation |
| :--- | :--- | :--- | :--- |
| `guava` & `coroutines-guava` | `com.google.guava:guava` | Required by Media3 player session callbacks to return `ListenableFuture` | **Keep** — required for Media3 session callbacks. |
| `materialKolor` | `com.materialkolor:material-kolor` | Custom dynamic styling and theme color generation | **Keep** — used in Theme UI classes. |
| `ucrop` | `com.github.yalantis:ucrop` | Local image cropping features | **Keep** — registered in Manifest and used in `LocalPlaylistScreen.kt`. |
| `shimmer` | `com.valentinilk.shimmer` | Loading placeholders | **Keep** — heavily used in lists/grids placeholder shimmers. |
| `kuromoji-ipadic` | `com.atilika.kuromoji` | Japanese lyrics word morphological analysis | **Keep** — used in `LyricsUtils.kt`. |
| `tinypinyin` | `com.github.promeG:tinypinyin` | Chinese lyrics character Pinyin helper | **Keep** — used in `LyricsUtils.kt`. |
| `ffmpeg-kit-full` | `com.arthenica:ffmpeg-kit-full` | Audio transcoding / exporting features | **Keep** — used in `AudioExportService.kt`. |
| `youtubedl-android` | `io.github.junkfood02.youtubedl-android` | Library wrapper for video/audio downloads | **Keep** — used for background media downloading. |

---

## 🛠️ Action Plan

1. **Phase 2 resource cleanup** must be completed and validated.
2. In a future task, remove `haze`, `concurrent-futures`, and `jsoup` from `app/build.gradle.kts` and `gradle/libs.versions.toml`.
3. Verify that the debug build still succeeds after dependency removal.
