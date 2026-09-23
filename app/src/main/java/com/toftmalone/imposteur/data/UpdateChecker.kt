package com.toftmalone.imposteur.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/** The installable file attached to a release. */
data class UpdatePackage(
    val fileName: String,
    val downloadUrl: String,
    val sizeBytes: Long,
    /** Lower-case hex SHA-256 published by GitHub, when it provides one. */
    val sha256: String?,
)

/** A release newer than the one running, ready to be offered to the player. */
data class AvailableUpdate(
    val versionName: String,
    val releaseUrl: String,
    /** The release notes as published, in Markdown. */
    val notes: String = "",
    /** Null when the release carries no APK, so it cannot be installed from the app. */
    val apk: UpdatePackage? = null,
)

/** Why a check produced no update, so the UI can say something useful. */
enum class UpdateCheckOutcome {
    /** A newer release exists. */
    UPDATE_AVAILABLE,

    /** The check worked and this build is current. */
    UP_TO_DATE,

    /** Nothing could be read: offline, rate limited, or the repo is private. */
    UNAVAILABLE,
}

data class UpdateCheckResult(
    val outcome: UpdateCheckOutcome,
    val update: AvailableUpdate? = null,
)

@Serializable
private data class GitHubRelease(
    @SerialName("tag_name") val tagName: String = "",
    @SerialName("html_url") val htmlUrl: String = "",
    val draft: Boolean = false,
    val prerelease: Boolean = false,
    val body: String? = null,
    val assets: List<GitHubAsset> = emptyList(),
)

@Serializable
private data class GitHubAsset(
    val name: String = "",
    @SerialName("browser_download_url") val downloadUrl: String = "",
    val size: Long = 0,
    /** "sha256:<hex>", computed by GitHub on upload. */
    val digest: String? = null,
)

/**
 * Asks GitHub for the newest published release and compares it with the build
 * that is running.
 *
 * Note: this reads the *public* releases endpoint with no credentials. A
 * private repository answers 404 to an anonymous request, which lands on
 * [UpdateCheckOutcome.UNAVAILABLE] — the app never ships a token, since
 * anything baked into an APK can be extracted from it.
 */
class UpdateChecker(
    private val currentVersion: String,
    private val repository: String = DEFAULT_REPOSITORY,
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun check(): UpdateCheckResult = withContext(Dispatchers.IO) {
        val body = fetchLatestRelease() ?: return@withContext UpdateCheckResult(UpdateCheckOutcome.UNAVAILABLE)
        interpret(body)
    }

    /** Reads the API answer. Apart from the network so it can be unit tested. */
    fun interpret(body: String): UpdateCheckResult {
        val release = runCatching { json.decodeFromString(GitHubRelease.serializer(), body) }.getOrNull()
            ?: return UpdateCheckResult(UpdateCheckOutcome.UNAVAILABLE)

        if (release.draft || release.tagName.isBlank()) {
            return UpdateCheckResult(UpdateCheckOutcome.UNAVAILABLE)
        }

        if (!AppVersion.isNewer(release.tagName, currentVersion)) {
            return UpdateCheckResult(UpdateCheckOutcome.UP_TO_DATE)
        }

        val apk = release.assets
            .firstOrNull { it.name.endsWith(".apk", ignoreCase = true) && it.downloadUrl.startsWith("https://") }
            ?.let { asset ->
                UpdatePackage(
                    fileName = asset.name,
                    downloadUrl = asset.downloadUrl,
                    sizeBytes = asset.size,
                    sha256 = asset.digest
                        ?.takeIf { it.startsWith("sha256:", ignoreCase = true) }
                        ?.substringAfter(':')
                        ?.lowercase()
                        ?.takeIf { SHA256_HEX.matches(it) },
                )
            }

        return UpdateCheckResult(
            outcome = UpdateCheckOutcome.UPDATE_AVAILABLE,
            update = AvailableUpdate(
                versionName = release.tagName.removePrefix("v").removePrefix("V"),
                releaseUrl = release.htmlUrl.ifBlank { releasesPageUrl() },
                notes = release.body.orEmpty(),
                apk = apk,
            ),
        )
    }

    fun releasesPageUrl(): String = "https://github.com/$repository/releases"

    private fun fetchLatestRelease(): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL("https://api.github.com/repos/$repository/releases/latest")
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MILLIS
                readTimeout = TIMEOUT_MILLIS
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "Imposteur-Android")
            }
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                null
            } else {
                connection.inputStream.bufferedReader().use { it.readText() }
            }
        } catch (error: Exception) {
            // Offline, DNS failure, timeout: a missed check must never be
            // louder than the game itself.
            null
        } finally {
            connection?.disconnect()
        }
    }

    companion object {
        const val DEFAULT_REPOSITORY = "ToftMalone/Imposteur"
        private const val TIMEOUT_MILLIS = 8_000
        private val SHA256_HEX = Regex("^[0-9a-f]{64}$")
    }
}
