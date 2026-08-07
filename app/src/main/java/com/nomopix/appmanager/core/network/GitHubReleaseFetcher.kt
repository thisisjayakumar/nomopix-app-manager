package com.nomopix.appmanager.core.network

import com.nomopix.appmanager.core.model.AppRelease
import com.nomopix.appmanager.core.model.NomopixApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubReleaseFetcher @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchAppInfo(repoRef: GitHubRepoRef): NomopixApp? = withContext(Dispatchers.IO) {
        try {
            val owner = repoRef.owner
            val repo = repoRef.repo

            // 1. Fetch Repository Metadata
            val repoApiUrl = "https://api.github.com/repos/$owner/$repo"
            val repoJsonStr = httpGet(repoApiUrl) ?: return@withContext null
            val repoObj = json.parseToJsonElement(repoJsonStr).jsonObject

            val description = repoObj["description"]?.jsonPrimitive?.content ?: "No description provided."
            val iconUrl = repoObj["owner"]?.jsonObject?.get("avatar_url")?.jsonPrimitive?.content
                ?: "https://github.com/$owner.png"

            val appTitle = repoObj["name"]?.jsonPrimitive?.content?.replace("-", " ")?.capitalizeWords() ?: repo

            // 2. Fetch Releases List
            val releasesApiUrl = "https://api.github.com/repos/$owner/$repo/releases"
            val releasesJsonStr = httpGet(releasesApiUrl) ?: ""
            
            var latestRelease: AppRelease? = null
            var previousRelease: AppRelease? = null

            if (releasesJsonStr.isNotBlank()) {
                val releasesArr = json.parseToJsonElement(releasesJsonStr).jsonArray
                if (releasesArr.isNotEmpty()) {
                    latestRelease = parseReleaseObject(releasesArr[0].jsonObject)
                }
                if (releasesArr.size > 1) {
                    previousRelease = parseReleaseObject(releasesArr[1].jsonObject)
                }
            }

            // Derive standard package name or fallback
            val packageName = when (repo.lowercase()) {
                "co-stream-songs", "co-stream", "syncplay" -> "com.syncplay.app"
                "app-manager", "nomopix-app-manager" -> "com.nomopix.appmanager"
                else -> "com.$owner.${repo.replace("-", "").lowercase()}"
            }

            NomopixApp(
                id = "$owner/$repo",
                name = appTitle,
                owner = owner,
                repoName = repo,
                repoUrl = repoRef.rawUrl,
                description = description,
                iconUrl = iconUrl,
                packageName = packageName,
                latestRelease = latestRelease,
                previousRelease = previousRelease
            )
        } catch (e: Exception) {
            Timber.e(e, "Error fetching GitHub app info for ${repoRef.owner}/${repoRef.repo}")
            null
        }
    }

    private fun parseReleaseObject(releaseObj: JsonObject): AppRelease? {
        val tag = releaseObj["tag_name"]?.jsonPrimitive?.content ?: "v1.0.0"
        val name = releaseObj["name"]?.jsonPrimitive?.content?.ifBlank { tag } ?: tag
        val publishedAt = releaseObj["published_at"]?.jsonPrimitive?.content?.take(10) ?: "Recent"
        val body = releaseObj["body"]?.jsonPrimitive?.content ?: "Release binaries"

        var apkName = ""
        var apkUrl = ""
        var apkSize = 0L

        val assets = releaseObj["assets"]?.jsonArray
        if (assets != null && assets.isNotEmpty()) {
            for (asset in assets) {
                val assetObj = asset.jsonObject
                val fileName = assetObj["name"]?.jsonPrimitive?.content ?: ""
                val downloadUrl = assetObj["browser_download_url"]?.jsonPrimitive?.content ?: ""
                val size = assetObj["size"]?.jsonPrimitive?.longOrNull ?: 0L

                if (fileName.endsWith(".apk", ignoreCase = true)) {
                    apkName = fileName
                    apkUrl = downloadUrl
                    apkSize = size
                    break
                }
            }
        }

        // If no apk asset found in release assets, provide fallback GitHub release asset link format
        if (apkUrl.isBlank()) {
            val ownerRepo = releaseObj["html_url"]?.jsonPrimitive?.content?.substringAfter("github.com/")?.substringBefore("/releases") ?: ""
            if (ownerRepo.isNotBlank()) {
                val repoNameOnly = ownerRepo.substringAfter("/")
                apkName = "$repoNameOnly-$tag.apk"
                apkUrl = "https://github.com/$ownerRepo/releases/download/$tag/$apkName"
            }
        }

        return AppRelease(
            tag = tag,
            name = name,
            publishedAt = publishedAt,
            body = body,
            apkName = apkName,
            apkDownloadUrl = apkUrl,
            apkSizeBytes = apkSize
        )
    }

    private fun httpGet(urlString: String): String? {
        return try {
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "NomopixAppManager/1.0")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
            }
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
            } else {
                Timber.w("GitHub API HTTP ${connection.responseCode} for $urlString")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Http GET error for $urlString")
            null
        }
    }

    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
