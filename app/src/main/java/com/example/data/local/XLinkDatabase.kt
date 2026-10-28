package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoomBillingRecord::class], version = 1, exportSchema = false)
abstract class XLinkDatabase : RoomDatabase() {
    abstract fun billingRecordDao(): BillingRecordDao

    companion object {
        @Volatile
        private var INSTANCE: XLinkDatabase? = null

        fun getDatabase(context: Context): XLinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XLinkDatabase::class.java,
                    "xlink_billing_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
