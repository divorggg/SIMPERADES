package com.example.simperades.DAO

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.simperades.ui.home.keuangan.dao.Keuangan
import com.example.simperades.ui.home.keuangan.dao.KeuanganDao

@Database(
    entities = [
        KerambaEntity::class,
        Keuangan::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun kerambaDao(): KerambaDao
    abstract fun keuanganDao(): KeuanganDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton pattern untuk mendapatkan instance database
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simperades_database"
                )
                    .addMigrations(MIGRATION_1_2) // ✅ Gunakan migration untuk production
                    // .fallbackToDestructiveMigration() // ❌ HAPUS INI untuk production
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Migration dari versi 1 ke 2
         * Menambahkan tabel keuangan tanpa menghapus data lama
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Buat tabel keuangan
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS keuangan (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tanggal INTEGER NOT NULL,
                        jenis TEXT NOT NULL,
                        kategori TEXT NOT NULL,
                        jumlah REAL NOT NULL,
                        keterangan TEXT NOT NULL,
                        kerambaId INTEGER
                    )
                """.trimIndent())
            }
        }

        /**
         * Hapus instance database (untuk testing)
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}