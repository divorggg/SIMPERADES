package com.example.simperades.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simperades.DAO.KerambaEntity
import com.example.simperades.DAO.KerambaRepository
import com.example.simperades.data.model.DailyDeathReport
import com.example.simperades.data.model.Keramba
import com.example.simperades.data.model.RiwayatKematian
import com.example.simperades.data.model.RiwayatPenambahan
import com.example.simperades.SimperadesApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Keramba.toEntity(): KerambaEntity {
    return KerambaEntity(
        id = this.id,
        nama = this.nama,
        lokasi = this.lokasi,
        jumlahIkan = this.JumlahIkan,
        jumlahMati = this.jumlahMati,
        lastAccessed = System.currentTimeMillis(),
        riwayatJson = "[]"
    )
}

class HomeViewModel(
    private val repository: KerambaRepository = KerambaRepository(SimperadesApp.kerambaDao)
) : ViewModel() {

    private val gson = Gson()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val kerambaList: StateFlow<List<KerambaEntity>> =
        repository.getAllKeramba().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalIkan: StateFlow<Int> =
        repository.getTotalIkan()
            .map { it ?: 0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            val existingCount = repository.getKerambaCount()
            if (existingCount == 0) initializeDummyData()
        }
    }

    private fun initializeDummyData() {
        val dummyList = (1..20).map { i ->
            Keramba(
                id = i,
                nama = "Keramba $i",
                lokasi = "Lokasi $i",
                JumlahIkan = (0),
                jumlahMati = (0)
            )
        }

        viewModelScope.launch {
            dummyList.forEach { repository.insert(it.toEntity()) }
        }
    }

    fun addKeramba(
        nama: String,
        lokasi: String,
        jumlahIkan: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newKeramba = KerambaEntity(
                    nama = nama,
                    lokasi = lokasi,
                    jumlahIkan = jumlahIkan,
                    jumlahMati = 0,
                    lastAccessed = System.currentTimeMillis()
                )
                repository.insert(newKeramba)
                onSuccess()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Updated: Record death automatically based on current time (pagi/siang/sore/malam)
    fun recordDeath(id: Int, jumlah: Int) {
        viewModelScope.launch {
            val kerambaLama = kerambaList.value.find { it.id == id }
            if (kerambaLama != null) {
                val currentRiwayat = try {
                    gson.fromJson(
                        kerambaLama.riwayatJson,
                        Array<RiwayatKematian>::class.java
                    ).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }

                val waktu = getWaktuSekarang()
                currentRiwayat.add(
                    RiwayatKematian(
                        kerambaNama = kerambaLama.nama,
                        tanggal = getCurrentDate(),
                        jumlah = jumlah,
                        waktu = waktu,
                        timestamp = System.currentTimeMillis()
                    )
                )

                val updated = kerambaLama.copy(
                    jumlahIkan = (kerambaLama.jumlahIkan - jumlah).coerceAtLeast(0),
                    jumlahMati = kerambaLama.jumlahMati + jumlah,
                    riwayatJson = gson.toJson(currentRiwayat),
                    lastAccessed = System.currentTimeMillis()
                )
                repository.update(updated)
            }
        }
    }

    // 🔹 Detect waktu otomatis: Pagi, Siang, Sore, Malam
    private fun getWaktuSekarang(): String {
        val now = LocalTime.now()
        return when (now.hour) {
            in 5..11 -> "pagi"
            in 12..14 -> "siang"
            in 15..17 -> "sore"
            else -> "malam"
        }
    }

    fun markAsAccessed(id: Int) {
        viewModelScope.launch {
            kerambaList.value.find { it.id == id }?.let { old ->
                repository.update(old.copy(lastAccessed = System.currentTimeMillis()))
            }
        }
    }

    fun getNextKerambaName(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val lastKeramba = repository.getLastKeramba()
            val nextNumber = if (lastKeramba != null) {
                val number = lastKeramba.nama.filter { it.isDigit() }.toIntOrNull() ?: 0
                number + 1
            } else 1
            onResult("Keramba $nextNumber")
        }
    }

    fun updateJumlahIkan(id: Int, newJumlah: Int) {
        viewModelScope.launch {
            val keramba = repository.getAllKeramba().first().find { it.id == id }
            keramba?.let {
                repository.update(it.copy(jumlahIkan = newJumlah))
            }
        }
    }

    fun latestSeenKerambaFlow(limit: Int = 5) = repository.getLatestSeenKeramba(limit)

    val latestRiwayatFlow = kerambaList
        .map { list ->
            list.flatMap { keramba ->
                try {
                    gson.fromJson(
                        keramba.riwayatJson,
                        Array<RiwayatKematian>::class.java
                    ).toList()
                } catch (e: Exception) {
                    emptyList()
                }
            }.sortedByDescending { it.timestamp }.take(5)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun getCurrentDate(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
        return now.format(formatter)
    }

    // ✅ Updated: Add 'malam' to the daily report too
    fun getDailyDeathReport(): List<DailyDeathReport> {
        val allRiwayat = kerambaList.value.flatMap { keramba ->
            try {
                gson.fromJson(
                    keramba.riwayatJson,
                    Array<RiwayatKematian>::class.java
                ).toList()
            } catch (e: Exception) {
                emptyList()
            }
        }

        val groupedByDate = allRiwayat.groupBy { it.tanggal.substring(0, 10) }

        return groupedByDate.map { (tanggal, riwayat) ->
            DailyDeathReport(
                tanggal = tanggal,
                pagi = riwayat.filter { it.waktu == "pagi" }.sumOf { it.jumlah },
                siang = riwayat.filter { it.waktu == "siang" }.sumOf { it.jumlah },
                sore = riwayat.filter { it.waktu == "sore" }.sumOf { it.jumlah },
                malam = riwayat.filter { it.waktu == "malam" }.sumOf { it.jumlah }
            )
        }
    }

    fun deleteKeramba(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ FUNGSI YANG DIPERBAIKI - Ganti fungsi yang lama dengan ini

    // 🔹 Hapus Riwayat Kematian + Update jumlahMati dan jumlahIkan
    fun deleteRiwayatKematian(kerambaId: Int, entry: RiwayatKematian) {
        viewModelScope.launch {
            val keramba = repository.getKerambaById(kerambaId)
            if (keramba != null) {
                val gson = Gson()
                val listType = object : TypeToken<List<RiwayatKematian>>() {}.type
                val currentList = gson.fromJson<List<RiwayatKematian>>(keramba.riwayatJson, listType)?.toMutableList() ?: mutableListOf()

                // Hapus riwayat yang diklik
                currentList.remove(entry)

                val updatedJson = gson.toJson(currentList)

                // ✅ PERBAIKAN: Update jumlahMati dan jumlahIkan
                val updatedKeramba = keramba.copy(
                    riwayatJson = updatedJson,
                    jumlahMati = (keramba.jumlahMati - entry.jumlah).coerceAtLeast(0), // ⬅️ Kurangi jumlah mati
                    jumlahIkan = keramba.jumlahIkan + entry.jumlah // ⬅️ Kembalikan ikan yang "mati" ke total
                )

                repository.update(updatedKeramba)
            }
        }
    }

    // 🔹 Hapus Riwayat Penambahan + Update jumlahIkan
    fun deleteRiwayatPenambahan(kerambaId: Int, entry: RiwayatPenambahan) {
        viewModelScope.launch {
            val keramba = repository.getKerambaById(kerambaId)
            if (keramba != null) {
                val gson = Gson()
                val listType = object : TypeToken<List<RiwayatPenambahan>>() {}.type
                val currentList = gson.fromJson<List<RiwayatPenambahan>>(keramba.riwayatTambahJson, listType)?.toMutableList() ?: mutableListOf()

                // Hapus entri penambahan ikan yang diklik
                currentList.remove(entry)

                val updatedJson = gson.toJson(currentList)

                // ✅ PERBAIKAN: Kurangi jumlahIkan sesuai yang dihapus
                val updatedKeramba = keramba.copy(
                    riwayatTambahJson = updatedJson,
                    jumlahIkan = (keramba.jumlahIkan - entry.jumlah).coerceAtLeast(0) // ⬅️ Kurangi total ikan
                )

                repository.update(updatedKeramba)
            }
        }
    }


    fun recordAddition(kerambaId: Int, jumlah: Int) {
        viewModelScope.launch {
            val keramba = kerambaList.value.find { it.id == kerambaId } // ✅ ambil dari flow reaktif
            if (keramba != null) {
                val gson = Gson()
                val listType = object : TypeToken<List<RiwayatPenambahan>>() {}.type
                val currentRiwayat: MutableList<RiwayatPenambahan> =
                    try {
                        gson.fromJson<List<RiwayatPenambahan>>(keramba.riwayatTambahJson, listType)?.toMutableList()
                            ?: mutableListOf()
                    } catch (e: Exception) {
                        mutableListOf()
                    }

                val newRiwayat = RiwayatPenambahan(
                    tanggal = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("id"))
                        .format(System.currentTimeMillis()),
                    jumlah = jumlah
                )

                currentRiwayat.add(newRiwayat)

                val updatedKeramba = keramba.copy(
                    jumlahIkan = keramba.jumlahIkan + jumlah,
                    riwayatTambahJson = gson.toJson(currentRiwayat),
                    lastAccessed = System.currentTimeMillis()
                )

                repository.update(updatedKeramba) // ✅ langsung update via repository
            }
        }
    }

}
