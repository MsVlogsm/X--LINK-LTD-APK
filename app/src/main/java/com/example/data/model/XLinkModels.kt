package com.example.data.model

enum class ConnectionStatus(val labelEn: String, val labelBn: String) {
    ACTIVE("Active", "সক্রিয়"),
    EXPIRED("Expired / Due", "মেয়াদোত্তীর্ণ / বকেয়া"),
    SUSPENDED("Suspended", "স্থগিত"),
    PENDING("Pending Setup", "অপেক্ষমান")
}

enum class PaymentMethod(val displayName: String, val providerName: String) {
    BKASH("bKash", "বিকাশ"),
    NAGAD("Nagad", "নগদ"),
    ROCKET("Rocket", "রকেট"),
    CARD("Visa / Master Card", "কার্ড"),
    CASH("Cash (Agent)", "নগদ (এজেন্ট কালেকশন)")
}

enum class RequestStatus(val labelEn: String, val labelBn: String) {
    SUBMITTED("Submitted", "আবেদন জমা হয়েছে"),
    FEASIBILITY_CHECK("Feasibility Check", "সম্ভাব্যতা যাচাই"),
    SURVEY_COMPLETED("Survey Completed", "সার্ভে সম্পন্ন"),
    FIBER_PULLED("Fiber Pulled", "ফাইবার সংযোগ টানা হয়েছে"),
    ACTIVATED("Activated", "সংযোগ চালু হয়েছে"),
    CANCELLED("Cancelled", "বাতিল")
}

enum class UserRole {
    CUSTOMER,
    AGENT_ADMIN
}

enum class Language {
    BANGLA,
    ENGLISH
}

data class Customer(
    val id: String,
    val customerCode: String,
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val areaZone: String,
    val pppoeUsername: String,
    val ipAddress: String,
    val onuMac: String,
    val bandwidthMbps: Int,
    val packageName: String,
    val monthlyFee: Double,
    val connectionStatus: ConnectionStatus,
    val billingCycleDay: Int = 1,
    val dueDate: String,
    val expiryDate: String,
    val lastPaymentDate: String,
    val balanceDue: Double,
    val opticalPowerDbm: Double = -19.4,
    val downloadSpeedMbps: Double = 48.6,
    val uploadSpeedMbps: Double = 47.9,
    val pingMs: Int = 4
)

data class Bill(
    val id: String,
    val customerId: String,
    val customerName: String,
    val invoiceNumber: String,
    val billingMonth: String,
    val billingMonthBn: String,
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean,
    val paidDate: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val transactionId: String? = null
)

data class ConnectionRequest(
    val id: String,
    val applicantName: String,
    val phone: String,
    val email: String,
    val fullAddress: String,
    val areaZone: String,
    val requestedPackage: String,
    val monthlyFee: Double,
    val routerNeeded: Boolean,
    val status: RequestStatus,
    val assignedAgent: String = "Unassigned",
    val notes: String = "",
    val requestDate: String
)

data class AppNotification(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val messageEn: String,
    val messageBn: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionType: String? = null
)

enum class NotificationType {
    BILL_DUE,
    PAYMENT_SUCCESS,
    DISCONNECTION_ALERT,
    NEW_CONNECTION,
    MAINTENANCE,
    API_SYNC
}

data class ApiSyncConfig(
    val baseUrl: String = "https://billing.xlink.com.bd/api/v1/",
    val apiKey: String = "xlk_live_9a87f8e3c12d4a5b",
    val bearerToken: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    val syncIntervalMinutes: Int = 5,
    val autoSyncEnabled: Boolean = true,
    val isRealtimeWebhook: Boolean = true,
    val lastSyncTime: String = "Just now"
)

data class SyncLogEntry(
    val id: String,
    val timestamp: String,
    val endpoint: String,
    val httpMethod: String,
    val statusCode: Int,
    val responseTimeMs: Long,
    val message: String,
    val isSuccess: Boolean
)

data class InternetPackage(
    val id: String,
    val name: String,
    val speedMbps: Int,
    val monthlyPrice: Double,
    val realIp: Boolean = false,
    val youtubeCache: Boolean = true,
    val bdixSpeedMbps: Int = 100,
    val description: String
)
