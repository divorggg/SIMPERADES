//package com.example.simperades.ui.home.keuangan.ViewModel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.simperades.ui.home.keuangan.dao.KeuanganDao
//
//class KeuanganViewModelFactory(
//    private val dao: KeuanganDao
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(KeuanganViewModel::class.java)) {
//            return KeuanganViewModel(dao) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
