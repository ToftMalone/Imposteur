package com.toftmalone.imposteur.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import java.io.File
import java.security.MessageDigest

/**
 * Reads the identity of the installed app and of a downloaded APK, so the
 * updater can refuse anything that is not a newer Imposteur signed with the
 * same key (see [ApkIdentity.problemWith]).
 *
 * Reading an archive's certificates makes Android verify its signature, and
 * the system installer checks it again when installing.
 */
object ApkVerifier {

    /** Why [apk] must not be installed, or null when it is a genuine update. */
    fun problemWith(context: Context, apk: File): String? {
        val pm = context.packageManager
        val installed = runCatching { identity(installedInfo(pm, context.packageName)) }.getOrNull()
            ?: return "Impossible de vérifier la signature d'Imposteur. Installation refusée."
        val candidate = runCatching { archiveInfo(pm, apk.path)?.let(::identity) }.getOrNull()
        return ApkIdentity.problemWith(installed, candidate)
    }

    private fun identity(info: PackageInfo): ApkIdentity? {
        val (current, history) = signers(info) ?: return null
        return ApkIdentity(
            packageName = info.packageName ?: return null,
            versionCode = PackageInfoCompat.getLongVersionCode(info),
            signers = current,
            signerHistory = history,
        )
    }

    @Suppress("DEPRECATION")
    private fun signers(info: PackageInfo): Pair<Set<String>, Set<String>>? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signing = info.signingInfo ?: return null
            val current = signing.apkContentsSigners.orEmpty().map(::fingerprint).toSet()
            val history = if (signing.hasMultipleSigners()) {
                current
            } else {
                signing.signingCertificateHistory.orEmpty().map(::fingerprint).toSet() + current
            }
            return current to history
        }
        val current = info.signatures.orEmpty().map(::fingerprint).toSet()
        return current to current
    }

    @Suppress("DEPRECATION")
    private fun installedInfo(pm: PackageManager, packageName: String): PackageInfo {
        val flags = signingFlag()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            pm.getPackageInfo(packageName, flags)
        }
    }

    @Suppress("DEPRECATION")
    private fun archiveInfo(pm: PackageManager, path: String): PackageInfo? {
        val flags = signingFlag()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageArchiveInfo(path, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            pm.getPackageArchiveInfo(path, flags)
        }
    }

    @Suppress("DEPRECATION")
    private fun signingFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            PackageManager.GET_SIGNATURES
        }

    private fun fingerprint(signature: Signature): String =
        MessageDigest.getInstance("SHA-256").digest(signature.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
