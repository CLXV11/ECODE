package com.example.editor.engine

import androidx.compose.ui.text.TextRange

/**
 * Handles automatic indentation, indenting, and outdenting for code editing.
 */
object AutoIndent {

    fun getIndentString(tabSize: Int, useSpaces: Boolean): String {
        return if (useSpaces) {
            " ".repeat(tabSize.coerceIn(1, 8))
        } else {
            "\t"
        }
    }

    /**
     * Calculates the indentation string to insert when user presses ENTER.
     */
    fun calculateEnterIndent(
        text: String,
        cursorOffset: Int,
        tabSize: Int,
        useSpaces: Boolean,
        supportsColonIndent: Boolean = false
    ): String {
        val indentUnit = getIndentString(tabSize, useSpaces)
        if (text.isEmpty() || cursorOffset <= 0) return ""

        // Find the start of the line before cursor
        val safeOffset = cursorOffset.coerceIn(0, text.length)
        val effectiveEnd = if (safeOffset > 0 && text[safeOffset - 1] == '\n') safeOffset - 1 else safeOffset
        if (effectiveEnd < 0) return ""
        val lineStart = text.lastIndexOf('\n', (effectiveEnd - 1).coerceAtLeast(0)).let {
            if (it == -1 || effectiveEnd == 0) 0 else it + 1
        }
        val lineBeforeCursor = text.substring(lineStart, effectiveEnd)

        // Extract leading spaces or tabs
        val leadingWhitespace = StringBuilder()
        for (c in lineBeforeCursor) {
            if (c == ' ' || c == '\t') {
                leadingWhitespace.append(c)
            } else {
                break
            }
        }

        val trimmed = lineBeforeCursor.trimEnd()
        // Check if line triggers deeper indentation: ends with '{', '(', '[', or ':' (for Python)
        val shouldIndentFurther = trimmed.endsWith('{') ||
                trimmed.endsWith('(') ||
                trimmed.endsWith('[') ||
                (supportsColonIndent && trimmed.endsWith(':'))

        if (shouldIndentFurther) {
            leadingWhitespace.append(indentUnit)
        }

        return leadingWhitespace.toString()
    }

    /**
     * Indents the selected lines (or current line) by one indent level.
     */
    fun indentLines(
        text: String,
        startOffset: Int,
        endOffset: Int,
        tabSize: Int,
        useSpaces: Boolean
    ): Pair<String, TextRange> {
        val indentUnit = getIndentString(tabSize, useSpaces)
        val minOffset = minOf(startOffset, endOffset).coerceIn(0, text.length)
        val maxOffset = maxOf(startOffset, endOffset).coerceIn(0, text.length)

        val firstLineStart = text.lastIndexOf('\n', (minOffset - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lastLineEnd = text.indexOf('\n', maxOffset).let {
            if (it == -1) text.length else it
        }

        val targetSection = text.substring(firstLineStart, lastLineEnd)
        val lines = targetSection.split('\n')
        val newLines = lines.map { indentUnit + it }
        val replaced = newLines.joinToString("\n")

        val newText = text.substring(0, firstLineStart) + replaced + text.substring(lastLineEnd)
        val addedPerLine = indentUnit.length
        val newEnd = (maxOffset + addedPerLine * lines.size).coerceAtMost(newText.length)
        val newStart = (minOffset + addedPerLine).coerceAtMost(newEnd)

        return Pair(newText, TextRange(newStart, newEnd))
    }

    /**
     * Dedents (outdents) the selected lines by removing one indent level.
     */
    fun dedentLines(
        text: String,
        startOffset: Int,
        endOffset: Int,
        tabSize: Int,
        useSpaces: Boolean
    ): Pair<String, TextRange> {
        val minOffset = minOf(startOffset, endOffset).coerceIn(0, text.length)
        val maxOffset = maxOf(startOffset, endOffset).coerceIn(0, text.length)

        val firstLineStart = text.lastIndexOf('\n', (minOffset - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lastLineEnd = text.indexOf('\n', maxOffset).let {
            if (it == -1) text.length else it
        }

        val targetSection = text.substring(firstLineStart, lastLineEnd)
        val lines = targetSection.split('\n')
        var totalRemoved = 0

        val newLines = lines.map { line ->
            var removedFromThisLine = 0
            val res = if (line.startsWith("\t")) {
                removedFromThisLine = 1
                line.substring(1)
            } else {
                var spacesToRemove = 0
                while (spacesToRemove < tabSize && spacesToRemove < line.length && line[spacesToRemove] == ' ') {
                    spacesToRemove++
                }
                removedFromThisLine = spacesToRemove
                line.substring(spacesToRemove)
            }
            totalRemoved += removedFromThisLine
            res
        }

        val replaced = newLines.joinToString("\n")
        val newText = text.substring(0, firstLineStart) + replaced + text.substring(lastLineEnd)
        val newStart = (minOffset - (totalRemoved / lines.size.coerceAtLeast(1))).coerceIn(0, newText.length)
        val newEnd = (maxOffset - totalRemoved).coerceIn(newStart, newText.length)

        return Pair(newText, TextRange(newStart, newEnd))
    }
}
