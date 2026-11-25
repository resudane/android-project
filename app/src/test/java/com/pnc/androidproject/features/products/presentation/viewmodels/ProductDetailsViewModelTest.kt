package com.pnc.androidproject.features.products.presentation.viewmodels

import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.domain.GetProductUseCase
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.domain.ProductRepository
import com.pnc.androidproject.features.products.domain.RefreshProductUseCase
import com.pnc.androidproject.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

class ProductDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeProductRepository: FakeProductRepository
    private lateinit var fakeRecentRepository: FakeRecentlyViewedRepository
    private lateinit var viewModel: ProductDetailsViewModel

    private class FakeProductRepository : ProductRepository {
        private val flows = mutableMapOf<Int, MutableStateFlow<Product?>>()
        var refreshException: Exception? = null
        var onRefreshProduct: ((Int) -> Unit)? = null

        fun setProduct(id: Int, product: Product?) {
            getFlow(id).value = product
        }

        private fun getFlow(id: Int): MutableStateFlow<Product?> {
            return flows.getOrPut(id) { MutableStateFlow(null) }
        }

        override fun observeProducts(): Flow<List<Product>> {
            throw UnsupportedOperationException()
        }

        override fun observeProduct(id: Int): Flow<Product?> = getFlow(id)

        override suspend fun refreshProducts() {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshProduct(id: Int) {
            refreshException?.let { throw it }
            onRefreshProduct?.invoke(id)
        }
    }

    private class FakeRecentlyViewedRepository : RecentlyViewedRepository {
        private val recentFlow = MutableStateFlow<List<Int>>(emptyList())
        var recordedViewProductId: Int? = null

        override fun observeRecentIds(): Flow<List<Int>> = recentFlow

        override suspend fun recordView(productId: Int) {
            recordedViewProductId = productId
        }

        override suspend fun clearHistory() {
            recentFlow.value = emptyList()
        }
    }

    private val sampleProduct = Product(
        productId = 42,
        name = "Mountain Helmet",
        productNumber = "MH-42",
        summary = "Protective gear",
        thumbnailPhoto = null,
        thumbnailPhotoFileName = null,
        warranty = "1 year",
        color = "Black",
        listPrice = 75.0,
        stockLevel = 10
    )

    @Before
    fun setup() {
        fakeProductRepository = FakeProductRepository()
        fakeRecentRepository = FakeRecentlyViewedRepository()

        val getProductUseCase = GetProductUseCase(fakeProductRepository)
        val refreshProductUseCase = RefreshProductUseCase(fakeProductRepository)

        viewModel = ProductDetailsViewModel(
            getProduct = getProductUseCase,
            refreshProduct = refreshProductUseCase,
            recentlyViewedRepository = fakeRecentRepository
        )
    }

    @Test
    fun loadProduct_whenCachedProductExists_setsSuccessStateAndRecordsView() = runTest {
        fakeProductRepository.setProduct(42, sampleProduct)

        viewModel.loadProduct(42)

        val state = viewModel.uiState.value
        assertTrue(state is LoadableState.Success)
        assertEquals(sampleProduct, (state as LoadableState.Success).data)
        assertEquals(42, fakeRecentRepository.recordedViewProductId)
    }

    @Test
    fun loadProduct_whenNoCacheExistsAndRefreshFails_setsErrorState() = runTest {
        fakeProductRepository.setProduct(42, null)
        fakeProductRepository.refreshException = RuntimeException("Network connection failed")

        viewModel.loadProduct(42)

        val state = viewModel.uiState.value
        assertTrue(state is LoadableState.Error)
        assertEquals("Network connection failed", (state as LoadableState.Error).message)
    }

    @Test
    fun loadProduct_whenNoCacheExistsAndNoInternet_setsNetworkErrorMessage() = runTest {
        fakeProductRepository.setProduct(42, null)
        fakeProductRepository.refreshException = IOException("No internet connection")

        viewModel.loadProduct(42)

        val state = viewModel.uiState.value
        assertTrue(state is LoadableState.Error)
        assertEquals(
            "Couldn't connect. Please check your network connection and try again.",
            (state as LoadableState.Error).message
        )
    }

    @Test
    fun loadProduct_whenNoCacheExistsAndUnknownHost_setsNetworkErrorMessage() = runTest {
        fakeProductRepository.setProduct(42, null)
        fakeProductRepository.refreshException = UnknownHostException("Host not found")

        viewModel.loadProduct(42)

        val state = viewModel.uiState.value
        assertTrue(state is LoadableState.Error)
        assertEquals(
            "Couldn't connect. Please check your network connection and try again.",
            (state as LoadableState.Error).message
        )
    }

    @Test
    fun loadProduct_whenNoCacheExistsAndRefreshSucceeds_emitsSuccess() = runTest {
        fakeProductRepository.setProduct(42, null)
        fakeProductRepository.onRefreshProduct = { id ->
            fakeProductRepository.setProduct(id, sampleProduct)
        }

        viewModel.loadProduct(42)

        val state = viewModel.uiState.value
        assertTrue(state is LoadableState.Success)
        assertEquals(sampleProduct, (state as LoadableState.Success).data)
        assertEquals(42, fakeRecentRepository.recordedViewProductId)
    }
}
