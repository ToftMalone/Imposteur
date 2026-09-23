package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.AppVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppVersionTest {

    @Test
    fun `parses the tag shapes the releases actually use`() {
        assertEquals(listOf(0, 1), AppVersion.parse("v0.1"))
        assertEquals(listOf(0, 1, 1), AppVersion.parse("v0.1.1"))
        assertEquals(listOf(0, 1, 1), AppVersion.parse("0.1.1"))
        assertEquals(listOf(1, 0, 0), AppVersion.parse("V1.0.0"))
        assertEquals(listOf(2, 3), AppVersion.parse("  v2.3  "))
        assertEquals(listOf(1, 2, 3), AppVersion.parse("1.2.3-beta"))
    }

    @Test
    fun `refuses tags that are not versions`() {
        assertNull(AppVersion.parse(""))
        assertNull(AppVersion.parse("v"))
        assertNull(AppVersion.parse("latest"))
        assertNull(AppVersion.parse("release-candidate"))
    }

    @Test
    fun `detects a newer version`() {
        assertTrue(AppVersion.isNewer("v0.1.1", "0.1"))
        assertTrue(AppVersion.isNewer("v0.2", "0.1.9"))
        assertTrue(AppVersion.isNewer("v1.0", "0.9.9"))
        assertTrue(AppVersion.isNewer("v0.10", "0.9"))
    }

    @Test
    fun `an equal version is not an update`() {
        assertFalse(AppVersion.isNewer("v0.1", "0.1"))
        // Trailing zeros must not look like a new release.
        assertFalse(AppVersion.isNewer("v0.1.0", "0.1"))
        assertFalse(AppVersion.isNewer("v0.1", "0.1.0"))
    }

    @Test
    fun `an older version never prompts`() {
        assertFalse(AppVersion.isNewer("v0.1", "0.1.1"))
        assertFalse(AppVersion.isNewer("v0.9", "1.0"))
        assertFalse(AppVersion.isNewer("v1.9.9", "2.0"))
    }

    @Test
    fun `unreadable input never prompts`() {
        assertFalse(AppVersion.isNewer("latest", "0.1"))
        assertFalse(AppVersion.isNewer("v0.2", "inconnue"))
        assertFalse(AppVersion.isNewer("", ""))
    }

    @Test
    fun `same version whatever the spelling`() {
        assertTrue(AppVersion.isSame("0.2", "v0.2.0"))
        assertTrue(AppVersion.isSame("0.2.1", "0.2.1-debug"))
        assertFalse(AppVersion.isSame("0.2.1", "0.2"))
        assertFalse(AppVersion.isSame("latest", "latest"))
    }
}
