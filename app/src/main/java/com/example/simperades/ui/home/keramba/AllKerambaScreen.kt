package com.example.simperades.ui.home.keramba

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simperades.ui.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllKerambaScreen(
    navController: NavController,
    vm: HomeViewModel = viewModel()
) {
    val kerambaList by vm.kerambaList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // 🌿 warna background lembut biar nggak flat putih
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 🔹 Header jumlah keramba
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0077B6)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Jumlah Keramba: ${kerambaList.size}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Daftar keramba
            if (kerambaList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data keramba", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // supaya gak ketimpa FAB
                ) {
                    items(
                        items = kerambaList,
                        key = { keramba -> keramba.id } // ✅ tambahkan key unik berdasarkan id
                    ) { keramba ->
                        KerambaCard(
                            keramba = keramba,
                            onClick = { navController.navigate("detail/${keramba.id}") }
                        )
                    }

                }
            }
        }

        // 🔹 Floating Action Button (FAB)
        FloatingActionButton(
            onClick = { navController.navigate("addKeramba") },
            containerColor = Color(0xFF0077B6),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 12.dp)
        ) {
            Text("+", color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
    }
}
