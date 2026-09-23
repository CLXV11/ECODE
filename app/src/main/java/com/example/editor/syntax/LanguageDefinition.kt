package com.example.editor.syntax

/**
 * Defines syntax rules and metadata for a programming or markup language.
 */
data class LanguageDefinition(
    val id: String,
    val name: String,
    val extensions: List<String>,
    val icon: LanguageIconDefinition = LanguageIcons.PLAIN_TEXT,
    val mimeTypes: List<String> = emptyList(),
    val shebangs: List<String> = emptyList(),
    val keywords: Set<String> = emptySet(),
    val types: Set<String> = emptySet(),
    val builtins: Set<String> = emptySet(),
    val constants: Set<String> = emptySet(),
    val lineCommentPrefixes: List<String> = emptyList(),
    val blockCommentStart: String? = null,
    val blockCommentEnd: String? = null,
    val stringDelimiters: List<String> = listOf("\"", "'"),
    val brackets: Set<Char> = setOf('(', ')', '{', '}', '[', ']'),
    val isXmlOrHtml: Boolean = false,
    val supportsColonIndent: Boolean = false,
    val isJson: Boolean = false,
    val defaultExtension: String = extensions.firstOrNull() ?: "txt"
) {
    companion object {
        val PLAIN_TEXT = LanguageDefinition(
            id = "plaintext",
            name = "Plain Text",
            extensions = listOf("txt", "text", "log"),
            icon = LanguageIcons.PLAIN_TEXT,
            mimeTypes = listOf("text/plain"),
            defaultExtension = "txt"
        )
    }
}
