package com.example.editor.settings

enum class ThemeMode {
    BLACK,
    DARK,
    LIGHT,
    SYSTEM
}

enum class EditorFont {
    MONOSPACE,
    SANS_SERIF,
    SERIF
}

/**
 * User-configurable settings for editor behavior, typography, and theme.
 */
data class EditorSettings(
    val fontSizeSp: Int = 14,
    val tabSize: Int = 4,
    val useSpacesForTab: Boolean = true,
    val wordWrap: Boolean = false,
    val showLineNumbers: Boolean = true,
    val highlightCurrentLine: Boolean = true,
    val autoIndent: Boolean = true,
    val autoCloseBrackets: Boolean = true,
    val syntaxHighlightingEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.BLACK,
    val editorFont: EditorFont = EditorFont.MONOSPACE,
    val autoSave: Boolean = true,
    val autoSaveIntervalSeconds: Int = 10,
    val autoSaveToDisk: Boolean = true,
    val restoreOpenTabs: Boolean = true,
    val confirmDelete: Boolean = true,
    val confirmCloseModified: Boolean = true,
    val uiLanguage: String = "system" // "system", "en", "ar"
)
