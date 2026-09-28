package com.example

import androidx.compose.ui.text.TextRange
import com.example.editor.engine.EditorTextActions
import com.example.editor.shortcuts.EditorShortcutActions
import com.example.editor.shortcuts.EditorShortcuts
import com.example.editor.syntax.LanguageRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class KeyboardShortcutsTest {

    private fun createKeyEvent(
        keyCode: Int,
        metaState: Int = 0,
        action: Int = android.view.KeyEvent.ACTION_DOWN
    ): androidx.compose.ui.input.key.KeyEvent {
        val nativeEvent = android.view.KeyEvent(
            0L,
            0L,
            action,
            keyCode,
            0,
            metaState
        )
        return androidx.compose.ui.input.key.KeyEvent(nativeEvent)
    }

    @Test
    fun testCtrlS_triggersSave() {
        var saved = false
        val actions = createMockActions(onSave = { saved = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_S,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(saved)
    }

    @Test
    fun testCtrlShiftS_triggersSaveAs() {
        var savedAs = false
        val actions = createMockActions(onSaveAs = { savedAs = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_S,
            metaState = android.view.KeyEvent.META_CTRL_ON or android.view.KeyEvent.META_SHIFT_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(savedAs)
    }

    @Test
    fun testCtrlZ_triggersUndo() {
        var undone = false
        val actions = createMockActions(onUndo = { undone = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_Z,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(undone)
    }

    @Test
    fun testCtrlShiftZ_triggersRedo() {
        var redone = false
        val actions = createMockActions(onRedo = { redone = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_Z,
            metaState = android.view.KeyEvent.META_CTRL_ON or android.view.KeyEvent.META_SHIFT_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(redone)
    }

    @Test
    fun testCtrlY_triggersRedo() {
        var redone = false
        val actions = createMockActions(onRedo = { redone = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_Y,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(redone)
    }

    @Test
    fun testCtrlF_triggersSearch() {
        var searched = false
        val actions = createMockActions(onSearch = { searched = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_F,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(searched)
    }

    @Test
    fun testCtrlG_triggersGoToLine() {
        var goToLine = false
        val actions = createMockActions(onGoToLine = { goToLine = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_G,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(goToLine)
    }

    @Test
    fun testCtrlW_triggersCloseTab() {
        var tabClosed = false
        val actions = createMockActions(onCloseTab = { tabClosed = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_W,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(tabClosed)
    }

    @Test
    fun testCtrlN_triggersNewFile() {
        var newFile = false
        val actions = createMockActions(onNewFile = { newFile = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_N,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(newFile)
    }

    @Test
    fun testCtrlA_triggersSelectAll() {
        var selectAll = false
        val actions = createMockActions(onSelectAll = { selectAll = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_A,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(selectAll)
    }

    @Test
    fun testCtrlD_triggersDuplicateLine() {
        var duplicateLine = false
        val actions = createMockActions(onDuplicateLine = { duplicateLine = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_D,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(duplicateLine)
    }

    @Test
    fun testCtrlSlash_triggersToggleComment() {
        var toggleComment = false
        val actions = createMockActions(onToggleComment = { toggleComment = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_SLASH,
            metaState = android.view.KeyEvent.META_CTRL_ON
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertTrue(handled)
        assertTrue(toggleComment)
    }

    @Test
    fun testTabAndShiftTab_triggersIndentAndDedent() {
        var indent = false
        var dedent = false
        val actions = createMockActions(
            onIndent = { indent = true },
            onDedent = { dedent = true }
        )

        val tabEvent = createKeyEvent(android.view.KeyEvent.KEYCODE_TAB)
        assertTrue(EditorShortcuts.handleKeyEvent(tabEvent, actions))
        assertTrue(indent)

        val shiftTabEvent = createKeyEvent(
            android.view.KeyEvent.KEYCODE_TAB,
            metaState = android.view.KeyEvent.META_SHIFT_ON
        )
        assertTrue(EditorShortcuts.handleKeyEvent(shiftTabEvent, actions))
        assertTrue(dedent)
    }

    @Test
    fun testKeyUp_ignored() {
        var saved = false
        val actions = createMockActions(onSave = { saved = true })

        val event = createKeyEvent(
            android.view.KeyEvent.KEYCODE_S,
            metaState = android.view.KeyEvent.META_CTRL_ON,
            action = android.view.KeyEvent.ACTION_UP
        )

        val handled = EditorShortcuts.handleKeyEvent(event, actions)
        assertFalse(handled)
        assertFalse(saved)
    }

    @Test
    fun testToggleComment_kotlin() {
        val original = "val a = 1\nval b = 2"
        val lang = LanguageRegistry.KOTLIN
        val (commented, _) = EditorTextActions.toggleComment(
            text = original,
            selection = TextRange(0, original.length),
            language = lang
        )
        assertEquals("// val a = 1\n// val b = 2", commented)

        // Toggling again should uncomment
        val (uncommented, _) = EditorTextActions.toggleComment(
            text = commented,
            selection = TextRange(0, commented.length),
            language = lang
        )
        assertEquals("val a = 1\nval b = 2", uncommented)
    }

    @Test
    fun testToggleComment_python() {
        val original = "def foo():\n    pass"
        val lang = LanguageRegistry.PYTHON
        val (commented, _) = EditorTextActions.toggleComment(
            text = original,
            selection = TextRange(0, original.length),
            language = lang
        )
        assertEquals("# def foo():\n    # pass", commented)
    }

    @Test
    fun testDuplicateLine_atCursor() {
        val original = "first line\nsecond line"
        val (duplicated, _) = EditorTextActions.duplicateLineOrSelection(
            text = original,
            selection = TextRange(3) // on "first line"
        )
        assertEquals("first line\nfirst line\nsecond line", duplicated)
    }

    @Test
    fun testDeleteLine_atCursor() {
        val original = "first line\nsecond line\nthird line"
        val (deleted, _) = EditorTextActions.deleteCurrentLine(
            text = original,
            selection = TextRange(15) // on "second line"
        )
        assertEquals("first line\nthird line", deleted)
    }

    private fun createMockActions(
        onSave: () -> Unit = {},
        onSaveAs: (() -> Unit)? = null,
        onUndo: () -> Unit = {},
        onRedo: () -> Unit = {},
        onSearch: () -> Unit = {},
        onGoToLine: () -> Unit = {},
        onCloseTab: () -> Unit = {},
        onNewFile: () -> Unit = {},
        onSelectAll: () -> Unit = {},
        onDuplicateLine: () -> Unit = {},
        onDeleteLine: () -> Unit = {},
        onToggleComment: () -> Unit = {},
        onIndent: () -> Unit = {},
        onDedent: () -> Unit = {}
    ): EditorShortcutActions {
        return EditorShortcutActions(
            onSave = onSave,
            onSaveAs = onSaveAs,
            onUndo = onUndo,
            onRedo = onRedo,
            onSearch = onSearch,
            onGoToLine = onGoToLine,
            onCloseTab = onCloseTab,
            onNewFile = onNewFile,
            onSelectAll = onSelectAll,
            onDuplicateLine = onDuplicateLine,
            onDeleteLine = onDeleteLine,
            onToggleComment = onToggleComment,
            onIndent = onIndent,
            onDedent = onDedent
        )
    }
}
