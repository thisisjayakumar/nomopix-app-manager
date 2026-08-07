package com.nomopix.appmanager.feature.manager

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nomopix.appmanager.core.model.AppRelease
import com.nomopix.appmanager.core.model.InstallStatus
import com.nomopix.appmanager.core.model.NomopixApp

@Composable
fun AppManagerScreen(viewModel: AppManagerViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showIndexSettingsModal by remember { mutableStateOf(false) }

    if (showIndexSettingsModal) {
        IndexSettingsDialog(
            currentIndexUrl = uiState.indexUrl,
            onDismiss = { showIndexSettingsModal = false },
            onSave = { newUrl ->
                viewModel.updateIndexUrl(newUrl)
                showIndexSettingsModal = false
                Toast.makeText(context, "Index URL updated! Refreshing apps...", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Nomopix Manager",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${uiState.apps.size} apps in index",
                            fontSize = 12.sp,
                            color = Color(0xFF818CF8)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { showIndexSettingsModal = true },
                        modifier = Modifier
                            .background(Color(0xFF1E293B), shape = RoundedCornerShape(12.dp))
                            .size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Index Settings",
                            tint = Color.LightGray
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refreshAppList() },
                        modifier = Modifier
                            .background(Color(0xFF1E293B), shape = RoundedCornerShape(12.dp))
                            .size(42.dp)
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                color = Color(0xFF6366F1),
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Index",
                                tint = Color(0xFF6366F1)
                            )
                        }
                    }
                }
            }

            // Search Filter Input
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search apps or developers...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            if (uiState.apps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isRefreshing) "Fetching GitHub app index..." else "No apps found. Tap Refresh to load index.txt",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(uiState.apps) { app ->
                        NomopixAppCard(
                            app = app,
                            onInstallRelease = { release -> viewModel.installRelease(app, release) },
                            onUninstall = { viewModel.uninstallApp(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NomopixAppCard(
    app: NomopixApp,
    onInstallRelease: (AppRelease) -> Unit,
    onUninstall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // App Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = app.iconUrl,
                        contentDescription = app.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF334155))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = app.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "@${app.owner}/${app.repoName}",
                            fontSize = 12.sp,
                            color = Color(0xFF818CF8),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Status Badge
                StatusBadge(app)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.description,
                fontSize = 12.sp,
                color = Color.LightGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Download Progress Bar
            if (app.downloadProgress.isDownloading) {
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    LinearProgressIndicator(
                        progress = app.downloadProgress.progressFloat,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF6366F1),
                        trackColor = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = app.downloadProgress.statusMessage,
                        fontSize = 11.sp,
                        color = Color(0xFF10B981)
                    )
                }
            }

            // Release Options: Latest & Previous
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Latest Release Card
                if (app.latestRelease != null) {
                    ReleaseRowItem(
                        release = app.latestRelease,
                        isLatest = true,
                        app = app,
                        onAction = { onInstallRelease(app.latestRelease) }
                    )
                }

                // Previous Release Card
                if (app.previousRelease != null) {
                    ReleaseRowItem(
                        release = app.previousRelease,
                        isLatest = false,
                        app = app,
                        onAction = { onInstallRelease(app.previousRelease) }
                    )
                }
            }

            // Uninstall Action Button
            if (app.installStatus != InstallStatus.NOT_INSTALLED) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onUninstall,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Uninstall App", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(app: NomopixApp) {
    val (text, bgColor, textColor) = when (app.installStatus) {
        InstallStatus.INSTALLED_UP_TO_DATE -> Triple("Installed ${app.installedVersionName ?: ""}", Color(0xFF10B981).copy(alpha = 0.2f), Color(0xFF10B981))
        InstallStatus.UPDATE_AVAILABLE -> Triple("Update Available", Color(0xFF6366F1).copy(alpha = 0.25f), Color(0xFF818CF8))
        InstallStatus.OLDER_VERSION_INSTALLED -> Triple("Installed ${app.installedVersionName ?: ""}", Color(0xFFF59E0B).copy(alpha = 0.2f), Color(0xFFF59E0B))
        InstallStatus.NOT_INSTALLED -> Triple("Not Installed", Color(0xFF334155), Color.Gray)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun ReleaseRowItem(
    release: AppRelease,
    isLatest: Boolean,
    app: NomopixApp,
    onAction: () -> Unit
) {
    val isInstalledThisVersion = app.installedVersionName?.contains(release.tag.removePrefix("v"), ignoreCase = true) == true
    val isUpgrade = isLatest && app.installStatus == InstallStatus.UPDATE_AVAILABLE
    val isDowngrade = !isLatest && app.installStatus != InstallStatus.NOT_INSTALLED

    val buttonText = when {
        isInstalledThisVersion -> "Reinstall ${release.tag}"
        isUpgrade -> "Upgrade to ${release.tag}"
        isDowngrade -> "Downgrade to ${release.tag}"
        else -> "Install ${release.tag}"
    }

    val buttonBg = when {
        isUpgrade -> Color(0xFF6366F1)
        isDowngrade -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isLatest) Icons.Default.NewReleases else Icons.Default.History,
                    contentDescription = null,
                    tint = if (isLatest) Color(0xFF6366F1) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isLatest) "Latest ${release.tag}" else "Previous ${release.tag}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = release.publishedAt,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    if (release.apkSizeBytes > 0) {
                        Text(
                            text = "${release.apkName} • ${release.apkSizeBytes / (1024 * 1024)} MB",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Button(
                onClick = onAction,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonBg)
            ) {
                Icon(
                    imageVector = if (isUpgrade) Icons.Default.ArrowUpward else if (isDowngrade) Icons.Default.ArrowDownward else Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = buttonText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun IndexSettingsDialog(
    currentIndexUrl: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var urlInput by remember { mutableStateOf(currentIndexUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Update App Index URL", fontWeight = FontWeight.Bold, color = Color.White)
        },
        text = {
            Column {
                Text(
                    text = "Specify the public index.txt URL containing the GitHub app links list:",
                    fontSize = 13.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("https://raw.githubusercontent.com/nomopix/index/main/index.txt") },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(urlInput) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Text("Save & Fetch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", fontSize = 12.sp)
            }
        },
        containerColor = Color(0xFF1E293B),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray
    )
}
