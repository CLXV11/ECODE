<div align="center">

# 🐊 CodeXCroc
### Next-Generation Android Code Editor & Mobile IDE
**محرر أكواد متقدم وبيئة تطوير برمجية متكاملة وفائقة السرعة لنظام أندرويد**

[![Android Build](https://img.shields.io/badge/Android-SDK%2024%20..%2036-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![GitHub Actions](https://img.shields.io/badge/CI%2FCD-Automated%20APK%20Build-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)](.github/workflows/build-apk.yml)
[![Theme](https://img.shields.io/badge/AMOLED-Pitch%20Black%20%23000000-000000?style=for-the-badge&logo=visualstudiocode&logoColor=white)](#-visual-themes--pitch-black-amoled)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)](LICENSE)

<p align="center">
  <a href="#-features--الميزات-الرئيسية">Features</a> •
  <a href="#-quick-start--التشغيل-السريع">Quick Start</a> •
  <a href="#-github-actions-apk-workflow">CI/CD APK Workflow</a> •
  <a href="#-architecture--البنية-البرمجية">Architecture</a> •
  <a href="#-supported-languages">Languages</a> •
  <a href="#-keyboard-shortcuts">Shortcuts</a>
</p>

---

</div>

## 🌟 Overview | نظرة عامة

**CodeXCroc** هو محرر أكواد برمجية احترافي وعالي الأداء تم بناؤه بالكامل بنظام **Native Android** باستخدام **Kotlin** و **Jetpack Compose (Material Design 3)**. صُمم خصيصاً للمبرمجين، ومطوري الويب، والباحثين، والطلاب الذين يحتاجون إلى تجربة برمجة حقيقية وسلسة مباشرة من هواتفهم وأجهزتهم اللوحية، دون أي تنازلات في السرعة أو جودة العرض.

> **CodeXCroc** is a lightning-fast, production-grade native Android code editor and mobile IDE built from the ground up with Kotlin and Jetpack Compose. Engineered for programmers, web developers, and competitive coders who demand desktop-class editing responsiveness on mobile devices.

---

## ✨ Features | الميزات الرئيسية

### 🚀 Core Capabilities
* **⚡ Ultra-Fast Multi-Tab Workspace**: تنقل لحظي بين عدة ملفات مبوبة مع حفظ الحالة ونقاط التعديل.
* **🎨 Precision Syntax Highlighting**: محرك تمييز بصري عالي الدقة مبني عبر Lexer متعدد اللغات (يدعم أكثر من 16 لغة برمجة وتقنية).
* **🌐 Integrated Live Web Runner & Console Bridge**: مشغل ويب حي ومباشر لملفات HTML و CSS و JavaScript مزود بجسر كونسول فوري (`console.log`, `console.warn`, `console.error`) واعتراض الأخطاء البرمجية.
* **🖤 Pure Pitch Black (AMOLED) Mode**: وضع أسود مطلق حقيقي (`#000000`) لتوفير استهلاك البطارية على شاشات OLED/AMOLED وتوفير راحة بصرية فائقة أثناء العمل الليلي.
* **⌨️ Smart Symbol Accessory Bar**: شريط علوي وسفلي ذكي للرموز البرمجية الأكثر استخداماً (`{ }`, `( )`, `[ ]`, `<`, `>`, `;`, `=>`, `=`, `"`, `'`, `/`, `\`, `_`, `|`, `&`, `!`, `?`, `Tab`).
* **📂 Native Android SAF & Tree Storage**: دعم كامل لنظام Storage Access Framework (SAF) لفتح ملفات أو مجلدات عمل كاملة مباشرة من الذاكرة أو بطاقة SD.
* **🔍 Advanced Regex Search & Replace**: بحث واستبدال متقدم يدعم التعابير النمطية (Regex)، مطابقة حالة الأحرف، والكلمات الكاملة.
* **📏 Professional Code Typography**: تحكم دقيق في حجم الخط (`fontSize`), أرقام الأسطر (`Line Numbers`), الالتفاف التلقائي (`Word Wrap`), والمسافات البادئة (`Tab Width`).
* **💾 Automatic & Instant Saving**: دعم الحفظ التلقائي مع مؤشر التعديل الذكي لضمان عدم ضياع أي سطر برمجي.

---

## 🎨 Visual Themes & Pitch Black AMOLED

| Theme | Background | Text Contrast | Best Suited For |
| :--- | :--- | :--- | :--- |
| **Pitch Black (AMOLED)** | `#000000` | High Dynamic Range | Battery saving, pure contrast, OLED screens |
| **Monokai Dark** | `#272822` | Vivid Pastel Accents | Long coding sessions, iconic editor feel |
| **One Dark Pro** | `#21252B` | Balanced Soft Tones | Modern visual aesthetics & readability |
| **Dracula Night** | `#282A36` | Neon Violet & Pink | High aesthetic vibe, web development |
| **GitHub Light** | `#FFFFFF` | Clear High Contrast | Daylight environments, documentation reading |

---

## 🔤 Supported Languages & Technologies

يدعم المحرك الداخلي لـ **CodeXCroc** التلوين البرمجي الذكي وحساب الرموز للعديد من اللغات:

```
├── ☕ Kotlin (.kt, .kts)
├── ☕ Java (.java)
├── 🐍 Python (.py)
├── 🌐 JavaScript (.js, .mjs)
├── 🔷 TypeScript (.ts, .tsx)
├── 🌐 HTML5 (.html, .htm)
├── 🎨 CSS3 / SCSS (.css, .scss)
├── 🦀 Rust (.rs)
├── 🐹 Go (.go)
├── ⚙️ C & C++ (.c, .cpp, .h, .hpp)
├── 🐘 PHP (.php)
├── 🗄️ SQL (.sql)
├── 🐚 Bash / Shell (.sh, .bash)
├── 📦 JSON / JSON5 (.json)
├── 📑 Markdown (.md)
└── 📄 XML / SVG / YAML (.xml, .svg, .yaml, .yml)
```

---

## 🛠️ GitHub Actions: Automated APK Build Workflow

تم تزويد المستودع بـ CI/CD Workflow متكامل واحترافي عبر **GitHub Actions** لبناء ملفات الـ **APK** آلياً عند كل تحديث أو عبر التشغيل اليدوي.

### 📍 مسار الملف:
```
.github/workflows/build-apk.yml
```

### ⚡ كيفية عمل الـ Workflow:
1. **Push أو Pull Request**: يقوم بالبناء والتحقق التلقائي وتشغيل كافة الـ Unit Tests واختبارات Robolectric عند كل دمج على فروع `main` أو `master`.
2. **Release التلقائي**: عند عمل `git tag` بصيغة `v1.0.0`، يقوم الـ Workflow ببناء الـ APK ونشره مباشرة كـ **GitHub Release** مرفق معه التطبيق الجاهز للتحميل.
3. **التشغيل اليدوي (Workflow Dispatch)**:
   - افتح تبويب **Actions** في مستودعك على GitHub.
   - اختر **Build & Release Android APK**.
   - اضغط على **Run workflow**.
   - اختر نوع البناء: `debug` أو `release`.
   - حدد خيار إنشاء Release إن أردت نشره فوراً.

### 📦 تحميل الـ APK من GitHub:
بمجرد اكتمال مهمة البناء، تجد الـ APK جاهزاً في قسم **Artifacts** باسم `CodeXCroc-APK`.

---

## 🏗️ Architecture & Project Structure | البنية البرمجية

يتبع المشروع أحدث مبادئ **Clean Architecture** و **MVVM** مع الفصل التام بين طبقة العرض وطبقة المنطق ومحرك التلوين:

```
CodeXCroc/
├── .github/
│   └── workflows/
│       └── build-apk.yml            # Automated CI/CD Android APK workflow
├── app/
│   ├── build.gradle.kts             # Module-level Gradle configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt   # Edge-to-edge entry point
│       │   │   └── editor/
│       │   │       ├── engine/       # Syntax lexer & highlight engine
│       │   │       ├── io/           # Storage Access Framework & file management
│       │   │       ├── runner/       # Live HTML/JS runner & console bridge
│       │   │       ├── settings/     # Preference persistence & themes
│       │   │       ├── syntax/       # Language registry & definitions
│       │   │       ├── ui/           # Jetpack Compose Screens & Components
│       │   │       │   ├── components/  # CodeEditorView, FileDrawer, AccessoryBar
│       │   │       │   ├── dialogs/     # SettingsDialog, SearchDialog, GoToLine
│       │   │       │   └── theme/       # Dynamic color schemes & typography
│       │   │       └── viewmodel/    # EditorViewModel & state management
│       │   └── res/                  # Icons, drawables, strings
│       └── test/                     # Robolectric & JVM unit test suites
├── gradle/
│   └── wrapper/
├── gradlew                          # Gradle wrapper executable
├── gradlew.bat                      # Windows Gradle wrapper
├── settings.gradle.kts              # Root project configuration
└── README.md                        # Documentation
```

---

## ⌨️ Productivity Shortcuts | اختصارات لوحة المفاتيح

عند توصيل لوحة مفاتيح خارجية (Physical Keyboard أو Bluetooth Keyboard)، يمكنك الاستفادة من الاختصارات المكتبية التالية:

| الاختصار | الوظيفة | Function |
| :---: | :--- | :--- |
| <kbd>Ctrl</kbd> + <kbd>S</kbd> | حفظ الملف النشط | Save Active File |
| <kbd>Ctrl</kbd> + <kbd>F</kbd> | فتح نافذة البحث | Open Find / Search Dialog |
| <kbd>Ctrl</kbd> + <kbd>H</kbd> | فتح البحث والاستبدال | Open Replace Dialog |
| <kbd>Ctrl</kbd> + <kbd>G</kbd> | الانتقال لرقم سطر محدد | Go to Specific Line |
| <kbd>Ctrl</kbd> + <kbd>W</kbd> | إغلاق التبويب الحالي | Close Active Tab |
| <kbd>Ctrl</kbd> + <kbd>R</kbd> | تشغيل في مشغل الويب | Run in Live Web Preview |
| <kbd>Ctrl</kbd> + <kbd>Z</kbd> | تراجع | Undo |
| <kbd>Ctrl</kbd> + <kbd>Y</kbd> | إعادة | Redo |
| <kbd>Ctrl</kbd> + <kbd>,</kbd> | فتح الإعدادات | Open Settings |

---

## 🚀 Quick Start | البناء والتشغيل محلياً

### المتطلبات الأساسية (Prerequisites)
* **JDK 21** أو أحدث.
* **Android Studio Ladybug (2024.2+)** أو أحدث.
* جهاز يعمل بنظام **Android 7.0 (API 24)** فما فوق.

### أوامر البناء عبر الطرفية (Terminal):

```bash
# 1. استنساخ المستودع (Clone Repository)
git clone https://github.com/amialhnina/CodeXCroc.git
cd CodeXCroc

# 2. منح صلاحيات التشغيل للـ Gradle Wrapper
chmod +x gradlew

# 3. تشغيل حزمة الاختبارات الشاملة (Run Unit & Robolectric Tests)
./gradlew :app:testDebugUnitTest

# 4. بناء نسخة الـ APK للاختبار (Build Debug APK)
./gradlew :app:assembleDebug

# 5. موقع ملف الـ APK الناتج:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔒 Security & Privacy | الأمان والخصوصية

* **Zero Tracking**: التطبيق لا يجمع أي بيانات شخصية ولا يحتوي على أي متتبعات أو إعلانات.
* **Local First**: كل الأكواد البرمجية والملفات تُخزن وتُعالج بالكامل على جهازك محلياً داخل بيئة تخزين آمنة.
* **Storage Access Framework (SAF)**: احترام كامل لمعايير الخصوصية الصارمة لنظام أندرويد عبر استخدام أذونات الملفات المحددة فقط دون طلب صلاحيات وصول واسعة للذاكرة.

---

## 🤝 Contributing | المساهمة

نرحب بجميع المساهمات لتطوير **CodeXCroc**!
1. قم بعمل **Fork** للمستودع.
2. أنشئ فرعاً جديداً لميزتك (`git checkout -b feature/awesome-feature`).
3. سجّل التعديلات برمز دلالي (`git commit -m "feat: add awesome feature"`).
4. ارفع الفرع (`git push origin feature/awesome-feature`).
5. افتح **Pull Request** للمراجعة.

---

## 📄 License | الترخيص

هذا المشروع مرخص بموجب رخصة **Apache License 2.0** - انظر ملف [LICENSE](LICENSE) لمزيد من التفاصيل.

---

<div align="center">
  <sub>صُنع بكل فخر وشغف بأيدي عربية لمجتمع المطورين حول العالم 🐊 💻</sub>
</div>
