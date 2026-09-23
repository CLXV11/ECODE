package com.example.editor.syntax

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual badge/symbol style representation for each file type and programming language.
 * Clean, lightweight, vector-rendered without emoji or external web dependencies.
 */
enum class LanguageIconStyle {
    PYTHON,
    JAVASCRIPT,
    TYPESCRIPT,
    JSX,
    TSX,
    HTML,
    CSS,
    SCSS,
    LESS,
    JSON,
    XML,
    KOTLIN,
    JAVA,
    C,
    CPP,
    CSHARP,
    GO,
    RUST,
    SWIFT,
    DART,
    PHP,
    RUBY,
    LUA,
    R,
    PERL,
    SHELL,
    POWERSHELL,
    SQL,
    YAML,
    TOML,
    MARKDOWN,
    OBJECTIVE_C,
    OBJECTIVE_CPP,
    SCALA,
    GROOVY,
    HASKELL,
    ELIXIR,
    ERLANG,
    ASSEMBLY,
    MAKEFILE,
    DOCKERFILE,
    GRADLE,
    INI,
    PROPERTIES,
    CSV,
    GRAPHQL,
    VUE,
    SVELTE,
    JULIA,
    LATEX,
    ASTRO,
    AUTOHOTKEY,
    GIT,
    GENERIC_CODE,
    PLAIN_TEXT
}

data class LanguageIconDefinition(
    val style: LanguageIconStyle,
    val primaryColor: Color,
    val badgeBgColor: Color,
    val label: String
)

object LanguageIcons {

    val PYTHON = LanguageIconDefinition(
        style = LanguageIconStyle.PYTHON,
        primaryColor = Color(0xFFFFD43B),
        badgeBgColor = Color(0xFF3776AB),
        label = "PY"
    )

    val JAVASCRIPT = LanguageIconDefinition(
        style = LanguageIconStyle.JAVASCRIPT,
        primaryColor = Color(0xFF000000),
        badgeBgColor = Color(0xFFF7DF1E),
        label = "JS"
    )

