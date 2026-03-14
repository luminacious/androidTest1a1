package com.example.testapp.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.style.TextAlign
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onQrCodeScanned: (Barcode) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraPermission.status.isGranted) {
            // Camera preview with QR scanning
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onQrCodeScanned = onQrCodeScanned
            )

            // Overlay hint text
            Text(
                text = "Point camera at a QR code",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            )
        } else {
            // Permission request UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                val message = if (cameraPermission.status.shouldShowRationale) {
                    "Camera permission is needed to scan QR codes. Please grant the permission."
                } else {
                    "Camera permission is required to scan QR codes.\nTap the button below to grant access."
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                    Text("Grant Camera Permission")
                }
            }
        }
    }
}
