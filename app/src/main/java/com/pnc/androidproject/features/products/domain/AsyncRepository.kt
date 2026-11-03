package com.pnc.androidproject.features.products.domain

import com.pnc.androidproject.features.products.data.HardCodedProductDataSource
import com.pnc.androidproject.features.products.data.ProductDataSource
import com.pnc.androidproject.features.products.data.ProductMapper

class AsyncRepository(
    private val dataSource: ProductDataSource = HardCodedProductDataSource()
) {
    suspend fun getProducts(): List<Product> {
        return dataSource.getProducts()
            .map { productDto ->
                ProductMapper.mapToDomain(productDto)
            }
    }
}