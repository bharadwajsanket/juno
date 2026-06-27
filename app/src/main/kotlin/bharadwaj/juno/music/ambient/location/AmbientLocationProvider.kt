package bharadwaj.juno.music.ambient.location

import bharadwaj.juno.music.ambient.model.AmbientLocation
import kotlinx.coroutines.flow.Flow

/**
 * Contract for obtaining device location within the Ambient system.
 *
 * Implementations are expected to:
 *   - Operate in low-power mode (passive/network providers preferred over GPS).
 *   - Respect the granted permission level (coarse vs fine).
 *   - Never throw from [locationUpdates] — errors should be represented as
 *     a cold (empty or never-completing) flow so consumers can handle the
 *     no-data case gracefully.
 */
interface AmbientLocationProvider {

    /**
     * Returns the most recently cached location available without starting
     * active tracking. Returns null if no location is available at all.
     *
     * This is a suspend function because on some implementations a round-trip
     * to the system service may be needed.
     */
    suspend fun getLastKnownLocation(): AmbientLocation?

    /**
     * Cold flow that emits new [AmbientLocation] values as they become available.
     *
     * Collecting this flow starts location tracking; cancelling the coroutine
     * or the collection scope stops it. Implementations must clean up system
     * listeners when the flow is no longer collected.
     *
     * The flow does not emit until a valid location fix is obtained.
     */
    fun locationUpdates(): Flow<AmbientLocation>
}
