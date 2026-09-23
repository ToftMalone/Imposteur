package com.toftmalone.imposteur.data

/**
 * Who an APK claims to be: its package, its version, and the certificates it
 * is signed with (as lower-case SHA-256 hex of each certificate).
 *
 * Kept free of Android types so the acceptance rules can be unit tested.
 */
data class ApkIdentity(
    val packageName: String,
    val versionCode: Long,
    /** Certificates signing this APK now. */
    val signers: Set<String>,
    /** Certificates this APK proves it rotated from (APK signature v3), current ones included. */
    val signerHistory: Set<String> = signers,
) {
    companion object {
        /**
         * Why [candidate] must not be installed over [installed], or null when it
         * is a genuine, newer Imposteur.
         *
         * Android already refuses an update of the same package signed with
         * another key, but it would happily install a *different* app. This check
         * makes sure the updater only ever offers Imposteur itself, signed by the
         * same developer, and never an older build.
         */
        fun problemWith(installed: ApkIdentity, candidate: ApkIdentity?): String? = when {
            candidate == null ->
                "Le fichier téléchargé n'est pas une application valide. Installation refusée."
            candidate.packageName != installed.packageName ->
                "Le fichier téléchargé n'est pas une mise à jour d'Imposteur. Installation refusée."
            candidate.versionCode <= installed.versionCode ->
                "Le fichier téléchargé n'est pas plus récent que la version installée."
            !sameDeveloper(installed, candidate) ->
                "Le fichier téléchargé n'est pas signé par le développeur d'Imposteur. Installation refusée."
            else -> null
        }

        private fun sameDeveloper(installed: ApkIdentity, candidate: ApkIdentity): Boolean {
            if (installed.signers.isEmpty() || candidate.signers.isEmpty()) return false
            if (candidate.signers == installed.signers) return true
            // Otherwise only a key rotation (APK signature v3), which Android allows
            // for a single signer, from the key installed now.
            return candidate.signers.size == 1 && installed.signers.size == 1 &&
                candidate.signerHistory.containsAll(installed.signers)
        }
    }
}
