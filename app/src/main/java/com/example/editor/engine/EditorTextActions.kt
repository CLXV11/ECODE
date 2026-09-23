package com.example.editor.engine

import androidx.compose.ui.text.TextRange
import com.example.editor.syntax.LanguageDefinition

/**
 * Pure functions for advanced editor text transformations (comments, duplication, deletion).
 */
object EditorTextActions {

    /**
     * Toggles single-line comment on the current line or selection according to language syntax.
     */
    fun toggleComment(
        text: String,
        selection: TextRange,
        language: LanguageDefinition
    ): Pair<String, TextRange> {
        val commentPrefix = language.lineCommentPrefixes.firstOrNull() ?: "//"
        val commentToken = "$commentPrefix "

        if (text.isEmpty()) {
            return commentToken to TextRange(commentToken.length)
        }

        val start = selection.min.coerceIn(0, text.length)
        val end = selection.max.coerceIn(0, text.length)

        // Find start of first line
        val firstLineStart = text.lastIndexOf('\n', (start - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }

        // Find end of last line
        val effectiveEnd = if (end > start && text[end - 1] == '\n') end - 1 else end
        val lastLineEnd = text.indexOf('\n', effectiveEnd).let {
            if (it == -1) text.length else it
        }

        val targetSubstring = text.substring(firstLineStart, lastLineEnd)
        val lines = targetSubstring.split("\n")

        // Check if all non-empty lines are already commented
        val nonBlankLines = lines.filter { it.isNotBlank() }
        val allCommented = nonBlankLines.isNotEmpty() && nonBlankLines.all { line ->
            line.trimStart().startsWith(commentPrefix)
        }

        val transformedLines = if (allCommented) {
            // Uncomment each line
            lines.map { line ->
                if (line.isBlank()) {
                    line
                } else {
                    val leadingSpaceCount = line.indexOfFirst { !it.isWhitespace() }
                    if (leadingSpaceCount != -1) {
                        val indent = line.substring(0, leadingSpaceCount)
                        val afterIndent = line.substring(leadingSpaceCount)
                        if (afterIndent.startsWith(commentToken)) {
                            indent + afterIndent.removePrefix(commentToken)
                        } else if (afterIndent.startsWith(commentPrefix)) {
                            indent + afterIndent.removePrefix(commentPrefix).trimStart(' ')
                        } else {
                            line
                        }
                    } else {
                        line
                    }
                }
            }
        } else {
            // Comment each non-blank line with the prefix
            lines.map { line ->
                if (line.isBlank()) {
                    line
                } else {
                    val leadingSpaceCount = line.indexOfFirst { !it.isWhitespace() }
                    if (leadingSpaceCount != -1) {
                        val indent = line.substring(0, leadingSpaceCount)
                        val content = line.substring(leadingSpaceCount)
                        "$indent$commentToken$content"
                    } else {
                        "$commentToken$line"
                    }
                }
            }
        }

        val newSubstring = transformedLines.joinToString("\n")
        val newText = text.substring(0, firstLineStart) + newSubstring + text.substring(lastLineEnd)
        val delta = newSubstring.length - targetSubstring.length

        val newSelection = if (selection.start == selection.end) {
            val newCursor = (selection.start + delta).coerceIn(0, newText.length)
            TextRange(newCursor)
        } else {
            TextRange(firstLineStart, (lastLineEnd + delta).coerceIn(firstLineStart, newText.length))
        }

        return newText to newSelection
    }

    /**
     * Duplicates the current line or selection.
     */
    fun duplicateLineOrSelection(
        text: String,
        selection: TextRange
    ): Pair<String, TextRange> {
        if (text.isEmpty()) {
            return "" to TextRange.Zero
        }

        if (selection.start != selection.end) {
            // Duplicate selection
            val start = selection.min.coerceIn(0, text.length)
            val end = selection.max.coerceIn(0, text.length)
            val selected = text.substring(start, end)
            val newText = text.substring(0, end) + selected + text.substring(end)
            return newText to TextRange(end, end + selected.length)
        } else {
            // Duplicate current line
            val cursor = selection.start.coerceIn(0, text.length)
            val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let {
                if (it == -1) 0 else it + 1
            }
            val lineEnd = text.indexOf('\n', cursor).let {
                if (it == -1) text.length else it
            }
            val lineText = text.substring(lineStart, lineEnd)

            val (newText, newCursor) = if (lineEnd == text.length) {
                (text + "\n" + lineText) to (text.length + 1 + (cursor - lineStart))
            } else {
                (text.substring(0, lineEnd) + "\n" + lineText + text.substring(lineEnd)) to (lineEnd + 1 + (cursor - lineStart))
            }

            return newText to TextRange(newCursor.coerceIn(0, newText.length))
        }
    }

    /**
     * Deletes the line at cursor or lines covered by selection.
     */
    fun deleteCurrentLine(
        text: String,
        selection: TextRange
    ): Pair<String, TextRange> {
        if (text.isEmpty()) return "" to TextRange.Zero

        val start = selection.min.coerceIn(0, text.length)
        val end = selection.max.coerceIn(0, text.length)

        val lineStart = text.lastIndexOf('\n', (start - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val nextNewline = text.indexOf('\n', end)
        val lineEnd = if (nextNewline == -1) text.length else nextNewline + 1

        val newText = text.substring(0, lineStart) + text.substring(lineEnd)
        val newCursor = lineStart.coerceIn(0, newText.length)

        return newText to TextRange(newCursor)
    }
}
