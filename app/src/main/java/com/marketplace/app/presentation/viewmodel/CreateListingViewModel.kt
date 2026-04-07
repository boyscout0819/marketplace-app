package com.marketplace.app.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.app.domain.model.ImageData
import com.marketplace.app.domain.model.Listing
import com.marketplace.app.domain.usecase.CreateListingUseCase
import com.marketplace.app.utils.ImageProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val createListingUseCase: CreateListingUseCase,
    private val imageProcessor: ImageProcessor
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")
    val images = mutableStateListOf<ImageData>()

    val isValid: Boolean
        get() = title.isNotBlank() &&
                description.isNotBlank() &&
                price.toDoubleOrNull() != null &&
                images.isNotEmpty()

    fun addImage(uri: Uri) {
        viewModelScope.launch {
            val imageData = imageProcessor.processImage(uri)
            images.add(imageData)
        }
    }

    fun addImageFromCamera(bitmap: Bitmap) {
        viewModelScope.launch {
            val imageData = imageProcessor.processBitmap(bitmap)
            images.add(imageData)
        }
    }

    private val _creationSuccess = kotlinx.coroutines.flow.MutableSharedFlow<Unit>()
    val creationSuccess = _creationSuccess.asSharedFlow()

    fun createListing() {
        viewModelScope.launch {
            val listing = Listing(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                price = price.toDouble(),
                category = "General",
                imageUrls = images.map { it.url },
                createdAt = Date(),
                updatedAt = Date()
            )

            val result = createListingUseCase(listing, images)
            if (result.isSuccess) {
                _creationSuccess.emit(Unit)
            }
        }
    }
}
