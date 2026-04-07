package com.marketplace.app.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marketplace.app.presentation.ui.components.BottomNavigationBar
import com.marketplace.app.presentation.ui.components.MarketplaceTopBar
import com.marketplace.app.presentation.ui.theme.MarketplaceTheme
import com.marketplace.app.presentation.viewmodel.SyncViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarketplaceTheme {
                MarketplaceApp()
            }
        }
    }
}

@Composable
fun MarketplaceApp() {
    val navController = rememberNavController()
    val syncViewModel: SyncViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != "listing/{listingId}" && currentRoute != "create") {
                MarketplaceTopBar(syncViewModel.syncStatus)
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "listings",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("listings") { ListingsScreen(navController) }
            composable("favorites") { FavoritesScreen(navController) }
            composable("create") { CreateListingScreen(navController) }
            composable("listing/{listingId}") { backStackEntry ->
                ListingDetailScreen(
                    listingId = backStackEntry.arguments?.getString("listingId") ?: "",
                    navController = navController
                )
            }
        }
    }
}
