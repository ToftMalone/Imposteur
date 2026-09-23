package com.toftmalone.imposteur.data

import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** Where an in-app update download stands, for the update window to show. */
sealed interface UpdateDownload {
    data object Idle : UpdateDownload

    data class Running(val downloadedBytes: Long, val totalBytes: Long) : UpdateDownload {
        /** Null while the size is unknown, so the bar can show indeterminate progress. */
        val fraction: Float?
            get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else null
    }

    data class Ready(val file: File) : UpdateDownload

    data class Failed(val reason: String) : UpdateDownload
}

/** A download that did not produce a file worth installing; the message is for the player. */
class UpdateDownloadException(message: String) : Exception(message)

/**
 * Fetches the APK of a release into the app's private cache, where the system
 * installer reads it through the FileProvider.
 *
 * The file is checked before it is offered: same size as GitHub announced, and
 * the same SHA-256 when GitHub publishes one. Android then refuses on its own
 * an APK signed with another key than the installed app, so a swapped file
 * could not replace Imposteur either way.
 */
class UpdateDownloader(
    private val directory: File,
    private val requireHttps: Boolean = true,
) {

    suspend fun download(
        pkg: UpdatePackage,
        onProgress: (downloadedBytes: Long, totalBytes: Long) -> Unit = { _, _ -> },
    ): File = withContext(Dispatchers.IO) {
        if (requireHttps && !pkg.downloadUrl.startsWith("https://")) {
            throw UpdateDownloadException("Adresse de téléchargement refusée.")
        }

        clearNow()
        directory.mkdirs()
        val target = File(directory, safeFileName(pkg.fileName))
        val partial = File(directory, target.name + ".part")

        var connection: HttpURLConnection? = null
        try {
            connection = (URL(pkg.downloadUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT_MILLIS
                readTimeout = READ_TIMEOUT_MILLIS
                // GitHub answers with a redirect to its file storage.
                instanceFollowRedirects = true
                setRequestProperty("Accept", "application/octet-stream")
                setRequestProperty("User-Agent", "Imposteur-Android")
            }
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                throw UpdateDownloadException("Le serveur a répondu $code. Réessaie dans un moment.")
            }

            val total = connection.contentLengthLong.takeIf { it > 0 } ?: pkg.sizeBytes
            val digest = MessageDigest.getInstance("SHA-256")
            val step = if (total > 0) maxOf(total / 100, 1L) else UNKNOWN_SIZE_STEP
            var downloaded = 0L
            var reported = 0L
            onProgress(0L, total)

            connection.inputStream.use { input ->
                partial.outputStream().use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (true) {
                        ensureActive()
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        digest.update(buffer, 0, read)
                        downloaded += read
                        // About a hundred updates per file, not one per buffer.
                        if (downloaded - reported >= step) {
                            reported = downloaded
                            onProgress(downloaded, total)
                        }
                    }
                }
            }
            onProgress(downloaded, total)

            problemWith(downloaded, digest.digest().toHex(), pkg)?.let { throw UpdateDownloadException(it) }
            if (!partial.renameTo(target)) {
                throw UpdateDownloadException("Impossible d'enregistrer le fichier sur le téléphone.")
            }
            target
        } catch (error: IOException) {
            partial.delete()
            throw UpdateDownloadException("Téléchargement interrompu. Vérifie ta connexion et réessaie.")
        } catch (error: Throwable) {
            // Cancellation and our own messages go through untouched.
            partial.delete()
            throw error
        } finally {
            connection?.disconnect()
        }
    }

    /** Deletes any APK left by an earlier update: installed by now, or stale. */
    suspend fun clear() = withContext(Dispatchers.IO) { clearNow() }

    private fun clearNow() {
        directory.listFiles()?.forEach { it.delete() }
    }

    companion object {
        private const val CONNECT_TIMEOUT_MILLIS = 15_000
        private const val READ_TIMEOUT_MILLIS = 30_000
        private const val BUFFER_SIZE = 16 * 1024
        private const val UNKNOWN_SIZE_STEP = 64L * 1024

        /** Why a finished download must not be installed, or null when it is fine. */
        fun problemWith(downloadedBytes: Long, sha256: String, pkg: UpdatePackage): String? = when {
            downloadedBytes <= 0 -> "Le fichier téléchargé est vide."
            pkg.sizeBytes > 0 && downloadedBytes != pkg.sizeBytes ->
                "Le fichier téléchargé est incomplet. Réessaie."
            pkg.sha256 != null && !pkg.sha256.equals(sha256, ignoreCase = true) ->
                "Le fichier téléchargé ne correspond pas à la version publiée. Réessaie."
            else -> null
        }

        /** The asset name comes from the network: keep it to a plain file name. */
        fun safeFileName(name: String): String {
            val cleaned = name.substringAfterLast('/').substringAfterLast('\\')
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
                .trimStart('.')
            return when {
                cleaned.isEmpty() -> "Imposteur-update.apk"
                cleaned.endsWith(".apk", ignoreCase = true) -> cleaned
                else -> "$cleaned.apk"
            }
        }

        private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    }
}

/** "1,6 Mo": the size as the update window shows it. */
fun formatMegabytes(bytes: Long): String =
    String.format(Locale.FRANCE, "%.1f Mo", bytes.coerceAtLeast(0) / (1024.0 * 1024.0))
