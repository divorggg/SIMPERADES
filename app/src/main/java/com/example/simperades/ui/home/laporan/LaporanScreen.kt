package com.example.simperades.ui.home.laporan

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.simperades.ui.home.viewmodel.HomeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    context: Context,
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    var showMonthlyDialog by remember { mutableStateOf(false) }
    var showDailyDialog by remember { mutableStateOf(false) }
    var exportedMonthlyFile by remember { mutableStateOf<File?>(null) }
    var exportedDailyFile by remember { mutableStateOf<File?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Laporan Keramba",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ekspor laporan aktivitas keramba untuk kebutuhan bulanan atau harian.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Tombol Bulanan
        Button(
            onClick = {
                val file = exportLaporanPDF(context, viewModel)
                exportedMonthlyFile = file
                showMonthlyDialog = true
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Export PDF Laporan Bulanan",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol Harian
        Button(
            onClick = {
                val file = exportLaporanHarianPDF(context, viewModel)
                exportedDailyFile = file
                showDailyDialog = true
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,

        ) {
            Text(
                text = "Export PDF Laporan Harian",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol Harian By Date (dengan error handling)
//        Button(
//            onClick = {
//                try {
//                    val calendar = Calendar.getInstance()
//                    DatePickerDialog(
//                        context,
//                        { _, year, month, dayOfMonth ->
//                            try {
//                                val selectedDate = Calendar.getInstance().apply {
//                                    set(year, month, dayOfMonth)
//                                }
//                                // Format tanggal sesuai data laporan: "dd MMM yyyy"
//                                val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id"))
//                                val dateString = formatter.format(selectedDate.time)
//
//                                // Jalankan di coroutine untuk menghindari blocking UI
//                                val file = exportLaporanHarianByDate(context, viewModel, dateString)
//                                exportedDailyFile = file
//                                if (file != null) {
//                                    showDailyDialog = true
//                                }
//                            } catch (e: Exception) {
//                                android.util.Log.e("LaporanScreen", "Error on date selection: ${e.message}", e)
//                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                            }
//                        },
//                        calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH)
//                    ).show()
//                } catch (e: Exception) {
//                    android.util.Log.e("LaporanScreen", "Error showing date picker: ${e.message}", e)
//                    Toast.makeText(context, "Error membuka kalender", Toast.LENGTH_SHORT).show()
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth(0.8f)
//                .height(50.dp),
//            shape = MaterialTheme.shapes.medium,
//            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
//        ) {
//            Text(
//                text = "Export PDF Harian By Date",
//                style = MaterialTheme.typography.labelLarge,
//                color = MaterialTheme.colorScheme.onTertiary
//            )
//        }
    }

    // Dialog Bulanan
    if (showMonthlyDialog && exportedMonthlyFile != null) {
        AlertDialog(
            onDismissRequest = { showMonthlyDialog = false },
            title = { Text("Laporan Bulanan Berhasil Diekspor") },
            text = { Text("Apakah Anda ingin membuka file laporan sekarang?") },
            confirmButton = {
                TextButton(onClick = {
                    openPdfFile(context, exportedMonthlyFile!!)
                    showMonthlyDialog = false
                }) { Text("Buka File") }
            },
            dismissButton = {
                TextButton(onClick = { showMonthlyDialog = false }) { Text("Tutup") }
            }
        )
    }

    // Dialog Harian
    if (showDailyDialog && exportedDailyFile != null) {
        AlertDialog(
            onDismissRequest = { showDailyDialog = false },
            title = { Text("Laporan Harian Berhasil Diekspor") },
            text = { Text("Apakah Anda ingin membuka file laporan sekarang?") },
            confirmButton = {
                TextButton(onClick = {
                    openPdfFile(context, exportedDailyFile!!)
                    showDailyDialog = false
                }) { Text("Buka File") }
            },
            dismissButton = {
                TextButton(onClick = { showDailyDialog = false }) { Text("Tutup") }
            }
        )
    }
}

private fun openPdfFile(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Buka file dengan..."))
}
