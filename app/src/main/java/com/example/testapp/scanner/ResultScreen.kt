package com.example.testapp.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun ResultScreen(
    barcode: Barcode?,
    onScanAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Scan Result",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (barcode == null) {
            Text("No data available", style = MaterialTheme.typography.bodyLarge)
        } else {
            // Type card
            InfoCard(title = "QR Type", value = getTypeName(barcode.valueType))

            Spacer(modifier = Modifier.height(12.dp))

            // Raw value
            InfoCard(title = "Raw Value", value = barcode.rawValue ?: "N/A", isCode = true)

            Spacer(modifier = Modifier.height(12.dp))

            // Type-specific fields
            when (barcode.valueType) {
                Barcode.TYPE_URL -> {
                    barcode.url?.let { url ->
                        InfoCard(title = "URL Title", value = url.title ?: "N/A")
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoCard(title = "URL", value = url.url ?: "N/A")
                    }
                }
                Barcode.TYPE_CONTACT_INFO -> {
                    barcode.contactInfo?.let { contact ->
                        contact.name?.let { name ->
                            InfoCard(title = "Name", value = "${name.first ?: ""} ${name.last ?: ""}".trim())
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        contact.organization?.let { org ->
                            InfoCard(title = "Organization", value = org)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        contact.phones.firstOrNull()?.let { phone ->
                            InfoCard(title = "Phone", value = phone.number ?: "N/A")
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        contact.emails.firstOrNull()?.let { email ->
                            InfoCard(title = "Email", value = email.address ?: "N/A")
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                Barcode.TYPE_WIFI -> {
                    barcode.wifi?.let { wifi ->
                        InfoCard(title = "SSID", value = wifi.ssid ?: "N/A")
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoCard(title = "Password", value = wifi.password ?: "N/A")
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoCard(title = "Encryption", value = when (wifi.encryptionType) {
                            Barcode.WiFi.TYPE_WPA -> "WPA/WPA2"
                            Barcode.WiFi.TYPE_WEP -> "WEP"
                            Barcode.WiFi.TYPE_OPEN -> "Open"
                            else -> "Unknown"
                        })
                    }
                }
                Barcode.TYPE_EMAIL -> {
                    barcode.email?.let { email ->
                        InfoCard(title = "To", value = email.address ?: "N/A")
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoCard(title = "Subject", value = email.subject ?: "N/A")
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoCard(title = "Body", value = email.body ?: "N/A")
                    }
                }
                Barcode.TYPE_PHONE -> {
                    InfoCard(title = "Phone Number", value = barcode.phone?.number ?: "N/A")
                }
                Barcode.TYPE_SMS -> {
                    barcode.sms?.let { sms ->
                        InfoCard(title = "Phone", value = sms.phoneNumber ?: "N/A")
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoCard(title = "Message", value = sms.message ?: "N/A")
                    }
                }
                Barcode.TYPE_GEO -> {
                    barcode.geoPoint?.let { geo ->
                        InfoCard(title = "Latitude", value = geo.lat.toString())
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoCard(title = "Longitude", value = geo.lng.toString())
                    }
                }
                else -> {
                    // Display value already shown as raw value above
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onScanAgain) {
                Text("Scan Again")
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String,
    isCode: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = if (isCode) {
                    MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun getTypeName(type: Int): String = when (type) {
    Barcode.TYPE_URL -> "URL"
    Barcode.TYPE_CONTACT_INFO -> "Contact"
    Barcode.TYPE_WIFI -> "Wi-Fi"
    Barcode.TYPE_EMAIL -> "Email"
    Barcode.TYPE_PHONE -> "Phone"
    Barcode.TYPE_SMS -> "SMS"
    Barcode.TYPE_GEO -> "Location"
    Barcode.TYPE_TEXT -> "Text"
    Barcode.TYPE_ISBN -> "ISBN"
    Barcode.TYPE_PRODUCT -> "Product"
    else -> "Unknown"
}
