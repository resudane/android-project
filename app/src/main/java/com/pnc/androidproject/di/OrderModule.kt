package com.pnc.androidproject.di


import com.pnc.androidproject.features.orders.data.OrderDataSource
import com.pnc.androidproject.features.orders.data.RemoteOrderDataSource
import com.pnc.androidproject.features.orders.data.RemoteOrderRepository
import com.pnc.androidproject.features.orders.domain.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {

    @Binds
    @Singleton
    abstract fun bindOrderDataSource(
        impl: RemoteOrderDataSource
    ): OrderDataSource

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: RemoteOrderRepository
    ): OrderRepository
    companion object {

        @Provides
        @Singleton
        fun provideRemoteOrderDataSource(
            retrofit: Retrofit
        ): RemoteOrderDataSource {
            return retrofit.create(RemoteOrderDataSource::class.java)
        }
    }
}