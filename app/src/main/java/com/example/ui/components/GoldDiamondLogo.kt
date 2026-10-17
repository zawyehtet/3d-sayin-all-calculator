package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Gold logo for "ShweKhinMyay".
 *
 * TO SWAP IN YOUR OWN LOGO:
 * 1. Place your drawable in `res/drawable/your_logo_name.png` (or `.xml`)
 * 2. Pass `logoRes = R.drawable.your_logo_name` when calling this composable,
 *    or replace R.drawable.ic_gold_diamond below.
 */
@Composable
fun ShweKhinMyayLogo(
    modifier: Modifier = Modifier,
    @DrawableRes logoRes: Int = R.drawable.ic_gold_diamond
) {
    val goldBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFE082),
            Color(0xFFFFB300),
            Color(0xFFFF8F00),
            Color(0xFFFFD54F)
        )
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "ShweKhinMyay Diamond Logo",
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "ShweKhinMyay",
            style = TextStyle(
                brush = goldBrush,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}
