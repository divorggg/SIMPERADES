package com.example.simperades.data.model

data class RiwayatKematian(
    val kerambaNama: String,
    val tanggal: String,
    val jumlah: Int,
    val waktu: String, // "pagi", "siang", atau "sore"
    val timestamp: Long // Tambahkan timestamp untuk pengurutan yang akurat
)
