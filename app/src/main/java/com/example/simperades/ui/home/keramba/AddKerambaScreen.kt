package com.example.simperades.ui.home.keramba

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simperades.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

private val PrimaryBlue = Color(0xFF0077B6)
private val BackgroundLight = Color(0xFFF0F4F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKerambaScreen(navController: NavController, vm: HomeViewModel) {
    var nama by remember { mutableStateOf("") }
    var jumlahIkan by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Status validasi
    val isJumlahIkanValid = jumlahIkan.toIntOrNull() != null && jumlahIkan.toInt() > 0
    val isFormValid = nama.isNotBlank() && isJumlahIkanValid

    // Ambil nama keramba otomatis saat halaman dibuka
    LaunchedEffect(Unit) {
        vm.getNextKerambaName { nextName ->
            nama = nextName
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // TopBar Manual
        TopAppBar(
            title = { Text("Tambah Keramba", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, "Kembali", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
        )

        // Konten Utama
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input Nama Keramba (Otomatis)
            OutlinedTextField(
                value = nama,
                onValueChange = {},
                label = { Text("Nama Keramba") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color(0xFFF0F0F0),
                    disabledLabelColor = Color.Gray,
                    disabledBorderColor = Color.LightGray
                ),
                enabled = false
            )
            Text(
                text = "Nama keramba diisi otomatis berdasarkan urutan.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input Jumlah Ikan
            OutlinedTextField(
                value = jumlahIkan,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        jumlahIkan = input
                    }
                },
                label = { Text("Jumlah Ikan Awal") },
                placeholder = { Text("Masukkan jumlah ikan (misal: 1000)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = jumlahIkan.isNotBlank() && !isJumlahIkanValid,
                supportingText = {
                    if (jumlahIkan.isNotBlank() && !isJumlahIkanValid) {
                        Text(
                            text = "Jumlah ikan harus berupa angka dan lebih dari 0.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    scope.launch {
                        vm.addKeramba(
                            nama = nama,
                            lokasi = "-",
                            jumlahIkan = jumlahIkan.toInt(),
                            onSuccess = { navController.popBackStack() }
                        )
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("SIMPAN KERAMBA", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}