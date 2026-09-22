package com.toftmalone.imposteur.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/** A release newer than the one running, ready to be offered to the player. */
data class AvailableUpdate(
    val versionName: String,
    val releaseUrl: String,
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

        val release = runCatching { json.decodeFromString(GitHubRelease.serializer(), body) }.getOrNull()
            ?: return@withContext UpdateCheckResult(UpdateCheckOutcome.UNAVAILABLE)

        if (release.draft || release.tagName.isBlank()) {
            return@withContext UpdateCheckResult(UpdateCheckOutcome.UNAVAILABLE)
        }

        if (AppVersion.isNewer(release.tagName, currentVersion)) {
            UpdateCheckResult(
                outcome = UpdateCheckOutcome.UPDATE_AVAILABLE,
                update = AvailableUpdate(
                    versionName = release.tagName.removePrefix("v").removePrefix("V"),
                    releaseUrl = release.htmlUrl.ifBlank { releasesPageUrl() },
                ),
            )
        } else {
            UpdateCheckResult(UpdateCheckOutcome.UP_TO_DATE)
        }
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
    }
}
