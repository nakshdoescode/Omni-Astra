// Theme.kt

package com.mochyy.mochymusic.ui.theme

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

val DefaultThemeColor = Color(0xFF4D8DFF)

/**
 * mochy renders dark-only.
 *
 * The light palette was never finished - large parts of the UI hardcode white text and dark
 * glass - so on a light-mode phone the app looked broken rather than light. Pinned here as one
 * switch instead of being decided independently at every call site.
 */
const val ForceDarkTheme = true

@Composable
fun MochyTheme(
    darkTheme: Boolean = ForceDarkTheme,
    pureBlack: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    // Dynamic (Material You) colour is deliberately not used - the app ships its own palette.
    // The branch is gone rather than dead-but-present: dynamic*ColorScheme is API 31.
    val baseColorScheme = run {

        if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF82B1FF),
                onPrimary = Color(0xFF071A33),
                primaryContainer = Color(0xFF1E3A62),
                onPrimaryContainer = Color(0xFFD8E6FF),
                secondary = Color(0xFFA8C7FA),
                onSecondary = Color(0xFF10213A),
                secondaryContainer = Color(0xFF263B59),
                onSecondaryContainer = Color(0xFFD8E6FF),
                background = Color(0xFF050A12),
                onBackground = Color.White,
                surface = Color(0xFF080E18),
                onSurface = Color.White,
                surfaceVariant = Color(0xFF172231),
                onSurfaceVariant = Color(0xFFD6E2F2),
                surfaceTint = Color(0xFF82B1FF)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF245DAA),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFD8E6FF),
                onPrimaryContainer = Color(0xFF102B50),
                secondary = Color(0xFF405F88),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFDCE7F7),
                onSecondaryContainer = Color(0xFF1B304B),
                background = Color(0xFFF7F9FD),
                onBackground = Color(0xFF111820),
                surface = Color(0xFFFBFCFF),
                onSurface = Color(0xFF111820),
                surfaceVariant = Color(0xFFE7EDF6),
                onSurfaceVariant = Color(0xFF35465B),
                surfaceTint = Color(0xFF245DAA)
            )
        }
    }

    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme) {
        if (darkTheme && pureBlack) {
            baseColorScheme.pureBlack(true)
        } else {
            baseColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
    ) {
        ProvideTextStyle(
            value = AppTypography.bodyLarge,
            content = content
        )
    }
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

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
