package com.marketplace.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.app.domain.model.Listing
import com.marketplace.app.domain.usecase.GetListingDetailUseCase
import com.marketplace.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    private val getListingDetailUseCase: GetListingDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listingId: String = checkNotNull(savedStateHandle["listingId"])

    private val _uiState = MutableStateFlow<ListingDetailUiState>(ListingDetailUiState.Loading)
    val uiState: StateFlow<ListingDetailUiState> = _uiState.asStateFlow()

    init {
        loadListing()
    }

    private fun loadListing() {
        viewModelScope.launch {
            getListingDetailUseCase(listingId)
                .collect { listing ->
                    if (listing != null) {
                        _uiState.value = ListingDetailUiState.Success(listing)
                    } else {
                        _uiState.value = ListingDetailUiState.Error("Listing not found")
                    }
                }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is ListingDetailUiState.Success) {
            viewModelScope.launch {
                toggleFavoriteUseCase(currentState.listing.id, !currentState.listing.isFavorite)
            }
        }
    }
}

sealed class ListingDetailUiState {
    object Loading : ListingDetailUiState()
    data class Success(val listing: Listing) : ListingDetailUiState()
    data class Error(val message: String) : ListingDetailUiState()
}
