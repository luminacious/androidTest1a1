package com.example.testapp

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.testapp.scanner.HardwareScannerScreen
import com.example.testapp.scanner.ResultScreen
import com.example.testapp.scanner.ScannerScreen
import com.example.testapp.ui.theme.TestAppTheme
import com.google.mlkit.vision.barcode.common.Barcode

class MainActivity : ComponentActivity() {

    // Hardware scanner state
    private var currentScreen by mutableStateOf(Screen.HOME)
    private var scannedBarcode by mutableStateOf<Barcode?>(null)
    private var hardwareScanBuffer = StringBuilder()
    private var hardwareScanResult by mutableStateOf("")
    private var hardwareScanHistory = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (currentScreen != Screen.HARDWARE_SCANNER || event == null) {
            return super.onKeyDown(keyCode, event)
        }

        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                // Scanner finished - commit the buffer
                val scanned = hardwareScanBuffer.toString().trim()
                if (scanned.isNotEmpty()) {
                    hardwareScanResult = scanned
                    hardwareScanHistory.add(0, scanned)
                }
                hardwareScanBuffer.clear()
                return true
            }
            // Let special keys pass through normally
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                return super.onKeyDown(keyCode, event)
            }
            else -> {
                // Append the character to the buffer
                val char = event.unicodeChar.toChar()
                if (char.code != 0) {
                    hardwareScanBuffer.append(char)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @Composable
    fun AppContent(modifier: Modifier = Modifier) {
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                modifier = modifier,
                onCameraScanClick = { currentScreen = Screen.SCANNER },
                onHardwareScanClick = {
                    hardwareScanResult = ""
                    hardwareScanBuffer.clear()
                    currentScreen = Screen.HARDWARE_SCANNER
                }
            )
            Screen.SCANNER -> ScannerScreen(
                modifier = modifier,
                onQrCodeScanned = { barcode ->
                    scannedBarcode = barcode
                    currentScreen = Screen.RESULT
                }
            )
            Screen.RESULT -> ResultScreen(
                modifier = modifier,
                barcode = scannedBarcode,
                onScanAgain = {
                    scannedBarcode = null
                    currentScreen = Screen.SCANNER
                }
            )
            Screen.HARDWARE_SCANNER -> HardwareScannerScreen(
                modifier = modifier,
                scannedValue = hardwareScanResult,
                scanHistory = hardwareScanHistory,
                onClear = {
                    hardwareScanResult = ""
                    hardwareScanHistory.clear()
                },
                onBack = { currentScreen = Screen.HOME }
            )
        }
    }
}

enum class Screen {
    HOME, SCANNER, RESULT, HARDWARE_SCANNER
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCameraScanClick: () -> Unit,
    onHardwareScanClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "QR Scanner",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Scan barcodes using camera or hardware scanner",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(onClick = onCameraScanClick) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Camera Scanner")
        }

        Spacer(modifier = Modifier.height(16.dp))

        FilledTonalButton(onClick = onHardwareScanClick) {
            Icon(
                imageVector = Icons.Default.DocumentScanner,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Hardware Scanner")
        }
    }
}
