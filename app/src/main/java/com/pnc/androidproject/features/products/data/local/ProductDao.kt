package com.pnc.androidproject.features.products.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlin.collections.map

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun observeProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun observeProduct(id: Int): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE productId NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<Int>)

    @Transaction
    suspend fun replaceAll(products: List<ProductEntity>) {
        upsertProduct(products)
        deleteNotIn(products.map { it.productId })
    }
}