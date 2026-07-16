# JUNO v5.5.4 — Motion & Polish

> **Release date:** 2026-07-16
> **Type:** Stable
> **Build:** Universal FOSS · Universal GMS

---

## What is JUNO?

JUNO is a calm, atmospheric Android music player built with Jetpack Compose. It plays your local library, streams when you want it to, downloads for offline listening, and shows synchronized lyrics — all inside an interface designed to recede into the background and let the music take over.

---

## Highlights

This release is a **quality and polish pass** across the entire application. No new features have been added. Instead, every interaction, transition, and animation has been made more intentional, more consistent, and more premium.

The philosophy: if an animation attracts attention, it's probably too much. Every motion change in this release serves the music experience — not the interface.

---

## What's Improved

### Motion System
- All animation durations, spring parameters, and easing curves are now defined in a single `JUNOMotion` design token system.
- No more ad-hoc `tween(300)` or inconsistent spring specs scattered across the codebase.
- Three easing curves — `EmphasizedDecelerate`, `EmphasizedAccelerate`, and `EmphasizedStandard` — are applied consistently for all enter/exit transitions.

### Player Experience
- The Mini Player and full Player view use spring-based artwork crossfades when songs change.
- Button press feedback uses organic spring dynamics (`TouchSpring`) instead of flat scale tweens.
- Progress indicators, volume controls, and sleep timer switches now animate with deceleration easing.

### Tactile Interactions
- Song rows, album cards, playlist tiles, queue items, FABs, and settings rows all respond to touch with a consistent bounce-spring press animation.
- A unified `.bounceClick` modifier handles all press-scale interactions — eliminating dozens of duplicated `interactionSource` + `graphicsLayer` patterns.

### UI Consistency
- Corner shapes are unified via `JUNOCorners` design tokens and respect user corner preference settings.
- Shimmer loading states use a standardized soft glow animation across all screens.
- Bottom sheet rounded tops, dialog tiles, and input fields share a consistent shape language.

### Navigation Polish
- Screen enter/exit transitions use `JUNOMotion` timing and feel continuous rather than abrupt.
- All shared element motion is smoother.

### Performance
- Lazy list recomposition during fast scrolling is optimized.
- Shape and preference reads are now scoped at the screen container level, not evaluated per list item.
- Memory allocation during grid scrolling is reduced.

---

## What's Fixed

| Area | Fix |
|---|---|
| Motion inconsistencies | Replaced with `JUNOMotion` tokens throughout |
| UI alignment issues | Spacing and shape tokens applied consistently |
| Build version mismatch | `versionCode` / `versionName` updated to `554` / `5.5.4` |
| Shimmer API mismatch | `rememberShimmer` now receives required `shimmerBounds` |
| Spring type errors | `Float` and `Dp` spring specs are now type-safe separate tokens |
| Debug keystore missing | Local debug keystore created for APK signing |

---

## Installing

1. Go to [Releases](https://github.com/bharadwajsanket/juno/releases/tag/v5.5.4).
2. Download `JUNO-5.5.4-Universal.apk` (FOSS) or `JUNO-5.5.4-Universal-GMS.apk` (with Cast support).
3. Enable installation from unknown sources if prompted, then install.

---

## Building from Source

```bash
git clone https://github.com/bharadwajsanket/juno.git
cd juno
./gradlew assembleUniversalFossRelease
```

Requires JDK 17 or 21 and Android SDK API 26+.

---

## Thank You

Thank you to everyone who reported issues, suggested improvements, and kept using JUNO through the development of this release. This is a quiet update — the kind that makes everything feel a little smoother without you noticing exactly why.

If JUNO made your listening a little calmer, consider giving the repository a star.

— Built with care, one release at a time.
