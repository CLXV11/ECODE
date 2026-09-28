# ECODE

> Native Android Code Editor & Mobile IDE engine built with Kotlin and Jetpack Compose.

[![Platform](https://img.shields.io/badge/Platform-Android%207.0+%20(API%2024--36)-2C3437?style=flat-square&logo=android&logoColor=3DDC84)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin%202.0-2C3437?style=flat-square&logo=kotlin&logoColor=7F52FF)](https://kotlinlang.org)
[![UI Framework](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-2C3437?style=flat-square&logo=jetpackcompose&logoColor=4285F4)](https://developer.android.com/jetpack/compose)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions%20APK%20Pipeline-2C3437?style=flat-square&logo=githubactions&logoColor=2088FF)](.github/workflows/build-apk.yml)
[![Theme](https://img.shields.io/badge/Display-AMOLED%20Black%20%23000000-2C3437?style=flat-square)](#visual-themes)
[![License](https://img.shields.io/badge/License-Apache%202.0-2C3437?style=flat-square)](LICENSE)

---

## Technical Specifications

| Parameter | Specification |
| :--- | :--- |
| Runtime Environment | Android Runtime (ART), Min SDK 24, Target SDK 36 |
| Core Language | Kotlin 2.0+ (Strict Type-Safety & Coroutines) |
| Architecture Pattern | Unidirectional Data Flow (UDF), MVVM, Clean Separation |
| UI Toolkit | Declarative Jetpack Compose with Material Design 3 |
| Storage Interface | Android Storage Access Framework (SAF) Documents & Tree Provider |
| Execution Engine | Local WebKit/WebView runtime with bidirectional JavaScript Bridge |
| Testing Framework | JVM Unit Tests + Robolectric + KSP |

---

## Community & Direct Developer Channels

Direct links for technical support, feature discussions, and build announcements:

| Channel | Identifier | Direct Access |
| :--- | :--- | :--- |
| Telegram | `@EPCD11` | [t.me/EPCD11](https://t.me/EPCD11) |
| Discord | Community Server | [discord.gg/FkssmYFY](https://discord.gg/FkssmYFY) |

---

## System Architecture

```
[ Android OS / Linux Kernel ]
       |
       v
[ Jetpack Compose UI Layer ]
  ├── TopAppBar (Session Info, Language Selector Chip, Action Bar)
  ├── EditorTabBar (Horizontal Scroll, Dirty State Tracker, Tab Manager)
  ├── CodeEditorView (Canvas Line Numbers, Syntax Highlight Engine, Caret)
  ├── SearchReplaceBar (Regex Engine, Case-Match, Token Navigator)
  ├── EditorBottomBar (Undo/Redo, Indentation Controls, Quick Symbol Bar)
  └── FileExplorerDrawer (SAF SAF-Tree Provider, Recents Cache, Direct Actions)
       |
       v
[ ViewModel & State Management ]
  ├── EditorViewModel (StateFlow<EditorUiState>, MVI Event Pipeline)
  ├── AutoSaveManager (Debounced Background I/O Coroutine)
  └── ShortcutRegistry (Physical Keyboard KeyEvent Dispatcher)
       |
       v
[ Engine & Business Logic Core ]
  ├── Lexer & Tokenizer (Multi-language Regex Matcher, Span Formatter)
  ├── WebRunnerContentBuilder (Payload Compiler, Script Injector)
  ├── SafStorageManager (Scoped Storage URIs, MIME Resolver)
  └── WebConsoleBridge (Bidirectional JavaScript Interface, Log Dispatcher)
```

---

## Functional Capabilities

### Editor Core
- Lexical syntax highlighter covering more than 16 languages and data formats.
- Canvas-rendered line number gutter synchronized with vertical scroll offsets.
- Automatic bracket pairing, delimiter completion, and smart indentation logic.
- Undo/Redo stack with snapshot compaction and zero memory leak retention.
- Regex-powered Find and Replace supporting case sensitivity and whole-word toggles.

### Web Runner & Runtime Sandbox
- Integrated live preview for HTML, CSS, JavaScript, SVG, Markdown, and JSON.
- Two-way bridge intercepting `console.log`, `console.warn`, and `console.error`.
- Interactive REPL input bar for real-time JavaScript code evaluation.
- Responsive viewport toggles: Fullscreen, Desktop, Tablet, and Mobile frames.

### Storage & Android Scoped Storage Access
- Full compliance with Android Storage Access Framework (SAF).
- Native support for opening individual documents (`ACTION_OPEN_DOCUMENT`) and entire directory trees (`ACTION_OPEN_DOCUMENT_TREE`).
- Automatic background file saving with configurable debounce intervals (5s, 10s, 30s, 60s).
- Encoding detector with UTF-8, UTF-8 BOM, UTF-16, and ASCII identification.

---

## Supported Languages & Formats

```
[Web & Markup]       HTML (.html, .htm), CSS (.css), SCSS (.scss), Markdown (.md), SVG (.svg)
[Scripting]          JavaScript (.js, .mjs), TypeScript (.ts, .tsx), Python (.py)
[Systems & JVM]      Kotlin (.kt, .kts), Java (.java), C (.c, .h), C++ (.cpp, .hpp), Rust (.rs), Go (.go)
[Query & Config]     SQL (.sql), JSON (.json), XML (.xml), YAML (.yaml, .yml), Shell (.sh, .bash)
```

---

## Visual Themes

| Identifier | Background | Foreground | Target Use |
| :--- | :--- | :--- | :--- |
| `AMOLED` | `#000000` | `#E6EDF3` | Battery optimization on OLED displays, pitch black contrast |
| `Monokai` | `#272822` | `#F8F8F2` | Classic high-contrast syntax palette |
| `One Dark Pro` | `#21252B` | `#ABB2BF` | Low-strain dark neutral palette |
| `Dracula` | `#282A36` | `#F8F8F2` | High-chroma violet and pastel accents |
| `GitHub Light` | `#FFFFFF` | `#24292F` | High ambient daylight reading |

---

## Hardware Keyboard Bindings

| Shortcut | Target Command |
| :--- | :--- |
| `Ctrl + S` | Persist active file to disk / SAF provider |
| `Ctrl + F` | Toggle Find bar |
| `Ctrl + H` | Toggle Find and Replace bar |
| `Ctrl + G` | Open line jump dialog |
| `Ctrl + W` | Terminate active tab session |
| `Ctrl + R` | Dispatch code to Web Runner runtime |
| `Ctrl + Z` | Revert last buffer modification |
| `Ctrl + Y` | Reapply reverted buffer modification |
| `Ctrl + ,` | Invoke configuration dialog |

---

## Automated CI/CD Pipeline (GitHub Actions)

A reproducible, containerized build workflow is configured under `.github/workflows/build-apk.yml`.

### Pipeline Triggers
1. **Push & Pull Requests**: Triggers on `main` and `master` branches.
2. **Tag Deployments**: Push tags matching `v*` to automatically generate GitHub Releases.
3. **Manual Dispatch**: Triggered from the Actions tab with configurable build parameters:
   - Variant selection: `debug`, `release`, or `both`.
   - Unit test toggle: execute or bypass test suites.
   - Release creation: direct publishing toggle.

### Artifact Outputs
- Location: GitHub Actions Run Summary -> `Artifacts` -> `ECODE-Android-APK`.
- Files:
  - `ECODE-debug.apk`
  - `ECODE-release.apk` (if release variant enabled)
  - `SHA256SUMS.txt` (cryptographic integrity verification)

---

## Local Build Instructions

### Prerequisites
- JDK 21 (Temurin, OpenJDK, or Zulu)
- Android SDK with Platform 36 and Build-Tools 36.0.0
- Linux, macOS, or Windows terminal

### Commands

```bash
# 1. Clone repository
git clone https://github.com/amialhnina/ECODE.git
cd ECODE

# 2. Grant executable permissions to wrapper
chmod +x gradlew

# 3. Execute JVM unit and Robolectric test suites
./gradlew :app:testDebugUnitTest

# 4. Compile debug APK
./gradlew :app:assembleDebug

# 5. Output location
# app/build/outputs/apk/debug/app-debug.apk
```

---

## Project Structure

```
ECODE/
├── .github/
│   └── workflows/
│       └── build-apk.yml            # CI/CD APK build and release pipeline
├── app/
│   ├── build.gradle.kts             # Module configuration, dependencies, and APK naming
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml  # Hardware declarations and permissions
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt   # System window insets & entry point
│       │   │   └── editor/
│       │   │       ├── engine/       # Lexical analysis and line gutter calculations
│       │   │       ├── io/           # SAF documents, trees, encoding detectors
│       │   │       ├── runner/       # WebView container, bridge, console REPL
│       │   │       ├── settings/     # Preference Datastore, i18n, themes
│       │   │       ├── shortcuts/    # Hardware key event mapping
│       │   │       ├── syntax/       # Language tokens and grammar rules
│       │   │       └── ui/           # Jetpack Compose screens, dialogs, components
│       │   └── res/
│       │       ├── drawable/         # Vector assets (Telegram, Discord, GitHub)
│       │       └── values/           # Theme resources and string catalogs
│       └── test/                     # Robolectric and JVM unit tests
├── gradle/
│   └── wrapper/                     # Gradle wrapper definitions
├── gradlew                          # Unix build script
├── gradlew.bat                      # Windows build script
├── settings.gradle.kts              # Project structure definitions
└── README.md                        # Documentation
```

---

## License

This software is released under the **Apache License 2.0**. Refer to the [LICENSE](LICENSE) file for terms and conditions.
