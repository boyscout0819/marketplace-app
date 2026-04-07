package com.marketplace.app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.marketplace.app.presentation.ui.components.ListingCard
import com.marketplace.app.presentation.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
            Text(
                text = "No favorite listings yet",
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
                items(favorites) { listing ->
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
