package com.marketplace.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.app.domain.model.Listing
import com.marketplace.app.domain.usecase.DeleteListingUseCase
import com.marketplace.app.domain.usecase.GetFavoriteListingsUseCase
import com.marketplace.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteListingsUseCase: GetFavoriteListingsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteListingUseCase: DeleteListingUseCase
) : ViewModel() {

    val favorites: StateFlow<List<Listing>> = getFavoriteListingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(listingId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(listingId, isFavorite)
        }
    }

    fun deleteListing(listingId: String) {
        viewModelScope.launch {
            deleteListingUseCase(listingId)
        }
    }
}
