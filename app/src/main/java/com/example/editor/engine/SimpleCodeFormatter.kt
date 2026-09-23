package com.example.editor.engine

import org.json.JSONArray
import org.json.JSONObject

/**
 * Safe, non-destructive code formatting utilities.
 */
object SimpleCodeFormatter {

    /**
     * Formats JSON content if valid; returns null if malformed.
     */
    fun formatJson(jsonStr: String, indentSpaces: Int = 4): String? {
        val trimmed = jsonStr.trim()
        return try {
            if (trimmed.startsWith("{")) {
                JSONObject(trimmed).toString(indentSpaces)
            } else if (trimmed.startsWith("[")) {
                JSONArray(trimmed).toString(indentSpaces)
            } else {
                null
            }
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Cleans trailing whitespace and normalizes indentation without altering logic.
     */
    fun formatCleanWhitespace(text: String): String {
        return text.lines().joinToString("\n") { it.trimEnd() }
    }
}
