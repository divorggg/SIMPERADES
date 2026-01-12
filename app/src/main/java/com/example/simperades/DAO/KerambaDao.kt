package com.example.simperades.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KerambaDao {
    // Mendapatkan semua keramba dan memancarkannya sebagai Flow (data reaktif)
    @Query("SELECT * FROM keramba ORDER BY id ASC")
    fun getAllKeramba(): Flow<List<KerambaEntity>>

    // Mendapatkan keramba berdasarkan waktu akses terbaru
    @Query("SELECT * FROM keramba ORDER BY lastAccessed DESC LIMIT :limit")
    fun getLatestSeenKeramba(limit: Int): Flow<List<KerambaEntity>>

    // Menyimpan atau mengganti keramba
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeramba(keramba: KerambaEntity)

    // Mengupdate keramba
    @Update
    suspend fun updateKeramba(keramba: KerambaEntity)

    // Mendapatkan keramba berdasarkan ID
    @Query("SELECT * FROM keramba WHERE id = :id")
    fun getKerambaById(id: Int): Flow<KerambaEntity?>

    @Query("SELECT COUNT(*) FROM keramba")
    suspend fun getCount(): Int

    // Mendapatkan keramba terakhir berdasarkan ID terbesar
    @Query("SELECT * FROM keramba ORDER BY id DESC LIMIT 1")
    suspend fun getLastKeramba(): KerambaEntity?

    @Query("DELETE FROM keramba WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM keramba WHERE id = :id LIMIT 1")
    suspend fun getKerambaById2(id: Int): KerambaEntity?

    @Query("UPDATE keramba SET riwayatTambahJson = :riwayatTambahJson, JumlahIkan = :jumlahIkan WHERE id = :id")
    suspend fun updateRiwayatTambahAndJumlahIkan(id: Int, riwayatTambahJson: String, jumlahIkan: Int)


    @Query("SELECT SUM(jumlahIkan) FROM keramba") // Ganti 'jumlahIkan' dan 'keramba' jika nama kolom/tabel Anda berbeda
    fun getTotalIkan(): Flow<Int?> // Menggunakan Flow agar data update otomatis & Int? untuk handle jika tabel kosong
}