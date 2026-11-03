package com.pnc.androidproject.features.products.data

import com.pnc.androidproject.features.products.data.local.ProductDao
import com.pnc.androidproject.features.products.data.local.ProductEntityMapper
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.domain.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val remote: ProductDataSource
): ProductRepository {
    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeProducts().map { list ->
            list.map{ entity ->
                ProductEntityMapper.mapToDomain(entity)
            }
        }

    override fun observeProduct(id: Int): Flow<Product?> =
        productDao.observeProduct(id).map { entity ->
            entity?.let { ProductEntityMapper.mapToDomain(it) }
        }

    override suspend fun refreshProducts() {
        val entities = remote.getProducts().map {
            ProductEntityMapper.mapToEntity(it)
        }
        productDao.replaceAll(entities)
    }

    override suspend fun refreshProduct(id: Int) {
        val entity = ProductEntityMapper.mapToEntity(remote.getProduct(id))
        productDao.upsertProduct(listOf(entity))
    }
}