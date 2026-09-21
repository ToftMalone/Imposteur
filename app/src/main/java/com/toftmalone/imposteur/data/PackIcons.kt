package com.toftmalone.imposteur.data

/**
 * Keys for the hand-drawn pack artwork. Packs store a key rather than a
 * drawable id so the data layer stays free of Android resources and custom
 * packs survive being written to disk. The drawables are resolved in
 * `ui/components/PackIconArt.kt`.
 */
object PackIcons {

    val ALL: List<String> = listOf(
        "lion", "globe", "helmet", "burger", "ball", "clapper", "tag", "car",
        "tools", "note", "gamepad", "star", "backpack", "house", "laptop",
        "party", "heart", "shirt", "temple", "theatre",
        "face", "eiffel", "wizard", "weather",
    )

    /** Used for a custom pack that has not picked an icon, or an unknown key. */
    const val DEFAULT: String = "star"

    fun isKnown(key: String): Boolean = key in ALL
}
