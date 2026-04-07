package com.marketplace.app.domain.model

import android.net.Uri

data class ImageData(
    val url: String,
    val thumbnailUrl: String,
    val uri: Uri
)
