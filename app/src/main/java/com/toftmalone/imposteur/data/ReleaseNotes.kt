package com.toftmalone.imposteur.data

/** One piece of the release notes, as the "Nouveautés" windows lay it out. */
sealed interface NotesBlock {
    data class Heading(val text: String) : NotesBlock
    data class Paragraph(val text: String) : NotesBlock

    /** A list item; [marker] is "•" for dashes, or the number ("1.") of an ordered list. */
    data class Bullet(val marker: String, val text: String) : NotesBlock
}

/** A run of text inside a block, with the only two styles the notes use. */
data class NotesSpan(
    val text: String,
    val bold: Boolean = false,
    val code: Boolean = false,
)

/** The notes of a version, ready for the "Quoi de neuf ?" window. */
data class WhatsNew(
    val versionName: String,
    val notes: List<NotesBlock>,
)

/**
 * A small reader for the release notes Markdown: section titles, paragraphs
 * and lists, with **bold** and `code` inside them. Nothing more is used.
 *
 * The same text is the body of the GitHub release, so it is written for
 * GitHub first. The app drops what makes no sense on the phone: the top
 * title (the window has its own), the install steps, and the emoji — the app
 * draws its own images rather than using emoji.
 *
 * Kept free of Android types so the rules can be unit tested.
 */
object ReleaseNotes {

    /** Sections that only make sense on the GitHub page, matched on their title. */
    private val WEB_ONLY_SECTIONS = listOf("installation")

    private val HEADING = Regex("""^(#{1,6})\s+(.*)$""")
    private val BULLET = Regex("""^[-*+]\s+(.*)$""")
    private val NUMBERED = Regex("""^(\d+)[.)]\s+(.*)$""")
    private val RULE = Regex("""^([-*_])(\s*\1){2,}$""")
    private val LINK = Regex("""\[([^\]]+)]\([^)]*\)""")
    private val INLINE = Regex("""\*\*(.+?)\*\*|`([^`]+)`""")
    private val VERSION = Regex("""\d+(?:\.\d+)+""")
    private val SPACES = Regex("""\s+""")

    /** Every block of [markdown], title and install steps included. */
    fun parse(markdown: String): List<NotesBlock> {
        val blocks = mutableListOf<NotesBlock>()
        var paragraph: StringBuilder? = null
        var bulletMarker: String? = null
        var bulletText: StringBuilder? = null

        fun flush() {
            paragraph?.let { text -> clean(text.toString()).takeIf { it.isNotEmpty() }?.let { blocks += NotesBlock.Paragraph(it) } }
            if (bulletMarker != null && bulletText != null) {
                clean(bulletText.toString()).takeIf { it.isNotEmpty() }?.let { blocks += NotesBlock.Bullet(bulletMarker!!, it) }
            }
            paragraph = null
            bulletMarker = null
            bulletText = null
        }

        for (rawLine in markdown.replace("\r\n", "\n").split('\n')) {
            val line = rawLine.trim()
            val heading = HEADING.matchEntire(line)
            val bullet = BULLET.matchEntire(line)
            val numbered = NUMBERED.matchEntire(line)
            when {
                line.isEmpty() -> flush()

                RULE.matches(line) -> flush()

                heading != null -> {
                    flush()
                    // Level 1 is the page title, which the window already shows.
                    val text = clean(heading.groupValues[2])
                    if (heading.groupValues[1].length > 1 && text.isNotEmpty()) {
                        blocks += NotesBlock.Heading(text)
                    }
                }

                bullet != null -> {
                    flush()
                    bulletMarker = "•"
                    bulletText = StringBuilder(bullet.groupValues[1])
                }

                numbered != null -> {
                    flush()
                    bulletMarker = "${numbered.groupValues[1]}."
                    bulletText = StringBuilder(numbered.groupValues[2])
                }

                // Wrapped lines continue whatever block is open.
                bulletText != null -> bulletText!!.append(' ').append(line)
                paragraph != null -> paragraph!!.append(' ').append(line)
                else -> paragraph = StringBuilder(line)
            }
        }
        flush()
        return blocks
    }

    /** The blocks worth showing in the app: [parse] minus the web-only sections. */
    fun forApp(markdown: String): List<NotesBlock> {
        var skipping = false
        return parse(markdown).filter { block ->
            if (block is NotesBlock.Heading) {
                val title = block.text.lowercase()
                skipping = WEB_ONLY_SECTIONS.any { it in title }
            }
            !skipping
        }
    }

    /** The version named in the top title ("# Imposteur 0.2.1" gives "0.2.1"). */
    fun titleVersion(markdown: String): String? {
        val title = markdown.replace("\r\n", "\n").split('\n')
            .map { it.trim() }
            .firstNotNullOfOrNull { line ->
                HEADING.matchEntire(line)?.takeIf { it.groupValues[1].length == 1 }?.groupValues?.get(2)
            }
            ?: return null
        return VERSION.find(title)?.value
    }

    /** Splits [text] into plain, **bold** and `code` runs. */
    fun spans(text: String): List<NotesSpan> {
        val spans = mutableListOf<NotesSpan>()
        var index = 0
        for (match in INLINE.findAll(text)) {
            if (match.range.first > index) spans += NotesSpan(text.substring(index, match.range.first))
            val bold = match.groups[1]
            if (bold != null) {
                spans += NotesSpan(bold.value, bold = true)
            } else {
                spans += NotesSpan(match.groupValues[2], code = true)
            }
            index = match.range.last + 1
        }
        if (index < text.length) spans += NotesSpan(text.substring(index))
        return spans
    }

    /** Link targets, emoji and doubled spaces removed; the words stay. */
    private fun clean(text: String): String {
        val withoutLinks = LINK.replace(text) { it.groupValues[1] }
        val out = StringBuilder(withoutLinks.length)
        var i = 0
        while (i < withoutLinks.length) {
            val codePoint = withoutLinks.codePointAt(i)
            if (!isEmoji(codePoint)) out.appendCodePoint(codePoint)
            i += Character.charCount(codePoint)
        }
        return out.toString().replace(SPACES, " ").trim()
    }

    private fun isEmoji(codePoint: Int): Boolean =
        codePoint in 0x1F000..0x1FAFF || // pictographs, emoticons, transport, flags
            codePoint in 0x2600..0x27BF || // symbols and dingbats (⚠ ✅ ✨ …)
            codePoint in 0x2300..0x23FF || // technical (⏳ ⌛ …)
            codePoint in 0x2B00..0x2BFF || // arrows and stars (⭐ …)
            codePoint in 0xE0020..0xE007F || // tag sequences
            codePoint == 0x2139 || // ℹ
            codePoint == 0xFE0F || // emoji presentation selector
            codePoint == 0x200D || // zero-width joiner
            codePoint == 0x20E3 // keycap
}
