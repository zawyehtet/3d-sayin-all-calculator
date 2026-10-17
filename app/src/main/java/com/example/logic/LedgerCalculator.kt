package com.example.logic

import com.example.model.LedgerCalculationResult
import com.example.model.LedgerInputState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

object LedgerConstants {
    val ROUND_OPTIONS: List<String> = listOf(
        "၁ ကြိမ်", "၂ ကြိမ်", "၃ ကြိမ်", "၄ ကြိမ်", "၅ ကြိမ်",
        "၆ ကြိမ်", "၇ ကြိမ်", "၈ ကြိမ်", "၉ ကြိမ်", "၁၀ ကြိမ်",
        "၁၁ ကြိမ်", "၁၂ ကြိမ်", "၁၃ ကြိမ်", "၁၄ ကြိမ်", "၁၅ ကြိမ်",
        "၁၆ ကြိမ်", "၁၇ ကြိမ်", "၁၈ ကြိမ်", "၁၉ ကြိမ်", "၂၀ ကြိမ်",
        "၂၁ ကြိမ်", "၂၂ ကြိမ်", "၂၃ ကြိမ်", "၂၄ ကြိမ်",
        "2D"
    )

    val COMMISSION_RATES = listOf(0.15, 0.17, 0.25, 0.30)
    val DAE_MULTIPLIERS = listOf(550, 600, 80, 75)
    val APA_MULTIPLIERS = listOf(3, 8, 15)
}

/**
 * Converts Burmese Unicode digits (၀-၉) to ASCII digits (0-9).
 */
