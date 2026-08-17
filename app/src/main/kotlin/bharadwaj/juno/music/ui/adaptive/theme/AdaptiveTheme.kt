package bharadwaj.juno.music.ui.adaptive.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import bharadwaj.juno.music.ui.adaptive.dimensions.AdaptiveDimensions
import bharadwaj.juno.music.ui.adaptive.spacing.AdaptiveSpacing
import bharadwaj.juno.music.ui.adaptive.tokens.AdaptiveTokens
import bharadwaj.juno.music.ui.adaptive.typography.AdaptiveTypography
import bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveDimensions
import bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveSpacing
import bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveTokens
import bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveTypography
import bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveWindowInfo
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo
import bharadwaj.juno.music.ui.adaptive.window.rememberAdaptiveWindowInfo

// Re-export composition locals for clean access via theme package
val LocalAdaptiveWindowInfo get() = bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveWindowInfo
val LocalAdaptiveTokens get() = bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveTokens
val LocalAdaptiveSpacing get() = bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveSpacing
val LocalAdaptiveDimensions get() = bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveDimensions
val LocalAdaptiveTypography get() = bharadwaj.juno.music.ui.adaptive.utils.LocalAdaptiveTypography

/**
 * Root Adaptive Theme wrapper that injects adaptive composition locals into the Compose hierarchy.
 */
@Composable
fun AdaptiveTheme(
    windowInfo: AdaptiveWindowInfo = rememberAdaptiveWindowInfo(),
    tokens: AdaptiveTokens = AdaptiveTokens.Default,
    content: @Composable () -> Unit
) {
    val spacing = AdaptiveSpacing.fromWindowInfo(windowInfo)
    val dimensions = AdaptiveDimensions.fromWindowInfo(windowInfo)
    val typographyScale = AdaptiveTypography.fromWindowInfo(windowInfo)

    val scaledMaterialTypography = AdaptiveTypography.applyAdaptiveScale(
        typography = MaterialTheme.typography,
        windowInfo = windowInfo
    )

    CompositionLocalProvider(
        LocalAdaptiveWindowInfo provides windowInfo,
        LocalAdaptiveTokens provides tokens,
        LocalAdaptiveSpacing provides spacing,
        LocalAdaptiveDimensions provides dimensions,
        LocalAdaptiveTypography provides typographyScale
    ) {
        MaterialTheme(
            typography = scaledMaterialTypography,
            content = content
        )
    }
}

/**
 * Easy theme access object for adaptive values.
 * Usage: `AdaptiveTheme.windowInfo`, `AdaptiveTheme.spacing`, `AdaptiveTheme.tokens`, etc.
 */
object AdaptiveTheme {
    val windowInfo: AdaptiveWindowInfo
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveWindowInfo.current

    val tokens: AdaptiveTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveTokens.current

    val spacing: AdaptiveSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveSpacing.current

    val dimensions: AdaptiveDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveDimensions.current

    val typography: AdaptiveTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveTypography.current
}
