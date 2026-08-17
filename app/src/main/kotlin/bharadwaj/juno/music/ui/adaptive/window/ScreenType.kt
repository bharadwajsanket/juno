package bharadwaj.juno.music.ui.adaptive.window

/**
 * High-level categorization of the target hardware layout mode.
 */
enum class ScreenType {
    /**
     * Standard mobile phone screen factor.
     */
    Phone,

    /**
     * Dual-screen or foldable hardware.
     */
    Foldable,

    /**
     * Tablet-sized form factor (sw >= 600dp).
     */
    Tablet,

    /**
     * Desktop / ChromeOS environment.
     */
    Desktop,

    /**
     * Automotive environment (Android Auto / Automotive OS).
     */
    Auto,

    /**
     * Large TV / Leanback environment.
     */
    TV;

    val isPhone: Boolean get() = this == Phone
    val isTablet: Boolean get() = this == Tablet
    val isFoldable: Boolean get() = this == Foldable
    val isDesktop: Boolean get() = this == Desktop
}
