package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.em

private val DarkColorScheme = darkColorScheme(
    primary = TealDark,
    onPrimary = OnTealDark,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = OnTealContainerDark,
    secondary = TealDark,
    onSecondary = OnTealDark,
    secondaryContainer = TealContainerDark,
    onSecondaryContainer = OnTealContainerDark,
    tertiary = GoldPrimary,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = TealLight,
    onPrimary = OnTealLight,
    primaryContainer = TealContainerLight,
    onPrimaryContainer = OnTealContainerLight,
    secondary = TealLight,
    onSecondary = OnTealLight,
    secondaryContainer = TealContainerLight,
    onSecondaryContainer = OnTealContainerLight,
    tertiary = GoldDark,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

// Sign colors composition locals
val LocalPlusColor = staticCompositionLocalOf { PlusColorLight }
val LocalMinusColor = staticCompositionLocalOf { MinusColorLight }

object LedgerColors {
    val plusColor: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalPlusColor.current

    val minusColor: Color
        @Composable
        @ReadOnlyComposable
        get() = LocalMinusColor.current
}

@Composable
fun LedgerCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val plusColor = if (darkTheme) PlusColorDark else PlusColorLight
    val minusColor = if (darkTheme) MinusColorDark else MinusColorLight

    CompositionLocalProvider(
        LocalPlusColor provides plusColor,
        LocalMinusColor provides minusColor
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.copy(
                    fontFamily = NotoSansMyanmarFontFamily,
                    lineHeight = 1.9.em
                ),
                content = content
            )
        }
    }
}
