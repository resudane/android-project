// Solomon Chambers - 10/21/2026

package com.pnc.androidproject.core

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pnc.androidproject.features.orders.prsentation.composables.OrdersList
import com.pnc.androidproject.features.orders.prsentation.viewmodels.OrdersViewModel
import com.pnc.androidproject.features.products.presentation.ProductListScreen
import com.pnc.androidproject.features.products.presentation.composables.ProductDetails
import com.pnc.androidproject.features.products.presentation.viewmodels.ProductDirectoryViewModel
import kotlinx.coroutines.launch

data class TabItem(val label: String, val route: String, val icon: ImageVector)

val tabItems = listOf(
    TabItem("Home", "home", Icons.Default.Home),
    TabItem("Products", "products", Icons.Default.ShoppingCart),
    TabItem("Orders", "orders", Icons.AutoMirrored.Filled.List)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    val showBottomBar = tabItems.any { it.route == currentRoute }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tabItems.find { it.route == currentRoute }?.label ?: "") },
                actions = {
                    if (currentRoute == "products") {
                        val navBackStackEntry = navController.currentBackStackEntry
                        if (navBackStackEntry != null) {
                            val viewModel: ProductDirectoryViewModel = hiltViewModel(navBackStackEntry)
                            IconButton(onClick = { viewModel.loadProducts(forceRefresh = true) }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh products")
                            }
                        }
                    }
                    if (currentRoute == "orders") {
                        val navBackStackEntry = navController.currentBackStackEntry
                        if (navBackStackEntry != null) {
                            val viewModel: OrdersViewModel = hiltViewModel(navBackStackEntry)
                            IconButton(onClick = { viewModel.loadOrders() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh orders")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(
                    onProductSelected = { productId ->
                        navController.navigate("product/$productId")
                    }
                )
            }
            composable("products") {
                ProductListScreen(
                    onProductSelected = { productId ->
                        navController.navigate("product/$productId")
                    }
                )
            }
            composable(
                route = "product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                ProductDetails(
                    navController = navController,
                    productId = productId
                )
            }
            composable("orders") {
                OrdersList(viewModel = hiltViewModel())
            }
        }
    }
}