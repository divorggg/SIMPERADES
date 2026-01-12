package com.example.simperades.data.model


data class DailyDeathReport(
    val tanggal: String,
    val pagi: Int = 0,
    val siang: Int = 0,
    val sore: Int = 0,
    val malam: Int = 0
) {
    val total: Int
        get() = pagi + siang + sore + malam
}