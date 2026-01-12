package com.example.simperades.ui.home.keuangan.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "keuangan")
data class Keuangan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tanggal: Long = System.currentTimeMillis(), // Timestamp
    val jenis: String, // "PEMASUKAN" atau "PENGELUARAN"
    val kategori: String, // "Pakan", "Bibit", "Penjualan", dll
    val jumlah: Double,
    val keterangan: String = "",
    val kerambaId: Int? = null // Opsional, terkait keramba tertentu
)
