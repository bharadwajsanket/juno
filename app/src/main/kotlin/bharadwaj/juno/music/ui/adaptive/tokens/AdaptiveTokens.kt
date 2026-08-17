package bharadwaj.juno.music.ui.adaptive.tokens

import androidx.compose.runtime.Immutable

/**
 * Root design tokens container for JUNO Music adaptive design system.
 */
@Immutable
data class AdaptiveTokens(
    val spacing: SpacingTokens = SpacingTokens,
    val padding: PaddingTokens = PaddingTokens,
    val radius: RadiusTokens = RadiusTokens,
    val sizes: SizeTokens = SizeTokens,
    val artwork: ArtworkSizeTokens = ArtworkSizeTokens,
    val contentWidth: ContentWidthTokens = ContentWidthTokens
) {
    companion object {
        val Default = AdaptiveTokens()
    }
}
