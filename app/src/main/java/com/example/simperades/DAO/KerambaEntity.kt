package com.example.simperades.DAO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keramba")
data class KerambaEntity(
    // ID yang dibuat otomatis oleh Room, dengan nilai default 0 saat insert baru
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nama: String,
    val lokasi: String,
    val jumlahIkan: Int,
    val jumlahMati: Int = 0,

    // Properti untuk fitur "Terakhir Dilihat"
    val lastAccessed: Long = 0,

    // Riwayat (disimpan sebagai string JSON untuk Room)
    val riwayatJson: String = "[]",
    val riwayatTambahJson: String = "[]"

)
