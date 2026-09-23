package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.NotesBlock
import com.toftmalone.imposteur.data.NotesSpan
import com.toftmalone.imposteur.data.ReleaseNotes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReleaseNotesTest {

    private val sample = """
        # Imposteur 0.2.1

        ## 📥 Les mises à jour s'installent depuis l'app

        Quand une nouvelle version sort, une fenêtre
        s'ouvre au lancement.

        - Le fichier est vérifié : même taille et même
          empreinte **SHA-256**.
        - Rien ne s'installe sans ton accord.

        ## ℹ️ Permissions

        L'app demande `REQUEST_INSTALL_PACKAGES`.

        ---

        ## 📲 Installation

        1. Télécharge `Imposteur-0.2.1.apk` ci-dessous.
        2. Ouvre le fichier.

        Android 7.0 minimum.
    """.trimIndent()

    @Test
    fun `reads headings, wrapped paragraphs and lists`() {
        val blocks = ReleaseNotes.parse(sample)
        assertEquals(
            listOf(
                NotesBlock.Heading("Les mises à jour s'installent depuis l'app"),
                NotesBlock.Paragraph("Quand une nouvelle version sort, une fenêtre s'ouvre au lancement."),
                NotesBlock.Bullet("•", "Le fichier est vérifié : même taille et même empreinte **SHA-256**."),
                NotesBlock.Bullet("•", "Rien ne s'installe sans ton accord."),
                NotesBlock.Heading("Permissions"),
                NotesBlock.Paragraph("L'app demande `REQUEST_INSTALL_PACKAGES`."),
                NotesBlock.Heading("Installation"),
                NotesBlock.Bullet("1.", "Télécharge `Imposteur-0.2.1.apk` ci-dessous."),
                NotesBlock.Bullet("2.", "Ouvre le fichier."),
                NotesBlock.Paragraph("Android 7.0 minimum."),
            ),
            blocks,
        )
    }

    @Test
    fun `the app leaves out the install steps meant for the GitHub page`() {
        val blocks = ReleaseNotes.forApp(sample)
        assertEquals(NotesBlock.Heading("Permissions"), blocks.last { it is NotesBlock.Heading })
        assertTrue(blocks.none { it is NotesBlock.Bullet && it.marker == "1." })
        assertTrue(blocks.none { it is NotesBlock.Paragraph && it.text.startsWith("Android 7.0") })
    }

    @Test
    fun `emoji never reach the app, the words stay`() {
        val text = ReleaseNotes.parse("## ✨ Fenêtre « Quoi de neuf ? » 🎉\n\nÇa marche ✅ — enfin… ⚠️ oui")
        assertEquals(
            listOf(
                NotesBlock.Heading("Fenêtre « Quoi de neuf ? »"),
                NotesBlock.Paragraph("Ça marche — enfin… oui"),
            ),
            text,
        )
    }

    @Test
    fun `links keep their label only`() {
        assertEquals(
            listOf(NotesBlock.Paragraph("Voir la page des releases pour le détail.")),
            ReleaseNotes.parse("Voir [la page des releases](https://github.com/ToftMalone/Imposteur/releases) pour le détail."),
        )
    }

    @Test
    fun `splits bold and code runs`() {
        assertEquals(
            listOf(
                NotesSpan("Appuie sur "),
                NotesSpan("Mettre à jour", bold = true),
                NotesSpan(" pour "),
                NotesSpan("Imposteur-0.2.1.apk", code = true),
                NotesSpan("."),
            ),
            ReleaseNotes.spans("Appuie sur **Mettre à jour** pour `Imposteur-0.2.1.apk`."),
        )
        assertEquals(listOf(NotesSpan("Sans style")), ReleaseNotes.spans("Sans style"))
    }

    @Test
    fun `finds the version in the top title`() {
        assertEquals("0.2.1", ReleaseNotes.titleVersion(sample))
        assertEquals("0.2", ReleaseNotes.titleVersion("# Imposteur 0.2\n\n## 0.1 en rappel"))
        assertNull(ReleaseNotes.titleVersion("## Pas de titre principal 0.3"))
        assertNull(ReleaseNotes.titleVersion(""))
    }

    @Test
    fun `empty or blank notes give nothing to show`() {
        assertEquals(emptyList(), ReleaseNotes.forApp(""))
        assertEquals(emptyList(), ReleaseNotes.forApp("\r\n  \r\n# Imposteur 0.3\r\n"))
    }
}
