package com.toftmalone.imposteur.data

/**
 * Player avatars are emoji rendered large on a coloured gradient, which keeps
 * the app asset free while still giving each player a recognisable face.
 */
object Avatars {

    val ALL: List<String> = listOf(
        "🕵️", // detective
        "💃", // dancer
        "🧑‍🎤", // singer
        "👩‍🚀", // astronaut
        "🧙", // mage
        "🦸", // superhero
        "🧛", // vampire
        "🤠", // cowboy
        "👨‍🍳", // cook
        "👮", // police officer
        "👷", // construction worker
        "🧑‍⚕️", // health worker
        "🥷", // ninja
        "🧝", // elf
        "🧞", // genie
        "🤡", // clown
        "👽", // alien
        "🤖", // robot
        "🐵", // monkey
        "🦊", // fox
        "🐼", // panda
        "🦁", // lion
        "🐸", // frog
        "🐙", // octopus
    )

    val count: Int get() = ALL.size

    fun emojiAt(index: Int): String = ALL[((index % count) + count) % count]

    /** Default names used when a player row is added without typing a name. */
    val DEFAULT_NAMES: List<String> = listOf(
        "Alex", "Camille", "Sacha", "Jordan", "Louison", "Charlie", "Morgan", "Noa",
        "Eliott", "Inès", "Maël", "Lou", "Robin", "Swan", "Célian", "Ambre",
        "Nael", "Léa", "Tom", "Zoé",
    )
}
