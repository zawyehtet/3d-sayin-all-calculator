package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.logic.cleanNumberInput

/**
 * VisualTransformation that inserts live thousands commas for the integer part.
 */
class ThousandsSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val parts = raw.split('.')
        val intPart = parts[0]
        val decPart = if (parts.size > 1) "." + parts[1] else ""

        val intLen = intPart.length
        val transformedInt = StringBuilder()
        val origToTrans = IntArray(intLen + 1)
        val transToOrig = mutableListOf<Int>()

        var commaCount = 0
        for (i in 0 until intLen) {
            val distFromRight = intLen - i
            if (i > 0 && distFromRight % 3 == 0) {
                transformedInt.append(',')
                transToOrig.add(i)
                commaCount++
            }
            origToTrans[i] = i + commaCount
            transformedInt.append(intPart[i])
            transToOrig.add(i)
        }
        origToTrans[intLen] = intLen + commaCount
        transToOrig.add(intLen)

        val fullTransformed = transformedInt.toString() + decPart
        val transformedIntLen = transformedInt.length

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= intLen) {
                    return origToTrans[offset.coerceIn(0, intLen)]
                }
                val decOffset = offset - intLen
                return (transformedIntLen + decOffset).coerceAtMost(fullTransformed.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= transformedIntLen) {
                    val safeIdx = offset.coerceIn(0, transToOrig.size - 1)
                    return transToOrig[safeIdx]
                }
                val decOffset = offset - transformedIntLen
                return (intLen + decOffset).coerceAtMost(raw.length)
            }
        }

        return TransformedText(AnnotatedString(fullTransformed), offsetMapping)
    }
}

@Composable
fun FormattedNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String = "",
    suffix: String? = "ကျပ်",
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newText ->
            val cleaned = cleanNumberInput(newText)
            onValueChange(cleaned)
        },
        label = { Text(label) },
        suffix = suffix?.let { { Text(it) } },
        leadingIcon = leadingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = ThousandsSeparatorVisualTransformation(),
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    )
}
