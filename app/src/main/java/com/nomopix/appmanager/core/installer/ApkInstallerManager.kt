package com.nomopix.appmanager.core.installer

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.nomopix.appmanager.core.model.AppDownloadProgress
import com.nomopix.appmanager.core.model.AppRelease
import com.nomopix.appmanager.core.model.InstallStatus
import com.nomopix.appmanager.core.model.NomopixApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkInstallerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    data class InstalledAppInfo(
        val isInstalled: Boolean,
        val versionName: String? = null,
        val versionCode: Long = 0L
    )

    fun getInstalledAppInfo(packageName: String): InstalledAppInfo {
        return try {
            val pm = context.packageManager
            val pInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }

            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode.toLong()
            }

            InstalledAppInfo(
                isInstalled = true,
                versionName = pInfo.versionName,
                versionCode = versionCode
            )
        } catch (e: PackageManager.NameNotFoundException) {
            InstalledAppInfo(isInstalled = false)
        } catch (e: Exception) {
            Timber.e(e, "Error querying package info for $packageName")
            InstalledAppInfo(isInstalled = false)
        }
    }

    fun evaluateInstallStatus(app: NomopixApp): InstallStatus {
        val info = getInstalledAppInfo(app.packageName)
        if (!info.isInstalled) return InstallStatus.NOT_INSTALLED

        val latestTag = app.latestRelease?.tag?.removePrefix("v") ?: ""
        val installedVer = info.versionName?.removePrefix("v") ?: ""

        if (installedVer.isBlank() || latestTag.isBlank()) {
            return InstallStatus.INSTALLED_UP_TO_DATE
        }

        return when {
            installedVer.equals(latestTag, ignoreCase = true) -> InstallStatus.INSTALLED_UP_TO_DATE
            compareVersions(installedVer, latestTag) < 0 -> InstallStatus.UPDATE_AVAILABLE
            else -> InstallStatus.OLDER_VERSION_INSTALLED
        }
    }

    fun downloadApk(release: AppRelease): Flow<AppDownloadProgress> = flow {
        emit(AppDownloadProgress(isDownloading = true, progressFloat = 0.05f, statusMessage = "Connecting..."))

        val apkDir = File(context.cacheDir, "apk_downloads").apply { mkdirs() }
        val targetFile = File(apkDir, release.apkName.ifBlank { "app-${release.tag}.apk" })

        try {
            val url = URL(release.apkDownloadUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 15000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "NomopixAppManager/1.0")
            }

            val totalBytes = if (connection.contentLengthLong > 0) connection.contentLengthLong else release.apkSizeBytes
            var downloadedBytes = 0L

            connection.inputStream.use { input ->
                FileOutputStream(targetFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var lastReportTime = System.currentTimeMillis()

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastReportTime > 200 || downloadedBytes == totalBytes) {
                            lastReportTime = currentTime
                            val progress = if (totalBytes > 0) downloadedBytes.toFloat() / totalBytes.toFloat() else 0.5f
                            emit(
                                AppDownloadProgress(
                                    isDownloading = true,
                                    progressFloat = progress.coerceIn(0f, 0.99f),
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes,
                                    statusMessage = "Downloading ${downloadedBytes / (1024 * 1024)}MB / ${totalBytes / (1024 * 1024)}MB"
                                )
                            )
                        }
                    }
                }
            }

            emit(AppDownloadProgress(isDownloading = false, progressFloat = 1.0f, downloadedBytes = totalBytes, totalBytes = totalBytes, statusMessage = "Download Complete! Ready to install."))
            Timber.i("APK downloaded successfully to ${targetFile.absolutePath}")
            
            // Trigger APK Installation prompt automatically
            installApkFile(targetFile)

        } catch (e: Exception) {
            Timber.e(e, "Error downloading APK from ${release.apkDownloadUrl}")
            emit(AppDownloadProgress(isDownloading = false, progressFloat = 0f, statusMessage = "Download Failed: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    fun installApkFile(apkFile: File) {
        try {
            if (!apkFile.exists()) {
                Timber.e("Cannot install APK: file does not exist at ${apkFile.absolutePath}")
                return
            }

            val authority = "${context.packageName}.fileprovider"
            val apkUri: Uri = FileProvider.getUriForFile(context, authority, apkFile)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Timber.i("Launched Android PackageInstaller intent for ${apkFile.name}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch APK installation intent")
        }
    }

    fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Timber.i("Launched uninstallation intent for package: $packageName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch uninstallation intent for $packageName")
        }
    }

    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.split(".").mapNotNull { it.toIntOrNull() }
        val parts2 = v2.split(".").mapNotNull { it.toIntOrNull() }
        val maxLen = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxLen) {
            val num1 = parts1.getOrElse(i) { 0 }
            val num2 = parts2.getOrElse(i) { 0 }
            if (num1 != num2) {
                return num1.compareTo(num2)
            }
        }
        return 0
    }
}
