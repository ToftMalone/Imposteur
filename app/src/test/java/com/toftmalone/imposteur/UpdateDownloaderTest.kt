package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.UpdateDownload
import com.toftmalone.imposteur.data.UpdateDownloadException
import com.toftmalone.imposteur.data.UpdateDownloader
import com.toftmalone.imposteur.data.UpdatePackage
import com.toftmalone.imposteur.data.formatMegabytes
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

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

    @Test
    fun `only GitHub and its file storage are contacted`() {
        assertTrue(UpdateDownloader.isGitHubHost("github.com"))
        assertTrue(UpdateDownloader.isGitHubHost("objects.githubusercontent.com"))
        assertTrue(UpdateDownloader.isGitHubHost("release-assets.githubusercontent.com"))
        assertFalse(UpdateDownloader.isGitHubHost("evilgithubusercontent.com"))
        assertFalse(UpdateDownloader.isGitHubHost("github.com.evil.com"))
        assertFalse(UpdateDownloader.isGitHubHost("gist.github.com.attacker.io"))
        assertFalse(UpdateDownloader.isGitHubHost("example.com"))
    }

    @Test
    fun `a file announced as too large is refused before anything is written`() = runBlocking {
        val dir = createTempDirectory("imposteur-dl").toFile()
        try {
            val error = assertFailsWith<UpdateDownloadException> {
                UpdateDownloader(dir).download(pkg(size = UpdateDownloader.MAX_APK_BYTES + 1))
            }
            assertTrue("gros" in error.message.orEmpty())
            assertEquals(emptyList(), dir.list().orEmpty().toList())
        } finally {
            dir.deleteRecursively()
        }
    }

    @Test
    fun `addresses outside GitHub or over plain http are refused before connecting`() = runBlocking {
        val dir = createTempDirectory("imposteur-dl").toFile()
        try {
            for (url in listOf(
                "https://example.com/Imposteur.apk",
                "http://github.com/ToftMalone/Imposteur/releases/download/v1/Imposteur.apk",
                "file:///sdcard/Imposteur.apk",
                "pas une adresse",
            )) {
                assertFailsWith<UpdateDownloadException>(url) {
                    UpdateDownloader(dir).download(pkg().copy(downloadUrl = url))
                }
            }
        } finally {
            dir.deleteRecursively()
        }
    }
}
