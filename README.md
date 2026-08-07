# 📱 Nomopix App Manager

**Nomopix App Manager** is a lightweight, self-contained Android wrapper application built with **Jetpack Compose**, **Kotlin Coroutines**, **Hilt Dependency Injection**, and **Material 3**.

It eliminates the need to manually download and reinstall app releases from GitHub by discovering apps from a public `index.txt` file and managing their lifecycle with one click.

---

## ✨ Features

- 🔍 **Automated App Discovery**: Fetches and parses GitHub repository links listed in `index.txt`.
- 🏷️ **Latest & Previous Release Display**: Shows the **latest release** (v1.x) and **one previous release** for every discovered app.
- ⚡ **One-Click Actions**:
  - 📥 **Install**: Download APK asset directly from GitHub release and prompt installation.
  - ⬆️ **Upgrade**: Upgrade an installed app to the latest release.
  - ⬇️ **Downgrade**: Downgrade an app to its previous release version.
  - 🗑️ **Uninstall**: One-click uninstall option for any managed app.
- 🔄 **Dynamic Index Refresh**: Refresh button and configurable Index URL dialog to update app listings whenever `index.txt` changes.
- 📱 **Self-Management**: The manager app itself can be downloaded and installed once, after which it manages all other Nomopix apps.
- 🎨 **Modern Aesthetics**: Built using Jetpack Compose with modern dark themes, glowing status pills, download progress bars, and Material 3 design system.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 1.9 (JVM 17 Target)
- **UI Framework**: Jetpack Compose with Material 3
- **Dependency Injection**: Dagger Hilt 2.50
- **Network Engine**: Custom GitHub REST API Parser & Index Parser via `HttpURLConnection` (Zero external network framework overhead)
- **Installer Engine**: Android `PackageInstaller` / `FileProvider` + `Intent.ACTION_VIEW` and `Intent.ACTION_DELETE`

---

## 📦 Build Verification Commands

```bash
# 1. Run full unit test suite
JAVA_HOME=/home/njayakumar/.local_sdk/jdk-17.0.10+7 ./gradlew test

# 2. Build Debug and Release APKs
JAVA_HOME=/home/njayakumar/.local_sdk/jdk-17.0.10+7 ./gradlew assembleDebug assembleRelease
```
