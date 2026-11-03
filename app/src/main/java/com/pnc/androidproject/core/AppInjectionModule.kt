package com.pnc.androidproject.core

import com.pnc.androidproject.features.products.data.OfflineProductRepository
import com.pnc.androidproject.features.products.domain.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
abstract class AppInjectionModule {

    @Binds
    abstract fun bindProductRepository(
        implementation: OfflineProductRepository
    ): ProductRepository

    companion object {
        @Provides
        @Singleton
        @ApplicationScope
        fun provideAppScope(): CoroutineScope {
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }
    }
}

