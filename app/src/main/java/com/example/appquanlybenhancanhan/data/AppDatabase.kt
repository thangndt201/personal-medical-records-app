package com.example.appquanlybenhancanhan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Patient::class, KetQuaXetNghiem::class, LichHen::class, DonThuoc::class, NhacNho::class, BenhAn::class, LuuBenhAnTimKiem::class],
    version = 14,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun ketQuaXetNghiemDao(): KetQuaXetNghiemDao
    abstract fun lichHenDao(): LichHenDao
    abstract fun donThuocDao(): DonThuocDao
    abstract fun nhacNhoDao(): NhacNhoDao
    abstract fun benhAnDao(): BenhAnDao
    abstract fun benhAnLuuDao(): LuuBenhAnTK
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "benhnhan_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}