package com.example.editor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.engine.AutoIndent
import com.example.editor.engine.BracketHelper
import com.example.editor.engine.EditorState
import com.example.editor.settings.EditorFont
import com.example.editor.settings.EditorSettings
import com.example.editor.shortcuts.EditorShortcutActions
import com.example.editor.shortcuts.EditorShortcuts
import androidx.compose.ui.input.key.onPreviewKeyEvent
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.SyntaxTheme
import com.example.editor.syntax.SyntaxVisualTransformation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch

@Composable
fun CodeEditorView(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    language: LanguageDefinition,
    settings: EditorSettings,
    theme: SyntaxTheme,
    searchRanges: List<IntRange> = emptyList(),
    activeSearchRangeIndex: Int = -1,
    shortcutActions: EditorShortcutActions? = null,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val text = value.text
    val cursor = value.selection.start

    // Compute line start offsets
    val lineOffsets = remember(text) {
        EditorState.getLineStartOffsets(text)
    }

    val currentLineNumber = remember(cursor, lineOffsets) {
        EditorState.getLineNumberForOffset(lineOffsets, cursor)
    }

    val matchingBracketPositions = remember(text, cursor) {
        BracketHelper.findMatchingBracket(text, cursor)
    }

    // Font configuration
    val fontFamily = when (settings.editorFont) {
        EditorFont.MONOSPACE -> FontFamily.Monospace
        EditorFont.SANS_SERIF -> FontFamily.SansSerif
        EditorFont.SERIF -> FontFamily.Serif
    }

    val textStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = settings.fontSizeSp.sp,
        lineHeight = (settings.fontSizeSp * 1.55).sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        color = theme.text
    )

    // Visual transformation with syntax highlighting
    val visualTransformation = remember(
        language,
        theme,
        searchRanges,
        activeSearchRangeIndex,
        matchingBracketPositions,
        settings.syntaxHighlightingEnabled
    ) {
        if (settings.syntaxHighlightingEnabled) {
            SyntaxVisualTransformation(
                language = language,
                theme = theme,
                searchRanges = searchRanges,
                activeSearchRangeIndex = activeSearchRangeIndex,
                matchingBracketPositions = matchingBracketPositions
            )
        } else {
            SyntaxVisualTransformation(
                language = LanguageDefinition.PLAIN_TEXT,
                theme = theme,
                searchRanges = searchRanges,
                activeSearchRangeIndex = activeSearchRangeIndex,
                matchingBracketPositions = matchingBracketPositions
            )
        }
    }

    // Always enforce LTR layout direction inside the code editor
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(theme.background)
        ) {
            val containerHeightPx = with(LocalDensity.current) { maxHeight.toPx() }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                // Line numbers gutter
                if (settings.showLineNumbers) {
                    val lineCount = maxOf(1, lineOffsets.size)
                    val charWidth = (settings.fontSizeSp * 0.65f).dp
                    val calculatedWidth = charWidth * (lineCount.toString().length + 2) + 14.dp
                    val gutterWidth = if (calculatedWidth < 38.dp) 38.dp else calculatedWidth

                    Box(
                        modifier = Modifier
                            .width(gutterWidth)
                            .background(theme.background)
                            .padding(top = 8.dp, bottom = 24.dp, start = 4.dp, end = 8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        val linesString = remember(lineCount) {
                            (1..lineCount).joinToString("\n")
                        }

                        Text(
                            text = linesString,
                            style = textStyle.copy(
                                color = theme.lineNumbers,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Vertical dividing line
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(theme.lineNumbers.copy(alpha = 0.25f))
                    )
                }

                // Editor Text Field Area
                val horizontalModifier = if (!settings.wordWrap) {
                    Modifier.horizontalScroll(horizontalScrollState)
                } else {
                    Modifier.fillMaxWidth()
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(horizontalModifier)
                        .padding(start = 12.dp, end = 48.dp, top = 8.dp, bottom = 24.dp)
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = { incoming ->
                            var finalValue = incoming

                            // 1. Intercept ENTER to compute auto-indentation
                            if (settings.autoIndent && incoming.text.length == text.length + 1) {
                                val changedIdx = incoming.selection.start - 1
                                if (changedIdx >= 0 && changedIdx < incoming.text.length && incoming.text[changedIdx] == '\n') {
                                    val indentStr = AutoIndent.calculateEnterIndent(
                                        text = incoming.text,
                                        cursorOffset = changedIdx,
                                        tabSize = settings.tabSize,
                                        useSpaces = settings.useSpacesForTab,
                                        supportsColonIndent = language.supportsColonIndent
                                    )
                                    if (indentStr.isNotEmpty()) {
                                        val newText = incoming.text.substring(0, changedIdx + 1) + indentStr + incoming.text.substring(changedIdx + 1)
                                        finalValue = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(changedIdx + 1 + indentStr.length)
                                        )
                                    }
                                }
                            }

                            // 2. Intercept single-character typing for bracket auto-close
                            if (settings.autoCloseBrackets && incoming.text.length == text.length + 1) {
                                val typedIdx = incoming.selection.start - 1
                                if (typedIdx in incoming.text.indices) {
                                    val typedChar = incoming.text[typedIdx]
                                    if (typedChar in "()[]{}\"'`") {
                                        val processed = BracketHelper.processCharInsertion(
                                            current = value,
                                            char = typedChar,
                                            autoCloseEnabled = true
                                        )
                                        if (processed != null) {
                                            finalValue = processed
                                        }
                                    }
                                }
                            }

                            // 3. Intercept backspace between matching brackets
                            if (incoming.text.length == text.length - 1 && value.selection.start == value.selection.end) {
                                val backspaceProcessed = BracketHelper.processBackspace(value)
                                if (backspaceProcessed != null) {
                                    finalValue = backspaceProcessed
                                }
                            }

                            onValueChange(finalValue)
                        },
                        textStyle = textStyle,
                        cursorBrush = SolidColor(theme.cursor),
                        visualTransformation = visualTransformation,
                        modifier = Modifier
                            .testTag("code_editor_text_field")
                            .then(
                                if (shortcutActions != null) {
                                    Modifier.onPreviewKeyEvent { event ->
                                        EditorShortcuts.handleKeyEvent(event, shortcutActions)
                                    }
                                } else {
                                    Modifier
                                }
                            )
                            .fillMaxWidth()
                            .widthIn(min = 600.dp)
                    )
                }
            }

            // High-precision fast scrollbar handle pinned to the right edge
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            ) {
                FastScrollerOverlay(
                    scrollState = verticalScrollState,
                    totalLines = maxOf(1, lineOffsets.size),
                    containerHeightPx = containerHeightPx
                )
            }
        }
    }
}

