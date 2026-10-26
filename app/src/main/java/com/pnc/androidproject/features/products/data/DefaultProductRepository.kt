package com.pnc.androidproject.features.products.data

import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.domain.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultProductRepository @Inject constructor(
    private val dataSource: ProductDataSource
): ProductRepository {

    override fun observeProducts(): Flow<List<Product>> = flow {
        emit( dataSource.getProducts().map {
            ProductMapper.mapToDomain(it)
        })
    }

    override fun observeProduct(id: Int): Flow<Product?> = flow {
        emit(ProductMapper.mapToDomain(dataSource.getProduct(id)))

    }

    override suspend fun refreshProducts() = Unit
    override suspend fun refreshProduct(id: Int) = Unit
}