# 📱 Nomopix App Manager — Version History & Release Changelog

All notable changes, feature additions, bug fixes, and architectural improvements to **Nomopix App Manager** are documented in this file in standard [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format.

---

## [[1.0.0]] - 2026-08-08

### 🚀 Initial Release & Features
- **Index-Based GitHub Release Fetcher**: Parses `index.txt` to discover repositories and fetch latest (v1.x) and previous releases via GitHub REST API.
- **One-Click Package Manager Operations**: Built `ApkInstallerManager.kt` supporting 1-click Install, Upgrade, Downgrade, and Uninstall using Android `PackageInstaller` and `FileProvider`.
- **Custom Versioned Release Asset Naming**: Configured GitHub Actions workflow (`.github/workflows/release.yml`) to upload versioned release assets named `Nomopix-Manager_${version}.apk` (e.g. `Nomopix-Manager_v1.0.0.apk`) and `Nomopix-Manager_${version}.aab`.
- **Launcher Icon & Intent Queries**: Generated launcher icons for all density tiers (`mipmap-mdpi` through `xxxhdpi`) from `nomopixmanager.ico` and added API 30+ `<queries>` entries in `AndroidManifest.xml`.
- **Ultra-Lightweight Size Optimization**: Minified with R8 (`isMinifyEnabled = true`, `isShrinkResources = true`) resulting in a 2.9 MB signed release APK.
