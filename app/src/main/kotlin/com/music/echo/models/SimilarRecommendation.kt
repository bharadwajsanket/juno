

package bharadwajsanket.aether.music.models

import com.music.innertube.models.YTItem
import bharadwajsanket.aether.music.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
