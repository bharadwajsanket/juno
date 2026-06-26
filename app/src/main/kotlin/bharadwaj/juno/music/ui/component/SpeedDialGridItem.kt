package bharadwaj.juno.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import bharadwaj.juno.music.R
import bharadwaj.juno.music.constants.ThumbnailCornerRadius

@Composable
fun SpeedDialGridItem(
    item: YTItem,
    isPinned: Boolean,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
) {
    val shape = remember(item) {
        if (item is ArtistItem) CircleShape else RoundedCornerShape(24.dp)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = shape
            )
            .clip(shape)
    ) {
        
        ItemThumbnail(
            thumbnailUrl = item.thumbnail,
            isActive = isActive,
            isPlaying = isPlaying,
            shape = shape,
            modifier = Modifier.fillMaxSize()
        )

        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.05f),
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 12.dp, vertical = 10.dp) 
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall, 
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            
            if (item !is SongItem) {
                Icon(
                    painter = painterResource(R.drawable.navigate_next),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
        }
    }
        
        if (isPinned) {
            Icon(
                painter = painterResource(R.drawable.ic_push_pin),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(16.dp)
            )
        }


    }
}
