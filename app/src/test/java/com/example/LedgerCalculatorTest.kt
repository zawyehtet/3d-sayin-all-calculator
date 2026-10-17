package com.example

import com.example.logic.buildSummaryText
import com.example.logic.calculateLedger
import com.example.logic.convertBurmeseToArabicDigits
import com.example.logic.formatAmount
import com.example.logic.formatRoundSummary
import com.example.logic.parseFormattedNumber
import com.example.model.LedgerInputState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LedgerCalculatorTest {

    @Test
    fun testBurmeseNumeralConversion() {
        assertEquals("1", convertBurmeseToArabicDigits("၁"))
        assertEquals("24", convertBurmeseToArabicDigits("၂၄"))
        assertEquals("1234567890", convertBurmeseToArabicDigits("၁၂၃၄၅၆၇၈၉၀"))
        assertEquals("1 ကြိမ်", formatRoundSummary("၁ ကြိမ်"))
        assertEquals("24 ကြိမ်", formatRoundSummary("၂၄ ကြိမ်"))
        assertEquals("2D", formatRoundSummary("2D"))
    }

    @Test
    fun testParseFormattedNumberWithBurmeseAndCommas() {
        assertEquals(100000.0, parseFormattedNumber("100,000"), 0.001)
        assertEquals(55000.5, parseFormattedNumber("၅၅,၀၀၀.၅"), 0.001)
        assertEquals(0.0, parseFormattedNumber(""), 0.001)
    }

    @Test
    fun testCalculationFormulas() {
        val input = LedgerInputState(
            salesInput = "100000",
            rate = 0.25,
            debtInput = "5000",
            lumpInput = "2000",
            daeAmountInput = "100",
            daeMultiplier = 550,
            thwadAmountInput = "200",
            apaAmountInput = "50",
            apaMultiplier = 3
        )
        val result = calculateLedger(input)

        // sales = 100,000
        // commission = 100,000 * 0.25 = 25,000
        assertEquals(25000.0, result.commission, 0.001)
        // net = 100,000 - 25,000 = 75,000
        assertEquals(75000.0, result.net, 0.001)
        // all = 75,000 + 5,000 + 2,000 = 82,000
        assertEquals(82000.0, result.all, 0.001)

        // dae = 100 * 550 = 55,000
        assertEquals(55000.0, result.daeResult, 0.001)
        // thwad = 200 * 10 = 2,000
        assertEquals(2000.0, result.thwadResult, 0.001)
        // apa = 50 * 3 = 150
        assertEquals(150.0, result.apaResult, 0.001)
        // pay = 55,000 + 2,000 + 150 = 57,150
        assertEquals(57150.0, result.pay, 0.001)

        // all (82,000) > pay (57,150) -> sign = "-", diff = 24,850
        assertEquals(24850.0, result.diff, 0.001)
        assertEquals("-", result.sign)
    }

    @Test
    fun testSignCases() {
        // Case 1: all > pay -> sign = "-"
        val inputMinus = LedgerInputState(
            salesInput = "10000",
            rate = 0.25, // net = 7500
            daeAmountInput = "10",
            daeMultiplier = 550 // pay = 5500
        )
        val resultMinus = calculateLedger(inputMinus)
        assertEquals("-", resultMinus.sign)
        assertEquals(2000.0, resultMinus.diff, 0.001)

        // Case 2: all < pay -> sign = "+"
        val inputPlus = LedgerInputState(
            salesInput = "10000",
            rate = 0.25, // net = 7500
            daeAmountInput = "20",
            daeMultiplier = 550 // pay = 11000
        )
        val resultPlus = calculateLedger(inputPlus)
        assertEquals("+", resultPlus.sign)
        assertEquals(3500.0, resultPlus.diff, 0.001)

        // Case 3: all == pay -> sign = "" (diff = 0)
        val inputZero = LedgerInputState(
            salesInput = "10000",
            rate = 0.25, // net = 7500
            thwadAmountInput = "750" // thwad = 750 * 10 = 7500
        )
        val resultZero = calculateLedger(inputZero)
        assertEquals("", resultZero.sign)
        assertEquals(0.0, resultZero.diff, 0.001)
    }

    @Test
    fun testSummaryHideRules() {
        // Complete state
        val fullInput = LedgerInputState(
            date = java.time.LocalDate.of(2026, 10, 17),
            amPm = "AM",
            round = "၁ ကြိမ်",
            name = "မောင်မောင်",
            salesInput = "100000",
            rate = 0.25,
            debtInput = "5000",
            lumpInput = "2000",
            daeAmountInput = "100",
            daeMultiplier = 550
        )
        val fullCalc = calculateLedger(fullInput)
        val fullSummary = buildSummaryText(fullInput, fullCalc)

        assertTrue(fullSummary.startsWith("1 ကြိမ်\n\n"))
        assertTrue(fullSummary.contains("ရက်စွဲ= 17.10.26 AM"))
        assertTrue(fullSummary.contains("အမည်= မောင်မောင်"))
        assertTrue(fullSummary.contains("ရောင်း: 100,000 ကျပ်"))
        assertTrue(fullSummary.contains("ကော် (25%):  25,000 ကျပ်"))
        assertTrue(fullSummary.contains("အပ်ငွေ= 75,000 ကျပ်"))
        assertTrue(fullSummary.contains("ကျန် -= 5,000 ကျပ်"))
        assertTrue(fullSummary.contains("အလုံး (-): 2,000 ကျပ်"))
        assertTrue(fullSummary.contains("စုစု -= 82,000 ကျပ်"))
        assertTrue(fullSummary.contains("အလျော်= 55,000 ကျပ်"))
        assertTrue(fullSummary.contains("စုစုပေါင်း= - 27,000 ကျပ်"))

        // Test hide rules:
        // 1. no name -> hide အမည်
        // 2. no date -> hide ရက်စွဲ
        // 3. no sales -> hide ရောင်း, ကော်, အပ်ငွေ
        // 4. no debt -> hide ကျန်
        // 5. no lump -> hide အလုံး
        // 6. no debt and no lump -> hide စုစု
        // 7. pay = 0 -> hide အလျော် and စုစုပေါင်း
        val emptyInput = LedgerInputState(
            date = null,
            name = "",
            salesInput = "0",
            debtInput = "0",
            lumpInput = "0",
            daeAmountInput = "0",
            thwadAmountInput = "0",
            apaAmountInput = "0"
        )
        val emptyCalc = calculateLedger(emptyInput)
        val emptySummary = buildSummaryText(emptyInput, emptyCalc)

        assertFalse(emptySummary.contains("ရက်စွဲ="))
        assertFalse(emptySummary.contains("အမည်="))
        assertFalse(emptySummary.contains("ရောင်း:"))
        assertFalse(emptySummary.contains("ကော်"))
        assertFalse(emptySummary.contains("အပ်ငွေ="))
        assertFalse(emptySummary.contains("ကျန် -="))
        assertFalse(emptySummary.contains("အလုံး (-):"))
        assertFalse(emptySummary.contains("စုစု -="))
        assertFalse(emptySummary.contains("အလျော်="))
        assertFalse(emptySummary.contains("စုစုပေါင်း="))
    }

    @Test
    fun testAmountFormatting() {
        assertEquals("0", formatAmount(0.0))
        assertEquals("1,000", formatAmount(1000.0))
        assertEquals("1,234.5", formatAmount(1234.5))
        assertEquals("1,234.56", formatAmount(1234.56))
        assertEquals("1,234.57", formatAmount(1234.567))
    }
}
