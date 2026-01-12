package com.example.simperades

import android.app.Application
import androidx.room.Room
import com.example.simperades.DAO.KerambaDao
import com.example.simperades.DAO.AppDatabase
import com.example.simperades.ui.home.keuangan.dao.KeuanganDao

class SimperadesApp : Application() {

    companion object {
        lateinit var database: AppDatabase
        lateinit var kerambaDao: KerambaDao
        lateinit var keuanganDao: KeuanganDao
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "simperades_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        kerambaDao = database.kerambaDao()
        keuanganDao = database.keuanganDao()
    }
}