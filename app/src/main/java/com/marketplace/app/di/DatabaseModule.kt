package com.marketplace.app.di

import android.content.Context
import com.marketplace.app.data.local.dao.ListingDao
import com.marketplace.app.data.local.dao.PendingOperationDao
import com.marketplace.app.data.local.database.MarketplaceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MarketplaceDatabase {
        return MarketplaceDatabase.getDatabase(context)
    }

    @Provides
    fun provideListingDao(database: MarketplaceDatabase): ListingDao {
        return database.listingDao()
    }

    @Provides
    fun providePendingOperationDao(database: MarketplaceDatabase): PendingOperationDao {
        return database.pendingOperationDao()
    }
}
