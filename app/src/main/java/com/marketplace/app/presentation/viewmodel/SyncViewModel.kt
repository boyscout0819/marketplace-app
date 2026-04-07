package com.marketplace.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.app.data.repository.ListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val repository: ListingRepository
) : ViewModel() {
    private val _syncStatus = MutableStateFlow(SyncStatus(false))
    val syncStatus = _syncStatus.asStateFlow()

    fun sync() {
        viewModelScope.launch {
            repository.syncWithRemote()
        }
    }
}

data class SyncStatus(val hasPendingOperations: Boolean)
