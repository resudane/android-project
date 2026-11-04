package com.pnc.androidproject.features.products.data

import android.util.Log
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
    private val remote: ProductDataSource,
    private val fallbackSource: HardCodedProductDataSource
): ProductRepository {
    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeProducts().map { list ->
            if (list.isEmpty()) {
                fallbackSource.getProducts().map {
                    ProductEntityMapper.mapToDomain(ProductEntityMapper.mapToEntity(it))
                }
            } else {
                list.map { entity ->
                    ProductEntityMapper.mapToDomain(entity)
                }
            }
        }

    override fun observeProduct(id: Int): Flow<Product?> =
        productDao.observeProduct(id).map { entity ->
            entity?.let { ProductEntityMapper.mapToDomain(it) }
        }

    override suspend fun refreshProducts() {
        val dtos = try {
            remote.getProducts()
        } catch (e: Exception) {
            fallbackSource.getProducts()
        }
        val entities = dtos.map {
            ProductEntityMapper.mapToEntity(it)
        }
        productDao.replaceAll(entities)
    }

    override suspend fun refreshProduct(id: Int) {
        val dto = try {
            remote.getProduct(id)
        } catch (e: Exception) {
            fallbackSource.getProduct(id)
        }
        val entity = ProductEntityMapper.mapToEntity(dto)
        productDao.upsertProduct(listOf(entity))
    }
}