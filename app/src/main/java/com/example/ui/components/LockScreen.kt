package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LockScreen(
    enteredDigitsCount: Int,
    isError: Boolean,
    errorMessage: String = "လျှို့ဝှက်ကုဒ် မှားယွင်းနေပါသည်",
    onDigitClick: (Char) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("app_lock_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / Brand
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShweKhinMyayLogo(
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "App Locked",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "လျှို့ဝှက်ကုဒ် ထည့်သွင်းပါ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "မူလ လျှို့ဝှက်နံပါတ်: 7777",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )

                // Error message
                AnimatedVisibility(
                    visible = isError,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .testTag("lock_error_message")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredDigitsCount
                        val dotColor = when {
                            isError -> MaterialTheme.colorScheme.error
                            isFilled -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled || isError) dotColor else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .testTag("pin_dot_$i")
                        )
                    }
                }
            }

            // Numeric Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val burmeseDigits = listOf("၁", "၂", "၃", "၄", "၅", "၆", "၇", "၈", "၉")

                // Rows 1-3
                for (row in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0 until 3) {
                            val digitNum = row * 3 + col + 1
                            val charDigit = digitNum.toString()[0]
                            val burmeseDigit = burmeseDigits[digitNum - 1]

                            KeypadButton(
                                primaryText = digitNum.toString(),
                                subText = burmeseDigit,
                                testTag = "key_digit_$digitNum",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onDigitClick(charDigit)
                                }
                            )
                        }
                    }
                }

                // Row 4: Clear, 0, Backspace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Clear button
                    KeypadActionButton(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        testTag = "key_clear",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClearClick()
                        }
                    )

                    // 0 key
                    KeypadButton(
                        primaryText = "0",
                        subText = "၀",
                        testTag = "key_digit_0",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDigitClick('0')
                        }
                    )

                    // Backspace button
                    KeypadActionButton(
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        testTag = "key_backspace",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onBackspaceClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    primaryText: String,
    subText: String,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = primaryText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun KeypadActionButton(
    icon: @Composable () -> Unit,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}
