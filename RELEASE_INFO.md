# Aether Music v2.0.0

Aether Music has arrived. This major release transitions the application into a standalone product with its own clean identity, refined design language, unified iconography, and a fully polished, robust architecture.

## 📱 Key Highlights

### 1. Rebranded Identity & Adaptive Vector Icon System
*   **Original Monogram Logo**: Redesigned the brand around a custom monoline geometric "A" vector icon.
*   **Vector Engine integration**: Adaptive launcher icons (`ic_launcher`, `ic_launcher_round`, and `legacy_icon`) now load high-definition XML paths dynamically rather than scaling legacy raster images.
*   **Themed Launcher support**: Complete support for monochrome themed icons.
*   **Clean Visuals**: Splash screens, launcher instances, and settings are fully aligned to the new Aether branding family.

### 2. Spacing & Typography Standardization
*   **Consistent Design System**: Spacing scale (`AetherSpacing`), corner shape scale (`AetherCorners`), and icon sizing scale (`AetherIconSize`) are systematically applied.
*   **Pure Black Mode Contrast**: Settings cards now use a subtle alpha overlay (`Color.White.copy(alpha = 0.06f)`) in Pure Black dark mode to make them pop off the deep black backgrounds.
*   **Typography Hierarchy**: Redesigned all section titles to use Inter Bold font with extra top-padding breathing room.

### 3. Home Navigation Stabilization
*   **Stable Popping Engine**: Refined Compose backstack navigation: tapping the bottom bar Home icon from any sub-screen cleanly pops the stack back to `home`, preventing duplicates, loops, and memory bloat.

### 4. Centralized Premium Haptics
*   **Crisp Click Triggers**: Integrated system tactile vibration triggers across playback controls, settings switches, and bottom navigation.
*   **Analog Slider Drag**: Introduced adaptive drag tick haptics with time-based throttling for seamless volume/scrubbing seek textures.
*   **Hardware Consent**: Properly registered permissions and integrated telemetry logs to track device haptic support.
*   **Global Toggle**: Option to toggle haptics fully persistent via Settings -> Personalization -> Haptics.

### 5. Repository Cleanup & Optimization
*   **Footprint Reduction**: Audited entire project and deleted over **3.3 MB** of obsolete source codes, migration files, templates, and unused raster assets.
*   **Code Namespace Alignment**: Renamed subpackage locations to match internal package structures.

---
Aether Music is open-source and released under the **GNU General Public License v3.0**.