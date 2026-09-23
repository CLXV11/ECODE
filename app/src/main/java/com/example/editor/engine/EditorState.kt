package com.example.editor.engine

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Snapshot of editor state for Undo/Redo operations.
 */
data class TextSnapshot(
    val text: String,
    val selection: TextRange,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Manages editor text buffer, undo/redo history, and line metrics.
 */
class EditorState(
    initialText: String = ""
) {
    private val undoStack = ArrayDeque<TextSnapshot>()
    private val redoStack = ArrayDeque<TextSnapshot>()
    private val maxHistorySize = 150

    var currentSnapshot: TextSnapshot = TextSnapshot(initialText, TextRange(0))
        private set

    init {
        undoStack.addLast(currentSnapshot)
    }

    /**
     * Resets state with new text (e.g. when opening a file).
     */
    fun reset(newText: String) {
        undoStack.clear()
        redoStack.clear()
        currentSnapshot = TextSnapshot(newText, TextRange(0))
        undoStack.addLast(currentSnapshot)
    }

    /**
     * Registers a new text change to the history stack.
     */
    fun recordChange(newValue: TextFieldValue) {
        val prev = currentSnapshot
        if (prev.text == newValue.text) {
            // Selection change only, just update current snapshot without cluttering undo stack
            currentSnapshot = TextSnapshot(newValue.text, newValue.selection)
            return
        }

        // Merge small sequential keystrokes if within 600ms and single character change
        val now = System.currentTimeMillis()
        val isSingleCharAppend = newValue.text.length == prev.text.length + 1 &&
                newValue.text.startsWith(prev.text) &&
                (now - prev.timestamp) < 700

        if (isSingleCharAppend && undoStack.size > 1) {
            // Update last undo entry rather than pushing a new one for every character
            currentSnapshot = TextSnapshot(newValue.text, newValue.selection, now)
        } else {
            if (undoStack.size >= maxHistorySize) {
                undoStack.removeFirst()
            }
            undoStack.addLast(currentSnapshot)
            currentSnapshot = TextSnapshot(newValue.text, newValue.selection, now)
            redoStack.clear()
        }
    }

    val canUndo: Boolean
        get() = undoStack.isNotEmpty() && (undoStack.size > 1 || undoStack.last().text != currentSnapshot.text)

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    fun undo(): TextFieldValue? {
        if (!canUndo) return null

        val current = currentSnapshot
        redoStack.addLast(current)

        val target = undoStack.removeLast()
        currentSnapshot = target
        return TextFieldValue(text = target.text, selection = target.selection)
    }

    fun redo(): TextFieldValue? {
        if (!canRedo) return null

        val target = redoStack.removeLast()
        undoStack.addLast(currentSnapshot)
        currentSnapshot = target
        return TextFieldValue(text = target.text, selection = target.selection)
    }

    companion object {
        /**
         * Calculates line numbers and start offsets for each line in the text.
         */
        fun getLineStartOffsets(text: String): List<Int> {
            val offsets = ArrayList<Int>()
            offsets.add(0)
            for (i in text.indices) {
                if (text[i] == '\n') {
                    offsets.add(i + 1)
                }
            }
            return offsets
        }

        /**
         * Finds 1-based line number for a given character index.
         */
        fun getLineNumberForOffset(lineOffsets: List<Int>, offset: Int): Int {
            if (lineOffsets.isEmpty()) return 1
            var low = 0
            var high = lineOffsets.size - 1
            var result = 0

            while (low <= high) {
                val mid = (low + high) ushr 1
                if (lineOffsets[mid] <= offset) {
                    result = mid
                    low = mid + 1
                } else {
                    high = mid - 1
                }
            }
            return result + 1
        }

        /**
         * Returns start character offset for a 1-based line number.
         */
        fun getOffsetForLine(lineOffsets: List<Int>, lineNumber: Int, maxLen: Int): Int {
            if (lineOffsets.isEmpty()) return 0
            val idx = (lineNumber - 1).coerceIn(0, lineOffsets.size - 1)
            return lineOffsets[idx].coerceIn(0, maxLen)
        }

        /**
         * Finds 1-based column number for a given character index.
         */
        fun getColumnNumberForOffset(lineOffsets: List<Int>, offset: Int): Int {
            if (lineOffsets.isEmpty()) return 1
            val lineNum = getLineNumberForOffset(lineOffsets, offset)
            val lineStartOffset = getOffsetForLine(lineOffsets, lineNum, offset)
            return (offset - lineStartOffset + 1).coerceAtLeast(1)
        }
    }
}
