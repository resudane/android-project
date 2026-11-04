package com.pnc.androidproject.di

import com.pnc.androidproject.features.products.data.OfflineProductRepository
import com.pnc.androidproject.features.products.data.OfflineRecentlyViewedRepository
import com.pnc.androidproject.features.products.data.ProductDataSource
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.data.RemoteProductDataSource
import com.pnc.androidproject.features.products.domain.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {

    @Binds
    @Singleton
    abstract fun bindProductDataSource(
        impl: RemoteProductDataSource
    ): ProductDataSource

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: OfflineProductRepository
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindRecentlyViewedRepository(
        impl: OfflineRecentlyViewedRepository
    ): RecentlyViewedRepository
}
