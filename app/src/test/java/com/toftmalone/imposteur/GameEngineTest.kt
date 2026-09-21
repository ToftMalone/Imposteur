package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.data.ImposterMode
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.data.Role
import com.toftmalone.imposteur.data.RoundOutcome
import com.toftmalone.imposteur.data.WordPacks
import com.toftmalone.imposteur.game.DealResult
import com.toftmalone.imposteur.game.GameEngine
import com.toftmalone.imposteur.game.GameError
import com.toftmalone.imposteur.game.Scoring
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameEngineTest {

    private fun roster(size: Int): List<Player> =
        (1..size).map { Player(id = "p$it", name = "Joueur $it", avatar = it) }

    private fun settings(
        imposters: Int = 1,
        mode: ImposterMode = ImposterMode.DIFFERENT_WORD,
        showCategory: Boolean = true,
        knowEachOther: Boolean = false,
    ) = GameSettings(
        imposterCount = imposters,
        imposterMode = mode,
        showCategoryToEveryone = showCategory,
        impostersKnowEachOther = knowEachOther,
        selectedPackIds = setOf("animaux"),
    )

    private fun dealOrFail(
        players: List<Player>,
        settings: GameSettings,
        seed: Int = 42,
    ) = (GameEngine.deal(players, settings, WordPacks.BUILT_IN, Random(seed)) as DealResult.Success).round

    // --- dealing ------------------------------------------------------------

    @Test
    fun `deals exactly the requested number of impostors`() {
        val round = dealOrFail(roster(7), settings(imposters = 3))
        assertEquals(3, round.assignments.count { it.role == Role.IMPOSTEUR })
        assertEquals(4, round.assignments.count { it.role == Role.CIVIL })
    }

    @Test
    fun `every civilian shares the same word and impostors get another one`() {
        val round = dealOrFail(roster(6), settings(imposters = 2))
        val civilWords = round.assignments.filter { it.role == Role.CIVIL }.map { it.word }.toSet()
        assertEquals(setOf(round.civilWord), civilWords)

        val imposterWords = round.assignments.filter { it.role == Role.IMPOSTEUR }.map { it.word }.toSet()
        assertEquals(setOf(round.imposterWord), imposterWords)
        assertTrue(round.civilWord != round.imposterWord, "impostor word must differ")
    }

    @Test
    fun `the impostor's word is always the partner of the civilians' word`() {
        // The whole point of the pairs: the impostor must never get an unrelated
        // word from elsewhere in the pack.
        repeat(200) { seed ->
            val round = dealOrFail(roster(5), settings(), seed = seed)
            val pack = WordPacks.findById(round.packId)!!
            val dealt = setOf(round.civilWord, round.imposterWord)
            val match = pack.pairs.any { setOf(it.first, it.second) == dealt }
            assertTrue(match, "seed $seed dealt ${round.civilWord} / ${round.imposterWord}")
        }
    }

    @Test
    fun `either side of a pair can fall to the civilians`() {
        val firstSideSeen = mutableSetOf<Boolean>()
        repeat(200) { seed ->
            val round = dealOrFail(roster(5), settings(), seed = seed)
            val pack = WordPacks.findById(round.packId)!!
            val pair = pack.pairs.first {
                setOf(it.first, it.second) == setOf(round.civilWord, round.imposterWord)
            }
            firstSideSeen += (pair.first == round.civilWord)
        }
        assertEquals(setOf(true, false), firstSideSeen, "pairs should be dealt both ways")
    }

    @Test
    fun `no-word mode leaves the impostor with nothing at all`() {
        val round = dealOrFail(roster(5), settings(mode = ImposterMode.NO_WORD, showCategory = true))
        val impostor = round.assignments.first { it.role == Role.IMPOSTEUR }
        assertNull(impostor.word, "impostor should not see a word")
        assertNull(impostor.categoryHint, "showing the theme to civilians must not leak it to the impostor")
        assertNull(round.imposterWord)

        val civil = round.assignments.first { it.role == Role.CIVIL }
        assertEquals(round.packName, civil.categoryHint)
    }

    @Test
    fun `category-hint mode gives the theme but never the word`() {
        val round = dealOrFail(roster(5), settings(mode = ImposterMode.CATEGORY_HINT, showCategory = false))
        val impostor = round.assignments.first { it.role == Role.IMPOSTEUR }
        assertNull(impostor.word)
        assertEquals(round.packName, impostor.categoryHint)

        val civil = round.assignments.first { it.role == Role.CIVIL }
        assertNull(civil.categoryHint, "civilians opted out of the theme")
    }

    @Test
    fun `impostors can be told about each other`() {
        val round = dealOrFail(roster(8), settings(imposters = 3, knowEachOther = true))
        val impostors = round.assignments.filter { it.role == Role.IMPOSTEUR }
        impostors.forEach { assignment ->
            assertEquals(2, assignment.fellowImposters.size)
            assertFalse(assignment.fellowImposters.any { it == "Joueur ${assignment.playerId.removePrefix("p")}" })
        }
        round.assignments.filter { it.role == Role.CIVIL }.forEach {
            assertTrue(it.fellowImposters.isEmpty())
        }
    }

    @Test
    fun `speaking order lists every player exactly once`() {
        val players = roster(9)
        val round = dealOrFail(players, settings())
        assertEquals(players.map { it.id }.sorted(), round.speakingOrder.sorted())
    }

    @Test
    fun `a fixed seed deals a reproducible round`() {
        val a = dealOrFail(roster(6), settings(imposters = 2), seed = 7)
        val b = dealOrFail(roster(6), settings(imposters = 2), seed = 7)
        assertEquals(a, b)
    }

    // --- validation ---------------------------------------------------------

    @Test
    fun `rejects a table that is too small`() {
        val result = GameEngine.deal(roster(2), settings(), WordPacks.BUILT_IN)
        assertEquals(GameError.NOT_ENOUGH_PLAYERS, (result as DealResult.Failure).error)
    }

    @Test
    fun `rejects impostors that would not be outnumbered`() {
        val result = GameEngine.deal(roster(4), settings(imposters = 2), WordPacks.BUILT_IN)
        assertEquals(GameError.TOO_MANY_IMPOSTERS, (result as DealResult.Failure).error)
    }

    @Test
    fun `rejects an empty pack selection`() {
        val result = GameEngine.deal(
            roster(5),
            settings().copy(selectedPackIds = emptySet()),
            WordPacks.BUILT_IN,
        )
        assertEquals(GameError.NO_PACK_SELECTED, (result as DealResult.Failure).error)
    }

    @Test
    fun `impostor cap keeps them a minority`() {
        assertEquals(1, GameEngine.maxImposters(3))
        assertEquals(1, GameEngine.maxImposters(4))
        assertEquals(2, GameEngine.maxImposters(5))
        assertEquals(2, GameEngine.maxImposters(6))
        assertEquals(3, GameEngine.maxImposters(7))
        assertEquals(9, GameEngine.maxImposters(20))
    }

    // --- voting -------------------------------------------------------------

    @Test
    fun `civilians win once the last impostor is voted out`() {
        val players = roster(5)
        val round = dealOrFail(players, settings(imposters = 1))
        val impostor = round.imposterIds.single()

        val result = GameEngine.eliminate(round, players.map { it.id }, impostor)
        assertTrue(result.wasImposter)
        assertEquals(RoundOutcome.CIVILS_WIN, result.outcome)
        assertTrue(result.remainingImposterIds.isEmpty())
    }

    @Test
    fun `voting out a civilian can hand the round to the impostors`() {
        val players = roster(3)
        val round = dealOrFail(players, settings(imposters = 1))
        val innocent = players.map { it.id }.first { it !in round.imposterIds }

        val result = GameEngine.eliminate(round, players.map { it.id }, innocent)
        assertFalse(result.wasImposter)
        // 1 impostor vs 1 civilian left: parity, impostors take it.
        assertEquals(RoundOutcome.IMPOSTERS_WIN, result.outcome)
    }

    @Test
    fun `the round continues while civilians still outnumber impostors`() {
        val players = roster(6)
        val round = dealOrFail(players, settings(imposters = 1))
        val innocent = players.map { it.id }.first { it !in round.imposterIds }

        val result = GameEngine.eliminate(round, players.map { it.id }, innocent)
        assertNull(result.outcome, "4 civilians vs 1 impostor: play on")
        assertEquals(5, result.remainingPlayerIds.size)
    }

    // --- impostor guess -----------------------------------------------------

    @Test
    fun `guessing ignores case accents and punctuation`() {
        val players = roster(5)
        val round = dealOrFail(players, settings()).copy(civilWord = "Étoile de mer")

        assertTrue(GameEngine.isGuessCorrect(round, "etoile de mer"))
        assertTrue(GameEngine.isGuessCorrect(round, "  ÉTOILE-DE-MER "))
        assertTrue(GameEngine.isGuessCorrect(round, "Étoile de mer"))
        assertFalse(GameEngine.isGuessCorrect(round, "etoile de mere"))
        assertFalse(GameEngine.isGuessCorrect(round, ""))
    }

    // --- scoring ------------------------------------------------------------

    @Test
    fun `civilian win pays the civilians only`() {
        val players = roster(5)
        val round = dealOrFail(players, settings(imposters = 1))
        val scored = Scoring.applyOutcome(players, round, RoundOutcome.CIVILS_WIN)

        scored.forEach { player ->
            val expected = if (player.id in round.imposterIds) 0 else Scoring.CIVIL_WIN_POINTS
            assertEquals(expected, player.score, "score for ${player.id}")
        }
    }

    @Test
    fun `impostor win and steal pay the impostors only`() {
        val players = roster(5)
        val round = dealOrFail(players, settings(imposters = 1))

        val won = Scoring.applyOutcome(players, round, RoundOutcome.IMPOSTERS_WIN)
        assertEquals(Scoring.IMPOSTER_WIN_POINTS, won.first { it.id in round.imposterIds }.score)
        assertEquals(0, won.first { it.id !in round.imposterIds }.score)

        val stolen = Scoring.applyOutcome(players, round, RoundOutcome.IMPOSTERS_STEAL)
        assertEquals(Scoring.IMPOSTER_STEAL_POINTS, stolen.first { it.id in round.imposterIds }.score)
    }

    @Test
    fun `scores accumulate across rounds`() {
        val players = roster(4)
        val round = dealOrFail(players, settings(imposters = 1))
        var current = players
        repeat(3) { current = Scoring.applyOutcome(current, round, RoundOutcome.CIVILS_WIN) }
        assertEquals(3 * Scoring.CIVIL_WIN_POINTS, current.first { it.id !in round.imposterIds }.score)
    }
}
