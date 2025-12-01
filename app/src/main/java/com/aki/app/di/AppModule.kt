package com.aki.app.di

import com.aki.core.data.repository.VpnRepository
import com.aki.core.data.repository.VpnRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindVpnRepository(vpnRepositoryImpl: VpnRepositoryImpl): VpnRepository

}
