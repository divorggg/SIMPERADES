package com.example.simperades.ui.home.keuangan.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "keuangan")
data class Keuangan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tanggal: Long = System.currentTimeMillis(),
    val jenis: String,
    val kategori: String,
    val jumlah: Double,

    // TAMBAHAN BARU
    val jumlahKg: Double? = null,
    val hargaPerKg: Double? = null,

    val keterangan: String = "",
    val kerambaId: Int? = null
)
