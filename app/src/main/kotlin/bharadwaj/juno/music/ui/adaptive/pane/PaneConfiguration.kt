package bharadwaj.juno.music.ui.adaptive.pane

/**
 * Supported multi-pane layout topologies.
 */
enum class PaneConfiguration {
    /**
     * Single pane visible at a time (standard phone view).
     */
    SinglePane,

    /**
     * Dual side-by-side panes (tablet list-detail, foldable dual view).
     */
    DualPane,

    /**
     * Triple side-by-side panes (desktop wide layout).
     */
    TriplePane;

    val isSinglePane: Boolean get() = this == SinglePane
    val isDualPane: Boolean get() = this == DualPane
    val isTriplePane: Boolean get() = this == TriplePane
}
