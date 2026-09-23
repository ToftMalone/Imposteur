package com.toftmalone.imposteur.data

/**
 * Compares the version names used by the GitHub releases ("v0.1", "v0.1.1")
 * with the one baked into the build.
 *
 * Kept free of Android types so the comparison rules can be unit tested: an
 * update prompt that fires on the wrong version is worse than none at all.
 */
object AppVersion {

    /**
     * Turns "v0.1.1", "0.1.1" or "1.2.3-beta" into [1, 2, 3]-style numbers.
     * Returns null when nothing numeric can be read, so callers can ignore
     * tags that do not look like versions at all.
     */
    fun parse(raw: String): List<Int>? {
        val cleaned = raw.trim()
            .removePrefix("v")
            .removePrefix("V")
            .substringBefore('-')
            .substringBefore('+')
        if (cleaned.isEmpty()) return null

        val parts = cleaned.split('.').map { part ->
            val digits = part.takeWhile { it.isDigit() }
            if (digits.isEmpty()) return null
            digits.toIntOrNull() ?: return null
        }
        return parts.ifEmpty { null }
    }

    /**
     * True when [candidate] is a strictly higher version than [current].
     * Missing components count as zero, so "0.2" beats "0.1.9" and ties
     * like "0.1" vs "0.1.0" are not treated as updates.
     */
    fun isNewer(candidate: String, current: String): Boolean {
        val a = parse(candidate) ?: return false
        val b = parse(current) ?: return false
        val size = maxOf(a.size, b.size)
        for (i in 0 until size) {
            val left = a.getOrElse(i) { 0 }
            val right = b.getOrElse(i) { 0 }
            if (left != right) return left > right
        }
        return false
    }

    /** True when both name the same version ("0.2" and "v0.2.0", or "0.2.1" and "0.2.1-debug"). */
    fun isSame(a: String, b: String): Boolean =
        parse(a) != null && parse(b) != null && !isNewer(a, b) && !isNewer(b, a)
}
