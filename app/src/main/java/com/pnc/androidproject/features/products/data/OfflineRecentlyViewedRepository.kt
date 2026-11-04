package com.pnc.androidproject.features.products.data

import android.util.Log
import com.pnc.androidproject.features.products.data.local.RecentlyViewedDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class OfflineRecentlyViewedRepository @Inject constructor(
    private val dao: RecentlyViewedDao
): RecentlyViewedRepository {

    override fun observeRecentIds(): Flow<List<Int>> = dao.observeIds()

    override suspend fun recordView(productId: Int) {
        Log.d("RECENT", "recordView($productId)")
        dao.record(productId)
    }

    override suspend fun clearHistory() = dao.clear()
}