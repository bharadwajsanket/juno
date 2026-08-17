package bharadwaj.juno.music.ui.adaptive.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveWindowInfo

/**
 * Adaptive system window insets data container.
 */
@Immutable
data class AdaptiveInsets(
    val top: Dp,
    val bottom: Dp,
    val left: Dp,
    val right: Dp
) {
    companion object {
        @Composable
        fun current(): AdaptiveInsets {
            val windowInfo = LocalAdaptiveWindowInfo.current
            val padding = WindowInsets.systemBars.asPaddingValues()
            return AdaptiveInsets(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
                left = padding.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                right = padding.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)
            )
        }
    }
}
