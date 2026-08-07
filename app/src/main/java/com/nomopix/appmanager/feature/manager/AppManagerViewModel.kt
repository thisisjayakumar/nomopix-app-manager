package com.nomopix.appmanager.feature.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nomopix.appmanager.core.installer.ApkInstallerManager
import com.nomopix.appmanager.core.model.AppDownloadProgress
import com.nomopix.appmanager.core.model.AppRelease
import com.nomopix.appmanager.core.model.NomopixApp
import com.nomopix.appmanager.core.network.GitHubReleaseFetcher
import com.nomopix.appmanager.core.network.IndexParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class AppManagerUiState(
    val apps: List<NomopixApp> = emptyList(),
    val isRefreshing: Boolean = false,
    val indexUrl: String = IndexParser.DEFAULT_INDEX_URL,
    val searchQuery: String = "",
    val activeDownloads: Map<String, AppDownloadProgress> = emptyMap()
)

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    private val indexParser: IndexParser,
    private val releaseFetcher: GitHubReleaseFetcher,
    private val installerManager: ApkInstallerManager
) : ViewModel() {

    private val _rawApps = MutableStateFlow<List<NomopixApp>>(emptyList())
    private val _isRefreshing = MutableStateFlow(false)
    private val _indexUrl = MutableStateFlow(IndexParser.DEFAULT_INDEX_URL)
    private val _searchQuery = MutableStateFlow("")
    private val _activeDownloads = MutableStateFlow<Map<String, AppDownloadProgress>>(emptyMap())

    val uiState: StateFlow<AppManagerUiState> = combine(
        _rawApps,
        _isRefreshing,
        _indexUrl,
        _searchQuery,
        _activeDownloads
    ) { rawList, refreshing, url, query, downloads ->
        val updatedList = rawList.map { app ->
            val installedInfo = installerManager.getInstalledAppInfo(app.packageName)
            val updatedApp = app.copy(
                installedVersionName = installedInfo.versionName,
                installedVersionCode = installedInfo.versionCode,
                downloadProgress = downloads[app.id] ?: AppDownloadProgress()
            )
            val evaluatedStatus = installerManager.evaluateInstallStatus(updatedApp)
            updatedApp.copy(installStatus = evaluatedStatus)
        }

        val filtered = if (query.isBlank()) {
            updatedList
        } else {
            updatedList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.owner.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        AppManagerUiState(
            apps = filtered,
            isRefreshing = refreshing,
            indexUrl = url,
            searchQuery = query,
            activeDownloads = downloads
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, AppManagerUiState())

    init {
        refreshAppList()
    }

    fun refreshAppList() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val repoRefs = indexParser.fetchIndex(_indexUrl.value)
                Timber.i("Discovered ${repoRefs.size} repos from index file")

                val fetchedApps = mutableListOf<NomopixApp>()
                repoRefs.forEach { ref ->
                    val app = releaseFetcher.fetchAppInfo(ref)
                    if (app != null) {
                        fetchedApps.add(app)
                    }
                }
                _rawApps.value = fetchedApps
                Timber.i("Successfully updated app list with ${fetchedApps.size} apps")
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing app list")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun updateIndexUrl(newUrl: String) {
        if (newUrl.isNotBlank() && newUrl != _indexUrl.value) {
            _indexUrl.value = newUrl.trim()
            refreshAppList()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun installRelease(app: NomopixApp, release: AppRelease) {
        if (release.apkDownloadUrl.isBlank()) {
            Timber.w("Cannot install release ${release.tag}: APK download URL is empty")
            return
        }

        viewModelScope.launch {
            installerManager.downloadApk(release).collect { progress ->
                val downloadsMap = _activeDownloads.value.toMutableMap()
                downloadsMap[app.id] = progress
                _activeDownloads.value = downloadsMap

                if (!progress.isDownloading) {
                    refreshAppList()
                }
            }
        }
    }

    fun uninstallApp(app: NomopixApp) {
        installerManager.uninstallApp(app.packageName)
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            refreshAppList()
        }
    }
}
