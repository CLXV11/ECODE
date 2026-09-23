package com.example.editor.io

import java.io.File
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.LanguageRegistry

data class WorkspaceItem(
    val file: File,
    val name: String,
    val isDirectory: Boolean,
    val extension: String = file.extension,
    val languageDefinition: LanguageDefinition = if (isDirectory) LanguageDefinition.PLAIN_TEXT else LanguageRegistry.detectLanguage(name),
    val languageName: String = if (isDirectory) "Folder" else languageDefinition.name,
    val sizeBytes: Long = 0L,
    val lastModified: Long = 0L
)
