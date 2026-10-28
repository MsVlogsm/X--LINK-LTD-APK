package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.components.ConnectionStatusChip
import com.example.ui.strings.Strings

@Composable
fun AgentDashboardScreen(
    customers: List<Customer>,
    connectionRequests: List<ConnectionRequest>,
    language: Language,
    onCollectCashClick: (Customer) -> Unit,
    onGrantGraceClick: (String) -> Unit,
    onUpdateStatusClick: (String, ConnectionStatus) -> Unit,
    onUpdateRequestStatus: (String, RequestStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Due only, 2: Active

    val totalDuesCount = customers.count { it.balanceDue > 0 }
    val activeCount = customers.count { it.connectionStatus == ConnectionStatus.ACTIVE }
    val pendingInstalls = connectionRequests.count { it.status != RequestStatus.ACTIVATED }

    val filteredCustomers = remember(customers, searchQuery, selectedFilter) {
        customers.filter { cust ->
            val matchesQuery = searchQuery.isEmpty() ||
                    cust.name.contains(searchQuery, ignoreCase = true) ||
                    cust.customerCode.contains(searchQuery, ignoreCase = true) ||
                    cust.phone.contains(searchQuery) ||
                    cust.pppoeUsername.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                1 -> cust.balanceDue > 0 || cust.connectionStatus == ConnectionStatus.EXPIRED
                2 -> cust.connectionStatus == ConnectionStatus.ACTIVE
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = Strings.get(language, "agent_dashboard_title"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Operator: Agent Rahim (Uttara & Mirpur Hub)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // KPI Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Today's Collection
                KpiCard(
                    title = Strings.get(language, "today_collection"),
                    value = "৳ 4,500",
                    color = Color(0xFF10B981),
                    icon = Icons.Default.Payments,
                    modifier = Modifier.weight(1f)
                )
                // Due Accounts
                KpiCard(
                    title = Strings.get(language, "pending_dues_count"),
                    value = "$totalDuesCount",
                    color = Color(0xFFF97316),
                    icon = Icons.Default.ReceiptLong,
                    modifier = Modifier.weight(1f)
                )
                // Pending Installs
                KpiCard(
                    title = Strings.get(language, "pending_installs"),
                    value = "$pendingInstalls",
                    color = Color(0xFF0284C7),
                    icon = Icons.Default.Build,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(Strings.get(language, "search_customer_hint"), fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("agent_customer_search_input")
            )
        }

        // Filter chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedFilter == 0,
                    onClick = { selectedFilter = 0 },
                    label = { Text(if (language == Language.BANGLA) "সকল গ্রাহক (${customers.size})" else "All (${customers.size})") }
                )
                FilterChip(
                    selected = selectedFilter == 1,
                    onClick = { selectedFilter = 1 },
                    label = { Text(if (language == Language.BANGLA) "বকেয়া / স্থগিত ($totalDuesCount)" else "Due / Expired ($totalDuesCount)") }
                )
                FilterChip(
                    selected = selectedFilter == 2,
                    onClick = { selectedFilter = 2 },
                    label = { Text(if (language == Language.BANGLA) "সক্রিয় সংযোগ ($activeCount)" else "Active ($activeCount)") }
                )
            }
        }

        // Customer Cards for Agent Operations
        items(filteredCustomers) { cust ->
            AgentCustomerCard(
                customer = cust,
                language = language,
                onCollectCash = { onCollectCashClick(cust) },
                onGrantGrace = { onGrantGraceClick(cust.id) },
                onToggleSuspension = {
                    val nextStatus = if (cust.connectionStatus == ConnectionStatus.ACTIVE)
                        ConnectionStatus.SUSPENDED
                    else
                        ConnectionStatus.ACTIVE
                    onUpdateStatusClick(cust.id, nextStatus)
                }
            )
        }
    }
}

@Composable
fun AgentCustomerCard(
    customer: Customer,
    language: Language,
    onCollectCash: () -> Unit,
    onGrantGrace: () -> Unit,
    onToggleSuspension: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = customer.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = customer.customerCode,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "${customer.phone} • ${customer.areaZone}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                ConnectionStatusChip(status = customer.connectionStatus, language = language)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tech details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PPPoE: ${customer.pppoeUsername}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${customer.bandwidthMbps} Mbps (৳${customer.monthlyFee.toInt()}/mo)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Balance & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == Language.BANGLA) "বকেয়া টাকা" else "Due Balance",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "৳ ${customer.balanceDue.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = if (customer.balanceDue > 0) MaterialTheme.colorScheme.error else Color(0xFF16A34A)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Grace period button (if due/expired)
                    if (customer.balanceDue > 0) {
                        OutlinedButton(
                            onClick = onGrantGrace,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = if (language == Language.BANGLA) "গ্রেস" else "Grace", fontSize = 11.sp)
                        }

                        // Collect Cash button
                        Button(
                            onClick = onCollectCash,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("agent_collect_button_${customer.customerCode}")
                        ) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == Language.BANGLA) "কালেকশন" else "Collect",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Toggle Suspend / Resume
                        OutlinedButton(
                            onClick = onToggleSuspension,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (customer.connectionStatus == ConnectionStatus.ACTIVE)
                                    (if (language == Language.BANGLA) "স্থগিত" else "Suspend")
                                else
                                    (if (language == Language.BANGLA) "চালু" else "Resume"),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgentCollectCashDialog(
    customer: Customer,
    language: Language,
    onDismiss: () -> Unit,
    onConfirmCollection: (Double) -> Unit
) {
    var amountInput by remember { mutableStateOf(customer.balanceDue.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("agent_cash_dialog"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Strings.get(language, "quick_bill_collection"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${customer.name} (${customer.customerCode})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text(if (language == Language.BANGLA) "নগদ আদায়কৃত টাকার পরিমাণ (৳)" else "Cash Amount Collected (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (language == Language.BANGLA)
                        "নগদ টাকা গ্রহণ করার সাথে সাথে কাস্টমারের সংযোগ অটোমেটিক সক্রিয় হয়ে যাবে এবং ওয়েব বিলিং সফটওয়্যারে ডেটা রিয়েল-টাইমে আপডেট হবে।"
                    else
                        "Collecting cash immediately reactivates customer PPPoE and synchronizes with Web Billing Server.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = if (language == Language.BANGLA) "বাতিল" else "Cancel")
                    }
                    Button(
                        onClick = {
                            val amount = amountInput.toDoubleOrNull() ?: customer.balanceDue
                            onConfirmCollection(amount)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("agent_confirm_collection_button")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = Strings.get(language, "collect_cash"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
