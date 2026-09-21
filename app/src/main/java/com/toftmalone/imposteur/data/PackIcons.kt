package com.toftmalone.imposteur.data

/**
 * Keys for the hand-drawn pack artwork. Packs name a key rather than a drawable
 * id so this layer stays free of Android resources; the drawables are resolved
 * in `ui/components/PackIconArt.kt`. [ALL] is the list a pack may pick from,
 * and the pack tests enforce that every built-in pack uses a known key.
 */
object PackIcons {

    val ALL: List<String> = listOf(
        "lion", "globe", "helmet", "burger", "ball", "clapper", "tag", "car",
        "tools", "note", "gamepad", "star", "backpack", "house", "laptop",
        "party", "heart", "shirt", "temple", "theatre",
        "face", "eiffel", "wizard", "weather",
    )

    /** Fallback for a pack whose key does not match any artwork. */
    const val DEFAULT: String = "star"

    fun isKnown(key: String): Boolean = key in ALL
}
