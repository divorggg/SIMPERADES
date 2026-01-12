package com.example.simperades.DAO

import com.example.simperades.DAO.KerambaDao
import com.example.simperades.DAO.KerambaEntity

class KerambaRepository(private val kerambaDao: KerambaDao) {

    // Data reaktif dari Room
    fun getAllKeramba() = kerambaDao.getAllKeramba()

    // Data reaktif untuk keramba yang terakhir dilihat
    fun getLatestSeenKeramba(limit: Int) = kerambaDao.getLatestSeenKeramba(limit)

    // Menambahkan fungsi untuk mengambil total ikan dari DAO
    fun getTotalIkan() = kerambaDao.getTotalIkan()

    suspend fun insert(keramba: KerambaEntity) {
        kerambaDao.insertKeramba(keramba)
    }

    suspend fun update(keramba: KerambaEntity) {
        kerambaDao.updateKeramba(keramba)
    }
    suspend fun getKerambaCount(): Int {
        return kerambaDao.getCount()
    }
    suspend fun getLastKeramba(): KerambaEntity? {
        return kerambaDao.getLastKeramba()
    }

    suspend fun deleteById(id: Int) {
        kerambaDao.deleteById(id)
    }

    suspend fun getKerambaById(id: Int): KerambaEntity? {
        return kerambaDao.getKerambaById2(id)
    }


}
