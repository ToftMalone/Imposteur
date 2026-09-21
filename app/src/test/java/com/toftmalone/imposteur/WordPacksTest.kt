package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.data.WordPacks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WordPacksTest {

    @Test
    fun `pack ids are unique`() {
        val ids = WordPacks.BUILT_IN.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "duplicate pack id: ${ids.groupBy { it }.filter { it.value.size > 1 }.keys}")
    }

    @Test
    fun `every pack is playable and reasonably large`() {
        WordPacks.BUILT_IN.forEach { pack ->
            assertTrue(pack.isPlayable, "${pack.id} needs at least two words")
            assertTrue(pack.words.size >= 40, "${pack.id} only has ${pack.words.size} words")
            assertTrue(pack.emoji.isNotBlank(), "${pack.id} has no emoji")
            assertTrue(pack.name.isNotBlank(), "${pack.id} has no name")
        }
    }

    @Test
    fun `no pack repeats a word`() {
        WordPacks.BUILT_IN.forEach { pack ->
            val duplicates = pack.words.groupBy { it.lowercase() }.filter { it.value.size > 1 }.keys
            assertTrue(duplicates.isEmpty(), "${pack.id} repeats $duplicates")
        }
    }

    @Test
    fun `no word is blank or padded`() {
        WordPacks.BUILT_IN.forEach { pack ->
            pack.words.forEach { word ->
                assertTrue(word.isNotBlank(), "${pack.id} has a blank word")
                assertEquals(word.trim(), word, "${pack.id} has padding around '$word'")
            }
        }
    }

    @Test
    fun `packs selected by default all exist`() {
        GameSettings.DEFAULT_SELECTED_PACKS.forEach { id ->
            assertNotNull(WordPacks.findById(id), "default pack '$id' is missing")
        }
    }

    @Test
    fun `lookup by id works for every pack`() {
        WordPacks.BUILT_IN.forEach { pack ->
            assertEquals(pack, WordPacks.findById(pack.id))
        }
    }
}
