package com.example.simperades.ui.home.keuangan.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.simperades.utils.formatRupiah
import com.example.simperades.utils.formatTanggal
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.io.OutputStream

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
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = { Text("Keuangan", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        exportPdf(context, allKeuangan, totalPemasukan, totalPengeluaran, saldo)
                    }) {
                        Icon(Icons.Filled.PictureAsPdf, "Export PDF", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    RingkasanKeuanganCard(
                        totalPemasukan = totalPemasukan,
                        totalPengeluaran = totalPengeluaran,
                        saldo = saldo
                    )
                }

                item {
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

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
                            onClick = {
                                navController.navigate("detail_keuangan/${keuangan.id}")
                            },
                            onDelete = {
                                vm.deleteKeuangan(keuangan)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pemasukan", color = Color.Gray)
                Text(formatRupiah(totalPemasukan), color = SuccessGreen)
            }

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
fun KeuanganItem(
    keuangan: Keuangan,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    color = if (keuangan.jenis == "PEMASUKAN")
                        SuccessGreen else DangerRed
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