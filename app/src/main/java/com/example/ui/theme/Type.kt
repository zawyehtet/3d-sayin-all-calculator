package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Bundled Noto Sans Myanmar font
val NotoSansMyanmarFontFamily = FontFamily(
    Font(R.font.noto_sans_myanmar, FontWeight.Normal),
    Font(R.font.noto_sans_myanmar, FontWeight.Medium),
    Font(R.font.noto_sans_myanmar, FontWeight.Bold)
)

// Material 3 Typography with ~1.9 line height ratio to avoid Burmese text clipping
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 108.sp
    ),
    displayMedium = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 85.sp
    ),
    displaySmall = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 68.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 60.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 53.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 45.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 38.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 30.sp
    ),
    titleSmall = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 26.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 30.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 26.sp
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 23.sp
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 26.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 23.sp
    ),
    labelSmall = TextStyle(
        fontFamily = NotoSansMyanmarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 21.sp
    )
)
