package com.pnc.androidproject.features.products.presentation.viewmodels

import com.pnc.androidproject.features.products.data.FavoritesRepository
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.data.local.FavoriteDao
import com.pnc.androidproject.features.products.data.local.FavoriteEntity
import com.pnc.androidproject.features.products.domain.GetAvailableProductsUseCase
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.domain.ProductRepository
import com.pnc.androidproject.features.products.domain.RefreshProductsUseCase
import com.pnc.androidproject.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

class ProductDirectoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeProductRepository: FakeProductRepository
    private lateinit var fakeFavoriteDao: FakeFavoriteDao
    private lateinit var fakeRecentRepository: FakeRecentlyViewedRepository
    private lateinit var viewModel: ProductDirectoryViewModel

    private class FakeProductRepository : ProductRepository {
        val productsFlow = MutableStateFlow<List<Product>>(emptyList())
        var refreshProductsException: Exception? = null

        override fun observeProducts(): Flow<List<Product>> = productsFlow
        override fun observeProduct(id: Int): Flow<Product?> = flowOf(null)

        override suspend fun refreshProducts() {
            refreshProductsException?.let { throw it }
        }

        override suspend fun refreshProduct(id: Int) {}
    }

    private class FakeFavoriteDao : FavoriteDao {
        private val favoritesFlow = MutableStateFlow<List<Int>>(emptyList())

        fun emitFavorites(ids: List<Int>) {
            favoritesFlow.value = ids
        }

        override fun observeFavoriteIds(): Flow<List<Int>> = favoritesFlow

        override suspend fun add(favorite: FavoriteEntity) {
            val current = favoritesFlow.value.toMutableList()
            if (!current.contains(favorite.productId)) {
                current.add(favorite.productId)
                favoritesFlow.value = current
            }
        }

        override suspend fun remove(id: Int) {
            val current = favoritesFlow.value.toMutableList()
            current.remove(id)
            favoritesFlow.value = current
        }
    }

    private class FakeRecentlyViewedRepository : RecentlyViewedRepository {
        private val recentFlow = MutableStateFlow<List<Int>>(emptyList())
        var clearHistoryCalled = false

        fun emitRecent(ids: List<Int>) {
            recentFlow.value = ids
        }

        override fun observeRecentIds(): Flow<List<Int>> = recentFlow

        override suspend fun recordView(productId: Int) {
            val current = recentFlow.value.toMutableList()
            current.remove(productId)
            current.add(0, productId)
            recentFlow.value = current
        }

        override suspend fun clearHistory() {
            clearHistoryCalled = true
            recentFlow.value = emptyList()
        }
    }

    @Before
    fun setup() {
        fakeProductRepository = FakeProductRepository()
        fakeFavoriteDao = FakeFavoriteDao()
        fakeRecentRepository = FakeRecentlyViewedRepository()

        val getAvailableProducts = GetAvailableProductsUseCase(fakeProductRepository)
        val refreshProducts = RefreshProductsUseCase(fakeProductRepository)
        val favoritesRepository = FavoritesRepository(fakeFavoriteDao)

        viewModel = ProductDirectoryViewModel(
            getAvailableProducts = getAvailableProducts,
            refreshProducts = refreshProducts,
            favoritesRepository = favoritesRepository,
            recentRepository = fakeRecentRepository
        )
    }

    @Test
    fun initialLoadingAndStateUpdates_updatesProductsFavoritesAndRecentIds() = runTest {
        val testProducts = listOf(
            Product(
                productId = 1,
                name = "Bike",
                productNumber = "B1",
                summary = "Speed bike",
                thumbnailPhoto = null,
                thumbnailPhotoFileName = null,
                warranty = null,
                color = "Red",
                listPrice = 500.0,
                stockLevel = 10
            )
        )
        fakeProductRepository.productsFlow.value = testProducts
        fakeFavoriteDao.emitFavorites(listOf(1))
        fakeRecentRepository.emitRecent(listOf(1, 2))

        val state = viewModel.uiState.value
        assertEquals(testProducts, state.products)
        assertEquals(setOf(1), state.favoriteIds)
        assertEquals(listOf(1, 2), state.recentIds)
    }

    @Test
    fun loadProducts_success_resetsLoadingAndError() = runTest {
        viewModel.loadProducts(forceRefresh = true)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun loadProducts_handlesNetworkException() = runTest {
        fakeProductRepository.refreshProductsException = IOException("No internet")

        viewModel.loadProducts(forceRefresh = true)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(
            "Couldn't connect. Please check your network connection and try again.",
            state.errorMessage
        )
    }

    @Test
    fun loadProducts_handlesUnknownHostException() = runTest {
        fakeProductRepository.refreshProductsException = UnknownHostException("Host not found")

        viewModel.loadProducts(forceRefresh = true)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(
            "Couldn't connect. Please check your network connection and try again.",
            state.errorMessage
        )
    }

    @Test
    fun loadProducts_handlesGenericException() = runTest {
        fakeProductRepository.refreshProductsException = RuntimeException("Server error 500")

        viewModel.loadProducts(forceRefresh = true)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Server error 500", state.errorMessage)
    }

    @Test
    fun toggleFavorite_addsAndRemovesFavoriteId() = runTest {
        viewModel.toggleFavorite(productId = 10, favorite = true)
        assertTrue(viewModel.uiState.value.favoriteIds.contains(10))

        viewModel.toggleFavorite(productId = 10, favorite = false)
        assertFalse(viewModel.uiState.value.favoriteIds.contains(10))
    }

    @Test
    fun setShowFavoritesOnly_updatesState() {
        viewModel.setShowFavoritesOnly(true)
        assertTrue(viewModel.uiState.value.showFavoritesOnly)

        viewModel.setShowFavoritesOnly(false)
        assertFalse(viewModel.uiState.value.showFavoritesOnly)
    }

    @Test
    fun setShowHistoryOnly_updatesStateAndResetsShowFavoritesOnly() {
        viewModel.setShowFavoritesOnly(true)
        assertTrue(viewModel.uiState.value.showFavoritesOnly)

        viewModel.setShowHistoryOnly(true)
        assertTrue(viewModel.uiState.value.showHistoryOnly)
        assertFalse(viewModel.uiState.value.showFavoritesOnly)
    }

    @Test
    fun clearHistory_clearsRecentHistoryInRepository() = runTest {
        fakeRecentRepository.emitRecent(listOf(1, 2, 3))
        assertEquals(listOf(1, 2, 3), viewModel.uiState.value.recentIds)

        viewModel.clearHistory()

        assertTrue(fakeRecentRepository.clearHistoryCalled)
        assertEquals(emptyList<Int>(), viewModel.uiState.value.recentIds)
    }
}
