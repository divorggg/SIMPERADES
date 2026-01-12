package com.example.simperades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.simperades.ui.home.Header
import com.example.simperades.ui.home.HomeScreen
import com.example.simperades.ui.home.keramba.AddKerambaScreen
import com.example.simperades.ui.home.keramba.AllKerambaScreen
import com.example.simperades.ui.home.keramba.DetailKerambaScreen
import com.example.simperades.ui.home.keuangan.screen.KeuanganScreen
import com.example.simperades.ui.home.keuangan.screen.TambahKeuanganScreen
import com.example.simperades.ui.home.laporan.LaporanScreen
import com.example.simperades.ui.home.viewmodel.HomeViewModel
import com.example.simperades.ui.theme.SimperadesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimperadesTheme {
                MainScaffold()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    val navController = rememberNavController()

    // ViewModel hanya dibuat sekali di level tertinggi
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    val homeViewModel: HomeViewModel = viewModel(viewModelStoreOwner!!)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val PrimaryBlue = Color(0xFF0077B6)

    // State untuk selectedItem
    var selectedItem by rememberSaveable { mutableStateOf("home") }

    // Determine if it's a full screen (tanpa TopBar)
    val isFullScreen = remember(currentRoute) {
        currentRoute?.startsWith("detail/") == true ||
                currentRoute == "keuangan" ||
                currentRoute == "tambah_keuangan"
    }

    // Determine if bottom bar should show
    val showBottomBar = remember(currentRoute) {
        currentRoute in listOf("home", "allKeramba", "laporan")
    }

    // Sinkronkan selectedItem dengan currentRoute
    LaunchedEffect(currentRoute) {
        when {
            currentRoute == "home" -> selectedItem = "home"
            currentRoute == "allKeramba" -> selectedItem = "allKeramba"
            currentRoute == "laporan" -> selectedItem = "laporan"
        }
    }

    Scaffold(
        topBar = {
            // TopBar sekarang dihandle oleh masing-masing screen
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedItem == "home",
                        onClick = {
                            if (currentRoute != "home") {
                                selectedItem = "home"
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                        )
                    )

                    NavigationBarItem(
                        selected = selectedItem == "allKeramba",
                        onClick = {
                            if (currentRoute != "allKeramba") {
                                selectedItem = "allKeramba"
                                navController.navigate("allKeramba") {
                                    popUpTo("home")
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Filled.BarChart,
                                contentDescription = "Data"
                            )
                        },
                        label = { Text("Data") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                        )
                    )

                    NavigationBarItem(
                        selected = selectedItem == "laporan",
                        onClick = {
                            if (currentRoute != "laporan") {
                                selectedItem = "laporan"
                                navController.navigate("laporan") {
                                    popUpTo("home")
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Filled.PictureAsPdf,
                                contentDescription = "Laporan"
                            )
                        },
                        label = { Text("Laporan") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // HOME SCREEN
            composable(route = "home") {
                Column(modifier = Modifier.fillMaxSize()) {
                    Header()
                    HomeScreen(
                        navController = navController,
                        vm = homeViewModel
                    )
                }
            }

            // ALL KERAMBA SCREEN
            composable(route = "allKeramba") {
                Column(modifier = Modifier.fillMaxSize()) {
                    Header()
                    AllKerambaScreen(
                        navController = navController,
                        vm = homeViewModel
                    )
                }
            }

            // KEUANGAN SCREEN
            composable(route = "keuangan") {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    KeuanganScreen(navController)
                }
            }

            // TAMBAH KEUANGAN SCREEN
            composable(route = "tambah_keuangan") {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TambahKeuanganScreen(navController)
                }
            }

            // LAPORAN SCREEN
            composable(route = "laporan") {
                val context = LocalContext.current
                Column(modifier = Modifier.fillMaxSize()) {
                    Header()
                    LaporanScreen(
                        context = context,
                        viewModel = homeViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // ADD KERAMBA SCREEN
            composable(route = "addKeramba") {
                AddKerambaScreen(
                    navController = navController,
                    vm = homeViewModel
                )
            }

            // DETAIL KERAMBA SCREEN
            composable(
                route = "detail/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1

                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    DetailKerambaScreen(
                        navController = navController,
                        kerambaId = id,
                        vm = homeViewModel
                    )
                }
            }
        }
    }
}