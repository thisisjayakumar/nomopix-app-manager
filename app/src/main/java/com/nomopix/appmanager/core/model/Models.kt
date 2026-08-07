package com.nomopix.appmanager.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AppRelease(
    val tag: String,
    val name: String,
    val publishedAt: String,
    val body: String,
    val apkName: String,
    val apkDownloadUrl: String,
    val apkSizeBytes: Long
)

enum class InstallStatus {
    NOT_INSTALLED,
    INSTALLED_UP_TO_DATE,
    UPDATE_AVAILABLE,
    OLDER_VERSION_INSTALLED
}

data class AppDownloadProgress(
    val isDownloading: Boolean = false,
    val progressFloat: Float = 0f,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val statusMessage: String = ""
)

data class NomopixApp(
    val id: String, // e.g. "thisisjayakumar/co-stream-songs"
    val name: String, // e.g. "SyncPlay"
    val owner: String, // e.g. "thisisjayakumar"
    val repoName: String, // e.g. "co-stream-songs"
    val repoUrl: String, // e.g. "https://github.com/thisisjayakumar/co-stream-songs"
    val description: String,
    val iconUrl: String,
    val packageName: String, // e.g. "com.syncplay.app"
    val latestRelease: AppRelease?,
    val previousRelease: AppRelease?,
    val installedVersionName: String? = null,
    val installedVersionCode: Long = 0L,
    val installStatus: InstallStatus = InstallStatus.NOT_INSTALLED,
    val downloadProgress: AppDownloadProgress = AppDownloadProgress()
)
