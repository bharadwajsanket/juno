package bharadwaj.juno.music.ambient.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import bharadwaj.juno.music.ambient.engine.AmbientAtmosphereCalculator
import bharadwaj.juno.music.ambient.model.AmbientAtmosphere
import bharadwaj.juno.music.ambient.model.AmbientState
import kotlinx.coroutines.delay

/** Minimum interval between atmosphere recalculations while the app is visible. */
private const val TICK_INTERVAL_MS = 60_000L

/**
 * Produces a continuously-updated [AmbientAtmosphere] that reflects the current
 * solar and lunar positions, cloud density, and star visibility.
 *
 * Mechanics:
 *  - Uses Compose's [produceState] so the coroutine is tied to the composition
 *    lifecycle — it starts when the composable enters the tree and is cancelled
 *    automatically when it leaves (app backgrounded, screen changed, etc.).
 *  - Recalculates every [TICK_INTERVAL_MS] (60 seconds) while visible.
 *  - On resume, the first calculation is immediate, ensuring the sky position
 *    is correct from the very first frame after returning from background.
 *  - No network calls, no I/O — pure math inside [AmbientAtmosphereCalculator].
 *
 * When [state] is not [AmbientState.Active] (e.g. Initializing, Permission Required),
 * returns [AmbientAtmosphere.Neutral] — a calm mid-afternoon atmosphere that
 * ensures the card never looks blank.
 *
 * The [state] parameter is a key for [produceState], so any repository update
 * (new weather, new location) causes an immediate recalculation in addition to
 * the regular 60-second tick.
 *
 * @param state   Current [AmbientState] collected from the repository.
 * @return        A [State] containing the latest [AmbientAtmosphere].
 */
@Composable
fun rememberAmbientAtmosphere(state: AmbientState): State<AmbientAtmosphere> =
    produceState(
        initialValue = AmbientAtmosphereCalculator.calculate(state),
        key1 = state,
    ) {
        // Recalculate immediately on state change (new weather/location from repo)
        value = AmbientAtmosphereCalculator.calculate(state)

        // Then continue ticking every 60 seconds for solar/lunar arc progression
        while (true) {
            delay(TICK_INTERVAL_MS)
            value = AmbientAtmosphereCalculator.calculate(state)
        }
    }
