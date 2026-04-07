package com.marketplace.app.domain.sync

import com.marketplace.app.domain.model.Listing
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ConflictResolverTest {

    private val resolver = ConflictResolver()

    private val baseListing = Listing(
        id = "1",
        title = "Base Title",
        description = "Base Description",
        price = 100.0,
        category = "Base Category",
        imageUrls = emptyList(),
        isFavorite = false,
        createdAt = Date(1000),
        updatedAt = Date(1000),
        version = 1
    )

    @Test
    fun resolve_remoteNewerVersion_returnsRemoteWithLocalFavorite() {
        val local = baseListing.copy(isFavorite = true, version = 1)
        val remote = baseListing.copy(title = "Remote Title", isFavorite = false, version = 2)

        val result = resolver.resolve(local, remote)

        assertEquals("Remote Title", result?.title)
        assertEquals(true, result?.isFavorite)
        assertEquals(2, result?.version)
    }

    @Test
    fun resolve_localNewerVersion_returnsLocal() {
        val local = baseListing.copy(title = "Local Title", version = 2)
        val remote = baseListing.copy(title = "Remote Title", version = 1)

        val result = resolver.resolve(local, remote)

        assertEquals("Local Title", result?.title)
        assertEquals(2, result?.version)
    }

    @Test
    fun resolve_sameVersionRemoteNewerDate_returnsRemoteWithLocalFavorite() {
        val local = baseListing.copy(
            isFavorite = true,
            updatedAt = Date(1000),
            version = 1
        )
        val remote = baseListing.copy(
            title = "Remote Title",
            isFavorite = false,
            updatedAt = Date(2000),
            version = 1
        )

        val result = resolver.resolve(local, remote)

        assertEquals("Remote Title", result?.title)
        assertEquals(true, result?.isFavorite)
        assertEquals(Date(2000), result?.updatedAt)
    }
}
