package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.AppVersion
import com.toftmalone.imposteur.data.NotesBlock
import com.toftmalone.imposteur.data.ReleaseNotes
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The notes in the assets are both the GitHub release text and the in-app
 * "Quoi de neuf ?" window, which only opens when their title names the
 * installed version. A version bump without new notes would silently lose it.
 */
class BundledNotesTest {

    // Unit tests run from the module directory.
    private val markdown = File("src/main/assets/RELEASE_NOTES.md").readText()

    @Test
    fun `the notes are written for the version being built`() {
        val notesVersion = ReleaseNotes.titleVersion(markdown)
        assertTrue(
            notesVersion != null && AppVersion.isSame(notesVersion, BuildConfig.VERSION_NAME),
            "RELEASE_NOTES.md announces $notesVersion but the app is ${BuildConfig.VERSION_NAME}",
        )
    }

    @Test
    fun `the notes have something to show in the app`() {
        val blocks = ReleaseNotes.forApp(markdown)
        assertTrue(blocks.any { it is NotesBlock.Heading })
        assertTrue(blocks.none { it is NotesBlock.Heading && "installation" in it.text.lowercase() })
    }
}
