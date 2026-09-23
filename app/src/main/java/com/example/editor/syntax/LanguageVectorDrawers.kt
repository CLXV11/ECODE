package com.example.editor.syntax

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

/**
 * Handcrafted, razor-sharp vector drawers for programming language emblems and file extensions.
 * Rendered with pure Canvas DrawScope math for zero bitmap overhead, infinite scalability,
 * and high-DPI fidelity that matches and surpasses VS Code / Acode icon themes.
 */
object LanguageVectorDrawers {

    fun drawKotlin(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF1B1B26), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))

            // Upper diagonal shape (JetBrains Purple)
            val p1 = Path().apply {
                moveTo(w * 0.12f, h * 0.12f)
                lineTo(w * 0.88f, h * 0.12f)
                lineTo(w * 0.12f, h * 0.88f)
                close()
            }
            drawPath(p1, Color(0xFF7F52FF))

            // Middle accent ribbon (JetBrains Pink / Violet)
            val p2 = Path().apply {
                moveTo(w * 0.50f, h * 0.12f)
                lineTo(w * 0.88f, h * 0.50f)
                lineTo(w * 0.50f, h * 0.88f)
                lineTo(w * 0.12f, h * 0.50f)
                close()
            }
            drawPath(p2, Color(0xFFC757BC))

            // Lower right corner triangle (JetBrains Orange)
            val p3 = Path().apply {
                moveTo(w * 0.88f, h * 0.12f)
                lineTo(w * 0.88f, h * 0.88f)
                lineTo(w * 0.35f, h * 0.88f)
                close()
            }
            drawPath(p3, Color(0xFFFF7D00))

            // Lower left wedge (JetBrains Cyan / Blue)
            val p4 = Path().apply {
                moveTo(w * 0.12f, h * 0.45f)
                lineTo(w * 0.55f, h * 0.88f)
                lineTo(w * 0.12f, h * 0.88f)
                close()
            }
            drawPath(p4, Color(0xFF0095D5))
        }
    }

    fun drawPython(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF1E232A), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))

            // Official Python Top Snake (Blue)
            val blueSnake = Path().apply {
                moveTo(w * 0.49f, h * 0.12f)
                cubicTo(w * 0.31f, h * 0.12f, w * 0.23f, h * 0.20f, w * 0.23f, h * 0.30f)
                lineTo(w * 0.23f, h * 0.39f)
                lineTo(w * 0.49f, h * 0.39f)
                lineTo(w * 0.49f, h * 0.43f)
                lineTo(w * 0.18f, h * 0.43f)
                cubicTo(w * 0.08f, h * 0.43f, w * 0.08f, h * 0.58f, w * 0.18f, h * 0.58f)
                lineTo(w * 0.28f, h * 0.58f)
                lineTo(w * 0.28f, h * 0.50f)
                cubicTo(w * 0.28f, h * 0.43f, w * 0.34f, h * 0.37f, w * 0.41f, h * 0.37f)
                lineTo(w * 0.61f, h * 0.37f)
                cubicTo(w * 0.67f, h * 0.37f, w * 0.72f, h * 0.32f, w * 0.72f, h * 0.26f)
                lineTo(w * 0.72f, h * 0.19f)
                cubicTo(w * 0.72f, h * 0.14f, w * 0.62f, h * 0.12f, w * 0.49f, h * 0.12f)
                close()
            }
            drawPath(blueSnake, color = Color(0xFF387EB8))
            drawCircle(Color.White, radius = w * 0.038f, center = Offset(w * 0.35f, h * 0.21f))

            // Official Python Bottom Snake (Yellow) - 180° Rotated Counterpart
            rotate(180f, Offset(w * 0.5f, h * 0.5f)) {
                drawPath(blueSnake, color = Color(0xFFFFD43B))
                drawCircle(Color.White, radius = w * 0.038f, center = Offset(w * 0.35f, h * 0.21f))
            }
        }
    }

    fun drawHtml(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val left = Path().apply {
                moveTo(w * 0.15f, h * 0.12f); lineTo(w * 0.5f, h * 0.12f); lineTo(w * 0.5f, h * 0.88f); lineTo(w * 0.22f, h * 0.80f); close()
            }
            drawPath(left, Color(0xFFE34F26))
            val right = Path().apply {
                moveTo(w * 0.5f, h * 0.12f); lineTo(w * 0.85f, h * 0.12f); lineTo(w * 0.78f, h * 0.80f); lineTo(w * 0.5f, h * 0.88f); close()
            }
            drawPath(right, Color(0xFFEF652A))

            // White < / >
            val brackets = Path().apply {
                moveTo(w * 0.42f, h * 0.35f); lineTo(w * 0.28f, h * 0.50f); lineTo(w * 0.42f, h * 0.65f)
                moveTo(w * 0.58f, h * 0.35f); lineTo(w * 0.72f, h * 0.50f); lineTo(w * 0.58f, h * 0.65f)
            }
            drawPath(brackets, Color.White, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawLine(Color.White, Offset(w * 0.54f, h * 0.32f), Offset(w * 0.46f, h * 0.68f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        }
    }

    fun drawCss(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val left = Path().apply {
                moveTo(w * 0.15f, h * 0.12f); lineTo(w * 0.5f, h * 0.12f); lineTo(w * 0.5f, h * 0.88f); lineTo(w * 0.22f, h * 0.80f); close()
            }
            drawPath(left, Color(0xFF1572B6))
            val right = Path().apply {
                moveTo(w * 0.5f, h * 0.12f); lineTo(w * 0.85f, h * 0.12f); lineTo(w * 0.78f, h * 0.80f); lineTo(w * 0.5f, h * 0.88f); close()
            }
            drawPath(right, Color(0xFF33A9DC))

            // White '3'
            val three = Path().apply {
                moveTo(w * 0.32f, h * 0.33f); lineTo(w * 0.68f, h * 0.33f); lineTo(w * 0.52f, h * 0.49f)
                lineTo(w * 0.64f, h * 0.49f); cubicTo(w * 0.72f, h * 0.49f, w * 0.72f, h * 0.70f, w * 0.58f, h * 0.70f)
                lineTo(w * 0.34f, h * 0.70f)
            }
            drawPath(three, Color.White, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawJs(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFF7DF1E), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))

            // Black JS monogram
            val stroke = Stroke(width = w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            // J
            val jPath = Path().apply {
                moveTo(w * 0.42f, h * 0.35f); lineTo(w * 0.42f, h * 0.62f)
                cubicTo(w * 0.42f, h * 0.74f, w * 0.24f, h * 0.74f, w * 0.24f, h * 0.62f)
            }
            drawPath(jPath, Color(0xFF1A1A1A), style = stroke)

            // S
            val sPath = Path().apply {
                moveTo(w * 0.76f, h * 0.42f)
                cubicTo(w * 0.70f, h * 0.33f, w * 0.54f, h * 0.33f, w * 0.54f, h * 0.48f)
                cubicTo(w * 0.54f, h * 0.62f, w * 0.76f, h * 0.58f, w * 0.76f, h * 0.70f)
                cubicTo(w * 0.76f, h * 0.82f, w * 0.54f, h * 0.82f, w * 0.50f, h * 0.72f)
            }
            drawPath(sPath, Color(0xFF1A1A1A), style = stroke)
        }
    }

    fun drawTs(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF3178C6), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))

            val stroke = Stroke(width = w * 0.11f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
            // T
            drawLine(Color.White, Offset(w * 0.20f, h * 0.36f), Offset(w * 0.50f, h * 0.36f), stroke.width)
            drawLine(Color.White, Offset(w * 0.35f, h * 0.36f), Offset(w * 0.35f, h * 0.74f), stroke.width)

            // S
            val sPath = Path().apply {
                moveTo(w * 0.78f, h * 0.42f)
                cubicTo(w * 0.72f, h * 0.33f, w * 0.56f, h * 0.33f, w * 0.56f, h * 0.48f)
                cubicTo(w * 0.56f, h * 0.62f, w * 0.78f, h * 0.58f, w * 0.78f, h * 0.70f)
                cubicTo(w * 0.78f, h * 0.82f, w * 0.56f, h * 0.82f, w * 0.52f, h * 0.72f)
            }
            drawPath(sPath, Color.White, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawReact(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val cyan = Color(0xFF61DAFB)
            drawRoundRect(Color(0xFF20232A), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            drawCircle(cyan, radius = w * 0.09f, center = Offset(w * 0.5f, h * 0.5f))

            val orbitStroke = Stroke(width = w * 0.06f)
            drawOval(cyan, topLeft = Offset(w * 0.16f, h * 0.36f), size = Size(w * 0.68f, h * 0.28f), style = orbitStroke)

            drawContext.transform.rotate(60f, Offset(w * 0.5f, h * 0.5f))
            drawOval(cyan, topLeft = Offset(w * 0.16f, h * 0.36f), size = Size(w * 0.68f, h * 0.28f), style = orbitStroke)
            drawContext.transform.rotate(-60f, Offset(w * 0.5f, h * 0.5f))

            drawContext.transform.rotate(-60f, Offset(w * 0.5f, h * 0.5f))
            drawOval(cyan, topLeft = Offset(w * 0.16f, h * 0.36f), size = Size(w * 0.68f, h * 0.28f), style = orbitStroke)
            drawContext.transform.rotate(60f, Offset(w * 0.5f, h * 0.5f))
        }
    }

    fun drawVue(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val outer = Path().apply {
                moveTo(w * 0.10f, h * 0.20f); lineTo(w * 0.50f, h * 0.84f); lineTo(w * 0.90f, h * 0.20f)
                lineTo(w * 0.72f, h * 0.20f); lineTo(w * 0.50f, h * 0.56f); lineTo(w * 0.28f, h * 0.20f); close()
            }
            drawPath(outer, Color(0xFF42B883), style = Fill)
            val inner = Path().apply {
                moveTo(w * 0.28f, h * 0.20f); lineTo(w * 0.50f, h * 0.56f); lineTo(w * 0.72f, h * 0.20f)
                lineTo(w * 0.58f, h * 0.20f); lineTo(w * 0.50f, h * 0.33f); lineTo(w * 0.42f, h * 0.20f); close()
            }
            drawPath(inner, Color(0xFF35495E), style = Fill)
        }
    }

    fun drawSvelte(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF221A18), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val sPath = Path().apply {
                moveTo(w * 0.74f, h * 0.28f)
                cubicTo(w * 0.60f, h * 0.16f, w * 0.30f, h * 0.18f, w * 0.30f, h * 0.38f)
                cubicTo(w * 0.30f, h * 0.54f, w * 0.70f, h * 0.50f, w * 0.70f, h * 0.68f)
                cubicTo(w * 0.70f, h * 0.86f, w * 0.38f, h * 0.86f, w * 0.26f, h * 0.74f)
            }
            drawPath(sPath, Color(0xFFFF3E00), style = Stroke(width = w * 0.13f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawRust(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val rustColor = Color(0xFFCE412B)
            val center = Offset(w * 0.5f, h * 0.5f)
            drawCircle(rustColor, radius = w * 0.36f, center = center)

            for (i in 0 until 8) {
                drawContext.transform.rotate(i * 45f, center)
                drawRect(rustColor, topLeft = Offset(w * 0.44f, h * 0.08f), size = Size(w * 0.12f, h * 0.12f))
                drawContext.transform.rotate(-i * 45f, center)
            }
            drawCircle(Color(0xFF1E1E2E), radius = w * 0.22f, center = center)

            val rPath = Path().apply {
                moveTo(w * 0.40f, h * 0.65f); lineTo(w * 0.40f, h * 0.35f); lineTo(w * 0.56f, h * 0.35f)
                cubicTo(w * 0.65f, h * 0.35f, w * 0.65f, h * 0.50f, w * 0.56f, h * 0.50f); lineTo(w * 0.40f, h * 0.50f)
                moveTo(w * 0.52f, h * 0.50f); lineTo(w * 0.62f, h * 0.65f)
            }
            drawPath(rPath, Color.White, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawGo(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF00ADD8), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Bold GO italic text
            val stroke = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            // G
            val gPath = Path().apply {
                moveTo(w * 0.46f, h * 0.38f); cubicTo(w * 0.24f, h * 0.30f, w * 0.22f, h * 0.70f, w * 0.46f, h * 0.66f)
                lineTo(w * 0.46f, h * 0.52f); lineTo(w * 0.36f, h * 0.52f)
            }
            drawPath(gPath, Color.White, style = stroke)
            // O
            drawOval(Color.White, topLeft = Offset(w * 0.54f, h * 0.36f), size = Size(w * 0.28f, h * 0.32f), style = stroke)
        }
    }

    fun drawJava(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val cyanSteam = Path().apply {
                moveTo(w * 0.40f, h * 0.35f); cubicTo(w * 0.48f, h * 0.25f, w * 0.32f, h * 0.18f, w * 0.42f, h * 0.10f)
            }
            drawPath(cyanSteam, Color(0xFF5382A1), style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
            val redSteam = Path().apply {
                moveTo(w * 0.56f, h * 0.38f); cubicTo(w * 0.66f, h * 0.26f, w * 0.48f, h * 0.20f, w * 0.58f, h * 0.12f)
            }
            drawPath(redSteam, Color(0xFFEA2D2E), style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
            val cup = Path().apply {
                moveTo(w * 0.24f, h * 0.45f); lineTo(w * 0.72f, h * 0.45f); cubicTo(w * 0.72f, h * 0.72f, w * 0.24f, h * 0.72f, w * 0.24f, h * 0.45f); close()
            }
            drawPath(cup, Color(0xFFEA2D2E), style = Fill)
            val saucer = Path().apply {
                moveTo(w * 0.18f, h * 0.76f); cubicTo(w * 0.35f, h * 0.86f, w * 0.65f, h * 0.86f, w * 0.82f, h * 0.76f)
            }
            drawPath(saucer, Color(0xFF5382A1), style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
        }
    }

    fun drawC(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF00599C), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val cPath = Path().apply {
                moveTo(w * 0.68f, h * 0.34f); cubicTo(w * 0.40f, h * 0.24f, w * 0.32f, h * 0.76f, w * 0.68f, h * 0.66f)
            }
            drawPath(cPath, Color.White, style = Stroke(width = w * 0.13f, cap = StrokeCap.Round))
        }
    }

    fun drawCpp(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF00599C), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // C
            val cPath = Path().apply {
                moveTo(w * 0.48f, h * 0.34f); cubicTo(w * 0.26f, h * 0.26f, w * 0.20f, h * 0.74f, w * 0.48f, h * 0.66f)
            }
            drawPath(cPath, Color.White, style = Stroke(width = w * 0.11f, cap = StrokeCap.Round))
            // ++
            val pStroke = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
            // Plus 1
            drawLine(Color(0xFF00B0FF), Offset(w * 0.58f, h * 0.44f), Offset(w * 0.70f, h * 0.44f), pStroke.width)
            drawLine(Color(0xFF00B0FF), Offset(w * 0.64f, h * 0.38f), Offset(w * 0.64f, h * 0.50f), pStroke.width)
            // Plus 2
            drawLine(Color(0xFF00B0FF), Offset(w * 0.76f, h * 0.54f), Offset(w * 0.88f, h * 0.54f), pStroke.width)
            drawLine(Color(0xFF00B0FF), Offset(w * 0.82f, h * 0.48f), Offset(w * 0.82f, h * 0.60f), pStroke.width)
        }
    }

    fun drawCSharp(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF68217A), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // C
            val cPath = Path().apply {
                moveTo(w * 0.46f, h * 0.34f); cubicTo(w * 0.26f, h * 0.26f, w * 0.20f, h * 0.74f, w * 0.46f, h * 0.66f)
            }
            drawPath(cPath, Color.White, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round))
            // #
            val hashStroke = Stroke(width = w * 0.05f, cap = StrokeCap.Round)
            drawLine(Color(0xFFB388FF), Offset(w * 0.56f, h * 0.36f), Offset(w * 0.56f, h * 0.64f), hashStroke.width)
            drawLine(Color(0xFFB388FF), Offset(w * 0.68f, h * 0.36f), Offset(w * 0.68f, h * 0.64f), hashStroke.width)
            drawLine(Color(0xFFB388FF), Offset(w * 0.50f, h * 0.44f), Offset(w * 0.74f, h * 0.44f), hashStroke.width)
            drawLine(Color(0xFFB388FF), Offset(w * 0.50f, h * 0.56f), Offset(w * 0.74f, h * 0.56f), hashStroke.width)
        }
    }

    fun drawSwift(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFF05138), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val bird = Path().apply {
                moveTo(w * 0.82f, h * 0.24f)
                cubicTo(w * 0.68f, h * 0.40f, w * 0.46f, h * 0.48f, w * 0.28f, h * 0.40f)
                cubicTo(w * 0.36f, h * 0.50f, w * 0.48f, h * 0.56f, w * 0.62f, h * 0.58f)
                cubicTo(w * 0.44f, h * 0.64f, w * 0.22f, h * 0.60f, w * 0.16f, h * 0.54f)
                cubicTo(w * 0.26f, h * 0.72f, w * 0.56f, h * 0.82f, w * 0.78f, h * 0.62f)
                cubicTo(w * 0.66f, h * 0.62f, w * 0.58f, h * 0.55f, w * 0.60f, h * 0.46f)
                cubicTo(w * 0.72f, h * 0.42f, w * 0.80f, h * 0.32f, w * 0.82f, h * 0.24f); close()
            }
            drawPath(bird, Color.White, style = Fill)
        }
    }

    fun drawDart(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val p1 = Path().apply {
                moveTo(w * 0.18f, h * 0.78f); lineTo(w * 0.78f, h * 0.18f); lineTo(w * 0.62f, h * 0.18f); lineTo(w * 0.18f, h * 0.62f); close()
            }
            drawPath(p1, Color(0xFF00B4AB), style = Fill)
            val p2 = Path().apply {
                moveTo(w * 0.32f, h * 0.78f); lineTo(w * 0.78f, h * 0.32f); lineTo(w * 0.78f, h * 0.54f); lineTo(w * 0.54f, h * 0.78f); close()
            }
            drawPath(p2, Color(0xFF0175C2), style = Fill)
        }
    }

    fun drawPhp(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF777BB4), size = Size(w, h), cornerRadius = CornerRadius(w * 0.32f))
            // Oval pill with 'php'
            drawOval(Color(0xFF4F5D95), topLeft = Offset(w * 0.10f, h * 0.20f), size = Size(w * 0.80f, h * 0.60f))
            val stroke = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
            // p
            drawLine(Color.White, Offset(w * 0.26f, h * 0.35f), Offset(w * 0.26f, h * 0.68f), stroke.width)
            drawCircle(Color.White, radius = w * 0.08f, center = Offset(w * 0.32f, h * 0.44f), style = stroke)
            // h
            drawLine(Color.White, Offset(w * 0.48f, h * 0.32f), Offset(w * 0.48f, h * 0.65f), stroke.width)
            val hArc = Path().apply {
                moveTo(w * 0.48f, h * 0.44f); cubicTo(w * 0.58f, h * 0.38f, w * 0.60f, h * 0.50f, w * 0.60f, h * 0.65f)
            }
            drawPath(hArc, Color.White, style = stroke)
            // p
            drawLine(Color.White, Offset(w * 0.70f, h * 0.35f), Offset(w * 0.70f, h * 0.68f), stroke.width)
            drawCircle(Color.White, radius = w * 0.08f, center = Offset(w * 0.76f, h * 0.44f), style = stroke)
        }
    }

    fun drawRuby(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val ruby = Path().apply {
                moveTo(w * 0.32f, h * 0.22f); lineTo(w * 0.68f, h * 0.22f); lineTo(w * 0.85f, h * 0.45f)
                lineTo(w * 0.50f, h * 0.84f); lineTo(w * 0.15f, h * 0.45f); close()
            }
            drawPath(ruby, Color(0xFFCC342D), style = Fill)
            val facets = Path().apply {
                moveTo(w * 0.32f, h * 0.45f); lineTo(w * 0.68f, h * 0.45f); lineTo(w * 0.50f, h * 0.84f); lineTo(w * 0.32f, h * 0.45f)
                moveTo(w * 0.32f, h * 0.22f); lineTo(w * 0.32f, h * 0.45f)
                moveTo(w * 0.68f, h * 0.22f); lineTo(w * 0.68f, h * 0.45f)
            }
            drawPath(facets, Color(0xFFFF5252), style = Stroke(width = w * 0.05f))
        }
    }

    fun drawLua(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawCircle(Color(0xFF000080), radius = w * 0.38f, center = Offset(w * 0.50f, h * 0.50f))
            drawCircle(Color.White.copy(alpha = 0.6f), radius = w * 0.38f, center = Offset(w * 0.50f, h * 0.50f), style = Stroke(width = w * 0.04f))
            drawCircle(Color(0xFFFFD700), radius = w * 0.11f, center = Offset(w * 0.72f, h * 0.28f))
        }
    }

    fun drawJulia(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val r = w * 0.17f
            drawCircle(Color(0xFFCB3C33), radius = r, center = Offset(w * 0.50f, h * 0.30f))
            drawCircle(Color(0xFF389826), radius = r, center = Offset(w * 0.30f, h * 0.68f))
            drawCircle(Color(0xFF9558B2), radius = r, center = Offset(w * 0.70f, h * 0.68f))
        }
    }

    fun drawLatex(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val bird = Path().apply {
                moveTo(w * 0.88f, h * 0.30f); lineTo(w * 0.68f, h * 0.38f)
                cubicTo(w * 0.60f, h * 0.15f, w * 0.35f, h * 0.12f, w * 0.30f, h * 0.20f)
                cubicTo(w * 0.40f, h * 0.35f, w * 0.48f, h * 0.42f, w * 0.52f, h * 0.48f)
                lineTo(w * 0.18f, h * 0.72f); cubicTo(w * 0.32f, h * 0.68f, w * 0.48f, h * 0.62f, w * 0.56f, h * 0.58f)
                cubicTo(w * 0.68f, h * 0.55f, w * 0.76f, h * 0.44f, w * 0.88f, h * 0.30f); close()
            }
            drawPath(bird, Color(0xFF009688), style = Fill)
        }
    }

    fun drawAssembly(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val tri = Path().apply {
                moveTo(w * 0.50f, h * 0.12f); lineTo(w * 0.90f, h * 0.84f); lineTo(w * 0.10f, h * 0.84f); close()
            }
            drawPath(tri, Color(0xFF6E4C94), style = Fill)
            val inner = Path().apply {
                moveTo(w * 0.50f, h * 0.36f); lineTo(w * 0.68f, h * 0.70f); lineTo(w * 0.32f, h * 0.70f); close()
            }
            drawPath(inner, Color.White, style = Stroke(width = w * 0.06f))
        }
    }

    fun drawAstro(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFBC52EE), size = Size(w, h), cornerRadius = CornerRadius(w * 0.28f))
            val rocket = Path().apply {
                moveTo(w * 0.50f, h * 0.18f); lineTo(w * 0.72f, h * 0.68f); lineTo(w * 0.62f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.54f); lineTo(w * 0.44f, h * 0.54f); lineTo(w * 0.38f, h * 0.68f)
                lineTo(w * 0.28f, h * 0.68f); close()
            }
            drawPath(rocket, Color.White, style = Fill)
            val flame = Path().apply {
                moveTo(w * 0.44f, h * 0.72f); lineTo(w * 0.50f, h * 0.84f); lineTo(w * 0.56f, h * 0.72f)
            }
            drawPath(flame, Color(0xFFFF5D00), style = Fill)
        }
    }

    fun drawAutoHotkey(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF339933), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val hPath = Path().apply {
                moveTo(w * 0.32f, h * 0.25f); lineTo(w * 0.32f, h * 0.75f)
                moveTo(w * 0.68f, h * 0.25f); lineTo(w * 0.68f, h * 0.75f)
                moveTo(w * 0.32f, h * 0.50f); lineTo(w * 0.68f, h * 0.50f)
            }
            drawPath(hPath, Color.White, style = Stroke(width = w * 0.13f, cap = StrokeCap.Square))
        }
    }

    fun drawBatch(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF1E88E5), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val chevron = Path().apply {
                moveTo(w * 0.26f, h * 0.32f); lineTo(w * 0.48f, h * 0.50f); lineTo(w * 0.26f, h * 0.68f)
            }
            drawPath(chevron, Color.White, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawLine(Color.White, Offset(w * 0.54f, h * 0.68f), Offset(w * 0.76f, h * 0.68f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
        }
    }

    fun drawShell(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF263238), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val lime = Color(0xFF4CAF50)
            val chevron = Path().apply {
                moveTo(w * 0.26f, h * 0.32f); lineTo(w * 0.48f, h * 0.50f); lineTo(w * 0.26f, h * 0.68f)
            }
            drawPath(chevron, lime, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawLine(lime, Offset(w * 0.54f, h * 0.68f), Offset(w * 0.76f, h * 0.68f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
        }
    }

    fun drawPowerShell(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF012456), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val chevron = Path().apply {
                moveTo(w * 0.24f, h * 0.32f); lineTo(w * 0.46f, h * 0.50f); lineTo(w * 0.24f, h * 0.68f)
            }
            drawPath(chevron, Color.White, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawLine(Color.White, Offset(w * 0.52f, h * 0.68f), Offset(w * 0.76f, h * 0.68f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        }
    }

    fun drawAsciidoc(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFE40046), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val aPath = Path().apply {
                moveTo(w * 0.26f, h * 0.75f); lineTo(w * 0.50f, h * 0.22f); lineTo(w * 0.74f, h * 0.75f)
                moveTo(w * 0.34f, h * 0.58f); lineTo(w * 0.66f, h * 0.58f)
            }
            drawPath(aPath, Color.White, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round))
        }
    }

    fun drawLiquid(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val waterColor = Color(0xFF4378A0)
            val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
            for (i in 0..2) {
                val y = h * (0.32f + i * 0.18f)
                val wave = Path().apply {
                    moveTo(w * 0.18f, y)
                    cubicTo(w * 0.34f, y - h * 0.08f, w * 0.44f, y + h * 0.08f, w * 0.60f, y)
                    cubicTo(w * 0.68f, y - h * 0.08f, w * 0.74f, y + h * 0.06f, w * 0.82f, y)
                }
                drawPath(wave, waterColor, style = stroke)
            }
        }
    }

    fun drawLisp(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val loop = Path().apply {
                moveTo(w * 0.65f, h * 0.25f)
                cubicTo(w * 0.20f, h * 0.25f, w * 0.20f, h * 0.75f, w * 0.65f, h * 0.75f)
                cubicTo(w * 0.82f, h * 0.75f, w * 0.82f, h * 0.50f, w * 0.50f, h * 0.50f)
            }
            drawPath(loop, Color(0xFFE65100), style = Stroke(width = w * 0.11f, cap = StrokeCap.Round))
        }
    }

    fun drawLog(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val doc = Path().apply {
                moveTo(w * 0.20f, h * 0.12f); lineTo(w * 0.62f, h * 0.12f); lineTo(w * 0.80f, h * 0.30f); lineTo(w * 0.80f, h * 0.88f); lineTo(w * 0.20f, h * 0.88f); close()
            }
            drawPath(doc, Color(0xFF4CAF50), style = Fill)
            val fold = Path().apply {
                moveTo(w * 0.62f, h * 0.12f); lineTo(w * 0.62f, h * 0.30f); lineTo(w * 0.80f, h * 0.30f); close()
            }
            drawPath(fold, Color(0xFF388E3C), style = Fill)
            drawLine(Color.White, Offset(w * 0.32f, h * 0.44f), Offset(w * 0.68f, h * 0.44f), w * 0.06f)
            drawLine(Color.White, Offset(w * 0.32f, h * 0.58f), Offset(w * 0.68f, h * 0.58f), w * 0.06f)
            drawLine(Color.White, Offset(w * 0.32f, h * 0.72f), Offset(w * 0.54f, h * 0.72f), w * 0.06f)
        }
    }

    fun drawApex(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val cloud = Path().apply {
                moveTo(w * 0.28f, h * 0.65f)
                cubicTo(w * 0.15f, h * 0.65f, w * 0.15f, h * 0.48f, w * 0.28f, h * 0.48f)
                cubicTo(w * 0.28f, h * 0.30f, w * 0.52f, h * 0.25f, w * 0.60f, h * 0.38f)
                cubicTo(w * 0.72f, h * 0.32f, w * 0.85f, h * 0.44f, w * 0.80f, h * 0.56f)
                cubicTo(w * 0.88f, h * 0.60f, w * 0.85f, h * 0.68f, w * 0.75f, h * 0.68f); close()
            }
            drawPath(cloud, Color(0xFF00A1E0), style = Fill)
            val braces = Path().apply {
                moveTo(w * 0.44f, h * 0.44f); lineTo(w * 0.40f, h * 0.48f); lineTo(w * 0.44f, h * 0.54f)
                moveTo(w * 0.56f, h * 0.44f); lineTo(w * 0.60f, h * 0.48f); lineTo(w * 0.56f, h * 0.54f)
            }
            drawPath(braces, Color.White, style = Stroke(width = w * 0.05f, cap = StrokeCap.Round))
        }
    }

    fun drawGit(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawContext.transform.rotate(45f, Offset(w * 0.5f, h * 0.5f))
            drawRoundRect(Color(0xFFF05032), topLeft = Offset(w * 0.16f, h * 0.16f), size = Size(w * 0.68f, h * 0.68f), cornerRadius = CornerRadius(w * 0.16f))
            drawContext.transform.rotate(-45f, Offset(w * 0.5f, h * 0.5f))

            drawLine(Color.White, Offset(w * 0.38f, h * 0.28f), Offset(w * 0.38f, h * 0.72f), w * 0.08f)
            val branch = Path().apply {
                moveTo(w * 0.38f, h * 0.55f); cubicTo(w * 0.50f, h * 0.55f, w * 0.62f, h * 0.48f, w * 0.62f, h * 0.36f)
            }
            drawPath(branch, Color.White, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
            drawCircle(Color.White, radius = w * 0.07f, center = Offset(w * 0.38f, h * 0.28f))
            drawCircle(Color.White, radius = w * 0.07f, center = Offset(w * 0.38f, h * 0.72f))
            drawCircle(Color.White, radius = w * 0.07f, center = Offset(w * 0.62f, h * 0.36f))
        }
    }

    fun drawDatabase(scope: DrawScope, size: Float, color: Color = Color(0xFF00758F)) {
        with(scope) {
            val w = size
            val h = size
            val dw = w * 0.64f
            val dh = h * 0.18f
            val left = w * 0.18f

            for (i in 0..2) {
                val topY = h * (0.20f + i * 0.22f)
                if (i < 2) {
                    drawRect(color, topLeft = Offset(left, topY + dh * 0.5f), size = Size(dw, h * 0.22f))
                }
                drawOval(color, topLeft = Offset(left, topY), size = Size(dw, dh), style = Fill)
                drawOval(Color.White.copy(alpha = 0.4f), topLeft = Offset(left, topY), size = Size(dw, dh), style = Stroke(width = w * 0.05f))
            }
        }
    }

    fun drawMarkdown(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF083FA1), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val m = Path().apply {
                moveTo(w * 0.20f, h * 0.68f); lineTo(w * 0.20f, h * 0.32f); lineTo(w * 0.34f, h * 0.48f)
                lineTo(w * 0.48f, h * 0.32f); lineTo(w * 0.48f, h * 0.68f)
            }
            drawPath(m, Color.White, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            val arrow = Path().apply {
                moveTo(w * 0.70f, h * 0.32f); lineTo(w * 0.70f, h * 0.68f)
                moveTo(w * 0.58f, h * 0.56f); lineTo(w * 0.70f, h * 0.68f); lineTo(w * 0.82f, h * 0.56f)
            }
            drawPath(arrow, Color.White, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawJson(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFFBC02D), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val stroke = Stroke(width = w * 0.10f, cap = StrokeCap.Round)
            val braces = Path().apply {
                moveTo(w * 0.42f, h * 0.26f); lineTo(w * 0.34f, h * 0.26f); lineTo(w * 0.34f, h * 0.46f)
                lineTo(w * 0.25f, h * 0.50f); lineTo(w * 0.34f, h * 0.54f); lineTo(w * 0.34f, h * 0.74f); lineTo(w * 0.42f, h * 0.74f)
                moveTo(w * 0.58f, h * 0.26f); lineTo(w * 0.66f, h * 0.26f); lineTo(w * 0.66f, h * 0.46f)
                lineTo(w * 0.75f, h * 0.50f); lineTo(w * 0.66f, h * 0.54f); lineTo(w * 0.66f, h * 0.74f); lineTo(w * 0.58f, h * 0.74f)
            }
            drawPath(braces, Color(0xFF212121), style = stroke)
        }
    }

    fun drawXml(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFE65100), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val tags = Path().apply {
                moveTo(w * 0.32f, h * 0.36f); lineTo(w * 0.18f, h * 0.50f); lineTo(w * 0.32f, h * 0.64f)
                moveTo(w * 0.68f, h * 0.36f); lineTo(w * 0.82f, h * 0.50f); lineTo(w * 0.68f, h * 0.64f)
            }
            drawPath(tags, Color.White, style = stroke)
            drawLine(Color.White, Offset(w * 0.58f, h * 0.32f), Offset(w * 0.42f, h * 0.68f), stroke.width)
        }
    }

    fun drawYaml(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFCB171E), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // White bold Y
            val yPath = Path().apply {
                moveTo(w * 0.26f, h * 0.30f); lineTo(w * 0.50f, h * 0.52f); lineTo(w * 0.74f, h * 0.30f)
                moveTo(w * 0.50f, h * 0.52f); lineTo(w * 0.50f, h * 0.74f)
            }
            drawPath(yPath, Color.White, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawToml(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF9C4221), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // [ T ]
            val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Square)
            drawLine(Color.White, Offset(w * 0.28f, h * 0.36f), Offset(w * 0.72f, h * 0.36f), stroke.width)
            drawLine(Color.White, Offset(w * 0.50f, h * 0.36f), Offset(w * 0.50f, h * 0.74f), stroke.width)
            // Brackets [ ]
            drawLine(Color.White.copy(alpha = 0.5f), Offset(w * 0.18f, h * 0.30f), Offset(w * 0.18f, h * 0.76f), w * 0.05f)
            drawLine(Color.White.copy(alpha = 0.5f), Offset(w * 0.82f, h * 0.30f), Offset(w * 0.82f, h * 0.76f), w * 0.05f)
        }
    }

    fun drawDocker(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val blue = Color(0xFF2496ED)
            // Whale body
            val whale = Path().apply {
                moveTo(w * 0.12f, h * 0.55f); lineTo(w * 0.85f, h * 0.55f)
                cubicTo(w * 0.92f, h * 0.65f, w * 0.85f, h * 0.85f, w * 0.50f, h * 0.85f)
                cubicTo(w * 0.25f, h * 0.85f, w * 0.10f, h * 0.75f, w * 0.10f, h * 0.60f); close()
            }
            drawPath(whale, blue, style = Fill)
            // Cargo boxes
            val boxColor = Color(0xFF1D78BE)
            val boxW = w * 0.10f
            val boxH = h * 0.08f
            for (col in 0..3) {
                drawRect(boxColor, topLeft = Offset(w * (0.32f + col * 0.12f), h * 0.44f), size = Size(boxW, boxH))
            }
            for (col in 1..3) {
                drawRect(boxColor, topLeft = Offset(w * (0.32f + col * 0.12f), h * 0.34f), size = Size(boxW, boxH))
            }
        }
    }

    fun drawGradle(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF02303A), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Emerald Gradle elephant curve
            val curve = Path().apply {
                moveTo(w * 0.20f, h * 0.60f)
                cubicTo(w * 0.20f, h * 0.25f, w * 0.70f, h * 0.20f, w * 0.78f, h * 0.45f)
                cubicTo(w * 0.85f, h * 0.65f, w * 0.55f, h * 0.80f, w * 0.40f, h * 0.68f)
            }
            drawPath(curve, Color(0xFF00C853), style = Stroke(width = w * 0.11f, cap = StrokeCap.Round))
        }
    }

    fun drawGraphql(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val magenta = Color(0xFFE10098)
            val center = Offset(w * 0.5f, h * 0.5f)
            val r = w * 0.34f
            val nodes = (0 until 6).map { i ->
                val angle = Math.toRadians((i * 60 - 30).toDouble())
                Offset((center.x + r * Math.cos(angle)).toFloat(), (center.y + r * Math.sin(angle)).toFloat())
            }
            val hex = Path().apply {
                moveTo(nodes[0].x, nodes[0].y)
                for (i in 1 until 6) lineTo(nodes[i].x, nodes[i].y)
                close()
                moveTo(nodes[0].x, nodes[0].y); lineTo(nodes[3].x, nodes[3].y)
                moveTo(nodes[1].x, nodes[1].y); lineTo(nodes[4].x, nodes[4].y)
                moveTo(nodes[2].x, nodes[2].y); lineTo(nodes[5].x, nodes[5].y)
            }
            drawPath(hex, magenta, style = Stroke(width = w * 0.05f))
            for (n in nodes) drawCircle(magenta, radius = w * 0.065f, center = n)
        }
    }

    fun drawHaskell(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val purple = Color(0xFF5E5086)
            drawRoundRect(purple, size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Lambda λ / double angle
            val p1 = Path().apply {
                moveTo(w * 0.20f, h * 0.25f); lineTo(w * 0.42f, h * 0.50f); lineTo(w * 0.20f, h * 0.75f)
            }
            drawPath(p1, Color.White, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            val p2 = Path().apply {
                moveTo(w * 0.40f, h * 0.25f); lineTo(w * 0.62f, h * 0.50f); lineTo(w * 0.40f, h * 0.75f)
            }
            drawPath(p2, Color(0xFF8F4E8B), style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawElixir(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF4E2A8E), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Teardrop
            val drop = Path().apply {
                moveTo(w * 0.50f, h * 0.18f)
                cubicTo(w * 0.68f, h * 0.42f, w * 0.75f, h * 0.60f, w * 0.75f, h * 0.68f)
                cubicTo(w * 0.75f, h * 0.82f, w * 0.64f, h * 0.86f, w * 0.50f, h * 0.86f)
                cubicTo(w * 0.36f, h * 0.86f, w * 0.25f, h * 0.82f, w * 0.25f, h * 0.68f)
                cubicTo(w * 0.25f, h * 0.60f, w * 0.32f, h * 0.42f, w * 0.50f, h * 0.18f); close()
            }
            drawPath(drop, Color(0xFFA582E8), style = Fill)
        }
    }

    fun drawScala(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val red = Color(0xFFDC322F)
            drawRoundRect(Color(0xFF2E1E1E), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // 3 spiral staircase steps
            for (i in 0..2) {
                val y = h * (0.24f + i * 0.20f)
                val step = Path().apply {
                    moveTo(w * (0.22f + i * 0.08f), y)
                    lineTo(w * (0.78f - (2 - i) * 0.04f), y)
                    lineTo(w * (0.74f - (2 - i) * 0.04f), y + h * 0.14f)
                    lineTo(w * (0.18f + i * 0.08f), y + h * 0.14f); close()
                }
                drawPath(step, red, style = Fill)
            }
        }
    }

    fun drawCsv(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF217346), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            val stroke = Stroke(width = w * 0.06f)
            drawRect(Color.White, topLeft = Offset(w * 0.22f, h * 0.22f), size = Size(w * 0.56f, h * 0.56f), style = stroke)
            drawLine(Color.White, Offset(w * 0.50f, h * 0.22f), Offset(w * 0.50f, h * 0.78f), stroke.width)
            drawLine(Color.White, Offset(w * 0.22f, h * 0.50f), Offset(w * 0.78f, h * 0.50f), stroke.width)
        }
    }

    fun drawDocument(scope: DrawScope, size: Float, color: Color = Color(0xFF78909C)) {
        with(scope) {
            val w = size
            val h = size
            val doc = Path().apply {
                moveTo(w * 0.20f, h * 0.12f); lineTo(w * 0.58f, h * 0.12f); lineTo(w * 0.80f, h * 0.34f)
                lineTo(w * 0.80f, h * 0.88f); lineTo(w * 0.20f, h * 0.88f); close()
            }
            drawPath(doc, color, style = Fill)
            val fold = Path().apply {
                moveTo(w * 0.58f, h * 0.12f); lineTo(w * 0.58f, h * 0.34f); lineTo(w * 0.80f, h * 0.34f); close()
            }
            drawPath(fold, color.copy(alpha = 0.65f), style = Fill)
            val lineStroke = Stroke(width = w * 0.05f, cap = StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.85f), Offset(w * 0.32f, h * 0.48f), Offset(w * 0.68f, h * 0.48f), lineStroke.width)
            drawLine(Color.White.copy(alpha = 0.85f), Offset(w * 0.32f, h * 0.60f), Offset(w * 0.68f, h * 0.60f), lineStroke.width)
            drawLine(Color.White.copy(alpha = 0.85f), Offset(w * 0.32f, h * 0.72f), Offset(w * 0.52f, h * 0.72f), lineStroke.width)
        }
    }
}
