package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(date: LocalDate?, onDateChange: (LocalDate) -> Unit) {
    var show by rememberSaveable { mutableStateOf(false) }
    val fmt = remember { DateTimeFormatter.ofPattern("dd.MM.yy") }

    Box {
        OutlinedTextField(
            value = date?.format(fmt) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("ရက်စွဲ") },
            placeholder = { Text("ရက်စွဲ ရွေးပါ") },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
            modifier = Modifier.fillMaxWidth()
        )
        // Transparent layer on top that receives the tap
        Box(
            Modifier
                .matchParentSize()
                .clickable { show = true }
        )
    }

    if (show) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { show = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onDateChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    show = false
                }) { Text("ရွေးမည်") }
            },
            dismissButton = { TextButton(onClick = { show = false }) { Text("ပယ်ဖျက်မည်") } }
        ) { DatePicker(state = state) }
    }
}
