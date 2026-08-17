# JUNO Music — Adaptive UI Architecture Documentation (Phases I - IV: Spread of Nature)

## Executive Summary

Project **Spread of Nature** transforms JUNO Music into a next-generation adaptive Android music application that feels native across all display form factors (Phones, Landscape, Foldables, Small Tablets, Large Tablets, ChromeOS, Desktop Windowed Mode).

- **Phase I (Architecture)**: Built the core adaptive foundation (`bharadwaj.juno.music.ui.adaptive`).
- **Phase II (Application Shell)**: Redesigned shell navigation (Bottom Navigation Bar, Navigation Rail, Permanent Drawer), master-detail settings, and responsive library grids.
- **Phase III (The Listening Experience)**: Rebuilt playback surfaces (Mini Player, Expanded Player, Synced Lyrics, Up Next Queue, Tablet/Landscape multi-pane player, Android Auto, Google Cast MediaSession).
- **Phase IV (Refinement)**: Standardized motion, haptics, accessibility semantics, touch target sizes, recomposition stability, and system performance.

The phone experience is sacred and preserved with zero visual regressions on compact portrait screens.

---

## 1. Package Architecture (`bharadwaj.juno.music.ui.adaptive`)

```
ui/adaptive/
├── window/          # Window size classes, posture, orientation, screen type, tablet detection
├── tokens/          # Central tokens: spacing, padding, radius, sizes, artwork, content width
├── spacing/         # Window margins, content padding, dynamic grid spacing
├── dimensions/      # Readable width, breakpoints, player dimensions
├── typography/      # Adaptive typography scaling & density helpers
├── animation/       # Pane transitions, scaffold animation specs
├── navigation/      # Navigation styles (BottomBar, Rail, Drawer) & navigation host abstractions
├── pane/            # Multi-pane state, pane roles, dual-pane configuration abstractions
├── scaffold/        # Central AdaptiveScaffold & configuration slots
├── layout/          # Primitives (AdaptiveRow, AdaptiveColumn, AdaptivePane, AdaptiveSpacer, etc.)
├── theme/           # AdaptiveTheme composition local providers & Material 3 integration
└── utils/           # Modifier extensions and composition local keys
```

---

## 2. Core Architectural Principles

### A. Central Window Size System (`ui/adaptive/window`)
- **`AdaptiveWindowInfo`**: Single source of truth containing:
  - `WindowWidthSizeClass`: `Compact` (<600dp), `Medium` (600dp-840dp), `Expanded` (>840dp).
  - `WindowHeightSizeClass`: `Compact` (<480dp), `Medium` (480dp-900dp), `Expanded` (>900dp).
  - `orientation`: `Portrait` vs `Landscape`.
  - `posture`: `Normal`, `Tabletop`, `Book` (foldable hinge aware).
  - `isTablet`: `smallestScreenWidthDp >= 600dp` or expanded width.
- **`rememberAdaptiveWindowInfo()`**: Reactively calculates window bounds from Compose context.

### B. Standardized Tokens (`ui/adaptive/tokens`)
- **`SpacingTokens`**: `Micro` (2dp), `Small` (8dp), `Medium` (12dp), `Normal` (16dp), `Large` (24dp), `ExtraLarge` (32dp), `Huge` (48dp).
- **`SizeTokens`**: Minimum touch target `48dp`, icon sizes (`18dp`, `24dp`, `32dp`), top bar `64dp`, mini player `64dp`.
- **`ArtworkSizeTokens`**: Responsive artwork dimensions (`Thumbnail` 40dp, `GridCompact` 120dp, `GridMedium` 160dp, `GridExpanded` 200dp, `ExpandedPlayer` 420dp).
- **`ContentWidthTokens`**: Limits max reading/form width to `600dp` (`MaxReadableWidth`) and single column width to `840dp` (`MaxSingleColumnWidth`).

### C. Navigation Strategy (`ui/adaptive/navigation`)
- **Phone Portrait (`Compact`)**: Bottom Navigation Bar.
- **Phone Landscape / Small Tablet (`Medium`)**: Navigation Rail.
- **Large Tablet / ChromeOS / Desktop (`Expanded`)**: Permanent Navigation Rail/Drawer.

### D. Multi-Pane & Bounded Layouts (`ui/adaptive/pane` & `ui/adaptive/layout`)
- **`AdaptivePaneLayout`**: Handles list-detail dual pane splits on wide screens while degrading seamlessly to single-pane navigation on phone viewports.
- **`AdaptiveContainer` & `.adaptiveContentWidth()`**: Centers and bounds wide layout containers to preserve comfortable reading line lengths.

---

## 3. Motion, Haptics & Accessibility Reference

### Motion System (`JUNOMotion`)
- All UI transitions use standardized Material 3 cubic bezier curves: `Emphasized`, `EmphasizedDecelerate`, `Standard`.
- Standardized durations: `DurationFast` (150ms), `DurationNormal` (300ms), `DurationSlow` (500ms).

### Haptic Feedback (`HapticManager`)
- Tactile feedback is reserved for intentional actions: Play/Pause toggles, Item selection, Drag-and-drop reordering, and Long-press context menus.

### Accessibility Guidelines
- Every interactive icon button provides localized `contentDescription`.
- Touch targets strictly enforce `48.dp` minimum size (`SizeTokens.MinimumTouchTarget`).
- Support font scaling, RTL layout directions, and screen readers (TalkBack).

---

## 4. Developer Onboarding & Migration Guide

When adding new screens or refactoring existing components:
1. **Always read window info via `AdaptiveTheme.windowInfo`**. Never measure screen width directly using `LocalConfiguration.current.screenWidthDp`.
2. **Use tokenized spacing (`AdaptiveTheme.spacing.windowMargin`, `SpacingTokens.Normal`)** instead of ad-hoc DP values.
3. **Wrap form fields, lyrics, and settings in `.adaptiveContentWidth()`** to maintain maximum readable line bounds.
4. **Use `AdaptivePaneLayout` for side-by-side list-detail screens**.
