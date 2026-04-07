package com.marketplace.app.data.remote.api

import com.google.gson.Gson
import com.marketplace.app.data.remote.model.ListingRequest
import com.marketplace.app.data.remote.model.ListingResponse
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.*

class MockApiInterceptor : Interceptor {
    private val listings = mutableMapOf<String, ListingResponse>()
    private val gson = Gson()

    init {
        // Add some mock data with stable IDs to prevent duplicates across app restarts
        val id1 = "mock-id-camera-001"
        listings[id1] = ListingResponse(
            id = id1,
            title = "Vintage Camera",
            description = "A classic 35mm film camera in great condition.",
            price = 120.0,
            category = "Electronics",
            imageUrls = listOf("https://images.unsplash.com/photo-1516035069371-29a1b244cc32"),
            createdAt = Date().toString(),
            updatedAt = Date().toString(),
            version = 1
        )
        val id2 = "mock-id-bike-002"
        listings[id2] = ListingResponse(
            id = id2,
            title = "Mountain Bike",
            description = "Durable mountain bike for all terrains.",
            price = 450.0,
            category = "Sports",
            imageUrls = listOf("https://images.unsplash.com/photo-1485965120184-e220f721d03e"),
            createdAt = Date().toString(),
            updatedAt = Date().toString(),
            version = 1
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        return when {
            request.method == "GET" && path == "/listings" -> {
                Response.Builder()
                    .code(200)
                    .message("OK")
                    .body(gson.toJson(listings.values.toList()).toResponseBody())
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .build()
            }
            request.method == "POST" && path == "/listings" -> {
                val body = request.body?.let {
                    // This is a simplified mock, normally you'd read the buffer
                    null as? ListingRequest
                }
                val newListing = ListingResponse(
                    id = UUID.randomUUID().toString(),
                    title = body?.title ?: "",
                    description = body?.description ?: "",
                    price = body?.price ?: 0.0,
                    category = body?.category ?: "",
                    imageUrls = body?.imageUrls ?: emptyList(),
                    createdAt = Date().toString(),
                    updatedAt = Date().toString(),
                    version = 1
                )
                listings[newListing.id] = newListing
                Response.Builder()
                    .code(201)
                    .message("Created")
                    .body(gson.toJson(newListing).toResponseBody())
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .build()
            }
            else -> {
                Response.Builder()
                    .code(404)
                    .message("Not Found")
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .build()
            }
        }
    }
}