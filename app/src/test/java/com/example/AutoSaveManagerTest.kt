package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.editor.autosave.AutoSaveManager
import com.example.editor.engine.EditorState
import com.example.editor.io.EditorFile
import com.example.editor.syntax.LanguageRegistry
import com.example.editor.tabs.EditorTab
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class AutoSaveManagerTest {

    private lateinit var context: Context
    private lateinit var autoSaveManager: AutoSaveManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        autoSaveManager = AutoSaveManager(context)
        runBlocking {
            autoSaveManager.clearAll()
        }
    }

    @Test
    fun testSaveAndLoadSnapshots() = runBlocking {
        val file1 = EditorFile(name = "index.html", absolutePath = "/test/index.html")
        val file2 = EditorFile(name = "main.py", absolutePath = "/test/main.py")

        val tab1 = EditorTab(
            id = "tab-1",
            file = file1,
            content = "<h1>Hello World</h1>",
            isModified = true,
            language = LanguageRegistry.HTML,
            cursorPosition = 10
        )
        val tab2 = EditorTab(
            id = "tab-2",
            file = file2,
            content = "print('Hello AutoSave')",
            isModified = false,
            language = LanguageRegistry.PYTHON,
            cursorPosition = 5
        )

        val saveTimestamp = autoSaveManager.saveSnapshots(listOf(tab1, tab2), activeIndex = 1)
        assertTrue(saveTimestamp > 0)
        assertTrue(autoSaveManager.hasSnapshots())

        val loaded = autoSaveManager.loadSnapshots()
        assertNotNull(loaded)
        val (snapshots, activeIndex) = loaded!!

        assertEquals(1, activeIndex)
        assertEquals(2, snapshots.size)

        val snap1 = snapshots.first { it.id == "tab-1" }
        assertEquals("index.html", snap1.name)
        assertEquals("<h1>Hello World</h1>", snap1.content)
        assertTrue(snap1.isModified)
        assertEquals(10, snap1.cursorPosition)
        assertEquals("html", snap1.languageId)

        val snap2 = snapshots.first { it.id == "tab-2" }
        assertEquals("main.py", snap2.name)
        assertEquals("print('Hello AutoSave')", snap2.content)
        assertFalse(snap2.isModified)
        assertEquals(5, snap2.cursorPosition)

        // Convert back to EditorTab
        val restoredTab1 = snap1.toEditorTab()
        assertEquals("tab-1", restoredTab1.id)
        assertEquals("index.html", restoredTab1.file.name)
        assertEquals("<h1>Hello World</h1>", restoredTab1.content)
        assertTrue(restoredTab1.isModified)
        assertEquals(10, restoredTab1.cursorPosition)
    }

    @Test
    fun testDiscardTabSnapshot() = runBlocking {
        val file = EditorFile(name = "temp.js", absolutePath = "/test/temp.js")
        val tab = EditorTab(
            id = "tab-discard",
            file = file,
            content = "console.log('discard me');",
            isModified = true,
            language = LanguageRegistry.JAVASCRIPT
        )

        autoSaveManager.saveSnapshots(listOf(tab), activeIndex = 0)
        assertTrue(autoSaveManager.hasSnapshots())

        autoSaveManager.discardTab("tab-discard")
        autoSaveManager.saveSnapshots(emptyList(), activeIndex = 0)
        assertFalse(autoSaveManager.hasSnapshots())
    }

    @Test
    fun testEditorStateColumnCalculation() {
        val text = "first line\nsecond line with code\nthird"
        val offsets = EditorState.getLineStartOffsets(text)

        // Start of first line
        assertEquals(1, EditorState.getColumnNumberForOffset(offsets, 0))
        // 5th character on first line
        assertEquals(6, EditorState.getColumnNumberForOffset(offsets, 5))

        // Start of second line (after '\n' at index 10)
        val secondLineStart = offsets[1]
        assertEquals(1, EditorState.getColumnNumberForOffset(offsets, secondLineStart))
        // 4th character on second line
        assertEquals(5, EditorState.getColumnNumberForOffset(offsets, secondLineStart + 4))
    }
}
