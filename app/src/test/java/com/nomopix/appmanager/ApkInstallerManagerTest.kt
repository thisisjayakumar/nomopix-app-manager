package com.nomopix.appmanager

import android.content.Context
import com.nomopix.appmanager.core.installer.ApkInstallerManager
import com.nomopix.appmanager.core.model.AppRelease
import com.nomopix.appmanager.core.model.NomopixApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class ApkInstallerManagerTest {

    private lateinit var installerManager: ApkInstallerManager
    private val mockContext: Context = mock(Context::class.java)

    @Before
    fun setUp() {
        installerManager = ApkInstallerManager(mockContext)
    }

    @Test
    fun testCompareVersionsOlderInstalled() {
        // v1.0.0 < v1.1.0 -> should return negative (< 0)
        val result = installerManager.compareVersions("1.0.0", "1.1.0")
        assertTrue(result < 0)
    }

    @Test
    fun testCompareVersionsWithVPrefix() {
        // v1.0.0 vs v1.2.0 -> should return negative
        val result = installerManager.compareVersions("v1.0.0", "v1.2.0")
        assertTrue(result < 0)
    }

    @Test
    fun testCompareVersionsEqual() {
        // v1.2.3 vs 1.2.3 -> should return 0
        val result = installerManager.compareVersions("v1.2.3", "1.2.3")
        assertEquals(0, result)
    }

    @Test
    fun testCompareVersionsMultiDigit() {
        // 1.9.0 vs 1.10.0 -> should return negative
        val result = installerManager.compareVersions("1.9.0", "1.10.0")
        assertTrue(result < 0)
    }

    @Test
    fun testIsSameVersionInstalled() {
        assertTrue(installerManager.isSameVersionInstalled("v1.0.0", "1.0.0"))
        assertTrue(installerManager.isSameVersionInstalled("1.2.3-release", "v1.2.3"))
        assertFalse(installerManager.isSameVersionInstalled("v1.0.0", "v1.1.0"))
    }

    @Test
    fun testEvaluateInstallStatusUpdateAvailable() {
        val app = NomopixApp(
            id = "test/repo",
            name = "Test App",
            owner = "test",
            repoName = "repo",
            repoUrl = "https://github.com/test/repo",
            description = "Test",
            iconUrl = "https://github.com/test.png",
            packageName = "com.nonexistent.testapp",
            latestRelease = AppRelease(
                tag = "v1.1.0",
                name = "v1.1.0",
                publishedAt = "2026-08-01",
                body = "Notes",
                apkName = "app.apk",
                apkDownloadUrl = "https://example.com/app.apk",
                apkSizeBytes = 1000L
            ),
            previousRelease = null,
            installedVersionName = "v1.0.0"
        )

        val versionComparison = installerManager.compareVersions(app.installedVersionName!!, app.latestRelease!!.tag)
        assertTrue(versionComparison < 0)
    }
}
