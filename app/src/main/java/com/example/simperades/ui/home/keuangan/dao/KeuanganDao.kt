package com.example.simperades.ui.home.keuangan.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface KeuanganDao {
    @Query("SELECT * FROM keuangan ORDER BY tanggal DESC")
    fun getAllKeuangan(): Flow<List<Keuangan>>

    @Query("SELECT * FROM keuangan WHERE jenis = :jenis ORDER BY tanggal DESC")
    fun getKeuanganByJenis(jenis: String): Flow<List<Keuangan>>

    @Query("SELECT SUM(jumlah) FROM keuangan WHERE jenis = 'PEMASUKAN'")
    fun getTotalPemasukan(): Flow<Double?>

    @Query("SELECT SUM(jumlah) FROM keuangan WHERE jenis = 'PENGELUARAN'")
    fun getTotalPengeluaran(): Flow<Double?>

    @Insert
    suspend fun insert(keuangan: Keuangan)

    @Update
    suspend fun update(keuangan: Keuangan)

    @Delete
    suspend fun delete(keuangan: Keuangan)

    @Query("DELETE FROM keuangan WHERE id = :id")
    suspend fun deleteById(id: Int)
}