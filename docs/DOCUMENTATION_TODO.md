# AETHER Documentation Update Checklist

This document details the checklist of documentation files that need updates following the rebranding and repository cleanup in version v3.5.4.

---

## 📋 Recommended Documentation Updates

### 1. 📖 README.md
- [ ] Update build instructions to state that release builds require `STORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` as environment variables.
- [ ] Remove any mentions of the old project name ("Echo Music") that remain.
- [ ] Verify that dependencies listed under the architecture section match the current version catalog (e.g. mention removal of `jsoup` and `haze`).

### 2. 📝 CHANGELOG.md
- [ ] Document the v3.5.4 release details.
- [ ] Add a section under v3.5.4 noting the codebase cleanup, removal of over 39+ unused drawable files, unused fonts (`sans_flex`), and dead widget assets.
- [ ] Document dependency optimization (removal of `jsoup`, `haze`, and `concurrent-futures` from version catalog).

### 3. ☁️ OTA Guide & Updater
- [ ] Ensure the OTA updater links match the new release tags correctly.
- [ ] Document the local environment variables needed to build clean, signed release bundles for OTA distribution.

### 4. 🚀 Installation Guide (SETUP.md)
- [ ] Document that release builds require specific environment variables for signing, or copy setup guidelines from the `gradle.properties.template` file.
- [ ] Clarify that developers can test GMS/FOSS features locally using the `./gradlew assembleUniversalGmsDebug` command (which doesn't require keys).

### 5. 📸 Screenshots & Artwork
- [ ] Check if status bar layout screenshot references show the old branding logo or layouts.
- [ ] Delete any old design screenshots in the assets/ directory that are unreferenced.

### 6. 🤝 Contributing Guide (CONTRIBUTING.md)
- [ ] Update guidelines on adding new resources to ensure developers check for duplicates and use standard HSL palettes.
- [ ] Add a rule that all newly added vector icons must be audited for redundancy to keep build sizes light.
