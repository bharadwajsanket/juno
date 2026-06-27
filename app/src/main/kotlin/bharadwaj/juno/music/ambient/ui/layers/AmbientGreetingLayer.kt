package bharadwaj.juno.music.ambient.ui.layers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bharadwaj.juno.music.ambient.ui.AmbientGreeting
import bharadwaj.juno.music.ambient.ui.AmbientSceneColors

/**
 * Layer 4 (topmost) — Greeting text and subtitle.
 *
 * Text is left-aligned within the left 60% of the card so it does not
 * overlap the celestial body (sun/moon), which occupies the right 30–35%.
 *
 * The greeting line wraps naturally up to 2 lines — no ellipsis is ever
 * applied, so long display names remain fully readable.
 *
 * A subtle drop-shadow is applied when [AmbientSceneColors.needsTextShadow]
 * is true to ensure legibility over vivid or dark sky gradients.
 */
@Composable
fun AmbientGreetingLayer(
    greeting: AmbientGreeting,
    colors: AmbientSceneColors,
    modifier: Modifier = Modifier,
) {
    val shadow = if (colors.needsTextShadow) {
        Shadow(
            color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.30f),
            offset = Offset(0f, 1f),
            blurRadius = 5f,
        )
    } else null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start,
    ) {
        // Greeting — wraps up to 2 lines, never truncates with ellipsis
        Text(
            text = greeting.greeting,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                shadow = shadow,
            ),
            color = colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Clip,
            modifier = Modifier.fillMaxWidth(0.62f),
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Subtitle — one or two lines of supporting context
        Text(
            text = greeting.subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.05.sp,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                shadow = shadow,
            ),
            color = colors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Clip,
            modifier = Modifier.fillMaxWidth(0.58f),
        )
    }
}
