

package bharadwaj.juno.music.ui.theme

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

val DefaultThemeColor = Color(0xFF9F86C0)

@Composable
fun junoMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    
    val useSystemDynamicColor = (themeColor == DefaultThemeColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    val baseColorScheme = if (useSystemDynamicColor) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        rememberDynamicColorScheme(
            seedColor = themeColor, 
            isDark = darkTheme,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
            style = PaletteStyle.TonalSpot 
        )
    }

    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme) {
        baseColorScheme.applyJunoColorScheme(isDark = darkTheme, pureBlack = pureBlack)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, 
        content = content
    )
}

fun Bitmap.extractThemeColor(): Color {
    val colorsToPopulation = Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .swatches
        .associate { it.rgb to it.population }
    val rankedColors = Score.score(colorsToPopulation)
    return Color(rankedColors.first())
}

fun Bitmap.extractGradientColors(): List<Color> {
    val extractedColors = Palette.from(this)
        .maximumColorCount(64)
        .generate()
        .swatches
        .associate { it.rgb to it.population }

    val orderedColors = Score.score(extractedColors, 2, 0xff4285f4.toInt(), true)
        .sortedByDescending { Color(it).luminance() }

    return if (orderedColors.size >= 2)
        listOf(Color(orderedColors[0]), Color(orderedColors[1]))
    else
        listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

fun ColorScheme.applyJunoColorScheme(isDark: Boolean, pureBlack: Boolean): ColorScheme {
    if (!isDark) return this
    val bg = if (pureBlack) Color.Black else Color(0xFF090909)
    val surf = if (pureBlack) Color.Black else Color(0xFF151515)
    val surfVar = if (pureBlack) Color(0xFF0C0C0C) else Color(0xFF1B1B1B)
    return copy(
        background = bg,
        surface = surf,
        surfaceVariant = surfVar,
        surfaceContainer = surfVar,
        surfaceContainerHigh = if (pureBlack) Color(0xFF121212) else Color(0xFF222222),
        surfaceContainerHighest = if (pureBlack) Color(0xFF181818) else Color(0xFF2B2B2B),
        surfaceContainerLow = if (pureBlack) Color(0xFF060606) else Color(0xFF121212),
        surfaceContainerLowest = if (pureBlack) Color.Black else Color(0xFF080808),
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFFA8A8A8),
        outline = if (pureBlack) Color(0xFF1B1B1B) else Color(0xFF2A2A2A),
        outlineVariant = if (pureBlack) Color(0xFF101010) else Color(0xFF1F1F1F)
    )
}

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
