

package bharadwaj.juno.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import bharadwaj.juno.music.ui.utils.fadingEdge

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import bharadwaj.juno.music.ui.theme.rememberBatterySaverState

@Composable
fun OnlineBlur(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
) {
    val isBatterySaver = rememberBatterySaverState()
    Box(modifier = modifier) {
        if (thumbnailUrl != null) {
            val context = LocalContext.current
            val request = remember(thumbnailUrl, isBatterySaver) {
                ImageRequest.Builder(context)
                    .data(thumbnailUrl)
                    .size(if (isBatterySaver) 32 else 128)
                    .crossfade(true)
                    .build()
            }
            AsyncImage(
                model = request,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (isBatterySaver) 15.dp else 50.dp)
                    .fadingEdge(bottom = 200.dp)
            )
        }
        
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        )
                    )
                )
        )
    }
}
