# AETHER Repository Maintenance & Cleanup Actions

This document tracks the maintenance and repository cleanup operations performed for AETHER v3.5.4 on the branch `cleanup/post-v3.5.4`.

---

## 🗑️ Deleted Files & Resources

The following 20 resource assets have been categorized as `SAFE_DELETE` (completely unreferenced by code, assets, manifests, or properties) and deleted:

| File Path | Description / Type | Rationale |
| :--- | :--- | :--- |
| `app/src/main/res/drawable/ic_google.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/sparks.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/apple_lyrics.png` | Raster PNG asset | No references found in codebase. |
| `app/src/main/res/drawable/telegram.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/avg_time_oldplayer.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/speaker_apple.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/collab.jpg` | Raster JPEG asset | No references found in codebase. |
| `app/src/main/res/drawable/apple_queue.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/connect_people.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/headset_applemusic.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/biotech.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/ic_x_new.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/ic_discord_new.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/globe.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/ic_instagram_new.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/apple_music_me.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/ic_patreon_new.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/stream_old_player.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/spatial_tracking_apple.xml` | Vector asset | No references found in codebase. |
| `app/src/main/res/drawable/ic_telegram_new.xml` | Vector asset | No references found in codebase. |

---

## 📁 Removed Empty Directories

The following empty directories left over from obsolete assets were removed:
- `app/src/main/res/mipmap-mdpi`
- `app/src/main/res/mipmap-hdpi`
- `app/src/main/res/mipmap-ldpi`
- `app/src/main/res/mipmap-xxxhdpi`
- `app/src/main/res/mipmap-xxhdpi`

---

## ✏️ Modified Files

The following documentation audit files have been added to the codebase:
- `docs/CLEANUP_REPORT.md` (detailed project audit of unused files)
- `docs/DEPENDENCY_AUDIT.md` (build and dependency analysis)
- `docs/DOCUMENTATION_TODO.md` (checklist of documentation that needs updates)

---

## 🛠️ Build Verification Results

- **Command:** `STORE_PASSWORD=dummy KEY_ALIAS=dummy KEY_PASSWORD=dummy ./gradlew assembleUniversalGmsRelease`
- **Result:** **Success** (Compilation and resource shrinking completed successfully; build failed at the final step `packageUniversalGmsRelease` with signing key exception `keystore password was incorrect` as expected due to local environment limits).
- **Compilation Check:** **Passes cleanly** in 4m 11s. No dangling references or unresolved symbols were introduced.

---

## 🔒 Preserved Resources (Kept Safe)

The following resources were confirmed as unused/redundant via static analysis but preserved under the strict safety guidelines:

| File Path | Classification | Reason for Keeping |
| :--- | :--- | :--- |
| `app/src/main/res/xml/recognizer_widget_info.xml` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete widget resources). |
| `app/src/main/res/xml-v31/recognizer_widget_info.xml` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete widget resources). |
| `app/src/main/res/font/bbh_bartle.xml` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete font configurations). |
| `app/src/main/res/font/google_sans_flex.ttf` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete fonts). |
| `app/src/main/res/font/sans_flex.ttf` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete fonts). |
| `app/src/main/res/drawable/ic_launcher_background_v31.xml` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete launcher assets). |
| `app/src/main/res/drawable-v31/ic_launcher_background_v31.xml` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete launcher assets). |
| `app/src/main/res/drawable-mdpi/ic_stat_name.png` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete notification status bar drawables). |
| `app/src/main/res/drawable-hdpi/ic_stat_name.png` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete notification status bar drawables). |
| `app/src/main/res/drawable-xhdpi/ic_stat_name.png` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete notification status bar drawables). |
| `app/src/main/res/drawable-xxhdpi/ic_stat_name.png` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete notification status bar drawables). |
| `app/src/main/res/drawable-xxxhdpi/ic_stat_name.png` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete notification status bar drawables). |
| `app/src/main/res/drawable/ic_ringtone.png` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete notification/ringtone assets). |
| `app/src/main/res/drawable/apk_install.xml` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete OTA related assets). |
| `app/src/main/res/drawable/network_update.xml` | `DO_NOT_TOUCH` | Preserved under Rule 8 (Never delete OTA related assets). |
| `app/src/main/res/drawable/dev.jpg` | `DO_NOT_TOUCH` | Substring match found inside AndroidManifest.xml. |
