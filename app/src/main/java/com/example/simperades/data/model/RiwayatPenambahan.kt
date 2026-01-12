package com.example.simperades.data.model

data class RiwayatPenambahan(
    val tanggal: String,
    val jumlah: Int,
    val timestamp: Long = System.currentTimeMillis()
)
