package com.pnc.androidproject.features.products.data

import com.pnc.androidproject.features.products.data.local.ProductEntity
import com.pnc.androidproject.features.products.data.local.ProductEntityMapper
import com.pnc.androidproject.features.products.domain.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductEntityMapperTest {

    @Test
    fun mapToEntity_mapsAllFieldsFromProductDto() {
        val dto = ProductDto(
            productId = 101,
            name = "Mountain Bike",
            productNumber = "BK-M101",
            summary = "A mountain bike",
            thumbnailPhoto = "photo_url",
            thumbnailPhotoFileName = "photo.jpg",
            warranty = "2 years",
            color = "Red",
            listPrice = 499.99,
            stockLevel = 15
        )

        val entity = ProductEntityMapper.mapToEntity(dto)

        assertEquals(101, entity.productId)
        assertEquals("Mountain Bike", entity.name)
        assertEquals("BK-M101", entity.productNumber)
        assertEquals("A mountain bike", entity.summary)
        assertEquals("photo_url", entity.thumbnailPhoto)
        assertEquals("photo.jpg", entity.thumbnailPhotoFileName)
        assertEquals("2 years", entity.warranty)
        assertEquals("Red", entity.color)
        assertEquals(499.99, entity.listPrice!!, 0.001)
        assertEquals(15, entity.stockLevel)
    }

    @Test
    fun mapToEntity_handlesNullAndDefaultValues() {
        val dto = ProductDto(
            productId = 102,
            name = null,
            productNumber = null,
            summary = null,
            thumbnailPhoto = null,
            thumbnailPhotoFileName = null,
            warranty = null,
            color = null,
            listPrice = null,
            stockLevel = null
        )

        val entity = ProductEntityMapper.mapToEntity(dto)

        assertEquals(102, entity.productId)
        assertEquals(null, entity.name)
        assertEquals(null, entity.productNumber)
        assertEquals(null, entity.summary)
        assertEquals(null, entity.thumbnailPhoto)
        assertEquals(null, entity.thumbnailPhotoFileName)
        assertEquals(null, entity.warranty)
        assertEquals(null, entity.color)
        assertEquals(null, entity.listPrice)
        assertEquals(null, entity.stockLevel)
    }

    @Test
    fun mapToDomain_mapsAllFieldsFromProductEntity() {
        val entity = ProductEntity(
            productId = 201,
            name = "Road Bike",
            productNumber = "BK-R201",
            summary = "A road bike",
            thumbnailPhoto = "road_photo_url",
            thumbnailPhotoFileName = "road_photo.jpg",
            warranty = "3 years",
            color = "Blue",
            listPrice = 899.99,
            stockLevel = 8
        )

        val product: Product = ProductEntityMapper.mapToDomain(entity)

        assertEquals(201, product.productId)
        assertEquals("Road Bike", product.name)
        assertEquals("BK-R201", product.productNumber)
        assertEquals("A road bike", product.summary)
        assertEquals("road_photo_url", product.thumbnailPhoto)
        assertEquals("road_photo.jpg", product.thumbnailPhotoFileName)
        assertEquals("3 years", product.warranty)
        assertEquals("Blue", product.color)
        assertEquals(899.99, product.listPrice, 0.001)
        assertEquals(8, product.stockLevel)
    }

    @Test
    fun mapToDomain_usesDefaultFallbacksForNullFields() {
        val entity = ProductEntity(
            productId = 202,
            name = null,
            productNumber = null,
            summary = null,
            thumbnailPhoto = null,
            thumbnailPhotoFileName = null,
            warranty = null,
            color = null,
            listPrice = null,
            stockLevel = null
        )

        val product: Product = ProductEntityMapper.mapToDomain(entity)

        assertEquals(202, product.productId)
        assertEquals("", product.name)
        assertEquals("", product.productNumber)
        assertEquals("", product.summary)
        assertEquals("", product.thumbnailPhoto)
        assertEquals("", product.thumbnailPhotoFileName)
        assertEquals("", product.warranty)
        assertEquals("", product.color)
        assertEquals(0.0, product.listPrice, 0.001)
        assertEquals(0, product.stockLevel)
    }
}
