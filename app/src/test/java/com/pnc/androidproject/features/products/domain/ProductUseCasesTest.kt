package com.pnc.androidproject.features.products.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductUseCasesTest {

    private lateinit var fakeRepository: FakeProductRepository
    private lateinit var getAvailableProductsUseCase: GetAvailableProductsUseCase
    private lateinit var getProductUseCase: GetProductUseCase
    private lateinit var refreshProductsUseCase: RefreshProductsUseCase
    private lateinit var refreshProductUseCase: RefreshProductUseCase

    private class FakeProductRepository : ProductRepository {
        var productsFlow: Flow<List<Product>> = flowOf(emptyList())
        var productMap: Map<Int, Product?> = emptyMap()
        var refreshProductsCalled = false
        var refreshedProductId: Int? = null

        override fun observeProducts(): Flow<List<Product>> = productsFlow
        override fun observeProduct(id: Int): Flow<Product?> = flowOf(productMap[id])

        override suspend fun refreshProducts() {
            refreshProductsCalled = true
        }

        override suspend fun refreshProduct(id: Int) {
            refreshedProductId = id
        }
    }

    @Before
    fun setup() {
        fakeRepository = FakeProductRepository()
        getAvailableProductsUseCase = GetAvailableProductsUseCase(fakeRepository)
        getProductUseCase = GetProductUseCase(fakeRepository)
        refreshProductsUseCase = RefreshProductsUseCase(fakeRepository)
        refreshProductUseCase = RefreshProductUseCase(fakeRepository)
    }

    @Test
    fun getAvailableProductsUseCase_filtersOutOfStockProductsAndSortsByName() = runTest {
        val products = listOf(
            Product(
                productId = 1,
                name = "Banana",
                productNumber = "P1",
                summary = "Yellow",
                thumbnailPhoto = null,
                thumbnailPhotoFileName = null,
                warranty = null,
                color = "Yellow",
                listPrice = 1.0,
                stockLevel = 5
            ),
            Product(
                productId = 2,
                name = "Apple",
                productNumber = "P2",
                summary = "Red",
                thumbnailPhoto = null,
                thumbnailPhotoFileName = null,
                warranty = null,
                color = "Red",
                listPrice = 2.0,
                stockLevel = 0
            ),
            Product(
                productId = 3,
                name = "Discontinued Item",
                productNumber = "P3",
                summary = "None",
                thumbnailPhoto = null,
                thumbnailPhotoFileName = null,
                warranty = null,
                color = null,
                listPrice = 0.0,
                stockLevel = -1
            ),
            Product(
                productId = 4,
                name = "Cherry",
                productNumber = "P4",
                summary = "Dark red",
                thumbnailPhoto = null,
                thumbnailPhotoFileName = null,
                warranty = null,
                color = "Red",
                listPrice = 3.0,
                stockLevel = 10
            )
        )
        fakeRepository.productsFlow = flowOf(products)

        val result = getAvailableProductsUseCase().first()

        assertEquals(3, result.size)
        assertEquals("Apple", result[0].name)
        assertEquals("Banana", result[1].name)
        assertEquals("Cherry", result[2].name)
    }

    @Test
    fun getProductUseCase_retrievesMatchingProductById() = runTest {
        val targetProduct = Product(
            productId = 42,
            name = "Smart Watch",
            productNumber = "SW-42",
            summary = "Wearable",
            thumbnailPhoto = null,
            thumbnailPhotoFileName = null,
            warranty = "1 year",
            color = "Black",
            listPrice = 199.99,
            stockLevel = 20
        )
        fakeRepository.productMap = mapOf(42 to targetProduct)

        val found = getProductUseCase(42).first()
        val notFound = getProductUseCase(99).first()

        assertEquals(targetProduct, found)
        assertNull(notFound)
    }

    @Test
    fun refreshProductsUseCase_triggersRepositoryRefreshProducts() = runTest {
        refreshProductsUseCase()
        assertTrue(fakeRepository.refreshProductsCalled)
    }

    @Test
    fun refreshProductUseCase_triggersRepositoryRefreshProduct() = runTest {
        refreshProductUseCase(42)
        assertEquals(42, fakeRepository.refreshedProductId)
    }
}
