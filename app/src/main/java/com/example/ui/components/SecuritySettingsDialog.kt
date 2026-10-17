package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.logic.convertBurmeseToArabicDigits

@Composable
fun SecuritySettingsDialog(
    isLockEnabled: Boolean,
    onToggleLock: (Boolean) -> Unit,
    onChangePin: (oldPin: String, newPin: String) -> Boolean,
    onResetToDefaultPin: () -> Unit,
    onDismiss: () -> Unit
) {
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showChangePinSection by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "လုံခြုံရေးနှင့် သော့ခတ်စနစ်",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Toggle Lock on/off
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "သော့ခတ်စနစ် အသုံးပြုမည်",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "အက်ပ်ဖွင့်တိုင်း လျှို့ဝှက်ကုဒ် တောင်းပါမည်",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
                        )
                    }
                    Switch(
                        checked = isLockEnabled,
                        onCheckedChange = onToggleLock,
                        modifier = Modifier.testTag("switch_lock_toggle")
                    )
                }

                HorizontalDivider()

                if (!showChangePinSection) {
                    OutlinedButton(
                        onClick = { showChangePinSection = true },
                        modifier = Modifier.fillMaxWidth().testTag("btn_show_change_pin")
                    ) {
                        Text("လျှို့ဝှက်ကုဒ် ပြောင်းလဲမည်")
                    }

                    OutlinedButton(
                        onClick = {
                            onResetToDefaultPin()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_reset_pin_default")
                    ) {
                        Icon(Icons.Default.LockReset, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("မူလကုဒ် 7777 သို့ ပြန်ထားမည်")
                    }
                } else {
                    Text(
                        text = "လျှို့ဝှက်ကုဒ် အသစ် သတ်မှတ်ရန်",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = oldPin,
                        onValueChange = { oldPin = it.take(4) },
                        label = { Text("လက်ရှိ လျှို့ဝှက်ကုဒ်") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_old_pin")
                    )

                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it.take(4) },
                        label = { Text("ကုဒ်အသစ် (ဂဏန်း ၄ လုံး)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_new_pin")
                    )

                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it.take(4) },
                        label = { Text("ကုဒ်အသစ် ထပ်ရိုက်ပါ") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_confirm_pin")
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.testTag("dialog_error_text")
                        )
                    }

                    Button(
                        onClick = {
                            val normOld = convertBurmeseToArabicDigits(oldPin).trim()
                            val normNew = convertBurmeseToArabicDigits(newPin).trim()
                            val normConfirm = convertBurmeseToArabicDigits(confirmPin).trim()

                            when {
                                normNew.length != 4 -> {
                                    errorMessage = "လျှို့ဝှက်ကုဒ်သည် ဂဏန်း ၄ လုံး ဖြစ်ရပါမည်"
                                }
                                normNew != normConfirm -> {
                                    errorMessage = "ကုဒ်အသစ် နှစ်ခု မကိုက်ညီပါ"
                                }
                                else -> {
                                    val success = onChangePin(normOld, normNew)
                                    if (success) {
                                        onDismiss()
                                    } else {
                                        errorMessage = "လက်ရှိ လျှို့ဝှက်ကုဒ် မှားယွင်းနေပါသည်"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_new_pin")
                    ) {
                        Text("ကုဒ်အသစ် သိမ်းဆည်းမည်")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("ပိတ်မည်")
            }
        }
    )
}