@Composable
private fun FastScrollerOverlay(
    scrollState: ScrollState,
    totalLines: Int,
    containerHeightPx: Float
) {
    if (scrollState.maxValue <= 0 || containerHeightPx <= 0f) return

    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }

    val trackHeight = containerHeightPx
    val thumbMinHeight = 54f
    val thumbHeight = (trackHeight * (trackHeight / (trackHeight + scrollState.maxValue)))
        .coerceIn(thumbMinHeight, trackHeight * 0.35f)
    val availableTravel = (trackHeight - thumbHeight).coerceAtLeast(1f)

    val currentRatio = (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
    val thumbOffset = currentRatio * availableTravel
    val currentLine = (currentRatio * (totalLines - 1)).toInt() + 1

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(44.dp)
            .pointerInput(scrollState.maxValue, availableTravel) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val newRatio = ((offset.y - thumbHeight / 2) / availableTravel).coerceIn(0f, 1f)
                        coroutineScope.launch {
                            scrollState.scrollTo((newRatio * scrollState.maxValue).toInt())
                        }
                    },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = (thumbOffset + dragAmount).coerceIn(0f, availableTravel)
                        val newRatio = (newOffset / availableTravel).coerceIn(0f, 1f)
                        coroutineScope.launch {
                            scrollState.scrollTo((newRatio * scrollState.maxValue).toInt())
                        }
                    }
                )
            }
    ) {
        // Fast scroller thumb handle pinned to the edge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(x = 0, y = thumbOffset.toInt()) }
                .padding(end = 3.dp)
                .width(if (isDragging) 8.dp else 5.dp)
                .height(with(LocalDensity.current) { thumbHeight.toDp() })
                .clip(RoundedCornerShape(6.dp))
                .background(if (isDragging) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.50f))
        )

        // Line number badge during fast scrub
        if (isDragging) {
            Surface(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = -130,
                            y = (thumbOffset + thumbHeight / 2 - 20).toInt().coerceAtLeast(10)
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 6.dp
            ) {
                Text(
                    text = "Ln $currentLine / $totalLines",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
