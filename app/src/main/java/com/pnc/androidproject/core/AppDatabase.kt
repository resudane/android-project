package com.pnc.androidproject.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pnc.androidproject.features.orders.data.local.LineItemEntity
import com.pnc.androidproject.features.orders.data.local.OrderDao
import com.pnc.androidproject.features.products.data.local.FavoriteDao
import com.pnc.androidproject.features.products.data.local.FavoriteEntity
import com.pnc.androidproject.features.products.data.local.ProductDao
import com.pnc.androidproject.features.products.data.local.ProductEntity
import com.pnc.androidproject.features.products.data.local.RecentlyViewedDao
import com.pnc.androidproject.features.products.data.local.RecentlyViewedEntity
import com.pnc.androidproject.features.orders.data.local.OrderEntity

@Database(
    entities = [ProductEntity::class, FavoriteEntity::class, RecentlyViewedEntity::class, OrderEntity::class, LineItemEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun orderDao(): OrderDao
}
