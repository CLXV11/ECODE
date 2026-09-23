package com.example.editor.engine

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Handles automatic bracket closing, selection wrapping, step-over, and bracket match detection.
 */
object BracketHelper {

    private val bracketPairs = mapOf(
        '(' to ')',
        '{' to '}',
        '[' to ']',
        '"' to '"',
        '\'' to '\'',
        '`' to '`'
    )

    private val reverseBracketPairs = mapOf(
        ')' to '(',
        '}' to '{',
        ']' to '['
    )

    fun getClosingChar(openChar: Char): Char? = bracketPairs[openChar]

    /**
     * Intercepts and auto-closes brackets, quotes, or wraps selected text.
     */
    fun processCharInsertion(
        current: TextFieldValue,
        char: Char,
        autoCloseEnabled: Boolean
    ): TextFieldValue? {
        if (!autoCloseEnabled) return null

        val text = current.text
        val selection = current.selection

        // 1. Text is selected: Wrap selection with paired brackets
        if (selection.start != selection.end) {
            val min = minOf(selection.start, selection.end).coerceIn(0, text.length)
            val max = maxOf(selection.start, selection.end).coerceIn(0, text.length)
            val closeChar = bracketPairs[char]
            if (closeChar != null) {
                val selectedText = text.substring(min, max)
                val wrapped = "$char$selectedText$closeChar"
                val newText = text.substring(0, min) + wrapped + text.substring(max)
                return TextFieldValue(newText, TextRange(min + 1, max + 1))
            }
            return null
        }

        val cursor = selection.start.coerceIn(0, text.length)

        // 2. Step over closing bracket or quote if already right at cursor
        if (char in reverseBracketPairs.keys || char == '"' || char == '\'' || char == '`') {
            if (cursor < text.length && text[cursor] == char) {
                // If it's a quote, only step over if not preceded by escape
                val isEscaped = cursor > 0 && text[cursor - 1] == '\\'
                if (!isEscaped) {
                    return TextFieldValue(text, TextRange(cursor + 1))
                }
            }
        }

        // 3. Auto-close opening bracket or quote
        val close = bracketPairs[char]
        if (close != null) {
            val nextChar = if (cursor < text.length) text[cursor] else null
            // Don't auto-close quotes if immediately preceding an alphanumeric or following an escape
            if (char == '"' || char == '\'' || char == '`') {
                if (cursor > 0 && text[cursor - 1] == '\\') return null
                if (nextChar != null && (nextChar.isLetterOrDigit() || nextChar == '_')) return null
            }

            val newText = text.substring(0, cursor) + "$char$close" + text.substring(cursor)
            return TextFieldValue(newText, TextRange(cursor + 1))
        }

        return null
    }

    /**
     * If backspacing between matching pairs (e.g. `(|)`), deletes both.
     */
    fun processBackspace(current: TextFieldValue): TextFieldValue? {
        val text = current.text
        val selection = current.selection
        if (selection.start != selection.end) return null

        val cursor = selection.start
        if (cursor in 1 until text.length) {
            val before = text[cursor - 1]
            val after = text[cursor]
            if (bracketPairs[before] == after) {
                val newText = text.substring(0, cursor - 1) + text.substring(cursor + 1)
                return TextFieldValue(newText, TextRange(cursor - 1))
            }
        }
        return null
    }

    /**
     * Detects matching bracket position for the character adjacent to cursor.
     */
    fun findMatchingBracket(text: String, cursor: Int): Pair<Int, Int>? {
        if (text.isEmpty() || cursor < 0) return null

        // Check character right before cursor, or at cursor
        val candidatePos = when {
            cursor > 0 && isBracket(text[cursor - 1]) -> cursor - 1
            cursor < text.length && isBracket(text[cursor]) -> cursor
            else -> return null
        }

        val ch = text[candidatePos]

        // Forward match for opening bracket
        val closing = bracketPairs[ch]
        if (closing != null && ch != closing) {
            var depth = 1
            for (i in (candidatePos + 1) until text.length) {
                if (text[i] == ch) {
                    depth++
                } else if (text[i] == closing) {
                    depth--
                    if (depth == 0) {
                        return Pair(candidatePos, i)
                    }
                }
            }
        }

        // Backward match for closing bracket
        val opening = reverseBracketPairs[ch]
        if (opening != null) {
            var depth = 1
            for (i in (candidatePos - 1) downTo 0) {
                if (text[i] == ch) {
                    depth++
                } else if (text[i] == opening) {
                    depth--
                    if (depth == 0) {
                        return Pair(i, candidatePos)
                    }
                }
            }
        }

        return null
    }

    private fun isBracket(c: Char): Boolean =
        c in "()[]{}"
}
