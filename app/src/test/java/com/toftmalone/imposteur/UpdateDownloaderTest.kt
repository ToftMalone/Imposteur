package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.UpdateDownload
import com.toftmalone.imposteur.data.UpdateDownloader
import com.toftmalone.imposteur.data.UpdatePackage
import com.toftmalone.imposteur.data.formatMegabytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UpdateDownloaderTest {

    private val hash = "0fdbbf027e2aa08e83cf2f1d85017fbf58a697620203b0bb9cfac607de680074"

    private fun pkg(size: Long = 1_658_389L, sha256: String? = hash) = UpdatePackage(
        fileName = "Imposteur-0.2.1.apk",
        downloadUrl = "https://github.com/ToftMalone/Imposteur/releases/download/v0.2.1/Imposteur-0.2.1.apk",
        sizeBytes = size,
        sha256 = sha256,
    )

    @Test
    fun `a complete file with the published fingerprint is accepted`() {
        assertNull(UpdateDownloader.problemWith(1_658_389L, hash, pkg()))
        assertNull(UpdateDownloader.problemWith(1_658_389L, hash.uppercase(), pkg()))
    }

    @Test
    fun `a truncated, empty or altered file is refused`() {
        assertNotNull(UpdateDownloader.problemWith(1_000_000L, hash, pkg()))
        assertNotNull(UpdateDownloader.problemWith(0L, hash, pkg()))
        assertNotNull(UpdateDownloader.problemWith(1_658_389L, "0".repeat(64), pkg()))
    }

    @Test
    fun `without published size or fingerprint, only an empty file is refused`() {
        val unknown = pkg(size = 0L, sha256 = null)
        assertNull(UpdateDownloader.problemWith(123L, "0".repeat(64), unknown))
        assertNotNull(UpdateDownloader.problemWith(0L, "0".repeat(64), unknown))
    }

    @Test
    fun `asset names cannot climb out of the download folder`() {
        assertEquals("Imposteur-0.2.1.apk", UpdateDownloader.safeFileName("Imposteur-0.2.1.apk"))
        assertEquals("evil.apk", UpdateDownloader.safeFileName("../../evil.apk"))
        assertEquals("evil.apk", UpdateDownloader.safeFileName("..\\..\\evil.apk"))
        assertEquals("Imposteur_v0.3.apk", UpdateDownloader.safeFileName("Imposteur v0.3"))
        assertEquals("Imposteur-update.apk", UpdateDownloader.safeFileName("..."))
    }

    @Test
    fun `progress is a fraction only when the size is known`() {
        assertEquals(0.5f, UpdateDownload.Running(50, 100).fraction)
        assertEquals(1f, UpdateDownload.Running(150, 100).fraction)
        assertNull(UpdateDownload.Running(50, 0).fraction)
    }

    @Test
    fun `sizes read the French way`() {
        assertEquals("1,6 Mo", formatMegabytes(1_658_389L))
        assertEquals("0,0 Mo", formatMegabytes(0L))
        assertEquals("12,0 Mo", formatMegabytes(12L * 1024 * 1024))
    }
}
