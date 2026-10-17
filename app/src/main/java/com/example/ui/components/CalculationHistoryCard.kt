package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedCalculation
import com.example.ui.theme.LedgerColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CalculationHistoryCard(
    recentCalculations: List<SavedCalculation>,
    onCopySummary: (String) -> Unit,
    onShareSummary: (String) -> Unit,
    onDeleteCalculation: (Long) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDetailItem by remember { mutableStateOf<SavedCalculation?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_calculation_history"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "မကြာသေးမီက သိမ်းဆည်းထားသော စာရင်းများ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (recentCalculations.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("btn_clear_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear All History",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = "နောက်ဆုံး သိမ်းဆည်းထားသော စာရင်း ၅ ခု အထိ အလိုအလျောက် မှတ်သားထားပါသည်",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
            )

            if (recentCalculations.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = "သိမ်းဆည်းထားသော စာရင်း မရှိသေးပါ။ အထက်ပါ စာရင်း အကျဉ်းချုပ်မှ '💾 စာရင်း သိမ်းဆည်းမည်' ကို နှိပ်၍ မှတ်တမ်းတင်နိုင်ပါသည်။",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    recentCalculations.forEachIndexed { index, item ->
                        SavedCalculationItemRow(
                            item = item,
                            index = index + 1,
                            onViewDetails = { selectedDetailItem = item },
                            onCopy = { onCopySummary(item.summaryText) },
                            onDelete = { onDeleteCalculation(item.id) }
                        )
                    }
                }
            }
        }
    }

    // Detail dialog when user clicks an item
    selectedDetailItem?.let { item ->
        CalculationDetailDialog(
            item = item,
            onDismiss = { selectedDetailItem = null },
            onCopy = {
                onCopySummary(item.summaryText)
                selectedDetailItem = null
            },
            onShare = {
                onShareSummary(item.summaryText)
                selectedDetailItem = null
            }
        )
    }

    // Confirmation dialog before clearing all
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("မှတ်တမ်းများ ရှင်းလင်းမည်") },
            text = { Text("သိမ်းဆည်းထားသော စာရင်းမှတ်တမ်းအားလုံးကို ဖျက်ရန် သေချာပါသလား?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("ဖျက်မည်", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("မလုပ်တော့ပါ")
                }
            }
        )
    }
}

@Composable
private fun SavedCalculationItemRow(
    item: SavedCalculation,
    index: Int,
    onViewDetails: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    val totalColor = when (item.sign) {
        "+" -> LedgerColors.plusColor
        "-" -> LedgerColors.minusColor
        else -> MaterialTheme.colorScheme.primary
    }

    val timeFormatted = remember(item.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.US)
        sdf.format(Date(item.timestamp))
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_item_$index"),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = if (item.name.isNotBlank()) item.name else "အမည်မရှိ"
                    Text(
                        text = "${item.round} • $displayName",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    val dateLine = if (item.dateText.isNotBlank()) "${item.dateText} $timeFormatted" else timeFormatted
                    Text(
                        text = "ရက်စွဲ: $dateLine",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                }

                // Final Total
                Text(
                    text = "${item.finalTotal} ကျပ်",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = totalColor
                    )
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onViewDetails,
                    modifier = Modifier.testTag("btn_view_detail_$index")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("အသေးစိတ်", fontSize = 12.sp)
                }

                TextButton(
                    onClick = onCopy,
                    modifier = Modifier.testTag("btn_copy_saved_$index")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 12.sp)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp).testTag("btn_delete_saved_$index")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculationDetailDialog(
    item: SavedCalculation,
    onDismiss: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "စာရင်း အသေးစိတ် (${item.round})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Text(
                    text = item.summaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onCopy,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy ယူမည်")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("မျှဝေမည်")
            }
        }
    )
}
