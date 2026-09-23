package com.example.editor.engine

import com.example.editor.syntax.LanguageDefinition

enum class CompletionKind {
    KEYWORD,
    FUNCTION,
    TYPE,
    VARIABLE,
    SNIPPET
}

data class CompletionItem(
    val label: String,
    val kind: CompletionKind,
    val insertText: String = label,
    val detail: String = ""
)

/**
 * Local code intelligence and autocomplete engine.
 */
object AutocompleteEngine {

    private val wordRegex = Regex("""\b[a-zA-Z_][a-zA-Z0-9_]{1,}\b""")

    /**
     * Extracts prefix being typed before cursor.
     */
    fun extractPrefixAtCursor(text: String, cursor: Int): Pair<String, Int> {
        if (text.isEmpty() || cursor <= 0) return Pair("", cursor)

        var start = cursor - 1
        while (start >= 0) {
            val c = text[start]
            if (c.isLetterOrDigit() || c == '_') {
                start--
            } else {
                break
            }
        }
        val prefixStart = start + 1
        val prefix = text.substring(prefixStart, cursor)
        return Pair(prefix, prefixStart)
    }

    /**
     * Generates autocomplete suggestions based on active language and file content.
     */
    fun getSuggestions(
        text: String,
        cursor: Int,
        language: LanguageDefinition,
        maxResults: Int = 12
    ): List<CompletionItem> {
        val (prefix, _) = extractPrefixAtCursor(text, cursor)
        if (prefix.length < 2) return emptyList()

        val results = mutableListOf<CompletionItem>()
        val seen = mutableSetOf<String>()
        val lowerPrefix = prefix.lowercase()

        // 1. Language Keywords
        for (kw in language.keywords) {
            if (kw.lowercase().startsWith(lowerPrefix) && kw != prefix) {
                if (seen.add(kw)) {
                    results.add(CompletionItem(kw, CompletionKind.KEYWORD, kw, "keyword"))
                }
            }
        }

        // 2. Builtins & Types
        for (tp in language.types) {
            if (tp.lowercase().startsWith(lowerPrefix) && tp != prefix) {
                if (seen.add(tp)) {
                    results.add(CompletionItem(tp, CompletionKind.TYPE, tp, "type"))
                }
            }
        }
        for (bi in language.builtins) {
            if (bi.lowercase().startsWith(lowerPrefix) && bi != prefix) {
                if (seen.add(bi)) {
                    results.add(CompletionItem(bi, CompletionKind.FUNCTION, "$bi()", "function"))
                }
            }
        }

        // 3. Document identifiers (variables, functions in the file)
        // Scan a window around cursor or up to 25k characters
        val sample = if (text.length > 30000) {
            val start = (cursor - 15000).coerceAtLeast(0)
            val end = (cursor + 15000).coerceAtMost(text.length)
            text.substring(start, end)
        } else {
            text
        }

        val docWords = wordRegex.findAll(sample)
        for (match in docWords) {
            val word = match.value
            if (word.length > 2 && word.lowercase().startsWith(lowerPrefix) && word != prefix) {
                if (seen.add(word)) {
                    val kind = if (word.first().isUpperCase()) CompletionKind.TYPE else CompletionKind.VARIABLE
                    results.add(CompletionItem(word, kind, word, "identifier"))
                    if (results.size >= maxResults) break
                }
            }
        }

        return results.take(maxResults)
    }
}
