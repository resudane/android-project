package com.pnc.androidproject.features.products.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val productId: Int,
    val viewedAt: Long
)

@Dao
abstract class RecentlyViewedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsert(entry: RecentlyViewedEntity)

    @Query(
        "DELETE FROM recently_viewed WHERE productId NOT IN " +
                "(SELECT productId FROM recently_viewed ORDER BY viewedAt DESC LIMIT 10)"
    )
    abstract suspend fun trim()

    @Transaction
    open suspend fun record(productId: Int) {
        upsert(RecentlyViewedEntity(productId, System.currentTimeMillis()))
        trim()
    }

    @Query("SELECT productId FROM recently_viewed ORDER BY viewedAt DESC LIMIT 10")
    abstract fun observeIds(): Flow<List<Int>>

    @Query("DELETE FROM recently_viewed")
    abstract suspend fun clear()
}