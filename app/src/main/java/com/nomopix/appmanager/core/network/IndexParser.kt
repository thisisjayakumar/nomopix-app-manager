package com.nomopix.appmanager.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class GitHubRepoRef(
    val owner: String,
    val repo: String,
    val rawUrl: String
)

@Singleton
class IndexParser @Inject constructor() {

    companion object {
        const val DEFAULT_INDEX_URL = "https://raw.githubusercontent.com/nomopix/index/main/index.txt"
        
        val PRESET_REPOS = listOf(
            GitHubRepoRef("thisisjayakumar", "co-stream-songs", "https://github.com/thisisjayakumar/co-stream-songs"),
            GitHubRepoRef("nomopix", "app-manager", "https://github.com/nomopix/app-manager")
        )
    }

    suspend fun fetchIndex(indexUrl: String): List<GitHubRepoRef> = withContext(Dispatchers.IO) {
        val repoRefs = mutableListOf<GitHubRepoRef>()
        try {
            Timber.i("Fetching index file from: $indexUrl")
            val url = URL(indexUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "NomopixAppManager/1.0")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { parseLine(it)?.let { ref -> repoRefs.add(ref) } }
                    }
                }
            } else {
                Timber.w("Index URL response code ${connection.responseCode}. Falling back to default preset index.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch index.txt from $indexUrl. Falling back to default preset repos.")
        }

        if (repoRefs.isEmpty()) {
            repoRefs.addAll(PRESET_REPOS)
        }

        repoRefs.distinctBy { "${it.owner}/${it.repo}" }
    }

    private fun parseLine(line: String): GitHubRepoRef? {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#")) return null

        // Format 1: https://github.com/owner/repo or https://github.com/owner/repo.git
        if (trimmed.contains("github.com/")) {
            val parts = trimmed.substringAfter("github.com/").removeSuffix(".git").split("/")
            if (parts.size >= 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
                return GitHubRepoRef(owner = parts[0], repo = parts[1], rawUrl = trimmed)
            }
        }

        // Format 2: owner/repo
        if (trimmed.contains("/") && !trimmed.contains("://")) {
            val parts = trimmed.split("/")
            if (parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
                return GitHubRepoRef(owner = parts[0], repo = parts[1], rawUrl = "https://github.com/${parts[0]}/${parts[1]}")
            }
        }

        return null
    }
}
