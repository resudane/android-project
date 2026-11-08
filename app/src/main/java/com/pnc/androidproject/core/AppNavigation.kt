// Solomon Chambers - 10/21/2026

package com.pnc.androidproject.core

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pnc.androidproject.features.products.presentation.ProductListScreen
import com.pnc.androidproject.features.products.presentation.composables.ProductDetails
import com.pnc.androidproject.features.orders.presentation.OrderHistoryScreen
import kotlinx.coroutines.launch

data class DrawerItem(val label: String, val route: String)

val drawerItems = listOf(
    DrawerItem("Home", "home"),
    DrawerItem("Products", "products"),
    DrawerItem("Orders", "orders")
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(drawerItems.find {it.route == currentRoute}?.label ?: "") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open menu")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") { HomeScreen(
                    onProductSelected = { productId ->
                        navController.navigate("product/$productId")
                    }

                ) }
                composable("products") {
                    ProductListScreen(
                        onProductSelected = { productId ->
                            navController.navigate("product/$productId")
                        }
                    )
                }
                composable("orders") {
                    OrderHistoryScreen()
                }
                composable(
                    route = "product/{productId}",
                    arguments = listOf(navArgument("productId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                    ProductDetails(productId = productId)
                }
            }
        }
    }
}