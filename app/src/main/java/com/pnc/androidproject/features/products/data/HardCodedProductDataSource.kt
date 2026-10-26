package com.pnc.androidproject.features.products.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HardCodedProductDataSource @Inject constructor(): ProductDataSource {
    override suspend fun getProducts(): List<ProductDto> {
        return listOf(
            ProductDto(
                productId = 1,
                name = "Product 1",
                productNumber = "12345",
                summary = "Product 1 summary",
                thumbnailPhoto = "https://example.com/product1.jpg",
                thumbnailPhotoFileName = "product1.jpg",
                warranty = "1 year",
                color = "Red",
                listPrice = 100.0,
                quantity = 10
            )
        )
    }

    override suspend fun getProduct(id: Int): ProductDto {
        return getProducts().first { it.productId == id }
    }
}