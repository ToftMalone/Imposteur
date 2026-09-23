package com.toftmalone.imposteur

import com.toftmalone.imposteur.data.ApkIdentity
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApkIdentityTest {

    private val ourKey = "367da3328035b46efced81f648aea487eb03b420e9ad9ad85c4edde4e2379c53"
    private val otherKey = "0".repeat(64)

    private val installed = ApkIdentity("com.toftmalone.imposteur", 6, setOf(ourKey))

    private fun candidate(
        packageName: String = "com.toftmalone.imposteur",
        versionCode: Long = 7,
        signers: Set<String> = setOf(ourKey),
        history: Set<String> = signers,
    ) = ApkIdentity(packageName, versionCode, signers, history)

    @Test
    fun `a newer Imposteur signed with the same key is accepted`() {
        assertNull(ApkIdentity.problemWith(installed, candidate()))
    }

    @Test
    fun `another app is refused, whatever its signature`() {
        assertNotNull(ApkIdentity.problemWith(installed, candidate(packageName = "com.evil.app")))
        assertNotNull(ApkIdentity.problemWith(installed, candidate(packageName = "com.evil.app", signers = setOf(otherKey))))
        // The debug build is a different package: it must not take the release APK.
        assertNotNull(ApkIdentity.problemWith(installed.copy(packageName = "com.toftmalone.imposteur.debug"), candidate()))
    }

    @Test
    fun `Imposteur signed with another key is refused`() {
        assertNotNull(ApkIdentity.problemWith(installed, candidate(signers = setOf(otherKey))))
        assertNotNull(ApkIdentity.problemWith(installed, candidate(signers = setOf(ourKey, otherKey))))
    }

    @Test
    fun `the same or an older version is refused`() {
        assertNotNull(ApkIdentity.problemWith(installed, candidate(versionCode = 6)))
        assertNotNull(ApkIdentity.problemWith(installed, candidate(versionCode = 5)))
    }

    @Test
    fun `unreadable or unsigned files are refused`() {
        assertNotNull(ApkIdentity.problemWith(installed, null))
        assertNotNull(ApkIdentity.problemWith(installed, candidate(signers = emptySet())))
        assertNotNull(ApkIdentity.problemWith(installed.copy(signers = emptySet()), candidate(signers = emptySet())))
    }

    @Test
    fun `a new key is accepted only with a signed rotation from ours`() {
        assertNull(ApkIdentity.problemWith(installed, candidate(signers = setOf(otherKey), history = setOf(ourKey, otherKey))))
        assertNotNull(ApkIdentity.problemWith(installed, candidate(signers = setOf(otherKey), history = setOf(otherKey))))
    }
}
