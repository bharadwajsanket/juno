package bharadwaj.juno.music.ambient.model

/**
 * Top-level state emitted by [bharadwaj.juno.music.ambient.AmbientRepository].
 *
 * Consumers should handle all states to provide graceful degradation:
 *   Initializing → waiting for first data
 *   LocationPermissionRequired → prompt the user at an appropriate moment
 *   Active → full ambient data is available
 *   Error → something went wrong; previous state may still be valid
 */
sealed class AmbientState {

    /** The repository has started but hasn't resolved an initial state yet. */
    data object Initializing : AmbientState()

    /**
     * Location permission (coarse at minimum) has not been granted.
     * The repository is paused until permission is provided and
     * [bharadwaj.juno.music.ambient.AmbientRepository.refresh] is called.
     */
    data object LocationPermissionRequired : AmbientState()

    /**
     * A fully resolved ambient state is available.
     *
     * @param scene           The currently active ambient scene.
     * @param location        The location snapshot used for this resolution.
     * @param weather         The weather snapshot used for this resolution.
     * @param timeData        Resolved time data including sunrise/sunset and bucket.
     * @param isOffline       True when weather data comes from cache due to no connectivity.
     * @param debugForcedScene When non-null, this scene overrides [scene] for rendering purposes.
     *                         Set via [bharadwaj.juno.music.ambient.AmbientRepository.forceScene].
     */
    data class Active(
        val scene: AmbientScene,
        val location: AmbientLocation,
        val weather: AmbientWeather,
        val timeData: AmbientTimeData,
        val isOffline: Boolean,
        val debugForcedScene: AmbientScene? = null,
    ) : AmbientState() {

        /**
         * The effective scene to render — respects debug override when set.
         */
        val effectiveScene: AmbientScene get() = debugForcedScene ?: scene
    }

    /**
     * An unrecoverable error occurred during state resolution.
     *
     * @param reason  Human-readable description for logging / debug display.
     */
    data class Error(val reason: String) : AmbientState()
}
