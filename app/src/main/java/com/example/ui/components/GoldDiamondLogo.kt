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
 * Clover brand logo for "ShweKhinMyay" using the reference 4-leaf pixel clover.
 */
@Composable
fun ShweKhinMyayLogo(
    modifier: Modifier = Modifier,
    @DrawableRes logoRes: Int = R.drawable.ic_clover
) {
    val emeraldBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8EE378),
            Color(0xFF4CAF50),
            Color(0xFF2E7D32)
        )
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "Clover Logo",
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = "ShweKhinMyay",
            style = TextStyle(
                brush = emeraldBrush,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}
