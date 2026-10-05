package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.DialerViewModel
import com.example.ui.MainScreen

class MainActivity : ComponentActivity() {

    private val viewModel: DialerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIncomingIntent(intent)
        setContent {
            MainScreen(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val data = intent?.data
        if (data != null && data.scheme == "tel") {
            val number = data.schemeSpecificPart
            if (!number.isNullOrBlank()) {
                viewModel.setKeypadInput(number)
            }
        }
    }
}
