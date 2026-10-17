package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.LedgerScreen
import com.example.ui.LedgerViewModel
import com.example.ui.theme.LedgerCalcTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LedgerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LedgerCalcTheme {
                LedgerScreen(viewModel = viewModel)
            }
        }
    }
}
