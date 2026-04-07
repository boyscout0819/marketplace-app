package com.marketplace.app.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import com.marketplace.app.data.remote.model.*
import okhttp3.MultipartBody

interface ApiService {
    @GET("listings")
    suspend fun getListings(): Response<List<ListingResponse>>

    @GET("listings/{id}")
    suspend fun getListing(@Path("id") id: String): Response<ListingResponse>

    @POST("listings")
    suspend fun createListing(@Body listing: ListingRequest): Response<ListingResponse>

    @PUT("listings/{id}")
    suspend fun updateListing(@Path("id") id: String, @Body listing: ListingRequest): Response<ListingResponse>

    @DELETE("listings/{id}")
    suspend fun deleteListing(@Path("id") id: String): Response<Unit>

    @Multipart
    @POST("images/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<ImageUploadResponse>
}