fun convertBurmeseToArabicDigits(input: String): String {
    val burmeseDigits = "၀၁၂၃၄၅၆၇၈၉"
    val sb = StringBuilder()
    for (ch in input) {
        val idx = burmeseDigits.indexOf(ch)
        if (idx >= 0) {
            sb.append(('0' + idx))
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

/**
 * Formats a round string for the summary.
 * For example: "၁ ကြိမ်" becomes "1 ကြိမ်", "၂၄ ကြိမ်" becomes "24 ကြိမ်", and "2D" remains "2D".
 */
fun formatRoundSummary(round: String): String {
    return convertBurmeseToArabicDigits(round).trim()
}

/**
 * Formats a Double value with thousands separators and up to 2 decimal places.
 * e.g., 0 -> "0", 1250000 -> "1,250,000", 1234.5 -> "1,234.5", 1234.56 -> "1,234.56"
 */
fun formatAmount(value: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#,##0.##", symbols)
    return df.format(value)
}

/**
 * Parses user input into a Double, accepting commas, spaces, and Burmese numbers.
 */
fun parseFormattedNumber(text: String): Double {
    if (text.isBlank()) return 0.0
    val normalized = convertBurmeseToArabicDigits(text)
        .replace(",", "")
        .replace(" ", "")
        .trim()
    return normalized.toDoubleOrNull() ?: 0.0
}

/**
 * Cleans live input string (removing extra commas) so that it can be stored and calculated.
 */
fun cleanNumberInput(input: String): String {
    val converted = convertBurmeseToArabicDigits(input)
    val filtered = converted.filter { it.isDigit() || it == '.' }
    val parts = filtered.split('.')
    return if (parts.size > 2) {
        parts[0] + "." + parts.subList(1, parts.size).joinToString("")
    } else {
        filtered
    }
}

/**
 * Formats live input with thousands separators while typing.
 */
fun formatInputForDisplay(cleanInput: String): String {
    if (cleanInput.isEmpty()) return ""
    val hasDecimal = cleanInput.contains('.')
    val parts = cleanInput.split('.')
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1] else ""

    val formattedInteger = if (integerPart.isNotEmpty()) {
        try {
            val num = integerPart.toLong()
            val symbols = DecimalFormatSymbols(Locale.US)
            val df = DecimalFormat("#,###", symbols)
            df.format(num)
        } catch (_: Exception) {
            integerPart
        }
    } else {
        ""
    }

    return if (hasDecimal) {
        if (formattedInteger.isEmpty()) "0.$decimalPart" else "$formattedInteger.$decimalPart"
    } else {
        formattedInteger
    }
}

/**
 * Pure calculation logic based on current inputs.
 */
fun calculateLedger(input: LedgerInputState): LedgerCalculationResult {
    val sales = parseFormattedNumber(input.salesInput)
    val commission = sales * input.rate
    val net = sales - commission

    val debt = parseFormattedNumber(input.debtInput)
    val lump = parseFormattedNumber(input.lumpInput)
    val all = net + debt + lump

    val daeAmount = parseFormattedNumber(input.daeAmountInput)
    val daeResult = daeAmount * input.daeMultiplier

    val thwadAmount = parseFormattedNumber(input.thwadAmountInput)
    val thwadResult = thwadAmount * 10.0

    val apaAmount = parseFormattedNumber(input.apaAmountInput)
    val apaResult = apaAmount * input.apaMultiplier

    val pay = daeResult + thwadResult + apaResult
    val diff = abs(all - pay)

    val sign = when {
        all > pay -> "-"
        all < pay -> "+"
        else -> ""
    }

    return LedgerCalculationResult(
        sales = sales,
        commission = commission,
        net = net,
        debt = debt,
        lump = lump,
        all = all,
        daeAmount = daeAmount,
        daeResult = daeResult,
        thwadAmount = thwadAmount,
        thwadResult = thwadResult,
        apaAmount = apaAmount,
        apaResult = apaResult,
        pay = pay,
        diff = diff,
        sign = sign
    )
}

/**
 * Generates the summary text strictly following the specified line order and hide rules.
 */
fun buildSummaryText(input: LedgerInputState, calc: LedgerCalculationResult): String {
    val lines = mutableListOf<String>()

    // Line 1: {round or 2D}
    if (input.round.isNotBlank()) {
        val roundFormatted = formatRoundSummary(input.round)
        lines.add(roundFormatted)
        lines.add("") // blank line after round
    }

    // Line 2: ရက်စွဲ= dd.MM.yy AM/PM (hide rule: no date -> hide)
    if (input.date != null) {
        val fmt = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy")
        val formattedDate = input.date.format(fmt)
        val dateValue = if (!input.amPm.isNullOrBlank()) {
            "ရက်စွဲ= $formattedDate ${input.amPm.trim()}"
        } else {
            "ရက်စွဲ= $formattedDate"
        }
        lines.add(dateValue)
    }

    // Line 3: အမည်= {name} (hide rule: no name -> hide)
    if (input.name.isNotBlank()) {
        lines.add("အမည်= ${input.name.trim()}")
    }

    // Line 4-6: ရောင်း, ကော်, အပ်ငွေ (hide rule: no sales -> hide)
    if (calc.sales > 0.0) {
        lines.add("ရောင်း: ${formatAmount(calc.sales)} ကျပ်")
        val ratePercent = (input.rate * 100).roundToInt()
        lines.add("ကော် (${ratePercent}%):  ${formatAmount(calc.commission)} ကျပ်")
        lines.add("အပ်ငွေ= ${formatAmount(calc.net)} ကျပ်")
    }

    // Line 7: ကျန် -= {debt} ကျပ် (hide rule: no debt -> hide)
    if (calc.debt > 0.0) {
        lines.add("ကျန် -= ${formatAmount(calc.debt)} ကျပ်")
    }

    // Line 8: အလုံး (-): {lump} ကျပ် (hide rule: no lump -> hide)
    if (calc.lump > 0.0) {
        lines.add("အလုံး (-): ${formatAmount(calc.lump)} ကျပ်")
    }

    // Line 9: စုစု -= {all} ကျပ် (hide rule: no debt and no lump -> hide)
    if (calc.debt > 0.0 || calc.lump > 0.0) {
        lines.add("စုစု -= ${formatAmount(calc.all)} ကျပ်")
    }

    // Line 10-11: အလျော်= {pay} ကျပ် and စုစုပေါင်း= {sign} {diff} ကျပ် (hide rule: pay = 0 -> hide)
    if (calc.pay > 0.0) {
        lines.add("အလျော်= ${formatAmount(calc.pay)} ကျပ်")
        val signPart = if (calc.sign.isNotEmpty()) "${calc.sign} " else ""
        lines.add("စုစုပေါင်း= ${signPart}${formatAmount(calc.diff)} ကျပ်")
    }

    return lines.joinToString("\n")
}