    val TYPESCRIPT = LanguageIconDefinition(
        style = LanguageIconStyle.TYPESCRIPT,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF3178C6),
        label = "TS"
    )

    val JSX = LanguageIconDefinition(
        style = LanguageIconStyle.JSX,
        primaryColor = Color(0xFF61DAFB),
        badgeBgColor = Color(0xFF20232A),
        label = "JSX"
    )

    val TSX = LanguageIconDefinition(
        style = LanguageIconStyle.TSX,
        primaryColor = Color(0xFF3178C6),
        badgeBgColor = Color(0xFF20232A),
        label = "TSX"
    )

    val HTML = LanguageIconDefinition(
        style = LanguageIconStyle.HTML,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFE34F26),
        label = "<>"
    )

    val CSS = LanguageIconDefinition(
        style = LanguageIconStyle.CSS,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF1572B6),
        label = "#"
    )

    val SCSS = LanguageIconDefinition(
        style = LanguageIconStyle.SCSS,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFCC6699),
        label = "SC"
    )

    val LESS = LanguageIconDefinition(
        style = LanguageIconStyle.LESS,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF1D365D),
        label = "LE"
    )

    val JSON = LanguageIconDefinition(
        style = LanguageIconStyle.JSON,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFCB3837),
        label = "{}"
    )

    val XML = LanguageIconDefinition(
        style = LanguageIconStyle.XML,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF0060A8),
        label = "XML"
    )

    val KOTLIN = LanguageIconDefinition(
        style = LanguageIconStyle.KOTLIN,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF7F52FF),
        label = "KT"
    )

    val JAVA = LanguageIconDefinition(
        style = LanguageIconStyle.JAVA,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFF89820),
        label = "JV"
    )

    val C = LanguageIconDefinition(
        style = LanguageIconStyle.C,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF555555),
        label = "C"
    )

    val CPP = LanguageIconDefinition(
        style = LanguageIconStyle.CPP,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF00599C),
        label = "C++"
    )

    val CSHARP = LanguageIconDefinition(
        style = LanguageIconStyle.CSHARP,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF239120),
        label = "C#"
    )

    val GO = LanguageIconDefinition(
        style = LanguageIconStyle.GO,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF00ADD8),
        label = "GO"
    )

    val RUST = LanguageIconDefinition(
        style = LanguageIconStyle.RUST,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFDEA584),
        label = "RS"
    )

    val SWIFT = LanguageIconDefinition(
        style = LanguageIconStyle.SWIFT,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFF05138),
        label = "SW"
    )

    val DART = LanguageIconDefinition(
        style = LanguageIconStyle.DART,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF0175C2),
        label = "DA"
    )

    val PHP = LanguageIconDefinition(
        style = LanguageIconStyle.PHP,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF777BB4),
        label = "PHP"
    )

    val RUBY = LanguageIconDefinition(
        style = LanguageIconStyle.RUBY,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFCC342D),
        label = "RB"
    )

    val LUA = LanguageIconDefinition(
        style = LanguageIconStyle.LUA,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF000080),
        label = "LUA"
    )

    val R = LanguageIconDefinition(
        style = LanguageIconStyle.R,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF276DC3),
        label = "R"
    )

    val PERL = LanguageIconDefinition(
        style = LanguageIconStyle.PERL,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF0073A1),
        label = "PL"
    )

    val SHELL = LanguageIconDefinition(
        style = LanguageIconStyle.SHELL,
        primaryColor = Color(0xFF4EAA25),
        badgeBgColor = Color(0xFF2E3440),
        label = "\$_"
    )

    val POWERSHELL = LanguageIconDefinition(
        style = LanguageIconStyle.POWERSHELL,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF012456),
        label = "PS"
    )

    val SQL = LanguageIconDefinition(
        style = LanguageIconStyle.SQL,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFE38C00),
        label = "SQL"
    )

    val YAML = LanguageIconDefinition(
        style = LanguageIconStyle.YAML,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFCB171E),
        label = "YML"
    )

    val TOML = LanguageIconDefinition(
        style = LanguageIconStyle.TOML,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF9C4221),
        label = "TO"
    )

    val MARKDOWN = LanguageIconDefinition(
        style = LanguageIconStyle.MARKDOWN,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF083FA1),
        label = "M↓"
    )

    val OBJECTIVE_C = LanguageIconDefinition(
        style = LanguageIconStyle.OBJECTIVE_C,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF438EFF),
        label = "OC"
    )

    val OBJECTIVE_CPP = LanguageIconDefinition(
        style = LanguageIconStyle.OBJECTIVE_CPP,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF1E5BB3),
        label = "OC+"
    )

    val SCALA = LanguageIconDefinition(
        style = LanguageIconStyle.SCALA,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFDC322F),
        label = "SC"
    )

    val GROOVY = LanguageIconDefinition(
        style = LanguageIconStyle.GROOVY,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF4298B8),
        label = "GV"
    )

    val HASKELL = LanguageIconDefinition(
        style = LanguageIconStyle.HASKELL,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF5E5086),
        label = "λ="
    )

    val ELIXIR = LanguageIconDefinition(
        style = LanguageIconStyle.ELIXIR,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF4E2A8E),
        label = "EX"
    )

    val ERLANG = LanguageIconDefinition(
        style = LanguageIconStyle.ERLANG,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFA90533),
        label = "ER"
    )

    val ASSEMBLY = LanguageIconDefinition(
        style = LanguageIconStyle.ASSEMBLY,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF6E4C13),
        label = "ASM"
    )

    val MAKEFILE = LanguageIconDefinition(
        style = LanguageIconStyle.MAKEFILE,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF3F51B5),
        label = "MK"
    )

    val DOCKERFILE = LanguageIconDefinition(
        style = LanguageIconStyle.DOCKERFILE,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF2496ED),
        label = "DK"
    )

    val GRADLE = LanguageIconDefinition(
        style = LanguageIconStyle.GRADLE,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF02303A),
        label = "GR"
    )

    val INI = LanguageIconDefinition(
        style = LanguageIconStyle.INI,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF546E7A),
        label = "INI"
    )

    val PROPERTIES = LanguageIconDefinition(
        style = LanguageIconStyle.PROPERTIES,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF455A64),
        label = "cfg"
    )

    val CSV = LanguageIconDefinition(
        style = LanguageIconStyle.CSV,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF217346),
        label = "CSV"
    )

    val GRAPHQL = LanguageIconDefinition(
        style = LanguageIconStyle.GRAPHQL,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFE10098),
        label = "GQL"
    )

    val VUE = LanguageIconDefinition(
        style = LanguageIconStyle.VUE,
        primaryColor = Color(0xFF35495E),
        badgeBgColor = Color(0xFF41B883),
        label = "VUE"
    )

    val SVELTE = LanguageIconDefinition(
        style = LanguageIconStyle.SVELTE,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFFF3E00),
        label = "SVT"
    )

    val JULIA = LanguageIconDefinition(
        style = LanguageIconStyle.JULIA,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF9558B2),
        label = "JL"
    )

    val LATEX = LanguageIconDefinition(
        style = LanguageIconStyle.LATEX,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF009688),
        label = "TEX"
    )

    val ASTRO = LanguageIconDefinition(
        style = LanguageIconStyle.ASTRO,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFBC52EE),
        label = "AST"
    )

    val AUTOHOTKEY = LanguageIconDefinition(
        style = LanguageIconStyle.AUTOHOTKEY,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF339933),
        label = "AHK"
    )

    val GIT = LanguageIconDefinition(
        style = LanguageIconStyle.GIT,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFFF05032),
        label = "GIT"
    )

    val GENERIC_CODE = LanguageIconDefinition(
        style = LanguageIconStyle.GENERIC_CODE,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF5E6773),
        label = "</>"
    )

    val PLAIN_TEXT = LanguageIconDefinition(
        style = LanguageIconStyle.PLAIN_TEXT,
        primaryColor = Color(0xFFFFFFFF),
        badgeBgColor = Color(0xFF78909C),
        label = "TXT"
    )
}

