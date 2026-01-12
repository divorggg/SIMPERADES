package com.example.simperades.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simperades.R
import com.example.simperades.data.RiwayatKematianItem
import com.example.simperades.ui.home.keramba.KerambaCard
import com.example.simperades.ui.home.viewmodel.HomeViewModel

// 🎨 Palet warna global
private val PrimaryBlue = Color(0xFF0077B6)
private val BackgroundLight = Color(0xFFF0F4F8)
private val AccentTeal = Color(0xFFCAF0F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = viewModel()) {
    val kerambaList by vm.kerambaList.collectAsState()
    val totalPopulasiIkan by vm.totalIkan.collectAsState()
    val latestDeathUpdates by vm.latestRiwayatFlow.collectAsState()
    val latestSeenKeramba by vm.latestSeenKerambaFlow().collectAsState(initial = emptyList())


    // Hilangkan Scaffold, langsung pakai LazyColumn
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Selamat Datang!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Dasbor Monitoring Perikanan Desa Sungai Duren",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        item { RingkasanSection(kerambaList.count(), totalPopulasiIkan) }
        item { AksiCepatSection(navController) }

        item {
            SectionHeader("Keramba Terakhir Dilihat")
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(latestSeenKeramba.size) { index ->
                    val keramba = latestSeenKeramba[index]
                    KerambaCard(
                        keramba = keramba,
                        onClick = { navController.navigate("detail/${keramba.id}") }
                    )
                }
            }
        }



        item {
            SectionHeader("Update Kematian Terakhir")
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (latestDeathUpdates.isEmpty()) {
                        Text(
                            "Belum ada pencatatan kematian terbaru.",
                            color = Color.Gray
                        )
                    } else {
                        latestDeathUpdates.forEachIndexed { index, riwayat ->
                            RiwayatKematianItem(riwayat)
                            if (index < latestDeathUpdates.count() - 1) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// 🔹 Section Header reusable
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(AccentTeal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    )
}

// 🔹 Ringkasan Section
@Composable
fun RingkasanSection(totalKeramba: Int, totalPopulasiIkan: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoCard(
            title = "Total Keramba",
            value = totalKeramba.toString(),
            icon = Icons.Filled.Water,
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            title = "Total Populasi Ikan",
            value = totalPopulasiIkan.toString(),
            icon = Icons.Filled.BarChart,
            modifier = Modifier.weight(1f)
        )
    }
}

// 🔹 Info Card
@Composable
fun InfoCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 🔹 Aksi Cepat Section
@Composable
fun AksiCepatSection(navController: NavController) {
    Column {
        SectionHeader("Aksi Cepat")
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(80.dp)
        ) {
            item {
                AksiCepatButton(
                    text = "Tambah Keramba",
                    icon = Icons.Filled.Add
                ) { navController.navigate("addKeramba") }
            }

            item {
                AksiCepatButton(
                    text = "Keuangan",
                    icon = Icons.Filled.Money
                ) { navController.navigate("keuangan") }
            }
        }
    }
}



// 🔹 Aksi Cepat Button
@Composable
fun AksiCepatButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
    val PrimaryBlue = Color(0xFF0077B6)

    TopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_logo_tanpa_text),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMPERADES",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }
                Text(
                    text = "Sistem Monitoring Perikanan Desa Sungai Duren",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
    )
}



