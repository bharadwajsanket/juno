package bharadwaj.juno.music.ambient.ui

import androidx.lifecycle.ViewModel
import bharadwaj.juno.music.ambient.AmbientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Lightweight ViewModel that surfaces [AmbientRepository] into the Compose
 * composition tree via [hiltViewModel].
 *
 * This wrapper exists because Hilt DI bindings live in the ViewModel scope,
 * not in the Composable scope — the ViewModel survives configuration changes
 * while keeping the repository reference stable.
 *
 * No business logic lives here; all logic is in [AmbientRepository].
 */
@HiltViewModel
class AmbientSceneHostViewModel @Inject constructor(
    val repository: AmbientRepository,
) : ViewModel()
