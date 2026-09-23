package com.example.editor.shortcuts

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

enum class ShortcutCategory(val title: String) {
    FILE("File Operations"),
    EDITING("Editing & History"),
    NAVIGATION("Search & Navigation"),
    LINE_OPS("Line Operations")
}

data class EditorShortcut(
    val id: String,
    val description: String,
    val keyCombo: String,
    val category: ShortcutCategory
)

data class EditorShortcutActions(
    val onSave: () -> Unit,
    val onSaveAs: (() -> Unit)? = null,
    val onUndo: () -> Unit,
    val onRedo: () -> Unit,
    val onSearch: () -> Unit,
    val onGoToLine: () -> Unit,
    val onCloseTab: () -> Unit,
    val onNewFile: () -> Unit,
    val onSelectAll: () -> Unit,
    val onDuplicateLine: () -> Unit,
    val onDeleteLine: () -> Unit,
    val onToggleComment: () -> Unit,
    val onIndent: () -> Unit,
    val onDedent: () -> Unit
)

object EditorShortcuts {

    val ALL_SHORTCUTS: List<EditorShortcut> = listOf(
        // File Operations
        EditorShortcut("save", "Save current file", "Ctrl + S", ShortcutCategory.FILE),
        EditorShortcut("save_as", "Save as / Export", "Ctrl + Shift + S", ShortcutCategory.FILE),
        EditorShortcut("new_file", "New file", "Ctrl + N", ShortcutCategory.FILE),
        EditorShortcut("close_tab", "Close active tab", "Ctrl + W", ShortcutCategory.FILE),

        // Editing & History
        EditorShortcut("undo", "Undo edit", "Ctrl + Z", ShortcutCategory.EDITING),
        EditorShortcut("redo", "Redo edit", "Ctrl + Y / Ctrl + Shift + Z", ShortcutCategory.EDITING),
        EditorShortcut("select_all", "Select all text", "Ctrl + A", ShortcutCategory.EDITING),
        EditorShortcut("toggle_comment", "Toggle line comment", "Ctrl + /", ShortcutCategory.EDITING),

        // Line Operations
        EditorShortcut("duplicate_line", "Duplicate line / selection", "Ctrl + D", ShortcutCategory.LINE_OPS),
        EditorShortcut("delete_line", "Delete current line", "Ctrl + Shift + K", ShortcutCategory.LINE_OPS),
        EditorShortcut("indent", "Indent line or selection", "Tab", ShortcutCategory.LINE_OPS),
        EditorShortcut("dedent", "Outdent / Dedent line", "Shift + Tab", ShortcutCategory.LINE_OPS),

        // Navigation
        EditorShortcut("find_replace", "Find & Replace", "Ctrl + F", ShortcutCategory.NAVIGATION),
        EditorShortcut("goto_line", "Go to line number", "Ctrl + G", ShortcutCategory.NAVIGATION)
    )

    /**
     * Intercepts key events and executes matching editor actions.
     * Returns true if the key event was recognized and consumed.
     */
    fun handleKeyEvent(event: KeyEvent, actions: EditorShortcutActions): Boolean {
        if (event.type != KeyEventType.KeyDown) {
            return false
        }

        val isCtrlOrCmd = event.isCtrlPressed || event.isMetaPressed

        if (isCtrlOrCmd) {
            return when (event.key) {
                // Save (Ctrl+S / Ctrl+Shift+S)
                Key.S -> {
                    if (event.isShiftPressed) {
                        actions.onSaveAs?.invoke() ?: actions.onSave()
                    } else {
                        actions.onSave()
                    }
                    true
                }

                // Undo / Redo (Ctrl+Z / Ctrl+Shift+Z)
                Key.Z -> {
                    if (event.isShiftPressed) {
                        actions.onRedo()
                    } else {
                        actions.onUndo()
                    }
                    true
                }

                // Redo (Ctrl+Y)
                Key.Y -> {
                    actions.onRedo()
                    true
                }

                // Search & Replace (Ctrl+F)
                Key.F -> {
                    actions.onSearch()
                    true
                }

                // Go to Line (Ctrl+G)
                Key.G -> {
                    actions.onGoToLine()
                    true
                }

                // Close Tab (Ctrl+W)
                Key.W -> {
                    actions.onCloseTab()
                    true
                }

                // New File (Ctrl+N)
                Key.N -> {
                    actions.onNewFile()
                    true
                }

                // Select All (Ctrl+A)
                Key.A -> {
                    actions.onSelectAll()
                    true
                }

                // Duplicate Line / Selection (Ctrl+D)
                Key.D -> {
                    actions.onDuplicateLine()
                    true
                }

                // Toggle Comment (Ctrl+/)
                Key.Slash, Key.Backslash -> {
                    actions.onToggleComment()
                    true
                }

                // Delete Current Line (Ctrl+Shift+K)
                Key.K -> {
                    if (event.isShiftPressed) {
                        actions.onDeleteLine()
                        true
                    } else {
                        false
                    }
                }

                else -> false
            }
        } else {
            // Non-modifier shortcuts
            if (event.key == Key.Tab) {
                if (event.isShiftPressed) {
                    actions.onDedent()
                    return true
                } else {
                    actions.onIndent()
                    return true
                }
            }
        }

        return false
    }
}

/**
 * Modifier extension to attach keyboard shortcuts listener.
 */
fun Modifier.editorKeyboardShortcuts(actions: EditorShortcutActions): Modifier {
    return this.onPreviewKeyEvent { event ->
        EditorShortcuts.handleKeyEvent(event, actions)
    }
}
