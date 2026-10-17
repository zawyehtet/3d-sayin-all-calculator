package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.data.CalculationHistoryRepository
import com.example.data.local.AppDatabase
import com.example.data.local.SavedCalculation
import com.example.logic.LedgerConstants
import com.example.logic.buildSummaryText
import com.example.logic.calculateLedger
import com.example.logic.cleanNumberInput
import com.example.model.LedgerCalculationResult
import com.example.model.LedgerInputState
import com.example.security.LockManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class LedgerUiState(
    val input: LedgerInputState = LedgerInputState(),
    val calculation: LedgerCalculationResult = LedgerCalculationResult(),
    val summaryText: String = "",
    val isLocked: Boolean = true,
    val isLockEnabled: Boolean = true,
    val enteredPin: String = "",
    val isPinError: Boolean = false
)

class LedgerViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val lockManager = LockManager(application)
    private val historyRepository = CalculationHistoryRepository(
        AppDatabase.getDatabase(application).savedCalculationDao()
    )

    val recentCalculations: StateFlow<List<SavedCalculation>> = historyRepository.recentCalculations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        private const val KEY_DATE = "ledger_date"
        private const val KEY_AM_PM = "ledger_am_pm"
        private const val KEY_ROUND = "ledger_round"
        private const val KEY_NAME = "ledger_name"
        private const val KEY_SALES = "ledger_sales"
        private const val KEY_RATE = "ledger_rate"
        private const val KEY_DEBT = "ledger_debt"
        private const val KEY_LUMP = "ledger_lump"
        private const val KEY_DAE_AMOUNT = "ledger_dae_amount"
        private const val KEY_DAE_MULTIPLIER = "ledger_dae_multiplier"
        private const val KEY_THWAD_AMOUNT = "ledger_thwad_amount"
        private const val KEY_APA_AMOUNT = "ledger_apa_amount"
        private const val KEY_APA_MULTIPLIER = "ledger_apa_multiplier"
        private const val KEY_IS_LOCKED = "ledger_is_locked"
    }

    // Lock states
    private val isLockEnabledFlow = MutableStateFlow(lockManager.isLockEnabled())
    private val isLockedFlow = savedStateHandle.getStateFlow(KEY_IS_LOCKED, lockManager.isLockEnabled())
    private val enteredPinFlow = MutableStateFlow("")
    private val isPinErrorFlow = MutableStateFlow(false)

    // State flows from SavedStateHandle to survive activity destruction & process death
    val dateFlow: StateFlow<LocalDate?> = savedStateHandle.getStateFlow<String?>(KEY_DATE, null)
        .map { it?.let { str -> runCatching { LocalDate.parse(str) }.getOrNull() } }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)
    val amPmFlow = savedStateHandle.getStateFlow<String?>(KEY_AM_PM, null)
    val roundFlow = savedStateHandle.getStateFlow(KEY_ROUND, LedgerConstants.ROUND_OPTIONS.first())
    val nameFlow = savedStateHandle.getStateFlow(KEY_NAME, "")
    val salesFlow = savedStateHandle.getStateFlow(KEY_SALES, "")
    val rateFlow = savedStateHandle.getStateFlow(KEY_RATE, 0.25)
    val debtFlow = savedStateHandle.getStateFlow(KEY_DEBT, "")
    val lumpFlow = savedStateHandle.getStateFlow(KEY_LUMP, "")
    val daeAmountFlow = savedStateHandle.getStateFlow(KEY_DAE_AMOUNT, "")
    val daeMultiplierFlow = savedStateHandle.getStateFlow(KEY_DAE_MULTIPLIER, LedgerConstants.DAE_MULTIPLIERS.first())
    val thwadAmountFlow = savedStateHandle.getStateFlow(KEY_THWAD_AMOUNT, "")
    val apaAmountFlow = savedStateHandle.getStateFlow(KEY_APA_AMOUNT, "")
    val apaMultiplierFlow = savedStateHandle.getStateFlow(KEY_APA_MULTIPLIER, LedgerConstants.APA_MULTIPLIERS.first())

    val uiState: StateFlow<LedgerUiState> = combine(
        combine(dateFlow, amPmFlow, roundFlow, nameFlow, salesFlow) { date, amPm, round, name, sales ->
            Tuple5(date, amPm, round, name, sales)
        },
        combine(rateFlow, debtFlow, lumpFlow, daeAmountFlow, daeMultiplierFlow) { rate, debt, lump, daeAmt, daeMult ->
            Tuple5(rate, debt, lump, daeAmt, daeMult)
        },
        combine(thwadAmountFlow, apaAmountFlow, apaMultiplierFlow) { thwadAmt, apaAmt, apaMult ->
            Triple(thwadAmt, apaAmt, apaMult)
        },
        combine(isLockedFlow, isLockEnabledFlow, enteredPinFlow, isPinErrorFlow) { locked, enabled, pin, error ->
            Tuple4(locked, enabled, pin, error)
        }
    ) { group1, group2, group3, lockGroup ->
        val input = LedgerInputState(
            date = group1.a,
            amPm = group1.b,
            round = group1.c,
            name = group1.d,
            salesInput = group1.e,
            rate = group2.a,
            debtInput = group2.b,
            lumpInput = group2.c,
            daeAmountInput = group2.d,
            daeMultiplier = group2.e,
            thwadAmountInput = group3.first,
            apaAmountInput = group3.second,
            apaMultiplier = group3.third
        )
        val calculation = calculateLedger(input)
        val summary = buildSummaryText(input, calculation)
        LedgerUiState(
            input = input,
            calculation = calculation,
            summaryText = summary,
            isLocked = lockGroup.a && lockGroup.b,
            isLockEnabled = lockGroup.b,
            enteredPin = lockGroup.c,
            isPinError = lockGroup.d
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LedgerUiState(isLocked = lockManager.isLockEnabled(), isLockEnabled = lockManager.isLockEnabled())
    )

    fun onPinDigitEntered(digit: Char) {
        if (!uiState.value.isLocked) return
        val current = enteredPinFlow.value
        if (current.length >= 4) return

        val newPin = current + digit
        enteredPinFlow.value = newPin

        if (newPin.length == 4) {
            viewModelScope.launch {
                if (lockManager.verifyPin(newPin)) {
                    isPinErrorFlow.value = false
                    savedStateHandle[KEY_IS_LOCKED] = false
                    enteredPinFlow.value = ""
                } else {
                    isPinErrorFlow.value = true
                    delay(500)
                    enteredPinFlow.value = ""
                    isPinErrorFlow.value = false
                }
            }
        }
    }

    fun onPinBackspace() {
        val current = enteredPinFlow.value
        if (current.isNotEmpty()) {
            enteredPinFlow.value = current.dropLast(1)
            isPinErrorFlow.value = false
        }
    }

    fun onPinClear() {
        enteredPinFlow.value = ""
        isPinErrorFlow.value = false
    }

    fun lockApp() {
        if (lockManager.isLockEnabled()) {
            savedStateHandle[KEY_IS_LOCKED] = true
            enteredPinFlow.value = ""
            isPinErrorFlow.value = false
        }
    }

    fun toggleLockEnabled(enabled: Boolean) {
        lockManager.setLockEnabled(enabled)
        isLockEnabledFlow.value = enabled
        if (!enabled) {
            savedStateHandle[KEY_IS_LOCKED] = false
        }
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        return if (lockManager.verifyPin(oldPin)) {
            lockManager.setPin(newPin)
            true
        } else {
            false
        }
    }

    fun resetPinToDefault() {
        lockManager.resetToDefault()
    }

    fun onDateSelected(date: LocalDate?) {
        savedStateHandle[KEY_DATE] = date?.toString()
    }

    fun onAmPmSelected(amPm: String?) {
        val current = savedStateHandle.get<String?>(KEY_AM_PM)
        savedStateHandle[KEY_AM_PM] = if (current == amPm) null else amPm
    }

    fun onRoundSelected(round: String) {
        savedStateHandle[KEY_ROUND] = round
    }

    fun onNameChanged(name: String) {
        savedStateHandle[KEY_NAME] = name
    }

    fun onSalesChanged(raw: String) {
        savedStateHandle[KEY_SALES] = cleanNumberInput(raw)
    }

    fun onRateSelected(rate: Double) {
        savedStateHandle[KEY_RATE] = rate
    }

    fun onDebtChanged(raw: String) {
        savedStateHandle[KEY_DEBT] = cleanNumberInput(raw)
    }

    fun onLumpChanged(raw: String) {
        savedStateHandle[KEY_LUMP] = cleanNumberInput(raw)
    }

    fun onDaeAmountChanged(raw: String) {
        savedStateHandle[KEY_DAE_AMOUNT] = cleanNumberInput(raw)
    }

    fun onDaeMultiplierSelected(multiplier: Int) {
        savedStateHandle[KEY_DAE_MULTIPLIER] = multiplier
    }

    fun onThwadAmountChanged(raw: String) {
        savedStateHandle[KEY_THWAD_AMOUNT] = cleanNumberInput(raw)
    }

    fun onApaAmountChanged(raw: String) {
        savedStateHandle[KEY_APA_AMOUNT] = cleanNumberInput(raw)
    }

    fun onApaMultiplierSelected(multiplier: Int) {
        savedStateHandle[KEY_APA_MULTIPLIER] = multiplier
    }

    fun saveCurrentCalculation(onSaved: () -> Unit = {}) {
        val currentUiState = uiState.value
        val input = currentUiState.input
        val calc = currentUiState.calculation
        val summary = currentUiState.summaryText

        if (summary.isBlank() && calc.finalDisplayTotal.isBlank()) return

        val dateDisplay = input.date?.toString() ?: ""
        viewModelScope.launch {
            historyRepository.save(
                round = input.round,
                name = input.name,
                dateText = dateDisplay,
                finalTotal = calc.finalDisplayTotal,
                sign = calc.sign,
                summaryText = summary
            )
            onSaved()
        }
    }

    fun deleteSavedCalculation(id: Long) {
        viewModelScope.launch {
            historyRepository.delete(id)
        }
    }

    fun clearSavedCalculations() {
        viewModelScope.launch {
            historyRepository.clear()
        }
    }

    fun reset() {
        clearAllInputs()
    }

    fun clearAllInputs() {
        savedStateHandle[KEY_NAME] = ""
        savedStateHandle[KEY_SALES] = ""
        savedStateHandle[KEY_DEBT] = ""
        savedStateHandle[KEY_LUMP] = ""
        savedStateHandle[KEY_DAE_AMOUNT] = ""
        savedStateHandle[KEY_THWAD_AMOUNT] = ""
        savedStateHandle[KEY_APA_AMOUNT] = ""
        savedStateHandle[KEY_DATE] = null
        savedStateHandle[KEY_AM_PM] = null
        savedStateHandle[KEY_ROUND] = LedgerConstants.ROUND_OPTIONS.first()
        savedStateHandle[KEY_RATE] = 0.25
        savedStateHandle[KEY_DAE_MULTIPLIER] = LedgerConstants.DAE_MULTIPLIERS.first()
        savedStateHandle[KEY_APA_MULTIPLIER] = LedgerConstants.APA_MULTIPLIERS.first()
    }
}

// Helpers for combine with 4 and 5 items
private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
