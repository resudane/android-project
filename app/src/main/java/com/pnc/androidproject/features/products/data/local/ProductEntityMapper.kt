package com.pnc.androidproject.features.products.data.local

import com.pnc.androidproject.features.products.data.ProductDto
import com.pnc.androidproject.features.products.domain.Product

object ProductEntityMapper {
    fun mapToDomain(entity: ProductEntity): Product {
        return Product(
            productId = entity.productId,
            name = entity.name,
            productNumber = entity.productNumber,
            summary = entity.summary,
            thumbnailPhoto = entity.thumbnailPhoto,
            thumbnailPhotoFileName = entity.thumbnailPhotoFileName,
            warranty = entity.warranty,
            color = entity.color,
            listPrice = entity.listPrice,
            quantity = entity.quantity
        )
    }

    fun mapToEntity(domain: ProductDto): ProductEntity {
        return ProductEntity(
            productId = domain.productId,
            name = domain.name,
            productNumber = domain.productNumber,
            summary = domain.summary,
            thumbnailPhoto = domain.thumbnailPhoto,
            thumbnailPhotoFileName = domain.thumbnailPhotoFileName,
            warranty = domain.warranty,
            color = domain.color,
            listPrice = domain.listPrice,
            quantity = domain.quantity
        )
    }
}