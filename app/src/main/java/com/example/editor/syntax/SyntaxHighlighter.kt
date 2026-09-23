package com.example.editor.syntax

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Syntax visual transformation and tokenization engine for Jetpack Compose.
 */
class SyntaxVisualTransformation(
    private val language: LanguageDefinition,
    private val theme: SyntaxTheme,
    private val searchRanges: List<IntRange> = emptyList(),
    private val activeSearchRangeIndex: Int = -1,
    private val matchingBracketPositions: Pair<Int, Int>? = null
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val annotated = highlightCode(
            text = raw,
            language = language,
            theme = theme,
            searchRanges = searchRanges,
            activeSearchRangeIndex = activeSearchRangeIndex,
            matchingBracketPositions = matchingBracketPositions
        )

        return TransformedText(annotated, OffsetMapping.Identity)
    }

    companion object {
        /**
         * Highlights code and returns an AnnotatedString with styling spans.
         */
        fun highlightCode(
            text: String,
            language: LanguageDefinition,
            theme: SyntaxTheme,
            searchRanges: List<IntRange> = emptyList(),
            activeSearchRangeIndex: Int = -1,
            matchingBracketPositions: Pair<Int, Int>? = null
        ): AnnotatedString {
            // Safety check for massive files (e.g. > 500k characters) to avoid UI freezing
            val length = text.length
            val isHuge = length > 300_000

            val builder = AnnotatedString.Builder(text)

            // Plain text only gets search highlights and bracket matching
            if (language.id == LanguageDefinition.PLAIN_TEXT.id || isHuge) {
                applySearchAndBracketHighlights(
                    builder = builder,
                    length = length,
                    theme = theme,
                    searchRanges = searchRanges,
                    activeSearchRangeIndex = activeSearchRangeIndex,
                    matchingBracketPositions = matchingBracketPositions
                )
                return builder.toAnnotatedString()
            }

            try {
                if (language.isJson) {
                    highlightJson(builder, text, theme)
                } else if (language.isXmlOrHtml) {
                    highlightXmlOrHtml(builder, text, language, theme)
                } else {
                    highlightGeneralCode(builder, text, language, theme)
                }
            } catch (_: Throwable) {
                // Fail gracefully without crashing
            }

            // Apply search match highlights on top
            applySearchAndBracketHighlights(
                builder = builder,
                length = length,
                theme = theme,
                searchRanges = searchRanges,
                activeSearchRangeIndex = activeSearchRangeIndex,
                matchingBracketPositions = matchingBracketPositions
            )

            return builder.toAnnotatedString()
        }

        private fun applySearchAndBracketHighlights(
            builder: AnnotatedString.Builder,
            length: Int,
            theme: SyntaxTheme,
            searchRanges: List<IntRange>,
            activeSearchRangeIndex: Int,
            matchingBracketPositions: Pair<Int, Int>?
        ) {
            // Search highlights
            for (i in searchRanges.indices) {
                val range = searchRanges[i]
                val start = range.first.coerceIn(0, length)
                val end = (range.last + 1).coerceIn(0, length)
                if (start < end) {
                    val bg = if (i == activeSearchRangeIndex) {
                        theme.activeSearchMatchBackground
                    } else {
                        theme.searchMatchBackground
                    }
                    builder.addStyle(
                        SpanStyle(background = bg, fontWeight = FontWeight.Bold),
                        start,
                        end
                    )
                }
            }

            // Matching brackets
            if (matchingBracketPositions != null) {
                val p1 = matchingBracketPositions.first
                val p2 = matchingBracketPositions.second
                if (p1 in 0 until length) {
                    builder.addStyle(
                        SpanStyle(
                            background = theme.matchingBracketBackground,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        p1,
                        p1 + 1
                    )
                }
                if (p2 in 0 until length) {
                    builder.addStyle(
                        SpanStyle(
                            background = theme.matchingBracketBackground,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        p2,
                        p2 + 1
                    )
                }
            }
        }

        private fun highlightGeneralCode(
            builder: AnnotatedString.Builder,
            text: String,
            language: LanguageDefinition,
            theme: SyntaxTheme
        ) {
            val len = text.length
            var i = 0

            val keywordStyle = SpanStyle(color = theme.keyword, fontWeight = FontWeight.Bold)
            val typeStyle = SpanStyle(color = theme.type, fontWeight = FontWeight.SemiBold)
            val constantStyle = SpanStyle(color = theme.constant, fontWeight = FontWeight.SemiBold)
            val builtinStyle = SpanStyle(color = theme.builtin)
            val stringStyle = SpanStyle(color = theme.string)
            val numberStyle = SpanStyle(color = theme.number)
            val commentStyle = SpanStyle(color = theme.comment)
            val operatorStyle = SpanStyle(color = theme.operator)
            val bracketStyle = SpanStyle(color = theme.bracket)
            val functionStyle = SpanStyle(color = theme.function)

            while (i < len) {
                val ch = text[i]

                // 1. Block comments
                if (language.blockCommentStart != null && text.startsWith(language.blockCommentStart, i)) {
                    val endToken = language.blockCommentEnd ?: "*/"
                    val end = text.indexOf(endToken, i + language.blockCommentStart.length)
                    val commentEnd = if (end != -1) end + endToken.length else len
                    builder.addStyle(commentStyle, i, commentEnd)
                    i = commentEnd
                    continue
                }

                // 2. Line comments
                var isLineComment = false
                for (prefix in language.lineCommentPrefixes) {
                    if (text.startsWith(prefix, i)) {
                        val end = text.indexOf('\n', i + prefix.length)
                        val commentEnd = if (end != -1) end else len
                        builder.addStyle(commentStyle, i, commentEnd)
                        i = commentEnd
                        isLineComment = true
                        break
                    }
                }
                if (isLineComment) continue

                // 3. String literals
                var isString = false
                for (delimiter in language.stringDelimiters) {
                    if (text.startsWith(delimiter, i)) {
                        val delimLen = delimiter.length
                        var j = i + delimLen
                        var escaped = false
                        while (j < len) {
                            if (text.startsWith(delimiter, j) && !escaped) {
                                j += delimLen
                                break
                            }
                            if (text[j] == '\\') {
                                escaped = !escaped
                            } else {
                                escaped = false
                            }
                            // Don't let single-line strings cross newlines (unless multiline delimiters)
                            if (delimLen == 1 && text[j] == '\n') {
                                break
                            }
                            j++
                        }
                        builder.addStyle(stringStyle, i, j.coerceAtMost(len))
                        i = j
                        isString = true
                        break
                    }
                }
                if (isString) continue

                // 4. Numbers
                if (ch.isDigit() || (ch == '.' && i + 1 < len && text[i + 1].isDigit())) {
                    val start = i
                    var j = i
                    // Hex or Binary or Decimal
                    if (ch == '0' && j + 1 < len && (text[j + 1] == 'x' || text[j + 1] == 'X' || text[j + 1] == 'b' || text[j + 1] == 'B')) {
                        j += 2
                        while (j < len && (text[j].isLetterOrDigit() || text[j] == '_')) j++
                    } else {
                        var hasDot = false
                        var hasExp = false
                        while (j < len) {
                            val c = text[j]
                            if (c.isDigit() || c == '_') {
                                j++
                            } else if (c == '.' && !hasDot) {
                                hasDot = true
                                j++
                            } else if ((c == 'e' || c == 'E') && !hasExp) {
                                hasExp = true
                                j++
                                if (j < len && (text[j] == '+' || text[j] == '-')) j++
                            } else if (c in "fFdDlL") {
                                j++
                                break
                            } else {
                                break
                            }
                        }
                    }
                    builder.addStyle(numberStyle, start, j.coerceAtMost(len))
                    i = j
                    continue
                }

                // 5. Identifiers & Keywords
                if (ch.isLetter() || ch == '_' || ch == '$' || ch == '@') {
                    val start = i
                    var j = i
                    while (j < len && (text[j].isLetterOrDigit() || text[j] == '_' || text[j] == '$' || text[j] == '@')) {
                        j++
                    }
                    val word = text.substring(start, j)

                    when {
                        language.keywords.contains(word) -> {
                            builder.addStyle(keywordStyle, start, j)
                        }
                        language.types.contains(word) -> {
                            builder.addStyle(typeStyle, start, j)
                        }
                        language.constants.contains(word) -> {
                            builder.addStyle(constantStyle, start, j)
                        }
                        language.builtins.contains(word) -> {
                            builder.addStyle(builtinStyle, start, j)
                        }
                        word.startsWith("@") -> {
                            // Annotation / Decorator
                            builder.addStyle(typeStyle, start, j)
                        }
                        else -> {
                            // Check if next non-whitespace char is '(' -> function call / definition
                            var k = j
                            while (k < len && (text[k] == ' ' || text[k] == '\t')) k++
                            if (k < len && text[k] == '(') {
                                builder.addStyle(functionStyle, start, j)
                            } else if (word.first().isUpperCase() && !word.all { it.isUpperCase() }) {
                                // Likely Class or Type PascalCase
                                builder.addStyle(typeStyle, start, j)
                            }
                        }
                    }
                    i = j
                    continue
                }

                // 6. Brackets & Operators
                if (language.brackets.contains(ch)) {
                    builder.addStyle(bracketStyle, i, i + 1)
                } else if (ch in "+-*/%=&|!<>^~?:;.,") {
                    builder.addStyle(operatorStyle, i, i + 1)
                }

                i++
            }
        }

        private fun highlightJson(
            builder: AnnotatedString.Builder,
            text: String,
            theme: SyntaxTheme
        ) {
            val len = text.length
            var i = 0

            val stringStyle = SpanStyle(color = theme.string)
            val keyStyle = SpanStyle(color = theme.keyword, fontWeight = FontWeight.SemiBold)
            val numberStyle = SpanStyle(color = theme.number)
            val constantStyle = SpanStyle(color = theme.constant, fontWeight = FontWeight.Bold)
            val bracketStyle = SpanStyle(color = theme.bracket)
            val punctuationStyle = SpanStyle(color = theme.operator)

            while (i < len) {
                val ch = text[i]

                if (ch == '"') {
                    val start = i
                    var j = i + 1
                    var escaped = false
                    while (j < len) {
                        if (text[j] == '"' && !escaped) {
                            j++
                            break
                        }
                        if (text[j] == '\\') escaped = !escaped else escaped = false
                        j++
                    }
                    // Check if followed by ':' -> this is a JSON key
                    var k = j
                    while (k < len && (text[k] == ' ' || text[k] == '\t' || text[k] == '\n' || text[k] == '\r')) k++
                    if (k < len && text[k] == ':') {
                        builder.addStyle(keyStyle, start, j.coerceAtMost(len))
                    } else {
                        builder.addStyle(stringStyle, start, j.coerceAtMost(len))
                    }
                    i = j
                    continue
                }

                if (ch.isDigit() || ch == '-') {
                    val start = i
                    var j = i
                    while (j < len && (text[j].isDigit() || text[j] in ".eE+-")) j++
                    builder.addStyle(numberStyle, start, j.coerceAtMost(len))
                    i = j
                    continue
                }

                if (ch.isLetter()) {
                    val start = i
                    var j = i
                    while (j < len && text[j].isLetter()) j++
                    val word = text.substring(start, j)
                    if (word == "true" || word == "false" || word == "null") {
                        builder.addStyle(constantStyle, start, j)
                    }
                    i = j
                    continue
                }

                if (ch in "{}[]") {
                    builder.addStyle(bracketStyle, i, i + 1)
                } else if (ch in ":,") {
                    builder.addStyle(punctuationStyle, i, i + 1)
                }

                i++
            }
        }

        private fun highlightXmlOrHtml(
            builder: AnnotatedString.Builder,
            text: String,
            language: LanguageDefinition,
            theme: SyntaxTheme
        ) {
            val len = text.length
            var i = 0

            val tagStyle = SpanStyle(color = theme.tag, fontWeight = FontWeight.Bold)
            val attrStyle = SpanStyle(color = theme.attribute)
            val stringStyle = SpanStyle(color = theme.string)
            val commentStyle = SpanStyle(color = theme.comment)
            val punctuationStyle = SpanStyle(color = theme.operator)

            while (i < len) {
                // Comments <!-- ... -->
                if (text.startsWith("<!--", i)) {
                    val end = text.indexOf("-->", i + 4)
                    val commentEnd = if (end != -1) end + 3 else len
                    builder.addStyle(commentStyle, i, commentEnd)
                    i = commentEnd
                    continue
                }

                // Tags <tag ... >
                if (text[i] == '<') {
                    val startTag = i
                    var j = i + 1
                    if (j < len && (text[j] == '/' || text[j] == '!' || text[j] == '?')) {
                        j++
                    }
                    // Tag name
                    val nameStart = j
                    while (j < len && (text[j].isLetterOrDigit() || text[j] in "-_:")) j++
                    if (nameStart < j) {
                        builder.addStyle(punctuationStyle, startTag, nameStart)
                        builder.addStyle(tagStyle, nameStart, j)
                    }

                    // Inside tag (attributes & values until '>')
                    while (j < len && text[j] != '>') {
                        val c = text[j]
                        if (c == '"' || c == '\'') {
                            val strStart = j
                            val quote = c
                            j++
                            while (j < len && text[j] != quote) {
                                if (text[j] == '\\' && j + 1 < len) j++
                                j++
                            }
                            if (j < len) j++
                            builder.addStyle(stringStyle, strStart, j.coerceAtMost(len))
                            continue
                        } else if (c.isLetter() || c == '_' || c == ':') {
                            val attrStart = j
                            while (j < len && (text[j].isLetterOrDigit() || text[j] in "-_:.")) j++
                            builder.addStyle(attrStyle, attrStart, j)
                            continue
                        } else if (c == '=' || c == '/') {
                            builder.addStyle(punctuationStyle, j, j + 1)
                        }
                        j++
                    }

                    if (j < len && text[j] == '>') {
                        builder.addStyle(punctuationStyle, j, j + 1)
                        j++
                    }
                    i = j
                    continue
                }

                i++
            }
        }
    }
}
