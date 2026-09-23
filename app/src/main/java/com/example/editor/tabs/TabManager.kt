package com.example.editor.tabs

import android.content.Context
import com.example.editor.io.EditorFile
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.LanguageRegistry
import org.json.JSONArray
import org.json.JSONObject

data class PersistedTab(
    val filePath: String?,
    val uriString: String?,
    val name: String,
    val isInternal: Boolean,
    val languageId: String
)

/**
 * Manages open tabs, tab switching, dirty state, and tab state persistence.
 */
class TabManager(context: Context) {

    private val prefs = context.getSharedPreferences("clxv11_tabs_state", Context.MODE_PRIVATE)

    private val _openTabs = mutableListOf<EditorTab>()
    val openTabs: List<EditorTab> get() = _openTabs

    var activeIndex: Int = 0
        private set

    val activeTab: EditorTab?
        get() = if (activeIndex in _openTabs.indices) _openTabs[activeIndex] else null

    fun selectTab(index: Int) {
        if (index in _openTabs.indices) {
            activeIndex = index
            persistTabs()
        }
    }

    /**
     * Opens a file into a new tab, or switches to it if already open.
     */
    fun openTab(
        file: EditorFile,
        content: String,
        customLanguage: LanguageDefinition? = null,
        isModified: Boolean = false,
        cursorPosition: Int = 0,
        customId: String? = null
    ): EditorTab {
        // Check if file is already open
        val existingIndex = _openTabs.indexOfFirst { tab ->
            (file.absolutePath != null && tab.file.absolutePath == file.absolutePath) ||
                    (file.uri != null && tab.file.uri == file.uri)
        }

        if (existingIndex != -1) {
            activeIndex = existingIndex
            persistTabs()
            return _openTabs[existingIndex]
        }

        val lang = customLanguage ?: LanguageRegistry.detectLanguage(file.name, content)
        val newTab = EditorTab(
            id = customId ?: java.util.UUID.randomUUID().toString(),
            file = file,
            content = content,
            language = lang,
            isModified = isModified,
            cursorPosition = cursorPosition
        )
        _openTabs.add(newTab)
        activeIndex = _openTabs.size - 1
        persistTabs()
        return newTab
    }

    /**
     * Restores a list of tabs directly (e.g. from AutoSave snapshot).
     */
    fun restoreTabs(tabs: List<EditorTab>, selectIndex: Int) {
        _openTabs.clear()
        _openTabs.addAll(tabs)
        activeIndex = if (_openTabs.isNotEmpty()) selectIndex.coerceIn(0, _openTabs.size - 1) else 0
        persistTabs()
    }

    /**
     * Closes the tab at specified index.
     * Returns the closed tab.
     */
    fun closeTab(index: Int): EditorTab? {
        if (index !in _openTabs.indices) return null
        val closed = _openTabs.removeAt(index)

        if (_openTabs.isEmpty()) {
            activeIndex = 0
        } else if (activeIndex >= _openTabs.size) {
            activeIndex = _openTabs.size - 1
        } else if (index < activeIndex) {
            activeIndex--
        }

        persistTabs()
        return closed
    }

    fun updateActiveTabContent(content: String, isModified: Boolean) {
        activeTab?.let { tab ->
            tab.content = content
            tab.isModified = isModified
        }
    }

    fun updateActiveTabLanguage(newLanguage: LanguageDefinition) {
        activeTab?.let { tab ->
            tab.language = newLanguage
            persistTabs()
        }
    }

    fun markActiveTabSaved(updatedFile: EditorFile) {
        activeTab?.let { tab ->
            val updated = tab.copy(file = updatedFile, isModified = false)
            _openTabs[activeIndex] = updated
            persistTabs()
        }
    }

    fun updateActiveTabFile(newFile: EditorFile, newLanguage: LanguageDefinition? = null) {
        activeTab?.let { tab ->
            val lang = newLanguage ?: LanguageRegistry.detectLanguage(newFile.name, tab.content)
            val updated = tab.copy(file = newFile, language = lang)
            _openTabs[activeIndex] = updated
            persistTabs()
        }
    }

    fun persistTabs() {
        try {
            val arr = JSONArray()
            for (tab in _openTabs) {
                val obj = JSONObject().apply {
                    put("filePath", tab.file.absolutePath)
                    put("uriString", tab.file.uri?.toString())
                    put("name", tab.file.name)
                    put("isInternal", tab.file.isInternalWorkspace)
                    put("languageId", tab.language.id)
                }
                arr.put(obj)
            }
            prefs.edit()
                .putString("persisted_tabs", arr.toString())
                .putInt("active_tab_index", activeIndex)
                .apply()
        } catch (_: Throwable) {
            // Fail gracefully
        }
    }

    fun getPersistedTabs(): Pair<List<PersistedTab>, Int> {
        val raw = prefs.getString("persisted_tabs", null) ?: return Pair(emptyList(), 0)
        val savedIndex = prefs.getInt("active_tab_index", 0)
        val list = mutableListOf<PersistedTab>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    PersistedTab(
                        filePath = if (obj.has("filePath") && !obj.isNull("filePath")) obj.getString("filePath") else null,
                        uriString = if (obj.has("uriString") && !obj.isNull("uriString")) obj.getString("uriString") else null,
                        name = obj.getString("name"),
                        isInternal = obj.optBoolean("isInternal", true),
                        languageId = obj.optString("languageId", "plaintext")
                    )
                )
            }
        } catch (_: Throwable) {
            // Fail gracefully
        }
        return Pair(list, savedIndex)
    }

    fun clearPersistedTabs() {
        prefs.edit().clear().apply()
    }
}
