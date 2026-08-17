package bharadwaj.juno.music.ui.adaptive.typography

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import bharadwaj.juno.music.ui.adaptive.window.AdaptiveWindowInfo
import bharadwaj.juno.music.ui.adaptive.window.WindowWidthSizeClass

/**
 * Adaptive typography helper that scales typography hierarchies smoothly across window size classes.
 */
@Immutable
data class AdaptiveTypography(
    val scaleFactor: Float = 1.0f
) {
    fun scaleTextStyle(style: TextStyle): TextStyle {
        if (scaleFactor == 1.0f) return style
        return style.copy(
            fontSize = style.fontSize * scaleFactor,
            lineHeight = style.lineHeight * scaleFactor
        )
    }

    companion object {
        fun fromWindowInfo(windowInfo: AdaptiveWindowInfo): AdaptiveTypography = when (windowInfo.widthSizeClass) {
            WindowWidthSizeClass.Compact -> AdaptiveTypography(scaleFactor = 1.0f)
            WindowWidthSizeClass.Medium -> AdaptiveTypography(scaleFactor = 1.05f)
            WindowWidthSizeClass.Expanded -> AdaptiveTypography(scaleFactor = 1.1f)
        }

        fun applyAdaptiveScale(typography: Typography, windowInfo: AdaptiveWindowInfo): Typography {
            val scale = fromWindowInfo(windowInfo)
            if (scale.scaleFactor == 1.0f) return typography

            return Typography(
                displayLarge = scale.scaleTextStyle(typography.displayLarge),
                displayMedium = scale.scaleTextStyle(typography.displayMedium),
                displaySmall = scale.scaleTextStyle(typography.displaySmall),
                headlineLarge = scale.scaleTextStyle(typography.headlineLarge),
                headlineMedium = scale.scaleTextStyle(typography.headlineMedium),
                headlineSmall = scale.scaleTextStyle(typography.headlineSmall),
                titleLarge = scale.scaleTextStyle(typography.titleLarge),
                titleMedium = scale.scaleTextStyle(typography.titleMedium),
                titleSmall = scale.scaleTextStyle(typography.titleSmall),
                bodyLarge = scale.scaleTextStyle(typography.bodyLarge),
                bodyMedium = scale.scaleTextStyle(typography.bodyMedium),
                bodySmall = scale.scaleTextStyle(typography.bodySmall),
                labelLarge = scale.scaleTextStyle(typography.labelLarge),
                labelMedium = scale.scaleTextStyle(typography.labelMedium),
                labelSmall = scale.scaleTextStyle(typography.labelSmall)
            )
        }
    }
}
