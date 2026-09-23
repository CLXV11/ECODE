package com.example.editor.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository to persist and stream EditorSettings.
 */
class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("clxv11_editor_settings", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<EditorSettings> = _settingsFlow.asStateFlow()

    fun loadSettings(): EditorSettings {
        return EditorSettings(
            fontSizeSp = prefs.getInt("fontSizeSp", 14),
            tabSize = prefs.getInt("tabSize", 4),
            useSpacesForTab = prefs.getBoolean("useSpacesForTab", true),
            wordWrap = prefs.getBoolean("wordWrap", false),
            showLineNumbers = prefs.getBoolean("showLineNumbers", true),
            highlightCurrentLine = prefs.getBoolean("highlightCurrentLine", true),
            autoIndent = prefs.getBoolean("autoIndent", true),
            autoCloseBrackets = prefs.getBoolean("autoCloseBrackets", true),
            syntaxHighlightingEnabled = prefs.getBoolean("syntaxHighlightingEnabled", true),
            themeMode = try {
                ThemeMode.valueOf(prefs.getString("themeMode", ThemeMode.BLACK.name) ?: ThemeMode.BLACK.name)
            } catch (e: Exception) {
                ThemeMode.BLACK
            },
            editorFont = EditorFont.valueOf(prefs.getString("editorFont", EditorFont.MONOSPACE.name) ?: EditorFont.MONOSPACE.name),
            autoSave = prefs.getBoolean("autoSave", true),
            autoSaveIntervalSeconds = prefs.getInt("autoSaveIntervalSeconds", 10),
            autoSaveToDisk = prefs.getBoolean("autoSaveToDisk", true),
            restoreOpenTabs = prefs.getBoolean("restoreOpenTabs", true),
            confirmDelete = prefs.getBoolean("confirmDelete", true),
            confirmCloseModified = prefs.getBoolean("confirmCloseModified", true),
            uiLanguage = prefs.getString("uiLanguage", "system") ?: "system"
        )
    }

    fun saveSettings(settings: EditorSettings) {
        prefs.edit()
            .putInt("fontSizeSp", settings.fontSizeSp)
            .putInt("tabSize", settings.tabSize)
            .putBoolean("useSpacesForTab", settings.useSpacesForTab)
            .putBoolean("wordWrap", settings.wordWrap)
            .putBoolean("showLineNumbers", settings.showLineNumbers)
            .putBoolean("highlightCurrentLine", settings.highlightCurrentLine)
            .putBoolean("autoIndent", settings.autoIndent)
            .putBoolean("autoCloseBrackets", settings.autoCloseBrackets)
            .putBoolean("syntaxHighlightingEnabled", settings.syntaxHighlightingEnabled)
            .putString("themeMode", settings.themeMode.name)
            .putString("editorFont", settings.editorFont.name)
            .putBoolean("autoSave", settings.autoSave)
            .putInt("autoSaveIntervalSeconds", settings.autoSaveIntervalSeconds)
            .putBoolean("autoSaveToDisk", settings.autoSaveToDisk)
            .putBoolean("restoreOpenTabs", settings.restoreOpenTabs)
            .putBoolean("confirmDelete", settings.confirmDelete)
            .putBoolean("confirmCloseModified", settings.confirmCloseModified)
            .putString("uiLanguage", settings.uiLanguage)
            .apply()

        _settingsFlow.value = settings
    }
}
