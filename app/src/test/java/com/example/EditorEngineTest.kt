package com.example

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.editor.engine.AutoIndent
import com.example.editor.engine.BracketHelper
import com.example.editor.engine.EditorState
import com.example.editor.engine.SearchEngine
import com.example.editor.io.EncodingDetector
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.LanguageRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorEngineTest {

    @Test
    fun testLanguageDetectionByExtension() {
        assertEquals("python", LanguageRegistry.detectLanguage("test.py").id)
        assertEquals("javascript", LanguageRegistry.detectLanguage("app.js").id)
        assertEquals("kotlin", LanguageRegistry.detectLanguage("MainActivity.kt").id)
        assertEquals("rust", LanguageRegistry.detectLanguage("main.rs").id)
        assertEquals("csharp", LanguageRegistry.detectLanguage("Program.cs").id)
        assertEquals("html", LanguageRegistry.detectLanguage("index.html").id)
        assertEquals("json", LanguageRegistry.detectLanguage("data.json").id)
        assertEquals("markdown", LanguageRegistry.detectLanguage("README.md").id)
        assertEquals("sql", LanguageRegistry.detectLanguage("queries.sql").id)
    }

    @Test
    fun testLanguageDetectionByShebang() {
        val pythonShebang = "#!/usr/bin/env python3\nprint('hello')"
        assertEquals("python", LanguageRegistry.detectLanguage("script", pythonShebang).id)

        val nodeShebang = "#!/usr/bin/env node\nconsole.log('hi')"
        assertEquals("javascript", LanguageRegistry.detectLanguage("runner", nodeShebang).id)

        val bashShebang = "#!/bin/bash\necho test"
        assertEquals("shell", LanguageRegistry.detectLanguage("build", bashShebang).id)
    }

    @Test
    fun testSearchEngine() {
        val sample = "val apple = 1\nval banana = 2\nval applePie = 3"

        // Plain search
        val matches = SearchEngine.findMatches(sample, "apple", matchCase = true, wholeWord = false, useRegex = false)
        assertEquals(2, matches.size)

        // Whole word search
        val wholeWordMatches = SearchEngine.findMatches(sample, "apple", matchCase = true, wholeWord = true, useRegex = false)
        assertEquals(1, wholeWordMatches.size)
        assertEquals(sample.indexOf("val apple").plus(4), wholeWordMatches[0].range.first)

        // Regex search
        val regexMatches = SearchEngine.findMatches(sample, "val \\w+ =", matchCase = false, wholeWord = false, useRegex = true)
        assertEquals(3, regexMatches.size)

        // Replace all
        val (replaced, count) = SearchEngine.replaceAll(sample, "val", "def", matchCase = true, wholeWord = false, useRegex = false)
        assertEquals(3, count)
        assertTrue(replaced.startsWith("def apple"))
    }

    @Test
    fun testAutoIndent() {
        val code = "def my_func():\n"
        val indent = AutoIndent.calculateEnterIndent(code, code.length, tabSize = 4, useSpaces = true, supportsColonIndent = true)
        assertEquals("    ", indent)

        val indentedLines = AutoIndent.indentLines("hello\nworld", 0, 11, tabSize = 4, useSpaces = true)
        assertEquals("    hello\n    world", indentedLines.first)

        val dedentedLines = AutoIndent.dedentLines("    hello\n    world", 0, 19, tabSize = 4, useSpaces = true)
        assertEquals("hello\nworld", dedentedLines.first)
    }

    @Test
    fun testBracketHelper() {
        // Auto-close opening bracket
        val initial = TextFieldValue("abc", TextRange(3))
        val processed = BracketHelper.processCharInsertion(initial, '(', autoCloseEnabled = true)
        assertNotNull(processed)
        assertEquals("abc()", processed?.text)
        assertEquals(4, processed?.selection?.start)

        // Step-over closing bracket
        val atParen = TextFieldValue("abc)", TextRange(3))
        val stepped = BracketHelper.processCharInsertion(atParen, ')', autoCloseEnabled = true)
        assertNotNull(stepped)
        assertEquals("abc)", stepped?.text)
        assertEquals(4, stepped?.selection?.start)

        // Backspace between pairs
        val between = TextFieldValue("abc()", TextRange(4))
        val backspaced = BracketHelper.processBackspace(between)
        assertNotNull(backspaced)
        assertEquals("abc", backspaced?.text)
    }

    @Test
    fun testEditorStateUndoRedo() {
        val state = EditorState()
        state.reset("version 1")

        state.recordChange(TextFieldValue("version 2", TextRange(9)))
        assertTrue(state.canUndo)
        assertFalse(state.canRedo)

        val undone = state.undo()
        assertNotNull(undone)
        assertEquals("version 1", undone?.text)
        assertTrue(state.canRedo)

        val redone = state.redo()
        assertNotNull(redone)
        assertEquals("version 2", redone?.text)
    }

    @Test
    fun testEncodingDetector() {
        // UTF-8 BOM
        val utf8Bom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte(), 'H'.code.toByte(), 'i'.code.toByte())
        val detectedBom = EncodingDetector.detect(utf8Bom)
        assertTrue(detectedBom.hasBom)
        assertEquals("UTF-8 BOM", detectedBom.name)
        assertEquals("Hi", EncodingDetector.decode(utf8Bom, detectedBom))

        // Standard UTF-8
        val utf8 = "Hello, CLXV11!".toByteArray(Charsets.UTF_8)
        val detectedUtf8 = EncodingDetector.detect(utf8)
        assertFalse(detectedUtf8.hasBom)
        assertEquals("Hello, CLXV11!", EncodingDetector.decode(utf8, detectedUtf8))
    }

    @Test
    fun testWebRunnerDetectionAndPayload() {
        assertTrue(com.example.editor.runner.WebRunnerContentBuilder.isWebRunnable("index.html"))
        assertTrue(com.example.editor.runner.WebRunnerContentBuilder.isWebRunnable("style.css"))
        assertTrue(com.example.editor.runner.WebRunnerContentBuilder.isWebRunnable("script.js"))
        assertTrue(com.example.editor.runner.WebRunnerContentBuilder.isWebRunnable("module.mjs"))
        assertFalse(com.example.editor.runner.WebRunnerContentBuilder.isWebRunnable("main.rs"))

        val htmlPayload = com.example.editor.runner.WebRunnerContentBuilder.buildPayload(
            title = "index.html",
            rawContent = "<!DOCTYPE html><html><head><title>Test</title></head><body><h1>Hello</h1></body></html>",
            fileType = com.example.editor.runner.WebFileType.HTML,
            fileDir = null,
            openTabs = emptyList()
        )
        assertTrue(htmlPayload.htmlToLoad.contains("CLXV11_Bridge"))
        assertTrue(htmlPayload.htmlToLoad.contains("<h1>Hello</h1>"))

        val jsPayload = com.example.editor.runner.WebRunnerContentBuilder.buildPayload(
            title = "calc.js",
            rawContent = "console.log('Result:', 1 + 2);",
            fileType = com.example.editor.runner.WebFileType.JAVASCRIPT,
            fileDir = null,
            openTabs = emptyList()
        )
        assertTrue(jsPayload.htmlToLoad.contains("CLXV11_Bridge"))
        assertTrue(jsPayload.htmlToLoad.contains("calc.js"))
        assertTrue(jsPayload.htmlToLoad.contains("1 + 2"))

        val cssPayload = com.example.editor.runner.WebRunnerContentBuilder.buildPayload(
            title = "theme.css",
            rawContent = "body { background: #000; color: #fff; }",
            fileType = com.example.editor.runner.WebFileType.CSS,
            fileDir = null,
            openTabs = emptyList()
        )
        assertTrue(cssPayload.htmlToLoad.contains("<style>"))
        assertTrue(cssPayload.htmlToLoad.contains("theme.css"))
    }
}
