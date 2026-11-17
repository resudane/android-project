// Solomon Chambers - 10/21/2026

package com.pnc.androidproject.core

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.pnc.androidproject.features.profile.ProfileScreen
import kotlinx.coroutines.launch

data class TabItem(val label: String, val route: String, val icon: ImageVector)

val tabItems = listOf(
    TabItem("Home", "home", Icons.Default.Home),
    TabItem("Products", "products", Icons.Default.ShoppingCart),
    TabItem("Orders", "orders", Icons.AutoMirrored.Filled.List),
    TabItem("Profile", "profile", Icons.Default.AccountCircle)
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
            val title = when {
                currentRoute?.startsWith("product/") == true -> "Details"
                else -> tabItems.find { it.route == currentRoute }?.label ?: ""
            }
            TopAppBar(
                title = { Text (title)
//                    if (currentRoute == "home") {
//                        Text(
//                            text = "Hello, Olivia",
//                            style = MaterialTheme.typography.displaySmall.copy(
//                                fontWeight = FontWeight.Bold
//                            ),
//                        )
//                    } else if (currentRoute == "products"){
//                        Text("Products")
//                    }
//                    else {
//                        Text(
//                            tabItems.find { it.route == currentRoute }?.label ?: "",
//                            style = MaterialTheme.typography.displaySmall.copy(
//                                fontWeight = FontWeight.Bold
//                            ),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (currentRoute == "products") {
                        val navBackStackEntry = navController.currentBackStackEntry
                        if (navBackStackEntry != null) {
                            val viewModel: ProductDirectoryViewModel =
                                hiltViewModel(navBackStackEntry)
                            IconButton(onClick = { viewModel.loadProducts(forceRefresh = true) }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh products"
                                )
                            }
                        }
                    }
                    if (currentRoute == "orders") {
                        val navBackStackEntry = navController.currentBackStackEntry
                        if (navBackStackEntry != null) {
                            val viewModel: OrdersViewModel = hiltViewModel(navBackStackEntry)
                            IconButton(onClick = { viewModel.loadOrders() }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh orders"
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
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
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
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
            composable("profile"){
                ProfileScreen()
            }
        }
    }
}