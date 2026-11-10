package com.pnc.androidproject.di

import com.pnc.androidproject.features.orders.data.DefaultOrderRepository
import com.pnc.androidproject.features.orders.data.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: DefaultOrderRepository
    ): OrderRepository
}
