package com.example.simperades.ui.home.keuangan.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simperades.ui.home.keuangan.ViewModel.KeuanganViewModel
import com.example.simperades.utils.formatRupiah
import com.example.simperades.utils.formatTanggal

private val PrimaryBlue = Color(0xFF0077B6)
private val SuccessGreen = Color(0xFF4CAF50)
private val DangerRed = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailKeuanganScreen(
    navController: NavController,
    id: Int,
    vm: KeuanganViewModel = viewModel()
) {

    val keuangan by vm.getKeuanganById(id).collectAsState(initial = null)

    keuangan?.let { data ->

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail Transaksi", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryBlue
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F4F8))
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                DetailItem("Jenis", data.jenis)
                DetailItem("Kategori", data.kategori)
                DetailItem("Tanggal", formatTanggal(data.tanggal))

                Divider()

                DetailItem(
                    "Jumlah",
                    formatRupiah(data.jumlah),
                    highlightColor = if (data.jenis == "PEMASUKAN")
                        SuccessGreen else DangerRed
                )

                // Jika Penjualan Ikan tampilkan detail tambahan
                if (data.kategori == "Penjualan Ikan") {
                    data.jumlahKg?.let {
                        DetailItem("Jumlah Ikan (Kg)", "$it Kg")
                    }
                    data.hargaPerKg?.let {
                        DetailItem("Harga per Kg", formatRupiah(it))
                    }
                }

                if (data.keterangan.isNotBlank()) {
                    Divider()
                    DetailItem("Keterangan", data.keterangan)
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    highlightColor: Color? = null
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = highlightColor ?: Color.Black
        )
    }
}
