package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.BillingRecordDao
import com.example.data.local.RoomBillingRecord
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "RoomFirestoreSync"

@IgnoreExtraProperties
data class FirestoreBillingRecord(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerCode: String = "",
    val amount: Double = 0.0,
    val billingMonth: String = "",
    val dueDate: String = "",
    val isPaid: Boolean = false,
    val paidDate: String? = null,
    val paymentMethod: String? = null,
    val transactionId: String? = null,
    val syncStatus: String = "SYNCED",
    @ServerTimestamp val updatedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class SyncStatusState(
    val isSyncing: Boolean = false,
    val totalRoomRecords: Int = 0,
    val totalCloudRecords: Int = 0,
    val pendingUploadCount: Int = 0,
    val lastSyncTime: String = "Not synced yet",
    val statusMessage: String = "Idle",
    val isSuccess: Boolean = true
)

class RoomFirestoreSyncService(
    private val context: Context,
    private val dao: BillingRecordDao
) {
    // CRITICAL: Mandatory initialization with named database ID from firebase_applet_config.xml
    private val firestoreDb: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    private val _syncState = MutableStateFlow(SyncStatusState())
    val syncState: StateFlow<SyncStatusState> = _syncState.asStateFlow()

    private var realtimeListener: ListenerRegistration? = null
    private val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.US)

    suspend fun initializeAndSeed(defaultRecords: List<RoomBillingRecord>) = withContext(Dispatchers.IO) {
        // Pre-populate Room if empty
        val pending = dao.getPendingSyncRecords()
        if (pending.isEmpty()) {
            dao.insertAll(defaultRecords)
            Log.d(TAG, "Pre-populated ${defaultRecords.size} default billing records in Room DB.")
        }
    }

    /**
     * Upload all local records with PENDING_UPLOAD status to cloud Firestore
     */
    suspend fun syncLocalToCloud(): Int = withContext(Dispatchers.IO) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            Log.d(TAG, "Sync to cloud deferred: User is not authenticated. Records safely queued in Room SQLite.")
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = "Offline Room storage active (Sign in with Google to sync to cloud)"
            )
            return@withContext 0
        }

        _syncState.value = _syncState.value.copy(isSyncing = true, statusMessage = "Uploading local records to Firestore...")
        val pendingRecords = dao.getPendingSyncRecords()
        var uploadedCount = 0

        try {
            val collection = firestoreDb.collection("billing_records")
            for (record in pendingRecords) {
                val firestoreDoc = FirestoreBillingRecord(
                    id = record.id,
                    customerId = record.customerId,
                    customerName = record.customerName,
                    customerCode = record.customerCode,
                    amount = record.amount,
                    billingMonth = record.billingMonth,
                    dueDate = record.dueDate,
                    isPaid = record.isPaid,
                    paidDate = record.paidDate,
                    paymentMethod = record.paymentMethod,
                    transactionId = record.transactionId,
                    syncStatus = "SYNCED"
                )

                collection.document(record.id).set(firestoreDoc).await()
                dao.updateSyncStatus(record.id, "SYNCED", System.currentTimeMillis())
                uploadedCount++
            }

            val nowStr = timeFormat.format(Date())
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncTime = nowStr,
                statusMessage = "Successfully uploaded $uploadedCount records to Firestore at $nowStr",
                isSuccess = true
            )
            Log.d(TAG, "Successfully synced $uploadedCount records from Room to Firestore.")
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "billing_records")
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = "Upload warning: ${e.localizedMessage ?: "Sync queued locally"}",
                isSuccess = false
            )
        }
        uploadedCount
    }

    /**
     * Download billing records from cloud Firestore and reconcile into Room DB
     */
    suspend fun syncCloudToLocal(): Int = withContext(Dispatchers.IO) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            Log.d(TAG, "Cloud fetch deferred: User is not authenticated. Using local Room database.")
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = "Offline Room cache active"
            )
            return@withContext 0
        }

        _syncState.value = _syncState.value.copy(isSyncing = true, statusMessage = "Fetching cloud records from Firestore...")
        var downloadedCount = 0

        try {
            val snapshot = firestoreDb.collection("billing_records").get().await()
            val roomEntities = snapshot.documents.mapNotNull { doc ->
                val cloudRecord = doc.toObject(FirestoreBillingRecord::class.java)
                cloudRecord?.let {
                    RoomBillingRecord(
                        id = it.id.ifEmpty { doc.id },
                        customerId = it.customerId,
                        customerName = it.customerName,
                        customerCode = it.customerCode,
                        amount = it.amount,
                        billingMonth = it.billingMonth,
                        dueDate = it.dueDate,
                        isPaid = it.isPaid,
                        paidDate = it.paidDate,
                        paymentMethod = it.paymentMethod,
                        transactionId = it.transactionId,
                        syncStatus = "SYNCED",
                        lastSyncedAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }

            if (roomEntities.isNotEmpty()) {
                dao.insertAll(roomEntities)
                downloadedCount = roomEntities.size
            }

            val nowStr = timeFormat.format(Date())
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                totalCloudRecords = downloadedCount,
                lastSyncTime = nowStr,
                statusMessage = "Synced $downloadedCount records from Firestore into Room at $nowStr",
                isSuccess = true
            )
            Log.d(TAG, "Successfully downloaded and cached $downloadedCount records in Room.")
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "billing_records")
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = "Download notice: ${e.localizedMessage ?: "Using offline Room cache"}",
                isSuccess = false
            )
        }
        downloadedCount
    }

    /**
     * Full bidirectional sync: push local pending changes, then pull cloud updates
     */
    suspend fun performFullSync(): Boolean = withContext(Dispatchers.IO) {
        _syncState.value = _syncState.value.copy(isSyncing = true, statusMessage = "Starting bidirectional Room-Firestore sync...")
        syncLocalToCloud()
        syncCloudToLocal()
        _syncState.value = _syncState.value.copy(isSyncing = false)
        true
    }

    /**
     * Start real-time Firestore snapshot listener to auto-reflect cloud billing changes into local Room
     */
    fun startRealtimeSync(scope: CoroutineScope) {
        if (realtimeListener != null) return

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            Log.d(TAG, "Real-time sync listener deferred: Waiting for user authentication.")
            return
        }

        try {
            realtimeListener = firestoreDb.collection("billing_records")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        handleFirestoreError(error, OperationType.LIST, "billing_records")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch(Dispatchers.IO) {
                            val cloudRecords = snapshot.documents.mapNotNull { doc ->
                                val it = doc.toObject(FirestoreBillingRecord::class.java)
                                it?.let {
                                    RoomBillingRecord(
                                        id = it.id.ifEmpty { doc.id },
                                        customerId = it.customerId,
                                        customerName = it.customerName,
                                        customerCode = it.customerCode,
                                        amount = it.amount,
                                        billingMonth = it.billingMonth,
                                        dueDate = it.dueDate,
                                        isPaid = it.isPaid,
                                        paidDate = it.paidDate,
                                        paymentMethod = it.paymentMethod,
                                        transactionId = it.transactionId,
                                        syncStatus = "SYNCED",
                                        lastSyncedAt = System.currentTimeMillis(),
                                        updatedAt = System.currentTimeMillis()
                                    )
                                }
                            }
                            dao.insertAll(cloudRecords)
                            _syncState.value = _syncState.value.copy(
                                totalCloudRecords = cloudRecords.size,
                                lastSyncTime = timeFormat.format(Date()),
                                statusMessage = "Real-time sync: ${cloudRecords.size} cloud records updated in Room"
                            )
                        }
                    }
                }
            Log.d(TAG, "Real-time Room-Firestore snapshot listener started.")
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "billing_records")
        }
    }

    fun stopRealtimeSync() {
        realtimeListener?.remove()
        realtimeListener = null
    }

    /**
     * Attach Firebase Auth listener to automatically manage sync lifecycle
     */
    fun attachAuthLifecycle(scope: CoroutineScope) {
        Firebase.auth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                Log.d(TAG, "Firebase Auth session active (${auth.currentUser?.email}). Starting sync.")
                startRealtimeSync(scope)
                scope.launch {
                    syncLocalToCloud()
                    syncCloudToLocal()
                }
            } else {
                Log.d(TAG, "Firebase Auth session unauthenticated. Pausing real-time listener.")
                stopRealtimeSync()
            }
        }
    }

    /**
     * Save or update a billing record locally in Room with PENDING_UPLOAD, then trigger cloud sync
     */
    suspend fun saveBillingRecord(record: RoomBillingRecord) = withContext(Dispatchers.IO) {
        // 1. Save to Room immediately
        val pendingRecord = record.copy(
            syncStatus = "PENDING_UPLOAD",
            updatedAt = System.currentTimeMillis()
        )
        dao.insertOrUpdate(pendingRecord)

        // 2. Trigger async push to Firestore
        syncLocalToCloud()
    }
}
