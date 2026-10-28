package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.data.model.UserRole
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.strings.Strings
import com.example.ui.theme.XLinkTheme
import com.example.ui.viewmodel.AgentTab
import com.example.ui.viewmodel.CustomerTab
import com.example.ui.viewmodel.XLinkViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XLinkTheme {
                XLinkApp()
            }
        }
    }
}

@Composable
fun XLinkApp(
    viewModel: XLinkViewModel = viewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val customerTab by viewModel.customerTab.collectAsStateWithLifecycle()
    val agentTab by viewModel.agentTab.collectAsStateWithLifecycle()

    val currentCustomer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    val connectionRequests by viewModel.connectionRequests.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()
    val apiConfig by viewModel.apiConfig.collectAsStateWithLifecycle()
    val packages = viewModel.availablePackages

    val isPaymentModalVisible by viewModel.isPaymentModalVisible.collectAsStateWithLifecycle()
    val isInvoiceModalVisible by viewModel.isInvoiceModalVisible.collectAsStateWithLifecycle()
    val selectedInvoice by viewModel.selectedInvoice.collectAsStateWithLifecycle()
    val isAgentCollectModalVisible by viewModel.isAgentCollectModalVisible.collectAsStateWithLifecycle()
    val collectTargetCustomer by viewModel.collectTargetCustomer.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncToastMessage by viewModel.syncToastMessage.collectAsStateWithLifecycle()

    val unreadNotifCount = notifications.count { !it.isRead }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            XLinkHeaderBar(
                language = language,
                currentRole = userRole,
                unreadNotifCount = unreadNotifCount,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onRoleSelected = { viewModel.setUserRole(it) },
                onOpenNotifications = {
                    if (userRole == UserRole.CUSTOMER) {
                        viewModel.setCustomerTab(CustomerTab.NOTIFICATIONS)
                    } else {
                        viewModel.setAgentTab(AgentTab.DASHBOARD)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_navigation_bar"),
                tonalElevation = 8.dp
            ) {
                if (userRole == UserRole.CUSTOMER) {
                    NavigationBarItem(
                        selected = customerTab == CustomerTab.OVERVIEW,
                        onClick = { viewModel.setCustomerTab(CustomerTab.OVERVIEW) },
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text(Strings.get(language, "nav_overview"), fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_overview_tab")
                    )
                    NavigationBarItem(
                        selected = customerTab == CustomerTab.BILLS,
                        onClick = { viewModel.setCustomerTab(CustomerTab.BILLS) },
                        icon = { Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null) },
                        label = { Text(Strings.get(language, "nav_bills"), fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_bills_tab")
                    )
                    NavigationBarItem(
                        selected = customerTab == CustomerTab.NEW_CONNECTION,
                        onClick = { viewModel.setCustomerTab(CustomerTab.NEW_CONNECTION) },
                        icon = { Icon(imageVector = Icons.Default.AddLink, contentDescription = null) },
                        label = { Text(Strings.get(language, "nav_new_conn"), fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_new_connection_tab")
                    )
                    NavigationBarItem(
                        selected = customerTab == CustomerTab.NOTIFICATIONS,
                        onClick = { viewModel.setCustomerTab(CustomerTab.NOTIFICATIONS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifCount > 0) {
                                        Badge { Text("$unreadNotifCount") }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
                            }
                        },
                        label = { Text(Strings.get(language, "nav_alerts"), fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_notifications_tab")
                    )
                } else {
                    NavigationBarItem(
                        selected = agentTab == AgentTab.DASHBOARD,
                        onClick = { viewModel.setAgentTab(AgentTab.DASHBOARD) },
                        icon = { Icon(imageVector = Icons.Default.Analytics, contentDescription = null) },
                        label = { Text(Strings.get(language, "nav_agent_dashboard"), fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_agent_dashboard_tab")
                    )
                    NavigationBarItem(
                        selected = agentTab == AgentTab.REQUESTS,
                        onClick = { viewModel.setAgentTab(AgentTab.REQUESTS) },
                        icon = { Icon(imageVector = Icons.Default.Build, contentDescription = null) },
                        label = { Text(Strings.get(language, "nav_requests"), fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_agent_requests_tab")
                    )
                    NavigationBarItem(
                        selected = agentTab == AgentTab.WEB_API,
                        onClick = { viewModel.setAgentTab(AgentTab.WEB_API) },
                        icon = { Icon(imageVector = Icons.Default.Dns, contentDescription = null) },
                        label = { Text(Strings.get(language, "nav_api_sync"), fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_agent_web_api_tab")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Screen Routing
            if (userRole == UserRole.CUSTOMER) {
                when (customerTab) {
                    CustomerTab.OVERVIEW -> {
                        CustomerDashboardScreen(
                            customer = currentCustomer,
                            bills = bills,
                            language = language,
                            onPayBillClick = { viewModel.openPaymentModal() },
                            onViewBill = { viewModel.openInvoice(it) },
                            onNavigateToNewConnection = { viewModel.setCustomerTab(CustomerTab.NEW_CONNECTION) }
                        )
                    }
                    CustomerTab.BILLS -> {
                        CustomerBillsScreen(
                            bills = bills,
                            language = language,
                            onPayBillClick = { viewModel.openPaymentModal() },
                            onViewBill = { viewModel.openInvoice(it) }
                        )
                    }
                    CustomerTab.HEALTH -> {
                        CustomerDashboardScreen(
                            customer = currentCustomer,
                            bills = bills,
                            language = language,
                            onPayBillClick = { viewModel.openPaymentModal() },
                            onViewBill = { viewModel.openInvoice(it) },
                            onNavigateToNewConnection = { viewModel.setCustomerTab(CustomerTab.NEW_CONNECTION) }
                        )
                    }
                    CustomerTab.NEW_CONNECTION -> {
                        NewConnectionScreen(
                            packages = packages,
                            requests = connectionRequests,
                            language = language,
                            onSubmitRequest = { name, phone, email, address, zone, pkgName, price, router ->
                                viewModel.submitNewConnection(name, phone, email, address, zone, pkgName, price, router)
                            }
                        )
                    }
                    CustomerTab.NOTIFICATIONS -> {
                        NotificationsScreen(
                            notifications = notifications,
                            language = language,
                            onMarkAllRead = { viewModel.markNotificationsRead() }
                        )
                    }
                }
            } else {
                when (agentTab) {
                    AgentTab.DASHBOARD, AgentTab.DIRECTORY, AgentTab.COLLECTION -> {
                        AgentDashboardScreen(
                            customers = allCustomers,
                            connectionRequests = connectionRequests,
                            language = language,
                            onCollectCashClick = { viewModel.openAgentCollect(it) },
                            onGrantGraceClick = { viewModel.grantGracePeriod(it) },
                            onUpdateStatusClick = { id, status -> viewModel.updateCustomerStatus(id, status) },
                            onUpdateRequestStatus = { id, status -> viewModel.updateRequestStatus(id, status) }
                        )
                    }
                    AgentTab.REQUESTS -> {
                        NewConnectionScreen(
                            packages = packages,
                            requests = connectionRequests,
                            language = language,
                            onSubmitRequest = { name, phone, email, address, zone, pkgName, price, router ->
                                viewModel.submitNewConnection(name, phone, email, address, zone, pkgName, price, router)
                            }
                        )
                    }
                    AgentTab.WEB_API -> {
                        WebBillingApiScreen(
                            config = apiConfig,
                            logs = syncLogs,
                            isSyncing = isSyncing,
                            language = language,
                            onSyncNow = { url, key, token -> viewModel.syncWebBillingApi(url, key, token) },
                            onTestPing = { url, key, token -> viewModel.testApiPing(url, key, token) }
                        )
                    }
                }
            }

            // Sync Toast Notification Banner
            AnimatedVisibility(
                visible = syncToastMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = syncToastMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Interactive Payment Modal
            if (isPaymentModalVisible) {
                val unpaid = bills.firstOrNull { it.customerId == currentCustomer.id && !it.isPaid }
                val amountToPay = unpaid?.amount ?: currentCustomer.monthlyFee
                PaymentModal(
                    customer = currentCustomer,
                    amount = amountToPay,
                    language = language,
                    onDismiss = { viewModel.closePaymentModal() },
                    onConfirmPayment = { method, walletAccount ->
                        viewModel.processCustomerPayment(method, walletAccount)
                    }
                )
            }

            // Invoice Dialog
            if (isInvoiceModalVisible && selectedInvoice != null) {
                val bill = selectedInvoice!!
                val targetCust = allCustomers.firstOrNull { it.id == bill.customerId } ?: currentCustomer
                InvoiceDialog(
                    bill = bill,
                    customer = targetCust,
                    language = language,
                    onDismiss = { viewModel.closeInvoice() }
                )
            }

            // Agent Cash Collection Dialog
            if (isAgentCollectModalVisible && collectTargetCustomer != null) {
                AgentCollectCashDialog(
                    customer = collectTargetCustomer!!,
                    language = language,
                    onDismiss = { viewModel.closeAgentCollect() },
                    onConfirmCollection = { amount ->
                        viewModel.processAgentCollection(collectTargetCustomer!!.id, amount)
                    }
                )
            }
        }
    }
}
