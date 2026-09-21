package com.toftmalone.imposteur.game

import com.toftmalone.imposteur.data.Assignment
import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.data.ImposterMode
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.data.Role
import com.toftmalone.imposteur.data.Round
import com.toftmalone.imposteur.data.RoundOutcome
import com.toftmalone.imposteur.data.WordPack
import kotlin.random.Random

/** Everything that can stop a round from being dealt. */
enum class GameError {
    NOT_ENOUGH_PLAYERS,
    TOO_MANY_PLAYERS,
    TOO_MANY_IMPOSTERS,
    NO_PACK_SELECTED,
    PACK_TOO_SMALL,
}

sealed interface DealResult {
    data class Success(val round: Round) : DealResult
    data class Failure(val error: GameError) : DealResult
}

/** Outcome of voting one player out, and whether that ends the round. */
data class EliminationResult(
    val eliminatedId: String,
    val wasImposter: Boolean,
    /** Null while the round keeps going. */
    val outcome: RoundOutcome?,
    val remainingPlayerIds: List<String>,
    val remainingImposterIds: List<String>,
)

/**
 * Pure game rules: dealing secret words, resolving votes and awarding points.
 * Deliberately free of Android types so it can be unit tested on the JVM.
 */
object GameEngine {

    const val MIN_PLAYERS = 3
    const val MAX_PLAYERS = 20

    /** Impostors must stay a minority, otherwise they win the moment the round starts. */
    fun maxImposters(playerCount: Int): Int =
        ((playerCount - 1) / 2).coerceAtLeast(1)

    fun validate(players: List<Player>, settings: GameSettings, packs: List<WordPack>): GameError? {
        if (players.size < MIN_PLAYERS) return GameError.NOT_ENOUGH_PLAYERS
        if (players.size > MAX_PLAYERS) return GameError.TOO_MANY_PLAYERS
        if (settings.imposterCount < 1 || settings.imposterCount > maxImposters(players.size)) {
            return GameError.TOO_MANY_IMPOSTERS
        }
        val selected = packs.filter { it.id in settings.selectedPackIds }
        if (selected.isEmpty()) return GameError.NO_PACK_SELECTED
        if (selected.none { it.isPlayable }) return GameError.PACK_TOO_SMALL
        return null
    }

    /**
     * Deals one round: picks a pack, a secret word, who the impostors are and
     * what each player gets to see.
     */
    fun deal(
        players: List<Player>,
        settings: GameSettings,
        packs: List<WordPack>,
        random: Random = Random.Default,
    ): DealResult {
        validate(players, settings, packs)?.let { return DealResult.Failure(it) }

        val pack = packs
            .filter { it.id in settings.selectedPackIds && it.isPlayable }
            .random(random)

        val words = pack.words.shuffled(random)
        val civilWord = words[0]
        val imposterWord = words[1]

        val imposterIds = players.map { it.id }.shuffled(random).take(settings.imposterCount).toSet()
        val imposterNames = players.filter { it.id in imposterIds }.map { it.name }

        val assignments = players.map { player ->
            val isImposter = player.id in imposterIds
            val role = if (isImposter) Role.IMPOSTEUR else Role.CIVIL
            Assignment(
                playerId = player.id,
                role = role,
                word = when {
                    !isImposter -> civilWord
                    settings.imposterMode == ImposterMode.DIFFERENT_WORD -> imposterWord
                    else -> null
                },
                // Civilians see the theme only when the option is on. What the impostor
                // learns is decided by the mode alone, so "sans mot" stays truly blind.
                categoryHint = if (!isImposter) {
                    pack.name.takeIf { settings.showCategoryToEveryone }
                } else {
                    when (settings.imposterMode) {
                        ImposterMode.CATEGORY_HINT -> pack.name
                        ImposterMode.DIFFERENT_WORD -> pack.name.takeIf { settings.showCategoryToEveryone }
                        ImposterMode.NO_WORD -> null
                    }
                },
                fellowImposters = if (isImposter && settings.impostersKnowEachOther) {
                    imposterNames.filter { it != player.name }
                } else {
                    emptyList()
                },
            )
        }

        val speakingOrder = if (settings.randomSpeakingOrder) {
            players.map { it.id }.shuffled(random)
        } else {
            players.map { it.id }
        }

        return DealResult.Success(
            Round(
                packId = pack.id,
                packName = pack.name,
                civilWord = civilWord,
                imposterWord = if (settings.imposterMode == ImposterMode.DIFFERENT_WORD) imposterWord else null,
                assignments = assignments,
                speakingOrder = speakingOrder,
            ),
        )
    }

    /**
     * Votes [eliminatedId] out and reports whether the round is over.
     * Civilians win once every impostor is gone; impostors win as soon as they
     * are no longer outnumbered.
     */
    fun eliminate(
        round: Round,
        alivePlayerIds: List<String>,
        eliminatedId: String,
    ): EliminationResult {
        val remaining = alivePlayerIds.filterNot { it == eliminatedId }
        val imposters = round.imposterIds.toSet()
        val remainingImposters = remaining.filter { it in imposters }
        val remainingCivils = remaining.filterNot { it in imposters }

        val outcome = when {
            remainingImposters.isEmpty() -> RoundOutcome.CIVILS_WIN
            remainingImposters.size >= remainingCivils.size -> RoundOutcome.IMPOSTERS_WIN
            else -> null
        }

        return EliminationResult(
            eliminatedId = eliminatedId,
            wasImposter = eliminatedId in imposters,
            outcome = outcome,
            remainingPlayerIds = remaining,
            remainingImposterIds = remainingImposters,
        )
    }

    /** True when the impostor's guess matches the civilians' word. */
    fun isGuessCorrect(round: Round, guess: String): Boolean =
        normalize(guess) == normalize(round.civilWord)

    private fun normalize(value: String): String =
        value.trim()
            .lowercase()
            .replace(Regex("[\\p{Punct}\\s]+"), "")
            .let(::stripAccents)

    private fun stripAccents(value: String): String {
        val from = "àáâãäåçèéêëìíîïñòóôõöùúûüýÿœæ"
        val to = listOf(
            "a", "a", "a", "a", "a", "a", "c", "e", "e", "e", "e", "i", "i", "i", "i",
            "n", "o", "o", "o", "o", "o", "u", "u", "u", "u", "y", "y", "oe", "ae",
        )
        val builder = StringBuilder(value.length)
        for (ch in value) {
            val index = from.indexOf(ch)
            if (index >= 0) builder.append(to[index]) else builder.append(ch)
        }
        return builder.toString()
    }
}

/** Points awarded at the end of a round. */
object Scoring {
    const val CIVIL_WIN_POINTS = 2
    const val IMPOSTER_WIN_POINTS = 3
    const val IMPOSTER_STEAL_POINTS = 2

    fun applyOutcome(players: List<Player>, round: Round, outcome: RoundOutcome): List<Player> {
        val imposters = round.imposterIds.toSet()
        return players.map { player ->
            val isImposter = player.id in imposters
            val gain = when (outcome) {
                RoundOutcome.CIVILS_WIN -> if (isImposter) 0 else CIVIL_WIN_POINTS
                RoundOutcome.IMPOSTERS_WIN -> if (isImposter) IMPOSTER_WIN_POINTS else 0
                RoundOutcome.IMPOSTERS_STEAL -> if (isImposter) IMPOSTER_STEAL_POINTS else 0
            }
            if (gain == 0) player else player.copy(score = player.score + gain)
        }
    }
}
