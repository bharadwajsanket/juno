package bharadwaj.juno.music.ui.adaptive.layout

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import bharadwaj.juno.music.ui.adaptive.theme.LocalAdaptiveSpacing

/**
 * Standardized adaptive spacer element using adaptive spacing tokens.
 */
@Composable
fun AdaptiveSpacer(
    modifier: Modifier = Modifier,
    height: Dp = LocalAdaptiveSpacing.current.itemSpacing,
    width: Dp = LocalAdaptiveSpacing.current.itemSpacing
) {
    Spacer(
        modifier = modifier
            .height(height)
            .width(width)
    )
}

@Composable
fun VerticalAdaptiveSpacer(
    height: Dp = LocalAdaptiveSpacing.current.itemSpacing,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier.height(height))
}

@Composable
fun HorizontalAdaptiveSpacer(
    width: Dp = LocalAdaptiveSpacing.current.itemSpacing,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier.width(width))
}
