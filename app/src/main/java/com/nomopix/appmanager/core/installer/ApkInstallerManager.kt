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

    fun isSameVersionInstalled(installedVersionName: String?, releaseTag: String): Boolean {
        if (installedVersionName.isNullOrBlank() || releaseTag.isBlank()) return false
        return compareVersions(installedVersionName, releaseTag) == 0
    }

    fun evaluateInstallStatus(app: NomopixApp): InstallStatus {
        val info = getInstalledAppInfo(app.packageName)
        if (!info.isInstalled) return InstallStatus.NOT_INSTALLED

        val latestTag = app.latestRelease?.tag ?: ""
        val installedVer = info.versionName ?: ""

        if (installedVer.isBlank() || latestTag.isBlank()) {
            return InstallStatus.INSTALLED_UP_TO_DATE
        }

        val comparison = compareVersions(installedVer, latestTag)
        return when {
            comparison < 0 -> InstallStatus.UPDATE_AVAILABLE
            comparison == 0 -> InstallStatus.INSTALLED_UP_TO_DATE
            else -> InstallStatus.OLDER_VERSION_INSTALLED
        }
    }

    fun openDownloadUrlInBrowser(url: String) {
        if (url.isBlank()) {
            Timber.w("Cannot open browser: download URL is blank.")
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Timber.i("Opened direct download URL in browser: $url")
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch browser intent for URL: $url")
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


    fun compareVersions(v1: String, v2: String): Int {
        if (v1.isBlank() && v2.isBlank()) return 0
        if (v1.isBlank()) return -1
        if (v2.isBlank()) return 1

        val clean1 = v1.trim().removePrefix("v").removePrefix("V")
        val clean2 = v2.trim().removePrefix("v").removePrefix("V")

        val parts1 = clean1.split(Regex("[.-]")).mapNotNull { Regex("^\\d+").find(it)?.value?.toIntOrNull() }
        val parts2 = clean2.split(Regex("[.-]")).mapNotNull { Regex("^\\d+").find(it)?.value?.toIntOrNull() }

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
