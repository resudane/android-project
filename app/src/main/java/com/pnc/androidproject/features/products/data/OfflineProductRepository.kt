package com.pnc.androidproject.features.products.data

import android.util.Log
import com.pnc.androidproject.features.products.data.local.ProductDao
import com.pnc.androidproject.features.products.data.local.ProductEntity
import com.pnc.androidproject.features.products.data.local.ProductEntityMapper
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.domain.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val remote: ProductDataSource,
    private val fallbackSource: HardCodedProductDataSource
) : ProductRepository {
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

    private fun mergeEntities(
        newEntity: ProductEntity,
        existingEntity: ProductEntity?
    ): ProductEntity {
        if (existingEntity == null) return newEntity
        return newEntity.copy(
            name = if (newEntity.name.isNullOrBlank()) existingEntity.name
                else newEntity.name,
            productNumber = if (newEntity.productNumber.isNullOrBlank()) existingEntity.productNumber
                else newEntity.productNumber,
            summary = if (newEntity.summary.isNullOrBlank()) existingEntity.summary
                else newEntity.summary,
            thumbnailPhoto = if (newEntity.thumbnailPhoto.isNullOrBlank()) existingEntity.thumbnailPhoto
                else newEntity.thumbnailPhoto,
            thumbnailPhotoFileName = if (newEntity.thumbnailPhotoFileName.isNullOrBlank()) existingEntity.thumbnailPhotoFileName
                else newEntity.thumbnailPhotoFileName,
            warranty = if (newEntity.warranty.isNullOrBlank()) existingEntity.warranty
                else newEntity.warranty,
            color = if (newEntity.color.isNullOrBlank()) existingEntity.color
                else newEntity.color,
            listPrice = if (newEntity.listPrice == null || newEntity.listPrice == 0.0) existingEntity.listPrice
                else newEntity.listPrice,
            stockLevel = if (newEntity.stockLevel == null || newEntity.stockLevel == 0) existingEntity.stockLevel
                else newEntity.stockLevel
        )
    }

    override suspend fun refreshProducts() {
        try {
            val dtos = remote.getProducts()
            val existingEntities = productDao
                .observeProducts()
                .first()
                .associateBy { it.productId }
            val entities = dtos.map { dto ->
                val entity = ProductEntityMapper.mapToEntity(dto)
                val existing = existingEntities[entity.productId]
                mergeEntities(entity, existing)
            }

            productDao.replaceAll(entities)
        } catch (e: Exception) {
            Log.w("OfflineProductRepository", "refreshProducts remote call failed: ${e.message}")
            val existing = productDao.observeProducts().first()
            if (existing.isEmpty()) {
                val fallbackDtos = fallbackSource.getProducts()
                val fallbackEntities = fallbackDtos.map { ProductEntityMapper.mapToEntity(it) }
                productDao.replaceAll(fallbackEntities)
            }
        }
    }

    override suspend fun refreshProduct(id: Int) {
        try {
            val dto = remote.getProduct(id)
            val existingEntity = productDao.observeProduct(id).first()
            val newEntity = ProductEntityMapper.mapToEntity(dto)
            val finalEntity = mergeEntities(newEntity, existingEntity)

            productDao.upsertProduct(listOf(finalEntity))
        } catch (e: Exception) {
            Log.w(
                "OfflineProductRepository",
                "refreshProduct($id) remote call failed: ${e.message}"
            )
            val existingEntity = productDao.observeProduct(id).first()
            if (existingEntity == null) {
                try {
                    val fallbackDto = fallbackSource.getProduct(id)
                    productDao.upsertProduct(listOf(ProductEntityMapper.mapToEntity(fallbackDto)))
                } catch (_: Exception) {
                    // Fallback source has no matching product
                }
            }
        }
    }
}