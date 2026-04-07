package com.marketplace.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.app.domain.model.Listing
import com.marketplace.app.domain.usecase.DeleteListingUseCase
import com.marketplace.app.domain.usecase.GetListingsUseCase
import com.marketplace.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingsViewModel @Inject constructor(
    private val getListingsUseCase: GetListingsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteListingUseCase: DeleteListingUseCase
) : ViewModel() {

    val listings: StateFlow<List<Listing>> = getListingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isLoading = MutableStateFlow(false)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            getListingsUseCase.repository.fetchAndSaveListings()
            isLoading.value = false
        }
    }

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
