package com.example.editor.io

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class RecentFileItem(
    val name: String,
    val pathOrUri: String,
    val isInternal: Boolean,
    val languageId: String,
    val lastOpened: Long
)

/**
 * Persists and retrieves recently opened files.
 */
class RecentFilesRepository(context: Context) {

    private val prefs = context.getSharedPreferences("recent_files_prefs", Context.MODE_PRIVATE)

    fun getRecentFiles(): List<RecentFileItem> {
        val raw = prefs.getString(KEY_RECENTS, null) ?: return emptyList()
        val items = mutableListOf<RecentFileItem>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                items.add(
                    RecentFileItem(
                        name = obj.getString("name"),
                        pathOrUri = obj.getString("pathOrUri"),
                        isInternal = obj.optBoolean("isInternal", true),
                        languageId = obj.optString("languageId", "plaintext"),
                        lastOpened = obj.optLong("lastOpened", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Throwable) {
            // Fail gracefully
        }
        return items.sortedByDescending { it.lastOpened }
    }

    fun addRecentFile(item: RecentFileItem) {
        val current = getRecentFiles().toMutableList()
        current.removeAll { it.pathOrUri == item.pathOrUri }
        current.add(0, item.copy(lastOpened = System.currentTimeMillis()))
        val trimmed = current.take(30)
        saveList(trimmed)
    }

    fun removeRecentFile(pathOrUri: String) {
        val current = getRecentFiles().toMutableList()
        current.removeAll { it.pathOrUri == pathOrUri }
        saveList(current)
    }

    fun clearAll() {
        prefs.edit().remove(KEY_RECENTS).apply()
    }

    private fun saveList(items: List<RecentFileItem>) {
        try {
            val arr = JSONArray()
            for (item in items) {
                val obj = JSONObject().apply {
                    put("name", item.name)
                    put("pathOrUri", item.pathOrUri)
                    put("isInternal", item.isInternal)
                    put("languageId", item.languageId)
                    put("lastOpened", item.lastOpened)
                }
                arr.put(obj)
            }
            prefs.edit().putString(KEY_RECENTS, arr.toString()).apply()
        } catch (_: Throwable) {
            // Fail gracefully
        }
    }

    companion object {
        private const val KEY_RECENTS = "recent_files_list"
    }
}
