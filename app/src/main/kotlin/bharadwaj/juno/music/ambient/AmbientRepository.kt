package bharadwaj.juno.music.ambient

import bharadwaj.juno.music.ambient.model.AmbientScene
import bharadwaj.juno.music.ambient.model.AmbientState
import kotlinx.coroutines.flow.StateFlow

/**
 * Single source of truth for the Ambient system.
 *
 * Exposes a [StateFlow] of [AmbientState] that consumers can collect to react
 * to scene changes without managing location or weather subscriptions directly.
 *
 * Threading: All public functions are coroutine-safe. The [state] flow is safe
 * to collect from any coroutine context.
 */
interface AmbientRepository {

    /**
     * Hot [StateFlow] of the current ambient state.
     *
     * Starts emitting [AmbientState.Initializing] and transitions through:
     *   [AmbientState.LocationPermissionRequired] → if no location permission
     *   [AmbientState.Active]                    → when data is available
     *   [AmbientState.Error]                     → on unrecoverable failure
     */
    val state: StateFlow<AmbientState>

    /**
     * Forces a specific scene for debugging purposes.
     *
     * When [scene] is non-null, [AmbientState.Active.effectiveScene] returns
     * [scene] instead of the engine-resolved scene.
     * Passing null clears the override and restores engine control.
     *
     * Has no effect when the current state is not [AmbientState.Active].
     *
     * @param scene  The scene to force, or null to clear the override.
     */
    fun forceScene(scene: AmbientScene?)

    /**
     * Triggers an immediate re-evaluation of location + weather + time.
     *
     * Useful after:
     *  - The user grants location permission.
     *  - The app foregrounds after a long background period.
     *  - The user manually refreshes from a debug/settings screen.
     */
    suspend fun refresh()

    /**
     * Pauses the location updates and background work when the UI is not visible
     * to conserve battery.
     */
    fun stop()
}
