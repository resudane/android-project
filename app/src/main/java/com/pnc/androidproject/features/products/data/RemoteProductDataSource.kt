package com.pnc.androidproject.features.products.data

import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteArtistDataSource: ProductDataSource {
    @GET("Product")
    override suspend fun getProducts(): List<ProductDto>

    @GET("Product/{id}")
    override suspend fun getProduct(
        @Path("id") id: Int
    ): ProductDto

}