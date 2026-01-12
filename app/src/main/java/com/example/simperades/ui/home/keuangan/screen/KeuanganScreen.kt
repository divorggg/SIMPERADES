package com.example.simperades.ui.home.keuangan.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simperades.ui.home.keuangan.dao.Keuangan
import com.example.simperades.ui.home.keuangan.ViewModel.KeuanganViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private val PrimaryBlue = Color(0xFF0077B6)
private val SuccessGreen = Color(0xFF4CAF50)
private val DangerRed = Color(0xFFE53935)
private val BackgroundLight = Color(0xFFF0F4F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeuanganScreen(
    navController: NavController,
    vm: KeuanganViewModel = viewModel()
) {
    val allKeuangan by vm.allKeuangan.collectAsState()
    val totalPemasukan by vm.totalPemasukan.collectAsState()
    val totalPengeluaran by vm.totalPengeluaran.collectAsState()
    val saldo = totalPemasukan - totalPengeluaran

    // TANPA SCAFFOLD - Langsung Column dengan TopBar manual
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TopBar Manual
            TopAppBar(
                title = { Text("Keuangan", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ringkasan Keuangan
                item {
                    RingkasanKeuanganCard(
                        totalPemasukan = totalPemasukan,
                        totalPengeluaran = totalPengeluaran,
                        saldo = saldo
                    )
                }

                // Header List
                item {
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                // List Keuangan
                if (allKeuangan.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada transaksi", color = Color.Gray)
                        }
                    }
                } else {
                    items(allKeuangan) { keuangan ->
                        KeuanganItem(
                            keuangan = keuangan,
                            onDelete = { vm.deleteKeuangan(keuangan) }
                        )
                    }
                }

                // Spacer untuk FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { navController.navigate("tambah_Keuangan") },
            containerColor = PrimaryBlue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "Tambah", tint = Color.White)
        }
    }
}

@Composable
fun RingkasanKeuanganCard(
    totalPemasukan: Double,
    totalPengeluaran: Double,
    saldo: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Ringkasan Keuangan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Saldo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Saldo", fontWeight = FontWeight.Bold)
                Text(
                    formatRupiah(saldo),
                    fontWeight = FontWeight.Bold,
                    color = if (saldo >= 0) SuccessGreen else DangerRed
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Pemasukan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pemasukan", color = Color.Gray)
                Text(formatRupiah(totalPemasukan), color = SuccessGreen)
            }

            // Pengeluaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pengeluaran", color = Color.Gray)
                Text(formatRupiah(totalPengeluaran), color = DangerRed)
            }
        }
    }
}

@Composable
fun KeuanganItem(keuangan: Keuangan, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    keuangan.kategori,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    keuangan.keterangan,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    formatTanggal(keuangan.tanggal),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatRupiah(keuangan.jumlah),
                    fontWeight = FontWeight.Bold,
                    color = if (keuangan.jenis == "PEMASUKAN") SuccessGreen else DangerRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Delete, "Hapus", tint = DangerRed)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Transaksi?") },
            text = { Text("Transaksi ini akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text("Hapus", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ")
}

fun formatTanggal(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}