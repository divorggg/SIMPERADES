package com.example.simperades.ui.home.keuangan.screen

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simperades.ui.home.keuangan.ViewModel.KeuanganViewModel
import com.example.simperades.ui.home.keuangan.dao.Keuangan

private val PrimaryBlue = Color(0xFF0077B6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahKeuanganScreen(
    navController: NavController,
    vm: KeuanganViewModel = viewModel()
) {

    var jenis by remember { mutableStateOf("PEMASUKAN") }
    var kategori by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var jumlahKg by remember { mutableStateOf("") }
    var hargaPerKg by remember { mutableStateOf("") }

    val kategoriPemasukan = listOf("Penjualan Ikan", "Investasi", "Lainnya")
    val kategoriPengeluaran = listOf("Pakan", "Bibit", "Perawatan", "Operasional", "Lainnya")

    val isPenjualan = jenis == "PEMASUKAN" && kategori == "Penjualan Ikan"

    val totalOtomatis by remember(jumlahKg, hargaPerKg) {
        derivedStateOf {
            (jumlahKg.toDoubleOrNull() ?: 0.0) *
                    (hargaPerKg.toDoubleOrNull() ?: 0.0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Transaksi", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F4F8))
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ==============================
            // JENIS TRANSAKSI
            // ==============================
            Text("Jenis Transaksi", style = MaterialTheme.typography.titleSmall)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = jenis == "PEMASUKAN",
                    onClick = {
                        jenis = "PEMASUKAN"
                        kategori = ""
                    },
                    label = { Text("Pemasukan") }
                )

                FilterChip(
                    selected = jenis == "PENGELUARAN",
                    onClick = {
                        jenis = "PENGELUARAN"
                        kategori = ""
                    },
                    label = { Text("Pengeluaran") }
                )
            }

            // ==============================
            // KATEGORI
            // ==============================
            Text("Kategori", style = MaterialTheme.typography.titleSmall)

            val daftarKategori =
                if (jenis == "PEMASUKAN") kategoriPemasukan
                else kategoriPengeluaran

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = kategori,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Kategori") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    daftarKategori.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                kategori = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            // ==============================
            // KHUSUS PENJUALAN IKAN
            // ==============================
            if (isPenjualan) {

                OutlinedTextField(
                    value = jumlahKg,
                    onValueChange = { jumlahKg = it },
                    label = { Text("Jumlah Ikan (Kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hargaPerKg,
                    onValueChange = { hargaPerKg = it },
                    label = { Text("Harga per Kg (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Total Otomatis: Rp ${"%,.0f".format(totalOtomatis)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2E7D32)
                )
            }

            // ==============================
            // JUMLAH MANUAL (NON PENJUALAN)
            // ==============================
            if (!isPenjualan) {
                OutlinedTextField(
                    value = jumlah,
                    onValueChange = { jumlah = it },
                    label = { Text("Jumlah (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ==============================
            // KETERANGAN
            // ==============================
            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = { Text("Keterangan (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // ==============================
            // BUTTON SIMPAN
            // ==============================
            Button(
                onClick = {

                    val total = if (isPenjualan) {
                        totalOtomatis
                    } else {
                        jumlah.toDoubleOrNull() ?: 0.0
                    }

                    val isValid = if (isPenjualan) {
                        jumlahKg.isNotBlank() && hargaPerKg.isNotBlank()
                    } else {
                        jumlah.isNotBlank()
                    }

                    if (kategori.isNotBlank() && isValid) {

                        val keuangan = Keuangan(
                            jenis = jenis,
                            kategori = kategori,
                            jumlah = total,
                            jumlahKg = if (isPenjualan) jumlahKg.toDoubleOrNull() else null,
                            hargaPerKg = if (isPenjualan) hargaPerKg.toDoubleOrNull() else null,
                            keterangan = keterangan
                        )

                        vm.insertKeuangan(keuangan)
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Simpan Transaksi", color = Color.White)
            }
        }
    }
}