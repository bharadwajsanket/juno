package bharadwaj.juno.music.ui.adaptive.utils

import androidx.compose.runtime.compositionLocalOf
import bharadwaj.juno.music.ui.adaptive.dimensions.AdaptiveDimensions
import bharadwaj.juno.music.ui.adaptive.spacing.AdaptiveSpacing
import bharadwaj.juno.music.ui.adaptive.tokens.AdaptiveTokens
import bharadwaj.juno.music.ui.adaptive.typography.AdaptiveTypography
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo

/**
 * Composition local providing current [AdaptiveWindowInfo].
 */
val LocalAdaptiveWindowInfo = compositionLocalOf { AdaptiveWindowInfo.Default }

/**
 * Composition local providing root [AdaptiveTokens].
 */
val LocalAdaptiveTokens = compositionLocalOf { AdaptiveTokens.Default }

/**
 * Composition local providing current [AdaptiveSpacing].
 */
val LocalAdaptiveSpacing = compositionLocalOf { AdaptiveSpacing.fromWindowInfo(AdaptiveWindowInfo.Default) }

/**
 * Composition local providing current [AdaptiveDimensions].
 */
val LocalAdaptiveDimensions = compositionLocalOf { AdaptiveDimensions.fromWindowInfo(AdaptiveWindowInfo.Default) }

/**
 * Composition local providing current [AdaptiveTypography].
 */
val LocalAdaptiveTypography = compositionLocalOf { AdaptiveTypography.fromWindowInfo(AdaptiveWindowInfo.Default) }
