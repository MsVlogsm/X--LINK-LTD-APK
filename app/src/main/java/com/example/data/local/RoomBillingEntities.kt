package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "billing_records")
data class RoomBillingRecord(
    @PrimaryKey
    val id: String, // e.g. "XLK-INV-2026-11-7842"
    val customerId: String,
    val customerName: String,
    val customerCode: String,
    val amount: Double,
    val billingMonth: String,
    val dueDate: String,
    val isPaid: Boolean,
    val paidDate: String? = null,
    val paymentMethod: String? = null,
    val transactionId: String? = null,
    val syncStatus: String = "SYNCED", // "SYNCED", "PENDING_UPLOAD", "FAILED"
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface BillingRecordDao {
    @Query("SELECT * FROM billing_records ORDER BY updatedAt DESC")
    fun getAllBillingRecords(): Flow<List<RoomBillingRecord>>

    @Query("SELECT * FROM billing_records WHERE customerId = :customerId ORDER BY updatedAt DESC")
    fun getRecordsForCustomer(customerId: String): Flow<List<RoomBillingRecord>>

    @Query("SELECT * FROM billing_records WHERE syncStatus = 'PENDING_UPLOAD'")
    suspend fun getPendingSyncRecords(): List<RoomBillingRecord>

    @Query("SELECT COUNT(*) FROM billing_records WHERE syncStatus = 'PENDING_UPLOAD'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM billing_records")
    fun getTotalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: RoomBillingRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<RoomBillingRecord>)

    @Query("UPDATE billing_records SET syncStatus = :status, lastSyncedAt = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    @Delete
    suspend fun delete(record: RoomBillingRecord)

    @Query("DELETE FROM billing_records")
    suspend fun clearAll()
}
