package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApiSyncConfig
import com.example.data.model.Language
import com.example.data.model.SyncLogEntry
import com.example.ui.strings.Strings

@Composable
fun WebBillingApiScreen(
    config: ApiSyncConfig,
    logs: List<SyncLogEntry>,
    isSyncing: Boolean,
    language: Language,
    onSyncNow: (String, String, String) -> Unit,
    onTestPing: (String, String, String) -> Unit,
    roomSyncState: com.example.data.sync.SyncStatusState = com.example.data.sync.SyncStatusState(),
    onTriggerRoomSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var baseUrl by remember(config) { mutableStateOf(config.baseUrl) }
    var apiKey by remember(config) { mutableStateOf(config.apiKey) }
    var bearerToken by remember(config) { mutableStateOf(config.bearerToken) }
    var showPayloadSchema by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Title & Description
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0284C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.get(language, "api_integration_title"),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Strings.get(language, "api_subtitle"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Live Connection Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF16A34A))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Strings.get(language, "status_connected"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "REST API v1.4",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Last Synced: ${config.lastSyncTime} • Two-Way Realtime Webhook active",
                        fontSize = 11.sp,
                        color = Color(0xFF166534)
                    )
                }
            }
        }

        // Room-to-Firestore Synchronization Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("room_firestore_sync_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF97316).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = Color(0xFFEA580C),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == Language.BANGLA) "রুম ➔ ফায়ারস্টোর ডেটা সিঙ্ক সার্ভিস" else "Room ➔ Firestore Data Sync Service",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Offline-First Room SQLite ⇄ Real-Time Cloud Firestore",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Firestore Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status and Count indicators
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Room Cache",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "SQLite DB",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Cloud Status",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = if (roomSyncState.isSyncing) "Syncing..." else "Real-time",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Last Synced",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = roomSyncState.lastSyncTime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = roomSyncState.statusMessage,
                        fontSize = 11.sp,
                        color = if (roomSyncState.isSuccess) Color(0xFF15803D) else MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onTriggerRoomSync,
                        enabled = !roomSyncState.isSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("sync_room_firestore_button")
                    ) {
                        if (roomSyncState.isSyncing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Syncing with Firestore...", fontSize = 12.sp)
                        } else {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.BANGLA) "রুম ও ফায়ারস্টোর এখনই সিঙ্ক করুন" else "Sync Room & Firestore Now",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Configuration Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == Language.BANGLA) "ওয়েব বিলিং সফটওয়্যার API কনফিগারেশন" else "Web Billing Server Configuration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Base URL
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = { Text(Strings.get(language, "api_base_url")) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Dns, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_base_url_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // API Key
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text(Strings.get(language, "api_key")) },
                        leadingIcon = { Icon(imageVector = Icons.Default.VpnKey, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bearer Token
                    OutlinedTextField(
                        value = bearerToken,
                        onValueChange = { bearerToken = it },
                        label = { Text(Strings.get(language, "bearer_token")) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Security, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons: Sync Now & Ping Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onTestPing(baseUrl, apiKey, bearerToken) },
                            enabled = !isSyncing,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("api_ping_button")
                        ) {
                            Icon(imageVector = Icons.Default.NetworkPing, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = Strings.get(language, "test_connection"), fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onSyncNow(baseUrl, apiKey, bearerToken) },
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("api_sync_now_button")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Syncing...", fontSize = 11.sp)
                            } else {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = Strings.get(language, "sync_now"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Expandable API Schema helper for Developers
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPayloadSchema = !showPayloadSchema },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == Language.BANGLA) "ওয়েব সফটওয়্যার API ইন্টিগ্রেশন স্কিমা (JSON)" else "Web ERP API Schema & Spec",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = if (showPayloadSchema) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }

                    if (showPayloadSchema) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = """
// POST /api/v1/payments/sync
{
  "customer_code": "XLK-7842",
  "pppoe_user": "xlink_mamun_7842",
  "amount_paid": 1000.0,
  "payment_method": "BKASH",
  "transaction_id": "BKH9208X71",
  "auto_renew": true,
  "agent_id": "AGT-RAHIM-01",
  "timestamp": "2026-10-28T10:45:00Z"
}
                                """.trimIndent(),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Log Inspector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get(language, "live_sync_logs"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${logs.size} Events Logged",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        items(logs) { log ->
            SyncLogItemCard(log = log)
        }
    }
}

@Composable
fun SyncLogItemCard(log: SyncLogEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (log.httpMethod == "GET") Color(0xFFE0F2FE) else Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = log.httpMethod,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.httpMethod == "GET") Color(0xFF0369A1) else Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.endpoint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (log.statusCode in 200..299) Color(0xFFDCFCE7) else Color(0xFFFFE4E6),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${log.statusCode}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (log.statusCode in 200..299) Color(0xFF15803D) else Color(0xFFBE123C),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${log.responseTimeMs}ms",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = log.message,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = log.timestamp,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
