package com.pnc.androidproject.features.orders.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface OrderDao {
    @Transaction
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    suspend fun getAllOrders(): List<OrderWithLineItems>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber")
    suspend fun getOrder(orderNumber: Int): OrderWithLineItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(lineItems: List<LineItemEntity>)

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()

    @Query("DELETE FROM line_items")
    suspend fun deleteAllLineItems()

    @Transaction
    suspend fun replaceOrders(
        orders: List<OrderEntity>,
        lineItems: List<LineItemEntity>
    ) {
        deleteAllLineItems()
        deleteAllOrders()
        insertOrders(orders)
        insertLineItems(lineItems)
    }
}