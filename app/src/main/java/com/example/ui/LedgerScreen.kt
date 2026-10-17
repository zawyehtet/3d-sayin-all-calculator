package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.logic.LedgerConstants
import com.example.logic.formatAmount
import com.example.ui.components.CalculationHistoryCard
import com.example.ui.components.DateField
import com.example.ui.components.FormattedNumberField
import com.example.ui.components.LockScreen
import com.example.ui.components.SecuritySettingsDialog
import com.example.ui.components.ShweKhinMyayLogo
import com.example.ui.theme.LedgerColors
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    viewModel: LedgerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recentCalculations by viewModel.recentCalculations.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showSecurityDialog by remember { mutableStateOf(false) }

    val copyToClipboard: (String, String) -> Unit = { text, message ->
        clipboardManager.setText(AnnotatedString(text))
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    // App Lock Gate: If locked, display LockScreen with default password 7777
    if (uiState.isLocked) {
        LockScreen(
            enteredDigitsCount = uiState.enteredPin.length,
            isError = uiState.isPinError,
            onDigitClick = { viewModel.onPinDigitEntered(it) },
            onBackspaceClick = { viewModel.onPinBackspace() },
            onClearClick = { viewModel.onPinClear() }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ငွေစာရင်းတွက်ချက်မှု",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        ShweKhinMyayLogo(
                            modifier = Modifier.testTag("header_logo")
                        )
                    }
                },
                actions = {
                    if (uiState.isLockEnabled) {
                        IconButton(
                            onClick = { viewModel.lockApp() },
                            modifier = Modifier.testTag("btn_lock_screen")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "သော့ခတ်ရန်",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(
                        onClick = { showSecurityDialog = true },
                        modifier = Modifier.testTag("btn_security_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "လုံခြုံရေး ဆက်တင်",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            StickyBottomTotalBar(
                finalTotalText = uiState.calculation.finalDisplayTotal,
                sign = uiState.calculation.sign,
                onCopyAll = {
                    val summary = uiState.summaryText
                    if (summary.isNotBlank()) {
                        copyToClipboard(summary, "စာရင်းအားလုံး Copy ကူးယူပြီးပါပြီ")
                    } else {
                        copyToClipboard(uiState.calculation.finalDisplayTotal, "စုစုပေါင်း ရလဒ် Copy ကူးယူပြီးပါပြီ")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // CARD 1: ခေါင်းစဉ်နှင့် အကြိမ်
                    CardHeaderAndRound(
                        date = uiState.input.date,
                        amPm = uiState.input.amPm,
                        round = uiState.input.round,
                        name = uiState.input.name,
                        onDateChange = { viewModel.onDateSelected(it) },
                        onAmPmSelected = { viewModel.onAmPmSelected(it) },
                        onRoundSelected = { viewModel.onRoundSelected(it) },
                        onNameChanged = { viewModel.onNameChanged(it) }
                    )

                    // CARD 2: ရောင်းငွေ နှင့် ကော်၊ အပ်ငွေ
                    CardSalesAndCommission(
                        salesInput = uiState.input.salesInput,
                        currentRate = uiState.input.rate,
                        commission = uiState.calculation.commission,
                        net = uiState.calculation.net,
                        onSalesChanged = { viewModel.onSalesChanged(it) },
                        onRateSelected = { viewModel.onRateSelected(it) },
                        onCopyCommission = {
                            copyToClipboard(formatAmount(uiState.calculation.commission), "ကော်ငွေ Copy ကူးယူပြီးပါပြီ")
                        },
                        onCopyNet = {
                            copyToClipboard(formatAmount(uiState.calculation.net), "အပ်ငွေ Copy ကူးယူပြီးပါပြီ")
                        }
                    )

                    // CARD 3: ပေါင်းထည့်ငွေများ
                    CardAdditions(
                        debtInput = uiState.input.debtInput,
                        lumpInput = uiState.input.lumpInput,
                        all = uiState.calculation.all,
                        onDebtChanged = { viewModel.onDebtChanged(it) },
                        onLumpChanged = { viewModel.onLumpChanged(it) },
                        onCopyAllNet = {
                            copyToClipboard(formatAmount(uiState.calculation.all), "စုစုပေါင်း အပ်ငွေ Copy ကူးယူပြီးပါပြီ")
                        }
                    )

                    // CARD 4: အလျော်
                    CardPayCalculations(
                        daeAmountInput = uiState.input.daeAmountInput,
                        daeMultiplier = uiState.input.daeMultiplier,
                        daeResult = uiState.calculation.daeResult,
                        thwadAmountInput = uiState.input.thwadAmountInput,
                        thwadResult = uiState.calculation.thwadResult,
                        apaAmountInput = uiState.input.apaAmountInput,
                        apaMultiplier = uiState.input.apaMultiplier,
                        apaResult = uiState.calculation.apaResult,
                        totalPay = uiState.calculation.pay,
                        finalTotalDisplay = uiState.calculation.finalDisplayTotal,
                        sign = uiState.calculation.sign,
                        onDaeAmountChanged = { viewModel.onDaeAmountChanged(it) },
                        onDaeMultiplierSelected = { viewModel.onDaeMultiplierSelected(it) },
                        onThwadAmountChanged = { viewModel.onThwadAmountChanged(it) },
                        onApaAmountChanged = { viewModel.onApaAmountChanged(it) },
                        onApaMultiplierSelected = { viewModel.onApaMultiplierSelected(it) }
                    )

                    // CARD 5: စာရင်း အကျဉ်းချုပ်
                    CardSummaryAndActions(
                        summaryText = uiState.summaryText,
                        onCopyAll = {
                            copyToClipboard(uiState.summaryText, "စာရင်းအားလုံး Copy ကူးယူပြီးပါပြီ")
                        },
                        onSaveCalculation = {
                            if (uiState.summaryText.isNotBlank()) {
                                viewModel.saveCurrentCalculation {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("စာရင်းကို သိမ်းဆည်းပြီးပါပြီ")
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("သိမ်းဆည်းရန် စာရင်း အချက်အလက် မရှိသေးပါ")
                                }
                            }
                        },
                        onSendSms = {
                            shareToSms(context, uiState.summaryText)
                        },
                        onSendViber = {
                            shareToViber(context, uiState.summaryText) { fallbackMsg ->
                                coroutineScope.launch { snackbarHostState.showSnackbar(fallbackMsg) }
                            }
                        },
                        onSendMessenger = {
                            shareToMessenger(context, uiState.summaryText) { fallbackMsg ->
                                coroutineScope.launch { snackbarHostState.showSnackbar(fallbackMsg) }
                            }
                        },
                        onShareGeneric = {
                            shareGeneric(context, uiState.summaryText)
                        },
                        onReset = {
                            viewModel.reset()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("အသစ်ပြန်စပြီးပါပြီ")
                            }
                        }
                    )

                    // CARD 6: မကြာသေးမီက သိမ်းဆည်းထားသော စာရင်းများ (နောက်ဆုံး ၅ ခု)
                    CalculationHistoryCard(
                        recentCalculations = recentCalculations,
                        onCopySummary = { text ->
                            copyToClipboard(text, "မှတ်တမ်း စာရင်း Copy ကူးယူပြီးပါပြီ")
                        },
                        onShareSummary = { text ->
                            shareGeneric(context, text)
                        },
                        onDeleteCalculation = { id ->
                            viewModel.deleteSavedCalculation(id)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("မှတ်တမ်း ဖျက်ပြီးပါပြီ")
                            }
                        },
                        onClearAll = {
                            viewModel.clearSavedCalculations()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("မှတ်တမ်းများအားလုံး ရှင်းလင်းပြီးပါပြီ")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Security & Password Settings Dialog
    if (showSecurityDialog) {
        SecuritySettingsDialog(
            isLockEnabled = uiState.isLockEnabled,
            onToggleLock = { viewModel.toggleLockEnabled(it) },
            onChangePin = { oldPin, newPin ->
                val success = viewModel.changePin(oldPin, newPin)
                if (success) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("လျှို့ဝှက်ကုဒ် အသစ် သတ်မှတ်ပြီးပါပြီ")
                    }
                }
                success
            },
            onResetToDefaultPin = {
                viewModel.resetPinToDefault()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("မူလကုဒ် 7777 သို့ ပြန်လည်သတ်မှတ်ပြီးပါပြီ")
                }
            },
            onDismiss = { showSecurityDialog = false }
        )
    }
}

// -------------------------------------------------------------
// CARD 1: ခေါင်းစဉ်နှင့် အကြိမ်
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardHeaderAndRound(
    date: LocalDate?,
    amPm: String?,
    round: String,
    name: String,
    onDateChange: (LocalDate) -> Unit,
    onAmPmSelected: (String) -> Unit,
    onRoundSelected: (String) -> Unit,
    onNameChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_header_round"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "၁။ ခေါင်းစဉ်နှင့် အကြိမ်",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Date Picker Row using DateField
            DateField(
                date = date,
                onDateChange = onDateChange
            )

            // AM / PM selectable chips (neither selected by default)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "အချိန်ပိုင်း:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                FilterChip(
                    selected = amPm == "AM",
                    onClick = { onAmPmSelected("AM") },
                    label = { Text("AM (မနက်ပိုင်း)", modifier = Modifier.padding(horizontal = 4.dp)) },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("chip_am"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = amPm == "PM",
                    onClick = { onAmPmSelected("PM") },
                    label = { Text("PM (ညနေပိုင်း)", modifier = Modifier.padding(horizontal = 4.dp)) },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("chip_pm"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            // Dropdown "အကြိမ် ရွေးရန်" with ၁ ကြိမ် to ၂၄ ကြိမ်, then 2D
            var dropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = round,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("အကြိမ် ရွေးရန်") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .testTag("round_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    LedgerConstants.ROUND_OPTIONS.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                onRoundSelected(opt)
                                dropdownExpanded = false
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            // Name text field "အမည်"
            OutlinedTextField(
                value = name,
                onValueChange = onNameChanged,
                label = { Text("အမည်") },
                placeholder = { Text("အမည် ရိုက်ထည့်ပါ") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_field")
            )
        }
    }
}

// -------------------------------------------------------------
// CARD 2: ရောင်းငွေ နှင့် ကော်၊ အပ်ငွေ
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CardSalesAndCommission(
    salesInput: String,
    currentRate: Double,
    commission: Double,
    net: Double,
    onSalesChanged: (String) -> Unit,
    onRateSelected: (Double) -> Unit,
    onCopyCommission: () -> Unit,
    onCopyNet: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_sales_commission"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "၂။ ရောင်းငွေ နှင့် ကော်၊ အပ်ငွေ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Number field "ရောင်းငွေ (ကျပ်)" with live thousands separators
            FormattedNumberField(
                value = salesInput,
                onValueChange = onSalesChanged,
                label = "ရောင်းငွေ (ကျပ်)",
                testTag = "sales_input_field"
            )

            // Commission rate selector: 15%, 17%, 25% (default), 30%
            Text(
                text = "ကော် ရာခိုင်နှုန်း ရွေးချယ်ရန်:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LedgerConstants.COMMISSION_RATES.forEach { rate ->
                    val percentInt = (rate * 100).roundToInt()
                    FilterChip(
                        selected = currentRate == rate,
                        onClick = { onRateSelected(rate) },
                        label = {
                            Text(
                                text = "$percentInt%",
                                fontWeight = if (currentRate == rate) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("rate_chip_$percentInt"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Results: ကော် and အပ်ငွေ, each with small Copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ကော် (${(currentRate * 100).roundToInt()}%)",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                    Text(
                        text = "${formatAmount(commission)} ကျပ်",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                IconButton(
                    onClick = onCopyCommission,
                    modifier = Modifier.size(48.dp).testTag("copy_commission_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Commission",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "အပ်ငွေ",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                    Text(
                        text = "${formatAmount(net)} ကျပ်",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                IconButton(
                    onClick = onCopyNet,
                    modifier = Modifier.size(48.dp).testTag("copy_net_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Net",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CARD 3: ပေါင်းထည့်ငွေများ
// -------------------------------------------------------------
@Composable
private fun CardAdditions(
    debtInput: String,
    lumpInput: String,
    all: Double,
    onDebtChanged: (String) -> Unit,
    onLumpChanged: (String) -> Unit,
    onCopyAllNet: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_additions"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "၃။ ပေါင်းထည့်ငွေများ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Number fields: "အကြွေးကျန်" and "အလုံး"
            FormattedNumberField(
                value = debtInput,
                onValueChange = onDebtChanged,
                label = "အကြွေးကျန်",
                testTag = "debt_input_field"
            )

            FormattedNumberField(
                value = lumpInput,
                onValueChange = onLumpChanged,
                label = "အလုံး",
                testTag = "lump_input_field"
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Show "စုစုပေါင်း အပ်ငွေ" with a Copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "စုစုပေါင်း အပ်ငွေ (အပ်ငွေ + အကြွေး + အလုံး)",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                    Text(
                        text = "${formatAmount(all)} ကျပ်",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                IconButton(
                    onClick = onCopyAllNet,
                    modifier = Modifier.size(48.dp).testTag("copy_all_net_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Total Net",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CARD 4: အလျော်
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardPayCalculations(
    daeAmountInput: String,
    daeMultiplier: Int,
    daeResult: Double,
    thwadAmountInput: String,
    thwadResult: Double,
    apaAmountInput: String,
    apaMultiplier: Int,
    apaResult: Double,
    totalPay: Double,
    finalTotalDisplay: String,
    sign: String,
    onDaeAmountChanged: (String) -> Unit,
    onDaeMultiplierSelected: (Int) -> Unit,
    onThwadAmountChanged: (String) -> Unit,
    onApaAmountChanged: (String) -> Unit,
    onApaMultiplierSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_pay_calculations"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "၄။ အလျော် တွက်ချက်ခြင်း",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // 1. ဒဲ့ amount × multiplier dropdown (550, 600, 80, 75)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormattedNumberField(
                    value = daeAmountInput,
                    onValueChange = onDaeAmountChanged,
                    label = "ဒဲ့ ပမာဏ",
                    modifier = Modifier.weight(1.3f),
                    testTag = "dae_amount_field"
                )

                var daeDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = daeDropdownExpanded,
                    onExpandedChange = { daeDropdownExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "× $daeMultiplier",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("အဆ") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = daeDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("dae_mult_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = daeDropdownExpanded,
                        onDismissRequest = { daeDropdownExpanded = false }
                    ) {
                        LedgerConstants.DAE_MULTIPLIERS.forEach { mult ->
                            DropdownMenuItem(
                                text = { Text("× $mult") },
                                onClick = {
                                    onDaeMultiplierSelected(mult)
                                    daeDropdownExpanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = "ဒဲ့ ရလဒ်: ${formatAmount(daeResult)} ကျပ်",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(start = 4.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // 2. သွဒ် amount × 10
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormattedNumberField(
                    value = thwadAmountInput,
                    onValueChange = onThwadAmountChanged,
                    label = "သွဒ် ပမာဏ",
                    modifier = Modifier.weight(1.3f),
                    testTag = "thwad_amount_field"
                )
                OutlinedTextField(
                    value = "× 10",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("အဆ") },
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = "သွဒ် ရလဒ်: ${formatAmount(thwadResult)} ကျပ်",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(start = 4.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // 3. အပါ amount × multiplier dropdown (3, 8, 15)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormattedNumberField(
                    value = apaAmountInput,
                    onValueChange = onApaAmountChanged,
                    label = "အပါ ပမာဏ",
                    modifier = Modifier.weight(1.3f),
                    testTag = "apa_amount_field"
                )

                var apaDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = apaDropdownExpanded,
                    onExpandedChange = { apaDropdownExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "× $apaMultiplier",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("အဆ") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = apaDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("apa_mult_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = apaDropdownExpanded,
                        onDismissRequest = { apaDropdownExpanded = false }
                    ) {
                        LedgerConstants.APA_MULTIPLIERS.forEach { mult ->
                            DropdownMenuItem(
                                text = { Text("× $mult") },
                                onClick = {
                                    onApaMultiplierSelected(mult)
                                    apaDropdownExpanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = "အပါ ရလဒ်: ${formatAmount(apaResult)} ကျပ်",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(start = 4.dp)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Read-only total အလျော် (ဒဲ့ + သွဒ် + အပါ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "စုစုပေါင်း အလျော်:",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = "${formatAmount(totalPay)} ကျပ်",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Large final "စုစုပေါင်း"
            val totalColor = when (sign) {
                "+" -> LedgerColors.plusColor
                "-" -> LedgerColors.minusColor
                else -> MaterialTheme.colorScheme.primary
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = totalColor.copy(alpha = 0.08f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.5.dp, brush = androidx.compose.ui.graphics.SolidColor(totalColor))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "စုစုပေါင်း ရလဒ်",
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "$finalTotalDisplay ကျပ်",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = totalColor
                        ),
                        modifier = Modifier.testTag("final_total_card_value")
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CARD 5: စာရင်း အကျဉ်းချုပ်
// -------------------------------------------------------------
@Composable
private fun CardSummaryAndActions(
    summaryText: String,
    onCopyAll: () -> Unit,
    onSaveCalculation: () -> Unit,
    onSendSms: () -> Unit,
    onSendViber: () -> Unit,
    onSendMessenger: () -> Unit,
    onShareGeneric: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_summary_actions"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "၅။ စာရင်း အကျဉ်းချုပ်",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Plain text summary box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Text(
                    text = if (summaryText.isNotBlank()) summaryText else "အချက်အလက်များ ထည့်သွင်းပါက ဤနေရာတွင် စာရင်း အကျဉ်းချုပ် ပေါ်လာပါမည်။",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (summaryText.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .padding(14.dp)
                        .testTag("summary_text_view")
                )
            }

            // Buttons under the summary:
            // 1. "📋 စာရင်းအားလုံး Copy ယူမည်"
            Button(
                onClick = onCopyAll,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_copy_all"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📋 စာရင်းအားလုံး Copy ယူမည်",
                    fontWeight = FontWeight.Bold
                )
            }

            // 2. "💾 စာရင်း သိမ်းဆည်းမည် (Save)"
            Button(
                onClick = onSaveCalculation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_save_calculation"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "💾 စာရင်း သိမ်းဆည်းမည် (Save)",
                    fontWeight = FontWeight.Bold
                )
            }

            // Row with SMS, Viber, Messenger, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSendSms,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_sms"),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = "SMS", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SMS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onSendViber,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_viber"),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Viber", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Viber", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onSendMessenger,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_messenger"),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Messenger", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onShareGeneric,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_share"),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // "🔄 အသစ်ပြန်တွက်မည် (Reset)"
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_reset"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = ButtonDefaults.outlinedButtonBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🔄 အသစ်ပြန်တွက်မည် (Reset)",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Sticky Bottom Bar
// -------------------------------------------------------------
@Composable
private fun StickyBottomTotalBar(
    finalTotalText: String,
    sign: String,
    onCopyAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalColor = when (sign) {
        "+" -> LedgerColors.plusColor
        "-" -> LedgerColors.minusColor
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "စုစုပေါင်း ရလဒ်",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )
                Text(
                    text = "$finalTotalText ကျပ်",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = totalColor
                    ),
                    modifier = Modifier.testTag("sticky_final_total_text")
                )
            }

            Button(
                onClick = onCopyAll,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("sticky_btn_copy_all"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy All", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// Intent helpers
// -------------------------------------------------------------
private fun shareToSms(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:")
        putExtra("sms_body", text)
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        val chooser = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            "SMS ပေးပို့ရန်"
        )
        context.startActivity(chooser)
    }
}

private fun shareToViber(context: Context, text: String, onFallback: (String) -> Unit) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.viber.voip")
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        onFallback("Viber မရှိသေးပါသဖြင့် ပုံမှန် မျှဝေခြင်း ဖွင့်ပါမည်")
        val chooser = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            "စာရင်း မျှဝေရန်"
        )
        context.startActivity(chooser)
    }
}

private fun shareToMessenger(context: Context, text: String, onFallback: (String) -> Unit) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.facebook.orca")
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        onFallback("Messenger မရှိသေးပါသဖြင့် ပုံမှန် မျှဝေခြင်း ဖွင့်ပါမည်")
        val chooser = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            "စာရင်း မျှဝေရန်"
        )
        context.startActivity(chooser)
    }
}

private fun shareGeneric(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, "စာရင်း မျှဝေရန်")
    context.startActivity(chooser)
}
