package com.toftmalone.imposteur.data

/**
 * Players are identified by an avatar index. The artwork itself lives in the UI
 * layer (`ui/components/AvatarArt.kt`) so this stays free of Android resources.
 */
object Avatars {

    /** How many portraits the game can deal out. */
    const val COUNT = 16

    val count: Int get() = COUNT

    /** Default names used when a player row is added without typing a name. */
    val DEFAULT_NAMES: List<String> = listOf(
        "Alex", "Camille", "Sacha", "Jordan", "Louison", "Charlie", "Morgan", "Noa",
        "Eliott", "Inès", "Maël", "Lou", "Robin", "Swan", "Célian", "Ambre",
        "Nael", "Léa", "Tom", "Zoé",
    )
}
