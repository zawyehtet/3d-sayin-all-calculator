package com.example.model

import java.time.LocalDate

/**
 * Immutable data representations for the calculation inputs and outputs.
 */
data class LedgerInputState(
    val date: LocalDate? = null,
    val amPm: String? = null, // null, "AM", or "PM"
    val round: String = "၁ ကြိမ်",
    val name: String = "",
    val salesInput: String = "",
    val rate: Double = 0.25, // default 25% (0.15, 0.17, 0.25, 0.30)
    val debtInput: String = "",
    val lumpInput: String = "",
    val daeAmountInput: String = "",
    val daeMultiplier: Int = 550, // default 550 (550, 600, 80, 75)
    val thwadAmountInput: String = "",
    val apaAmountInput: String = "",
    val apaMultiplier: Int = 3 // default 3 (3, 8, 15)
)

data class LedgerCalculationResult(
    val sales: Double = 0.0,
    val commission: Double = 0.0,
    val net: Double = 0.0,
    val debt: Double = 0.0,
    val lump: Double = 0.0,
    val all: Double = 0.0,
    val daeAmount: Double = 0.0,
    val daeResult: Double = 0.0,
    val thwadAmount: Double = 0.0,
    val thwadResult: Double = 0.0,
    val apaAmount: Double = 0.0,
    val apaResult: Double = 0.0,
    val pay: Double = 0.0,
    val diff: Double = 0.0,
    val sign: String = "" // "-" if all > pay, "+" if all < pay, else ""
) {
    val finalDisplayTotal: String
        get() {
            val formattedDiff = com.example.logic.formatAmount(diff)
            return when {
                sign.isNotEmpty() -> "$sign $formattedDiff"
                diff == 0.0 -> "0"
                else -> formattedDiff
            }
        }
}
