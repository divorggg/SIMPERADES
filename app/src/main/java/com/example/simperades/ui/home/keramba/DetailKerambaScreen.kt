package com.example.simperades.ui.home.keramba

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pets // GANTI IKON FISH DENGAN INI
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simperades.data.model.RiwayatKematian
import com.example.simperades.data.model.RiwayatPenambahan
import com.example.simperades.ui.home.viewmodel.HomeViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailKerambaScreen(
    navController: NavController,
    kerambaId: Int,
    vm: HomeViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val kerambaList by vm.kerambaList.collectAsState()

    LaunchedEffect(kerambaId) { vm.markAsAccessed(kerambaId) }

    val kerambaEntity = kerambaList.find { it.id == kerambaId }

    val riwayatList = remember(kerambaEntity) {
        if (kerambaEntity == null) emptyList()
        else {
            val gson = Gson()
            try {
                val listType = object : TypeToken<List<RiwayatKematian>>() {}.type
                gson.fromJson<List<RiwayatKematian>>(kerambaEntity.riwayatJson, listType)
                    ?.sortedByDescending { it.timestamp } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showMatiDialog by remember { mutableStateOf(false) }
    var inputJumlah by remember { mutableStateOf("") }
    var inputMati by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar( // 🔧 GANTI SmallTopAppBar -> TopAppBar
                title = { Text("Detail Keramba") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        if (kerambaEntity == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Keramba tidak ditemukan", style = MaterialTheme.typography.titleMedium)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header Keramba
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Pets, // 🐟 Ganti Fish dengan Pets
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = kerambaEntity.nama,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("ID Keramba: ${kerambaEntity.id}", color = Color.Gray)
                    }
                }
            }

            item {
                // Jumlah Ikan
                SectionCard(title = "Jumlah Ikan") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${kerambaEntity.jumlahIkan} ekor",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Mati: ${kerambaEntity.jumlahMati} ekor",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            // === Riwayat Penambahan Ikan ===
            item {
                SectionCard(title = "Riwayat Penambahan Ikan") {
                    val tambahList = remember(kerambaEntity) {
                        val gson = Gson()
                        try {
                            val listType = object : TypeToken<List<RiwayatPenambahan>>() {}.type
                            gson.fromJson<List<RiwayatPenambahan>>(kerambaEntity.riwayatTambahJson, listType)
                                ?.sortedByDescending { it.timestamp } ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }

                    if (tambahList.isEmpty()) {
                        Text(
                            "Belum ada riwayat penambahan ikan.",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        tambahList.take(5).forEach { entry ->
                            RiwayatPenambahanItem(entry) { selected ->
                                vm.deleteRiwayatPenambahan(kerambaEntity.id, selected)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Riwayat penambahan ikan berhasil dihapus")
                                }
                            }
                        }

                        if (tambahList.size > 5) {
                            Text(
                                "Menampilkan 5 penambahan terbaru...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tambah Ikan")
                    }
                }
            }



            item {
                // Catat Kematian
                SectionCard(title = "Catat Kematian Baru") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (kerambaEntity.jumlahIkan <= 0) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Tidak bisa mencatat kematian karena tidak ada ikan.")
                                    }
                                } else {
                                    vm.recordDeath(kerambaEntity.id, 1)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Berhasil mencatat 1 ikan mati")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+1 Mati")
                        }


                        Button(
                            onClick = {
                                if (kerambaEntity.jumlahIkan <= 0) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Tidak bisa mencatat kematian karena tidak ada ikan.")
                                    }
                                } else {
                                    vm.recordDeath(kerambaEntity.id, 5)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Berhasil mencatat 5 ikan mati")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+5 Mati")
                        }


                        OutlinedButton(
                            onClick = {
                                if (kerambaEntity.jumlahIkan <= 0) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Tidak bisa mencatat kematian karena tidak ada ikan.")
                                    }
                                } else {
                                    showMatiDialog = true
                                    inputMati = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Custom")
                        }

                    }
                }
            }

            item {
                // Riwayat
                SectionCard(
                    title = "Riwayat Kematian (${riwayatList.size})",
                    icon = Icons.Filled.History
                ) {
                    if (riwayatList.isEmpty()) {
                        Text(
                            "Belum ada riwayat kematian yang dicatat.",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        riwayatList.take(5).forEach { entry ->
                            RiwayatItem(entry) { selected ->
                                vm.deleteRiwayatKematian(kerambaEntity.id, selected)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Riwayat kematian berhasil dihapus")
                                }
                            }
                        }

                        if (riwayatList.size > 5) {
                            Text(
                                "Menampilkan 5 catatan terbaru...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
            // 🔥 Tombol Hapus Keramba
            item {
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus Keramba", color = Color.White)
                }
            }
        }

        // === Dialogs ===
        if (showEditDialog) {
            InputDialog(
                title = "Edit Jumlah Ikan",
                label = "Jumlah ikan baru",
                initialValue = inputJumlah,
                onValueChange = { inputJumlah = it },
                onDismiss = { showEditDialog = false },
                onConfirm = {
                    val jumlah = inputJumlah.toIntOrNull()
                    if (jumlah != null && jumlah > 0) {
                        vm.recordAddition(kerambaEntity.id, jumlah)
                        showEditDialog = false
                    }
                }

            )
        }

        if (showMatiDialog) {
            InputDialog(
                title = "Masukkan Jumlah Ikan Mati",
                label = "Jumlah ikan mati (> 0)",
                initialValue = inputMati,
                onValueChange = { inputMati = it },
                onDismiss = { showMatiDialog = false },
                onConfirm = {
                    val jumlah = inputMati.toIntOrNull()
                    if (jumlah != null && jumlah > 0) {
                        if (kerambaEntity.jumlahIkan <= 0) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Tidak bisa mencatat kematian karena tidak ada ikan.")
                            }
                        } else if (jumlah > kerambaEntity.jumlahIkan) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Jumlah kematian melebihi jumlah ikan.")
                            }
                        } else {
                            vm.recordDeath(kerambaEntity.id, jumlah)
                            showMatiDialog = false
                        }
                    }

                }
            )
        }
        // 🗑️ Dialog Konfirmasi Hapus
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Keramba") },
                text = { Text("Apakah Anda yakin ingin menghapus keramba '${kerambaEntity.nama}'? Tindakan ini tidak bisa dibatalkan.") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            coroutineScope.launch {
                                vm.deleteKeramba(kerambaEntity.id)
                                snackbarHostState.showSnackbar("Keramba berhasil dihapus")
                                showDeleteDialog = false
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Hapus", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}


@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun RiwayatItem(
    entry: RiwayatKematian,
    onDelete: (RiwayatKematian) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { showDialog = true }, // ⬅ klik untuk menampilkan dialog
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(entry.tanggal, style = MaterialTheme.typography.bodyMedium)
            Text(
                "+${entry.jumlah} ekor",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Detail Riwayat Kematian") },
            text = {
                Column {
                    Text("📅 Tanggal: ${entry.tanggal}")
                    Text("💀 Jumlah: ${entry.jumlah} ekor")
                    Spacer(Modifier.height(12.dp))
                    Text("Apakah Anda ingin menghapus riwayat ini?", color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(entry)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus", color = Color.White)
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

@Composable
fun RiwayatPenambahanItem(
    entry: RiwayatPenambahan,
    onDelete: (RiwayatPenambahan) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { showDialog = true }, // klik untuk detail + hapus
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // hijau lembut
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(entry.tanggal, style = MaterialTheme.typography.bodyMedium)
            Text(
                "+${entry.jumlah} ekor",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Detail Riwayat Penambahan") },
            text = {
                Column {
                    Text("📅 Tanggal: ${entry.tanggal}")
                    Text("🐟 Jumlah: ${entry.jumlah} ekor")
                    Spacer(Modifier.height(12.dp))
                    Text("Apakah Anda ingin menghapus riwayat ini?", color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(entry)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus", color = Color.White)
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



@Composable
fun InputDialog(
    title: String,
    label: String,
    initialValue: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = initialValue,
                onValueChange = onValueChange,
                label = { Text(label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
