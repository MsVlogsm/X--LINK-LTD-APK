package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.ConnectionStatusChip
import com.example.ui.components.SignalMetricsWidget
import com.example.ui.strings.Strings

@Composable
fun CustomerDashboardScreen(
    customer: Customer,
    bills: List<Bill>,
    language: Language,
    onPayBillClick: () -> Unit,
    onViewBill: (Bill) -> Unit,
    onNavigateToNewConnection: () -> Unit,
    onOpenAiSupport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val unpaidBill = bills.firstOrNull { it.customerId == customer.id && !it.isPaid }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Welcome and Hero Subscription Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0B192C),
                                    Color(0xFF1E3E62)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = Strings.get(language, "welcome_user"),
                                    fontSize = 12.sp,
                                    color = Color(0xFFBAE6FD)
                                )
                                Text(
                                    text = customer.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ID: ${customer.customerCode} • PPPoE: ${customer.pppoeUsername}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF7DD3FC)
                                )
                            }
                            ConnectionStatusChip(status = customer.connectionStatus, language = language)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Current plan pill & IP
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = Strings.get(language, "current_plan"),
                                    fontSize = 10.sp,
                                    color = Color(0xFFBAE6FD)
                                )
                                Text(
                                    text = customer.packageName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "IP: ${customer.ipAddress} • ONU: ${customer.onuMac.take(8)}...",
                                    fontSize = 10.sp,
                                    color = Color(0xFFE0F2FE).copy(alpha = 0.8f)
                                )
                            }
                            Surface(
                                color = Color(0xFF00D2FF),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${customer.bandwidthMbps} Mbps",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF082F49),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bill Summary Card with Pay Now Action
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bill_summary_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (unpaidBill != null) Color(0xFFFFF7ED) else Color(0xFFF0FDF4)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (unpaidBill != null) Color(0xFFFDBA74) else Color(0xFF86EFAC)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (unpaidBill != null)
                                    Strings.get(language, "due_amount")
                                else
                                    if (language == Language.BANGLA) "চলতি মাসের বিল পরিশোধিত" else "Bill Paid & Current",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (unpaidBill != null) Color(0xFFC2410C) else Color(0xFF15803D)
                            )
                            Text(
                                text = "৳ ${if (unpaidBill != null) unpaidBill.amount.toInt() else 0}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = if (unpaidBill != null) Color(0xFF9A3412) else Color(0xFF166534)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = Strings.get(language, "due_date"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = customer.dueDate,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (unpaidBill != null) {
                        Button(
                            onClick = onPayBillClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("quick_pay_button")
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${Strings.get(language, "pay_now")} (৳${unpaidBill.amount.toInt()})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.BANGLA)
                                    "আপনার পরবর্তী বিলিং সাইকেল শুরু হবে ১লা ডিসেম্বর ২০২৬"
                                else
                                    "Next billing cycle renews on 1st Dec 2026",
                                fontSize = 12.sp,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }
        }

        // Live Optical & Signal Metrics
        item {
            SignalMetricsWidget(
                opticalDbm = customer.opticalPowerDbm,
                pingMs = customer.pingMs,
                downloadMbps = customer.downloadSpeedMbps,
                uploadMbps = customer.uploadSpeedMbps,
                language = language
            )
        }

        // Gemini AI Network Diagnostics Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAiSupport() }
                    .testTag("ai_assistant_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0C4A6E), Color(0xFF0369A1))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF082F49),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.BANGLA) "Gemini AI অপটিক্যাল সিগন্যাল বিশ্লেষণ" else "Gemini AI Signal & Health",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (language == Language.BANGLA) "লাইভ পিং, ফাইবার ড্রপ ও স্পিড টিপস পান" else "Instant fiber health check & Wi-Fi optimizer",
                                fontSize = 11.sp,
                                color = Color(0xFFBAE6FD)
                            )
                        }
                        Button(
                            onClick = onOpenAiSupport,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D2FF)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (language == Language.BANGLA) "বিশ্লেষণ" else "Analyze",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF082F49)
                            )
                        }
                    }
                }
            }
        }

        // Recent Invoices section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get(language, "recent_invoices"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(bills.take(3)) { bill ->
            BillItemCard(
                bill = bill,
                language = language,
                onViewReceipt = { onViewBill(bill) },
                onPayNow = onPayBillClick
            )
        }

        // Need Another Line? Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToNewConnection() }
                    .testTag("apply_new_line_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddLink,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get(language, "request_new_connection"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = Strings.get(language, "new_connection_desc"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Helpline & Support
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HeadsetMic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.BANGLA) "২৪/৭ কাস্টমার সাপোর্ট হেল্পলাইন" else "24/7 Dedicated Support",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hotline: +880 9638-000111 • Email: support@xlink.com.bd",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerBillsScreen(
    bills: List<Bill>,
    language: Language,
    onPayBillClick: () -> Unit,
    onViewBill: (Bill) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Due, 2: Paid

    val filteredBills = remember(bills, selectedFilter) {
        when (selectedFilter) {
            1 -> bills.filter { !it.isPaid }
            2 -> bills.filter { it.isPaid }
            else -> bills
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = Strings.get(language, "nav_bills"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedFilter == 0,
                    onClick = { selectedFilter = 0 },
                    label = { Text(if (language == Language.BANGLA) "সকল ইনভয়েস (${bills.size})" else "All (${bills.size})") }
                )
                FilterChip(
                    selected = selectedFilter == 1,
                    onClick = { selectedFilter = 1 },
                    label = { Text(if (language == Language.BANGLA) "বকেয়া (${bills.count { !it.isPaid }})" else "Due (${bills.count { !it.isPaid }})") }
                )
                FilterChip(
                    selected = selectedFilter == 2,
                    onClick = { selectedFilter = 2 },
                    label = { Text(if (language == Language.BANGLA) "পরিশোধিত (${bills.count { it.isPaid }})" else "Paid (${bills.count { it.isPaid }})") }
                )
            }
        }

        items(filteredBills) { bill ->
            BillItemCard(
                bill = bill,
                language = language,
                onViewReceipt = { onViewBill(bill) },
                onPayNow = onPayBillClick
            )
        }
    }
}

