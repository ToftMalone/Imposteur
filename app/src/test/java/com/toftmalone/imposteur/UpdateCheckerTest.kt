package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.UpdateCheckOutcome
import com.toftmalone.imposteur.data.UpdateChecker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UpdateCheckerTest {

    private val hash = "0fdbbf027e2aa08e83cf2f1d85017fbf58a697620203b0bb9cfac607de680074"

    /** Trimmed from a real answer of the releases API (v0.2). */
    private fun release(
        tag: String = "v0.2.1",
        draft: Boolean = false,
        assets: String = """
            [
              {"name": "checksum.txt", "size": 84,
               "browser_download_url": "https://github.com/ToftMalone/Imposteur/releases/download/$tag/checksum.txt"},
              {"name": "Imposteur-0.2.1.apk", "size": 1658389, "digest": "sha256:${hash.uppercase()}",
               "content_type": "application/vnd.android.package-archive",
               "browser_download_url": "https://github.com/ToftMalone/Imposteur/releases/download/$tag/Imposteur-0.2.1.apk"}
            ]
        """,
    ) = """
        {
          "tag_name": "$tag",
          "name": "Imposteur $tag",
          "html_url": "https://github.com/ToftMalone/Imposteur/releases/tag/$tag",
          "draft": $draft,
          "prerelease": false,
          "body": "# Imposteur 0.2.1\n\n## Nouveau\n\n- Mises à jour dans l'app",
          "assets": $assets
        }
    """.trimIndent()

    @Test
    fun `a newer release comes with its notes and its APK`() {
        val result = UpdateChecker(currentVersion = "0.2").interpret(release())

        assertEquals(UpdateCheckOutcome.UPDATE_AVAILABLE, result.outcome)
        val update = assertNotNull(result.update)
        assertEquals("0.2.1", update.versionName)
        assertEquals("https://github.com/ToftMalone/Imposteur/releases/tag/v0.2.1", update.releaseUrl)
        assertEquals("# Imposteur 0.2.1\n\n## Nouveau\n\n- Mises à jour dans l'app", update.notes)

        val apk = assertNotNull(update.apk)
        assertEquals("Imposteur-0.2.1.apk", apk.fileName)
        assertEquals(1_658_389L, apk.sizeBytes)
        assertEquals(hash, apk.sha256)
        assertEquals("https://github.com/ToftMalone/Imposteur/releases/download/v0.2.1/Imposteur-0.2.1.apk", apk.downloadUrl)
    }

    @Test
    fun `a debug build compares on its version, not its suffix`() {
        assertEquals(
            UpdateCheckOutcome.UPDATE_AVAILABLE,
            UpdateChecker(currentVersion = "0.2-debug").interpret(release()).outcome,
        )
        assertEquals(
            UpdateCheckOutcome.UP_TO_DATE,
            UpdateChecker(currentVersion = "0.2.1-debug").interpret(release()).outcome,
        )
    }

    @Test
    fun `the same or an older release is not an update`() {
        assertEquals(UpdateCheckOutcome.UP_TO_DATE, UpdateChecker("0.2.1").interpret(release()).outcome)
        assertEquals(UpdateCheckOutcome.UP_TO_DATE, UpdateChecker("0.3").interpret(release()).outcome)
        assertNull(UpdateChecker("0.3").interpret(release()).update)
    }

    @Test
    fun `a release without APK is still announced, just not installable`() {
        val update = assertNotNull(UpdateChecker("0.2").interpret(release(assets = "[]")).update)
        assertNull(update.apk)
    }

    @Test
    fun `only APKs attached to this repository's releases are taken`() {
        for (url in listOf(
            "https://example.com/ToftMalone/Imposteur/releases/download/v1/Imposteur.apk",
            "https://github.com/SomeoneElse/Imposteur/releases/download/v1/Imposteur.apk",
            "https://github.com/ToftMalone/Imposteur/raw/main/Imposteur.apk",
        )) {
            val assets = """[{"name": "Imposteur.apk", "size": 10, "browser_download_url": "$url"}]"""
            assertNull(assertNotNull(UpdateChecker("0.2").interpret(release(assets = assets)).update).apk, url)
        }
    }

    @Test
    fun `the release page opened in the browser stays on this repository`() {
        val evil = release().replace(
            "https://github.com/ToftMalone/Imposteur/releases/tag/v0.2.1",
            "https://evil.example/phishing",
        )
        assertEquals(
            "https://github.com/ToftMalone/Imposteur/releases",
            assertNotNull(UpdateChecker("0.2").interpret(evil).update).releaseUrl,
        )
        val sneaky = release().replace(
            "https://github.com/ToftMalone/Imposteur/releases/tag/v0.2.1",
            "https://github.com/ToftMalone/Imposteur/releases.evil.example/x",
        )
        assertEquals(
            "https://github.com/ToftMalone/Imposteur/releases",
            assertNotNull(UpdateChecker("0.2").interpret(sneaky).update).releaseUrl,
        )
    }

    @Test
    fun `an oversized answer is not read into memory`() {
        val limit = 1024
        assertEquals("petit", UpdateChecker.readCapped("petit".byteInputStream(), limit))
        assertEquals("é".repeat(512), UpdateChecker.readCapped("é".repeat(512).byteInputStream(), limit))
        assertNull(UpdateChecker.readCapped("x".repeat(limit + 1).byteInputStream(), limit))
    }

    @Test
    fun `an APK that is not served over https is ignored`() {
        val assets = """[{"name": "Imposteur.apk", "size": 10, "browser_download_url": "http://example.com/Imposteur.apk"}]"""
        assertNull(assertNotNull(UpdateChecker("0.2").interpret(release(assets = assets)).update).apk)
    }

    @Test
    fun `a digest that is not a SHA-256 is dropped, not trusted`() {
        val assets = """[{"name": "Imposteur.apk", "size": 10, "digest": "md5:abc",
            "browser_download_url": "https://github.com/ToftMalone/Imposteur/releases/download/v0.2.1/Imposteur.apk"}]"""
        val apk = assertNotNull(assertNotNull(UpdateChecker("0.2").interpret(release(assets = assets)).update).apk)
        assertNull(apk.sha256)
    }

    @Test
    fun `drafts and unreadable answers are reported as unavailable`() {
        assertEquals(UpdateCheckOutcome.UNAVAILABLE, UpdateChecker("0.2").interpret(release(draft = true)).outcome)
        assertEquals(UpdateCheckOutcome.UNAVAILABLE, UpdateChecker("0.2").interpret("<html>rate limited</html>").outcome)
        assertEquals(UpdateCheckOutcome.UNAVAILABLE, UpdateChecker("0.2").interpret("""{"tag_name": ""}""").outcome)
    }
}
