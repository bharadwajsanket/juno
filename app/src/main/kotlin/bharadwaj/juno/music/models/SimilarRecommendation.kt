

package bharadwaj.juno.music.models

import com.music.innertube.models.YTItem
import bharadwaj.juno.music.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
