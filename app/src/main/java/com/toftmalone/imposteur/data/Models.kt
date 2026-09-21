package com.toftmalone.imposteur.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/** A person sitting around the table. Identity is stable across rounds so scores can accumulate. */
@Serializable
data class Player(
    val id: String,
    val name: String,
    val avatar: Int,
    /**
     * Points for the current session only. Marked transient so a fresh launch
     * always starts everyone back at zero.
     */
    @Transient val score: Int = 0,
)

/** What a player secretly is during a round. */
enum class Role { CIVIL, IMPOSTEUR }

/** How much the impostors are told about the secret word. */
enum class ImposterMode {
    /** Impostors get a *different* word taken from the same pack. */
    DIFFERENT_WORD,

    /** Impostors are told nothing but their role. */
    NO_WORD,

    /** Impostors only learn which pack the word came from. */
    CATEGORY_HINT,
}

/**
 * Two words close enough that describing one sounds a lot like describing the
 * other: "Pizza" and "Quiche", "Lion" and "Tigre". Neither side is fixed as the
 * civilians' word -- the deal picks a direction each round.
 */
@Serializable
data class WordPair(
    val first: String,
    val second: String,
) {
    val isComplete: Boolean
        get() = first.isNotBlank() && second.isNotBlank() && !first.equals(second, ignoreCase = true)
}

/** A themed list of word pairs. Built-in packs ship with the app; custom ones are user made. */
@Serializable
data class WordPack(
    val id: String,
    val name: String,
    /** A [PackIcons] key naming the artwork to draw for this pack. */
    val icon: String = PackIcons.DEFAULT,
    val pairs: List<WordPair> = emptyList(),
    val isCustom: Boolean = false,
) {
    /** A pack needs at least one usable pair to be dealt from. */
    val isPlayable: Boolean get() = pairs.any { it.isComplete }

    /** How many words the pack contributes, counting both sides of every pair. */
    val wordCount: Int get() = pairs.size * 2
}

/** Everything the player can tune before starting a game. Persisted between sessions. */
@Serializable
data class GameSettings(
    val imposterCount: Int = 1,
    val imposterMode: ImposterMode = ImposterMode.DIFFERENT_WORD,
    val showCategoryToEveryone: Boolean = true,
    val impostersKnowEachOther: Boolean = false,
    val randomSpeakingOrder: Boolean = true,
    val allowImposterGuess: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val selectedPackIds: Set<String> = DEFAULT_SELECTED_PACKS,
) {
    companion object {
        val DEFAULT_SELECTED_PACKS = setOf("animaux", "lieux", "metiers", "nourriture")
    }
}

/** The secret handed to one player for one round. */
@Serializable
data class Assignment(
    val playerId: String,
    val role: Role,
    /** The word shown to this player, or null when the impostor plays blind. */
    val word: String?,
    /** Pack name shown as a hint, or null when hidden. */
    val categoryHint: String?,
    /** Names of the other impostors, when impostors know each other. */
    val fellowImposters: List<String> = emptyList(),
)

/** One full round: who got what, plus the order people speak in. */
@Serializable
data class Round(
    val packId: String,
    val packName: String,
    val civilWord: String,
    val imposterWord: String?,
    val assignments: List<Assignment>,
    val speakingOrder: List<String>,
) {
    fun assignmentFor(playerId: String): Assignment =
        assignments.first { it.playerId == playerId }

    val imposterIds: List<String>
        get() = assignments.filter { it.role == Role.IMPOSTEUR }.map { it.playerId }
}

/** Why a round ended, which decides who scores. */
enum class RoundOutcome {
    /** Every impostor was voted out. */
    CIVILS_WIN,

    /** Impostors reached parity with the civilians. */
    IMPOSTERS_WIN,

    /** A caught impostor guessed the secret word and stole the round. */
    IMPOSTERS_STEAL,
}
