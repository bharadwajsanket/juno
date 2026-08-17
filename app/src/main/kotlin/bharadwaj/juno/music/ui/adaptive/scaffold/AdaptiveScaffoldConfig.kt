package bharadwaj.juno.music.ui.adaptive.scaffold

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import bharadwaj.juno.music.ui.adaptive.navigation.AdaptiveNavigationStyle
import bharadwaj.juno.music.ui.adaptive.pane.PaneConfiguration

/**
 * Configuration options for [AdaptiveScaffold].
 */
@Immutable
data class AdaptiveScaffoldConfig(
    val navigationStyle: AdaptiveNavigationStyle = AdaptiveNavigationStyle.BottomBar,
    val paneConfiguration: PaneConfiguration = PaneConfiguration.SinglePane,
    val showMiniPlayer: Boolean = true,
    val containerColor: Color = Color.Unspecified
)
