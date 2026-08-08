# 📱 Nomopix App Manager

> Automated Android Application Manager for GitHub Releases

**Nomopix App Manager** is an automated Android app manager built with Jetpack Compose, Material 3, and Hilt. It parses `index.txt` GitHub links and enables 1-click installation, upgrades, downgrades, and uninstallation of apps directly from GitHub Releases.

---

## ⚡ Features

- 🔍 **`index.txt` Index Parser**: Reads repository links dynamically.
- 📦 **1-Click Package Lifecycle**: Install, upgrade, downgrade, or uninstall applications seamlessly.
- 🚀 **Custom Release Naming (`release.yml`)**: GitHub Actions releases binaries as `Nomopix-Manager_${version}.apk`.
- ⚡ **Ultra-Lightweight (2.9 MB)**: Minified using R8 code and resource shrinking.

---

## 🛠️ Verification & Build Commands

```bash
# Run unit tests
JAVA_HOME=/home/njayakumar/.local_sdk/jdk-17.0.10+7 ./gradlew test

# Build Release APK
JAVA_HOME=/home/njayakumar/.local_sdk/jdk-17.0.10+7 ./gradlew assembleRelease
```

---

## 📜 Documentation

For full release history and developer handover details, see [CHANGELOG.md](CHANGELOG.md) and [HANDOVER.md](HANDOVER.md).
