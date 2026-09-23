package com.example.editor.syntax

import androidx.compose.ui.graphics.Color

/**
 * Color scheme for syntax highlighting and editor elements.
 */
data class SyntaxTheme(
    val isDark: Boolean,
    val background: Color,
    val text: Color,
    val cursor: Color,
    val lineNumbers: Color,
    val currentLineBackground: Color,
    val selection: Color,
    val keyword: Color,
    val function: Color,
    val type: Color,
    val string: Color,
    val number: Color,
    val comment: Color,
    val operator: Color,
    val bracket: Color,
    val constant: Color,
    val builtin: Color,
    val tag: Color,
    val attribute: Color,
    val searchMatchBackground: Color,
    val activeSearchMatchBackground: Color,
    val matchingBracketBackground: Color
) {
    companion object {
        val PitchBlack = SyntaxTheme(
            isDark = true,
            background = Color(0xFF000000),
            text = Color(0xFFF0F6FC),
            cursor = Color(0xFF58A6FF),
            lineNumbers = Color(0xFF7D8590),
            currentLineBackground = Color(0xFF0F141C),
            selection = Color(0x66264F78),
            keyword = Color(0xFFFF7B72),
            function = Color(0xFFD2A8FF),
            type = Color(0xFFFFA657),
            string = Color(0xFFA5D6FF),
            number = Color(0xFF79C0FF),
            comment = Color(0xFF8B949E),
            operator = Color(0xFFFF7B72),
            bracket = Color(0xFFC9D1D9),
            constant = Color(0xFF79C0FF),
            builtin = Color(0xFFFFA657),
            tag = Color(0xFF7EE787),
            attribute = Color(0xFF79C0FF),
            searchMatchBackground = Color(0x66E3B341),
            activeSearchMatchBackground = Color(0xAAF2CC60),
            matchingBracketBackground = Color(0x4458A6FF)
        )

        val Dark = SyntaxTheme(
            isDark = true,
            background = Color(0xFF161B22),
            text = Color(0xFFE6EDF3),
            cursor = Color(0xFF58A6FF),
            lineNumbers = Color(0xFF6E7681),
            currentLineBackground = Color(0xFF21262D),
            selection = Color(0x55388BFD),
            keyword = Color(0xFFFF7B72),
            function = Color(0xFFD2A8FF),
            type = Color(0xFFFFA657),
            string = Color(0xFFA5D6FF),
            number = Color(0xFF79C0FF),
            comment = Color(0xFF8B949E),
            operator = Color(0xFFFF7B72),
            bracket = Color(0xFFC9D1D9),
            constant = Color(0xFF79C0FF),
            builtin = Color(0xFFFFA657),
            tag = Color(0xFF7EE787),
            attribute = Color(0xFF79C0FF),
            searchMatchBackground = Color(0x66E3B341),
            activeSearchMatchBackground = Color(0xAAF2CC60),
            matchingBracketBackground = Color(0x4458A6FF)
        )

        val Light = SyntaxTheme(
            isDark = false,
            background = Color(0xFFFFFFFF),
            text = Color(0xFF24292F),
            cursor = Color(0xFF0969DA),
            lineNumbers = Color(0xFF8C959F),
            currentLineBackground = Color(0xFFF6F8FA),
            selection = Color(0x4454AEFF),
            keyword = Color(0xFFCF222E),
            function = Color(0xFF8250DF),
            type = Color(0xFF953800),
            string = Color(0xFF0A3069),
            number = Color(0xFF0550AE),
            comment = Color(0xFF6E7781),
            operator = Color(0xFFCF222E),
            bracket = Color(0xFF24292F),
            constant = Color(0xFF0550AE),
            builtin = Color(0xFF953800),
            tag = Color(0xFF116329),
            attribute = Color(0xFF0550AE),
            searchMatchBackground = Color(0x66FFDF5D),
            activeSearchMatchBackground = Color(0xCCFFD33D),
            matchingBracketBackground = Color(0x440969DA)
        )
    }
}
