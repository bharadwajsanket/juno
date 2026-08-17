package bharadwaj.juno.music.ui.adaptive.navigation

/**
 * High-level navigation component styles supported by JUNO Music adaptive layout architecture.
 */
enum class AdaptiveNavigationStyle {
    /**
     * Standard phone bottom navigation bar.
     */
    BottomBar,

    /**
     * Vertical navigation rail for medium displays and landscape modes.
     */
    NavigationRail,

    /**
     * Permanent navigation drawer for wide expanded displays / desktop.
     */
    PermanentDrawer,

    /**
     * Modal navigation drawer for compact views.
     */
    ModalDrawer;

    val isBottomBar: Boolean get() = this == BottomBar
    val isNavigationRail: Boolean get() = this == NavigationRail
    val isPermanentDrawer: Boolean get() = this == PermanentDrawer
    val isModalDrawer: Boolean get() = this == ModalDrawer
}
