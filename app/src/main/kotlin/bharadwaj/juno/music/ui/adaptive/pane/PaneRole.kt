package bharadwaj.juno.music.ui.adaptive.pane

/**
 * Identifies the functional role of a pane within multi-pane adaptive layouts.
 */
enum class PaneRole {
    /**
     * Main navigation list or primary view (e.g. Song list, Library index).
     */
    Primary,

    /**
     * Secondary detail view (e.g. Now Playing, Album details, Lyrics).
     */
    Secondary,

    /**
     * Additional details or metadata inspector.
     */
    Detail,

    /**
     * Auxiliary panel (e.g. Queue list, Equalizer panel).
     */
    Auxiliary
}
