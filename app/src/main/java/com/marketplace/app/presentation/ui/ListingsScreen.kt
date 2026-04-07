package com.marketplace.app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.marketplace.app.presentation.ui.components.ListingCard
import com.marketplace.app.presentation.viewmodel.ListingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(
    navController: NavController,
    viewModel: ListingsViewModel = hiltViewModel()
) {
    val listings by viewModel.listings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = { viewModel.refresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(onClick = { navController.navigate("create") }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Listing")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (isLoading && listings.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (listings.isEmpty()) {
                    Text(
                        text = "No listings found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listings) { listing ->
                            ListingCard(
                                listing = listing,
                                onClick = {
                                    navController.navigate("listing/${listing.id}")
                                },
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(listing.id, !listing.isFavorite)
                                },
                                onDeleteClick = {
                                    viewModel.deleteListing(listing.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