@Composable
fun BillItemCard(
    bill: Bill,
    language: Language,
    onViewReceipt: () -> Unit,
    onPayNow: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == Language.BANGLA) bill.billingMonthBn else bill.billingMonth,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (bill.isPaid) Color(0xFFDCFCE7) else Color(0xFFFFEDD5),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (bill.isPaid) Strings.get(language, "paid_status") else Strings.get(language, "unpaid_status"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (bill.isPaid) Color(0xFF15803D) else Color(0xFFC2410C),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${bill.invoiceNumber} • Due: ${bill.dueDate}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.outline
                )
                if (bill.isPaid && bill.transactionId != null) {
                    Text(
                        text = "Trx: ${bill.transactionId} (${bill.paymentMethod?.displayName ?: "Online"})",
                        fontSize = 10.sp,
                        color = Color(0xFF16A34A)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳ ${bill.amount.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (bill.isPaid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (bill.isPaid) {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(text = Strings.get(language, "view_receipt"), fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onPayNow,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(text = Strings.get(language, "pay_now"), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun NewConnectionScreen(
    packages: List<InternetPackage>,
    requests: List<ConnectionRequest>,
    language: Language,
    onSubmitRequest: (String, String, String, String, String, String, Double, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf("Uttara - Zone 2") }
    var selectedPackage by remember { mutableStateOf(packages[1]) }
    var routerNeeded by remember { mutableStateOf(true) }
    var submittedSuccessfully by remember { mutableStateOf(false) }

    val zones = listOf(
        "Uttara - Zone 2",
        "Gulshan / Banani - Zone 1",
        "Dhanmondi - Zone 1",
        "Mirpur - Zone 4",
        "Bashundhara R/A - Zone 3",
        "Mohakhali DOHS - Zone 1",
        "Mohammadpur - Zone 5"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = Strings.get(language, "request_new_connection"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = Strings.get(language, "new_connection_desc"),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // Form Fields
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(Strings.get(language, "full_name")) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_conn_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(Strings.get(language, "mobile_number")) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_conn_phone_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(Strings.get(language, "email_address")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(Strings.get(language, "full_address")) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = Strings.get(language, "select_zone"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        zones.take(3).forEach { zone ->
                            FilterChip(
                                selected = selectedZone == zone,
                                onClick = { selectedZone = zone },
                                label = { Text(zone.substringBefore(" -"), fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = Strings.get(language, "select_package"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        packages.forEach { pkg ->
                            val isSelected = selectedPackage.id == pkg.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                                    .clickable { selectedPackage = pkg }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = pkg.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${pkg.speedMbps} Mbps • BDIX ${pkg.bdixSpeedMbps} Mbps", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Text(
                                        text = "৳ ${pkg.monthlyPrice.toInt()}/mo",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { routerNeeded = !routerNeeded }
                    ) {
                        Checkbox(checked = routerNeeded, onCheckedChange = { routerNeeded = it })
                        Text(text = Strings.get(language, "router_needed"), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (name.isNotEmpty() && phone.isNotEmpty()) {
                                onSubmitRequest(
                                    name,
                                    phone,
                                    email.ifEmpty { "client@xlink.com" },
                                    address.ifEmpty { "Uttara, Dhaka" },
                                    selectedZone,
                                    selectedPackage.name,
                                    selectedPackage.monthlyPrice,
                                    routerNeeded
                                )
                                submittedSuccessfully = true
                            }
                        },
                        enabled = name.isNotEmpty() && phone.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("submit_new_conn_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get(language, "submit_request"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (submittedSuccessfully) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "✓ " + Strings.get(language, "request_submitted_success"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A)
                        )
                    }
                }
            }
        }

        // Existing Tracking Status
        item {
            Text(
                text = Strings.get(language, "track_request_status"),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(requests) { req ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = req.applicantName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = when (req.status) {
                                RequestStatus.ACTIVATED -> Color(0xFFDCFCE7)
                                RequestStatus.FIBER_PULLED, RequestStatus.SURVEY_COMPLETED -> Color(0xFFE0F2FE)
                                else -> Color(0xFFFEF3C7)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (language == Language.BANGLA) req.status.labelBn else req.status.labelEn,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (req.status) {
                                    RequestStatus.ACTIVATED -> Color(0xFF15803D)
                                    RequestStatus.FIBER_PULLED, RequestStatus.SURVEY_COMPLETED -> Color(0xFF0369A1)
                                    else -> Color(0xFFB45309)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${req.requestedPackage} • Zone: ${req.areaZone}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Notes: ${req.notes} • Assigned: ${req.assignedAgent}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
