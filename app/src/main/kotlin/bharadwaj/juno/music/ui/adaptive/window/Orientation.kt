package bharadwaj.juno.music.ui.adaptive.window

/**
 * Screen orientation concept for adaptive layout calculations.
 */
enum class Orientation {
    Portrait,
    Landscape;

    val isPortrait: Boolean get() = this == Portrait
    val isLandscape: Boolean get() = this == Landscape
}
