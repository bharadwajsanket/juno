# Changelog

All notable changes to JUNO are documented here.

---

## v5.5.4 — Motion & Polish

> Released: 2026-07-16

### Improved

- Unified motion system — all animation durations, spring constants, and easing curves are defined in a single `JUNOMotion` token system
- Premium player animations — Mini Player and full Player use spring-based artwork crossfades and intentional deceleration curves
- Tactile interactions — song rows, album cards, playlist tiles, and FABs respond with consistent bounce-spring press animations via `.bounceClick`
- UI consistency — corner shapes, shimmer loading states, and bottom sheet styling are unified across all screens
- Navigation polish — screen enter/exit transitions are standardized and feel continuous
- Scrolling performance — lazy list recomposition optimized; shape and preference lookups scoped at the container level, not per-item

### Fixed

- Motion inconsistencies — inconsistent durations and mismatched easing replaced with `JUNOMotion` tokens
- UI alignment issues — spacing and shape tokens applied consistently throughout
- Build and version cleanup — `versionCode` and `versionName` updated to `554` / `5.5.4`
- Shimmer API mismatch — `rememberShimmer` now receives required `shimmerBounds` parameter
- Spring type mismatches — `Float` and `Dp` spring specs separated into type-safe token variants

---
