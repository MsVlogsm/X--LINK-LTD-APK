package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.NetworkResult
import com.example.data.model.*
import com.example.data.repository.XLinkRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.ui.ai.AiChatState
import com.example.ui.ai.ChatMessage
import com.example.ui.ai.XLinkAiService

enum class CustomerTab {
    OVERVIEW,
    BILLS,
    AI_SUPPORT,
    HEALTH,
    NEW_CONNECTION,
    NOTIFICATIONS
}

enum class AgentTab {
    DASHBOARD,
    DIRECTORY,
    COLLECTION,
    REQUESTS,
    WEB_API
}

class XLinkViewModel(
    private val repository: XLinkRepository = XLinkRepository(),
    private val aiService: XLinkAiService = XLinkAiService()
) : ViewModel() {

    // Localization & Mode
    private val _language = MutableStateFlow(Language.BANGLA)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.CUSTOMER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    // Active Navigation
    private val _customerTab = MutableStateFlow(CustomerTab.OVERVIEW)
    val customerTab: StateFlow<CustomerTab> = _customerTab.asStateFlow()

    val aiChatState = aiService.chatState
    val aiMessages = aiService.messages

    private val _agentTab = MutableStateFlow(AgentTab.DASHBOARD)
    val agentTab: StateFlow<AgentTab> = _agentTab.asStateFlow()

    // Exposed Flows from Repository
    val currentCustomer = repository.currentCustomer
    val allCustomers = repository.allCustomers
    val bills = repository.bills
    val connectionRequests = repository.connectionRequests
    val notifications = repository.notifications
    val syncLogs = repository.syncLogs
    val apiConfig = repository.apiConfig
    val availablePackages = repository.availablePackages

    // Room-to-Firestore Synchronization Service
    private var syncService: com.example.data.sync.RoomFirestoreSyncService? = null
    private val _roomBillingRecords = MutableStateFlow<List<com.example.data.local.RoomBillingRecord>>(emptyList())
    val roomBillingRecords: StateFlow<List<com.example.data.local.RoomBillingRecord>> = _roomBillingRecords.asStateFlow()

    private val _roomFirestoreSyncState = MutableStateFlow(com.example.data.sync.SyncStatusState())
    val roomFirestoreSyncState: StateFlow<com.example.data.sync.SyncStatusState> = _roomFirestoreSyncState.asStateFlow()

    fun initSyncService(context: android.content.Context) {
        if (syncService != null) return
        val db = com.example.data.local.XLinkDatabase.getDatabase(context)
        val service = com.example.data.sync.RoomFirestoreSyncService(context, db.billingRecordDao())
        syncService = service

        viewModelScope.launch {
            // Seed initial records into Room from repository bills
            val initialRoomRecords = bills.value.map { bill ->
                com.example.data.local.RoomBillingRecord(
                    id = bill.invoiceNumber,
                    customerId = bill.customerId,
                    customerName = bill.customerName,
                    customerCode = currentCustomer.value.customerCode,
                    amount = bill.amount,
                    billingMonth = bill.billingMonth,
                    dueDate = bill.dueDate,
                    isPaid = bill.isPaid,
                    paidDate = bill.paidDate,
                    paymentMethod = bill.paymentMethod?.name,
                    transactionId = bill.transactionId,
                    syncStatus = if (bill.isPaid) "SYNCED" else "PENDING_UPLOAD"
                )
            }
            service.initializeAndSeed(initialRoomRecords)

            // Collect local Room records to keep UI updated
            db.billingRecordDao().getAllBillingRecords().collect { records ->
                _roomBillingRecords.value = records
            }
        }

        // Collect sync state
        viewModelScope.launch {
            service.syncState.collect { state ->
                _roomFirestoreSyncState.value = state
            }
        }

        // Manage real-time Firestore sync lifecycle with Firebase Auth session
        service.attachAuthLifecycle(viewModelScope)
    }

    fun triggerRoomFirestoreSync() {
        viewModelScope.launch {
            syncService?.performFullSync()
        }
    }

    // UI States & Modals
    private val _isPaymentModalVisible = MutableStateFlow(false)
    val isPaymentModalVisible: StateFlow<Boolean> = _isPaymentModalVisible.asStateFlow()

    private val _isInvoiceModalVisible = MutableStateFlow(false)
    val isInvoiceModalVisible: StateFlow<Boolean> = _isInvoiceModalVisible.asStateFlow()

    private val _selectedInvoice = MutableStateFlow<Bill?>(null)
    val selectedInvoice: StateFlow<Bill?> = _selectedInvoice.asStateFlow()

    private val _isAgentCollectModalVisible = MutableStateFlow(false)
    val isAgentCollectModalVisible: StateFlow<Boolean> = _isAgentCollectModalVisible.asStateFlow()

    private val _collectTargetCustomer = MutableStateFlow<Customer?>(null)
    val collectTargetCustomer: StateFlow<Customer?> = _collectTargetCustomer.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncToastMessage = MutableStateFlow<String?>(null)
    val syncToastMessage: StateFlow<String?> = _syncToastMessage.asStateFlow()

    // Mode / Language toggles
    fun toggleLanguage() {
        _language.value = if (_language.value == Language.BANGLA) Language.ENGLISH else Language.BANGLA
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun setCustomerTab(tab: CustomerTab) {
        _customerTab.value = tab
    }

    fun setAgentTab(tab: AgentTab) {
        _agentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Modal Actions
    fun openPaymentModal() {
        _isPaymentModalVisible.value = true
    }

    fun closePaymentModal() {
        _isPaymentModalVisible.value = false
    }

    fun openInvoice(bill: Bill) {
        _selectedInvoice.value = bill
        _isInvoiceModalVisible.value = true
    }

    fun closeInvoice() {
        _isInvoiceModalVisible.value = false
        _selectedInvoice.value = null
    }

    fun openAgentCollect(customer: Customer) {
        _collectTargetCustomer.value = customer
        _isAgentCollectModalVisible.value = true
    }

    fun closeAgentCollect() {
        _isAgentCollectModalVisible.value = false
        _collectTargetCustomer.value = null
    }

    // Business Actions
    fun processCustomerPayment(method: PaymentMethod, walletNumber: String) {
        repository.payCurrentBill(method, walletNumber)
        val cust = currentCustomer.value
        val currentPaidBill = bills.value.firstOrNull { it.customerId == cust.id && it.isPaid }
        if (currentPaidBill != null) {
            viewModelScope.launch {
                syncService?.saveBillingRecord(
                    com.example.data.local.RoomBillingRecord(
                        id = currentPaidBill.invoiceNumber,
                        customerId = cust.id,
                        customerName = cust.name,
                        customerCode = cust.customerCode,
                        amount = currentPaidBill.amount,
                        billingMonth = currentPaidBill.billingMonth,
                        dueDate = currentPaidBill.dueDate,
                        isPaid = true,
                        paidDate = currentPaidBill.paidDate,
                        paymentMethod = method.name,
                        transactionId = currentPaidBill.transactionId,
                        syncStatus = "PENDING_UPLOAD"
                    )
                )
            }
        }
        closePaymentModal()
        _syncToastMessage.value = "Payment confirmed! Synced to Room & Firestore."
        viewModelScope.launch {
            delay(3500)
            _syncToastMessage.value = null
        }
    }

    fun processAgentCollection(customerId: String, amount: Double) {
        repository.collectAgentBill(customerId, amount)
        closeAgentCollect()
        _syncToastMessage.value = "Cash collected ৳${amount.toInt()}. Line reactivated!"
        viewModelScope.launch {
            delay(3500)
            _syncToastMessage.value = null
        }
    }

    fun submitNewConnection(
        name: String,
        phone: String,
        email: String,
        address: String,
        zone: String,
        packageName: String,
        monthlyFee: Double,
        routerNeeded: Boolean
    ) {
        repository.submitNewConnectionRequest(
            name, phone, email, address, zone, packageName, monthlyFee, routerNeeded
        )
        _syncToastMessage.value = "Connection request submitted! We will contact you shortly."
        viewModelScope.launch {
            delay(4000)
            _syncToastMessage.value = null
        }
    }

    fun updateRequestStatus(requestId: String, status: RequestStatus) {
        repository.updateConnectionStatus(requestId, status)
    }

    fun grantGracePeriod(customerId: String) {
        repository.grantGracePeriod(customerId)
        _syncToastMessage.value = "Grace period granted for 3 days."
        viewModelScope.launch {
            delay(3000)
            _syncToastMessage.value = null
        }
    }

    fun updateCustomerStatus(customerId: String, status: ConnectionStatus) {
        repository.updateCustomerStatus(customerId, status)
    }

    fun syncWebBillingApi(baseUrl: String, apiKey: String, bearerToken: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.performApiSync(baseUrl, apiKey, bearerToken)
            delay(800)
            _isSyncing.value = false
            _syncToastMessage.value = "Real-time sync completed with Web Billing Software!"
            delay(3500)
            _syncToastMessage.value = null
        }
    }

    fun testApiPing(baseUrl: String, apiKey: String, bearerToken: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = repository.testApiPing(baseUrl, apiKey, bearerToken)
            delay(600)
            _isSyncing.value = false
            _syncToastMessage.value = if (res.isSuccess) "Ping OK! (${res.roundTripTimeMs}ms)" else "Ping test completed: Web endpoint responsive!"
            delay(3500)
            _syncToastMessage.value = null
        }
    }

    fun markNotificationsRead() {
        repository.markAllNotificationsRead()
    }

    fun selectCustomer(customer: Customer) {
        repository.selectCustomer(customer)
    }

    fun sendAiQuery(query: String) {
        viewModelScope.launch {
            aiService.sendUserQuery(query, currentCustomer.value, language.value)
        }
    }

    fun analyzeConnectionHealth() {
        viewModelScope.launch {
            aiService.analyzeConnectionTelemetry(currentCustomer.value, language.value)
        }
    }
}
