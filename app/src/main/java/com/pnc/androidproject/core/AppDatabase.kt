package com.pnc.androidproject.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pnc.androidproject.features.products.data.local.FavoriteDao
import com.pnc.androidproject.features.products.data.local.FavoriteEntity
import com.pnc.androidproject.features.products.data.local.ProductDao
import com.pnc.androidproject.features.products.data.local.ProductEntity

@Database(entities = [ProductEntity::class, FavoriteEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun favoriteDao(): FavoriteDao
}
