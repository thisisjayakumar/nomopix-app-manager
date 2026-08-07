# 📱 Nomopix App Manager — Project Handover Document

> **Repository:** `nomopix-app-manager`  
> **Location:** `/home/njayakumar/Documents/projects/nomopix-app-manager`  
> **Version:** `v1.0.0`  
> **Last Updated:** 2026-08-07  

---

## 1. Executive Summary & Tech Stack

**Nomopix App Manager** is an automated Android wrapper app created to eliminate manual GitHub APK downloads. It parses GitHub URLs from `index.txt` and manages app installations, upgrades, downgrades, and uninstallation.

### Stack
- **Language**: Kotlin 1.9 (JVM 17)
- **UI Framework**: Jetpack Compose + Material 3
- **Dependency Injection**: Dagger Hilt 2.50
- **Networking**: Built-in `HttpURLConnection` REST fetcher (zero external networking overhead)
- **Package Manager**: Android `PackageInstaller` / `FileProvider` + `Intent.ACTION_VIEW` and `Intent.ACTION_DELETE`

---

## 2. Key Features

1. **`index.txt` Parser (`IndexParser.kt`)**: Fetches & parses GitHub repository URLs.
2. **GitHub API Release Fetcher (`GitHubReleaseFetcher.kt`)**: Resolves app name, description, owner avatar, **latest release** (v1.x), and **one previous release**.
3. **One-Click Operations**:
   - Install Latest or Previous versions
   - Upgrade to Latest version
   - Downgrade to Previous version
   - Uninstall app
4. **Live Download Progress**: Real-time progress bar with byte counts during APK downloading.
5. **Configurable Index Source**: Interactive dialog allowing users to update the `index.txt` source URL at runtime.

---

## 3. Verification Commands

```bash
# Run unit test suite
JAVA_HOME=/home/njayakumar/.local_sdk/jdk-17.0.10+7 ./gradlew test

# Build Debug and Release APKs
JAVA_HOME=/home/njayakumar/.local_sdk/jdk-17.0.10+7 ./gradlew assembleDebug assembleRelease
```
