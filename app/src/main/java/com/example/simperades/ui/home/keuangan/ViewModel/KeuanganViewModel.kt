package com.example.simperades.ui.home.keuangan.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simperades.DAO.AppDatabase
import com.example.simperades.ui.home.keuangan.dao.Keuangan
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KeuanganViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).keuanganDao()

    val allKeuangan: StateFlow<List<Keuangan>> = dao.getAllKeuangan()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalPemasukan: StateFlow<Double> = dao.getTotalPemasukan()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalPengeluaran: StateFlow<Double> = dao.getTotalPengeluaran()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val saldo: StateFlow<Double> = dao.getTotalPemasukan()
        .map { pemasukan ->
            val totalPemasukan = pemasukan ?: 0.0
            val totalPengeluaran = totalPengeluaran.value
            totalPemasukan - totalPengeluaran
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun insertKeuangan(keuangan: Keuangan) {
        viewModelScope.launch {
            dao.insert(keuangan)
        }
    }

    fun deleteKeuangan(keuangan: Keuangan) {
        viewModelScope.launch {
            dao.delete(keuangan)
        }
    }
}
