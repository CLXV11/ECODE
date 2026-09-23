package com.example.editor.tabs

import com.example.editor.io.EditorFile
import com.example.editor.syntax.LanguageDefinition
import java.util.UUID

/**
 * Model representing an open editor tab.
 */
data class EditorTab(
    val id: String = UUID.randomUUID().toString(),
    val file: EditorFile,
    var content: String,
    var isModified: Boolean = false,
    var language: LanguageDefinition,
    var cursorPosition: Int = 0
) {
    val title: String
        get() = file.name
}
