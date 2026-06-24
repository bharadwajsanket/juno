

package bharadwaj.juno.music.ui.utils

fun String.resize(
    width: Int? = null,
    height: Int? = null,
): String {
    var w = width
    var h = height
    if (bharadwaj.juno.music.constants.DataSaverConfig.isSuperDataSaverEnabled) {
        if (w != null) w = if (w / 3 < 1) 1 else w / 3
        if (h != null) h = if (h / 3 < 1) 1 else h / 3
    }
    if (w == null && h == null) return this

    
    
    
    
    if (this.contains("i.ytimg.com")) {
        val targetQuality = if (w != null && w >= 1200) "maxresdefault.jpg" else "hqdefault.jpg"
        return this.replace(
            Regex("(default|mqdefault|hqdefault|sddefault|maxresdefault)\\.jpg"),
            targetQuality
        )
    }

    
    if (this.contains("googleusercontent.com") && this.contains("=w")) {
        val baseUrl = this.split("=w")[0]
        val wVal = w ?: 0
        val hVal = h ?: w ?: 0
        
        return "$baseUrl=w$wVal-h$hVal-p-l90-rj"
    }

    
    if (this.contains("yt3.ggpht.com")) {
        
        val baseUrl = this.split("=")[0].split("-s")[0]
        return "$baseUrl=s${w ?: h}"
    }

    
    "https://lh\\d\\.googleusercontent\\.com/.*".toRegex().matchEntire(this)?.let {
        val wVal = w ?: 0
        val hVal = h ?: w ?: 0
        return "${this.split("=")[0]}=w$wVal-h$hVal-p-l90-rj"
    }

    return this
}