/**
 * Renders an authentic Vector language icon badge with crisp typography or shapes.
 */
@Composable
fun LanguageIcon(
    iconDef: LanguageIconDefinition,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    val cornerRadius = (size.value * 0.22f).dp

    when (iconDef.style) {
        LanguageIconStyle.KOTLIN -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawKotlin(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.PYTHON -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawPython(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.HTML -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawHtml(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.CSS, LanguageIconStyle.SCSS, LanguageIconStyle.LESS -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawCss(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.JAVASCRIPT -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawJs(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.TYPESCRIPT -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawTs(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.JSX, LanguageIconStyle.TSX -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawReact(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.VUE -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawVue(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.SVELTE -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawSvelte(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.RUST -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawRust(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.GO -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawGo(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.JAVA -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawJava(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.C, LanguageIconStyle.OBJECTIVE_C -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawC(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.CPP, LanguageIconStyle.OBJECTIVE_CPP -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawCpp(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.CSHARP -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawCSharp(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.SWIFT -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawSwift(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.DART -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawDart(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.PHP -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawPhp(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.RUBY -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawRuby(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.LUA -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawLua(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.JULIA -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawJulia(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.LATEX -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawLatex(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.ASSEMBLY -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawAssembly(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.ASTRO -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawAstro(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.AUTOHOTKEY -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawAutoHotkey(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.GIT -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawGit(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.SHELL -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawShell(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.POWERSHELL -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawPowerShell(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.SQL -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawDatabase(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.MARKDOWN -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawMarkdown(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.JSON -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawJson(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.XML -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawXml(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.YAML -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawYaml(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.TOML, LanguageIconStyle.INI, LanguageIconStyle.PROPERTIES -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawToml(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.DOCKERFILE -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawDocker(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.GRADLE, LanguageIconStyle.GROOVY -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawGradle(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.GRAPHQL -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawGraphql(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.HASKELL -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawHaskell(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.ELIXIR, LanguageIconStyle.ERLANG -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawElixir(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.SCALA -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawScala(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.CSV -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawCsv(this, this.size.minDimension)
            }
        }
        LanguageIconStyle.PLAIN_TEXT -> {
            Canvas(modifier = modifier.size(size)) {
                LanguageVectorDrawers.drawDocument(this, this.size.minDimension)
            }
        }
        else -> {
            Box(
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(iconDef.badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                val fontSize = when {
                    iconDef.label.length >= 4 -> (size.value * 0.30f).sp
                    iconDef.label.length == 3 -> (size.value * 0.36f).sp
                    iconDef.label.length == 2 -> (size.value * 0.42f).sp
                    else -> (size.value * 0.50f).sp
                }
                Text(
                    text = iconDef.label,
                    color = iconDef.primaryColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
        }
    }
}
