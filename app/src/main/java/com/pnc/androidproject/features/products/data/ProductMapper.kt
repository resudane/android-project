package com.pnc.androidproject.features.products.data

import com.pnc.androidproject.features.products.domain.Product

object ProductMapper {
    fun mapToDomain(dto: ProductDto): Product {
        return Product(
            productId = dto.productId,
            name = dto.name,
            productNumber = dto.productNumber,
            summary = dto.summary,
            thumbnailPhoto = dto.thumbnailPhoto,
            thumbnailPhotoFileName = dto.thumbnailPhotoFileName,
            warranty = dto.warranty,
            color = dto.color,
            listPrice = dto.listPrice,
            quantity = dto.quantity
        )
    }

    fun mapToDto(domain: Product): ProductDto {
        return ProductDto(
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