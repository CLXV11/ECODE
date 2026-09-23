package com.example.editor.engine

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Result of a search hit in code.
 */
data class SearchMatch(
    val range: IntRange,
    val lineNumber: Int,
    val lineSnippet: String
)

/**
 * High-performance search and replace engine.
 */
object SearchEngine {

    fun findMatches(
        text: String,
        query: String,
        matchCase: Boolean = false,
        wholeWord: Boolean = false,
        useRegex: Boolean = false
    ): List<SearchMatch> {
        if (query.isEmpty() || text.isEmpty()) return emptyList()

        val matches = mutableListOf<SearchMatch>()
        val lineOffsets = EditorState.getLineStartOffsets(text)

        try {
            if (useRegex) {
                var flags = 0
                if (!matchCase) flags = flags or Pattern.CASE_INSENSITIVE
                val pattern = Pattern.compile(query, flags)
                val matcher = pattern.matcher(text)
                var count = 0
                while (matcher.find() && count < 2000) {
                    val start = matcher.start()
                    val end = matcher.end()
                    if (start == end) continue // prevent infinite loop on empty match

                    val lineNum = EditorState.getLineNumberForOffset(lineOffsets, start)
                    val snippet = extractSnippet(text, start, end)
                    matches.add(SearchMatch(IntRange(start, end - 1), lineNum, snippet))
                    count++
                }
            } else {
                val targetText = if (matchCase) text else text.lowercase()
                val targetQuery = if (matchCase) query else query.lowercase()
                val queryLen = targetQuery.length

                var index = 0
                var count = 0
                while (index <= targetText.length - queryLen && count < 2000) {
                    val found = targetText.indexOf(targetQuery, index)
                    if (found == -1) break

                    var valid = true
                    if (wholeWord) {
                        val beforeValid = found == 0 || !text[found - 1].isLetterOrDigit() && text[found - 1] != '_'
                        val afterIndex = found + queryLen
                        val afterValid = afterIndex >= text.length || !text[afterIndex].isLetterOrDigit() && text[afterIndex] != '_'
                        valid = beforeValid && afterValid
                    }

                    if (valid) {
                        val lineNum = EditorState.getLineNumberForOffset(lineOffsets, found)
                        val snippet = extractSnippet(text, found, found + queryLen)
                        matches.add(SearchMatch(IntRange(found, found + queryLen - 1), lineNum, snippet))
                        count++
                    }

                    index = found + queryLen.coerceAtLeast(1)
                }
            }
        } catch (_: PatternSyntaxException) {
            // Invalid regex syntax - return empty matches safely
        } catch (_: Throwable) {
            // General exception safety
        }

        return matches
    }

    fun replaceMatch(
        text: String,
        match: SearchMatch,
        replacement: String
    ): Pair<String, IntRange> {
        val start = match.range.first.coerceIn(0, text.length)
        val end = (match.range.last + 1).coerceIn(0, text.length)
        if (start >= end) return Pair(text, match.range)

        val newText = text.substring(0, start) + replacement + text.substring(end)
        val newRange = IntRange(start, start + replacement.length - 1)
        return Pair(newText, newRange)
    }

    fun replaceAll(
        text: String,
        query: String,
        replacement: String,
        matchCase: Boolean = false,
        wholeWord: Boolean = false,
        useRegex: Boolean = false
    ): Pair<String, Int> {
        val matches = findMatches(text, query, matchCase, wholeWord, useRegex)
        if (matches.isEmpty()) return Pair(text, 0)

        val sb = StringBuilder()
        var lastIdx = 0
        var count = 0

        for (m in matches) {
            val start = m.range.first
            val end = m.range.last + 1
            if (start >= lastIdx) {
                sb.append(text.substring(lastIdx, start))
                sb.append(replacement)
                lastIdx = end
                count++
            }
        }
        if (lastIdx < text.length) {
            sb.append(text.substring(lastIdx))
        }

        return Pair(sb.toString(), count)
    }

    private fun extractSnippet(text: String, matchStart: Int, matchEnd: Int): String {
        val lineStart = text.lastIndexOf('\n', matchStart - 1).let { if (it == -1) 0 else it + 1 }
        val lineEnd = text.indexOf('\n', matchEnd).let { if (it == -1) text.length else it }
        val rawLine = text.substring(lineStart, lineEnd).trim()
        return if (rawLine.length > 80) rawLine.substring(0, 80) + "..." else rawLine
    }
}
