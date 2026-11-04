package com.pnc.androidproject.features.products.data

import com.pnc.androidproject.features.products.data.local.FavoriteDao
import com.pnc.androidproject.features.products.data.local.FavoriteEntity
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FavoritesRepository @Inject constructor(
    private val dao: FavoriteDao
) {
    fun observeFavoriteIds(): Flow<Set<Int>> =
        dao.observeFavoriteIds().map { it.toSet() }

    suspend fun setFavorite(productId: Int, favorite: Boolean) {
        if (favorite) dao.add(FavoriteEntity(productId)) else dao.remove(productId)
    }
}