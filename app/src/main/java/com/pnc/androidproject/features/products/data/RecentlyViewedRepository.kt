package com.pnc.androidproject.features.products.data

import kotlinx.coroutines.flow.Flow

interface RecentlyViewedRepository {
    fun observeRecentIds(): Flow<List<Int>>
    suspend fun recordView(productId: Int)
    suspend fun clearHistory()
}