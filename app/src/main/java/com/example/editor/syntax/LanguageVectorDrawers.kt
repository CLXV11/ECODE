package com.example.editor.syntax

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
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

            // Official JetBrains Kotlin rounded square
            val badge = Path().apply {
                addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(w * 0.20f)))
            }
            clipPath(badge) {
                // Top-Left Triangle (JetBrains Purple #7F52FF)
                val p1 = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(0f, h)
                    close()
                }
                drawPath(p1, Color(0xFF7F52FF))

                // Bottom-Right Triangle base (JetBrains Magenta/Pink #E4485D)
                val pOrange = Path().apply {
                    moveTo(w, 0f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(pOrange, Color(0xFFE4485D))

                // Lower right corner triangle (JetBrains Orange #FF8900)
                val p3 = Path().apply {
                    moveTo(w * 0.35f, h)
                    lineTo(w, h * 0.35f)
                    lineTo(w, h)
                    close()
                }
                drawPath(p3, Color(0xFFFF8900))

                // Lower left wedge (JetBrains Blue #0095D5)
                val p4 = Path().apply {
                    moveTo(0f, h * 0.48f)
                    lineTo(w * 0.52f, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(p4, Color(0xFF0095D5))
            }
        }
    }

    fun drawPython(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size

            // Authentic official Python logo geometry
            val snakeHalf = Path().apply {
                moveTo(w * 0.495f, h * 0.05f)
                cubicTo(w * 0.23f, h * 0.05f, w * 0.24f, h * 0.16f, w * 0.24f, h * 0.16f)
                lineTo(w * 0.24f, h * 0.27f)
                lineTo(w * 0.495f, h * 0.27f)
                lineTo(w * 0.495f, h * 0.31f)
                lineTo(w * 0.15f, h * 0.31f)
                cubicTo(w * 0.15f, h * 0.31f, w * 0.05f, h * 0.29f, w * 0.05f, h * 0.55f)
                cubicTo(w * 0.05f, h * 0.77f, w * 0.14f, h * 0.77f, w * 0.14f, h * 0.77f)
                lineTo(w * 0.21f, h * 0.77f)
                lineTo(w * 0.21f, h * 0.66f)
                cubicTo(w * 0.21f, h * 0.66f, w * 0.20f, h * 0.54f, w * 0.33f, h * 0.54f)
                lineTo(w * 0.58f, h * 0.54f)
                cubicTo(w * 0.58f, h * 0.54f, w * 0.69f, h * 0.54f, w * 0.69f, h * 0.43f)
                lineTo(w * 0.69f, h * 0.16f)
                cubicTo(w * 0.69f, h * 0.16f, w * 0.71f, h * 0.05f, w * 0.495f, h * 0.05f)
                close()
            }

            // Upper Official Python Blue Snake (#3776AB)
            drawPath(snakeHalf, color = Color(0xFF3776AB))
            // Blue Snake White Eye with clear resolution
            drawCircle(Color.White, radius = w * 0.052f, center = Offset(w * 0.34f, h * 0.16f))

            // Lower Official Python Gold Snake (#FFD43B) (official 180° rotation)
            rotate(180f, pivot = Offset(w * 0.5f, h * 0.5f)) {
                drawPath(snakeHalf, color = Color(0xFFFFD43B))
                drawCircle(Color.White, radius = w * 0.052f, center = Offset(w * 0.34f, h * 0.16f))
            }
        }
    }

    fun drawHtml(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Official HTML5 Shield
            val left = Path().apply {
                moveTo(w * 0.12f, h * 0.08f)
                lineTo(w * 0.50f, h * 0.08f)
                lineTo(w * 0.50f, h * 0.92f)
                lineTo(w * 0.20f, h * 0.83f)
                close()
            }
            drawPath(left, Color(0xFFE34F26))
            val right = Path().apply {
                moveTo(w * 0.50f, h * 0.08f)
                lineTo(w * 0.88f, h * 0.08f)
                lineTo(w * 0.80f, h * 0.83f)
                lineTo(w * 0.50f, h * 0.92f)
                close()
            }
            drawPath(right, Color(0xFFEF652A))

            // White 5 facets
            val whiteRight = Path().apply {
                moveTo(w * 0.50f, h * 0.22f)
                lineTo(w * 0.77f, h * 0.22f)
                lineTo(w * 0.74f, h * 0.35f)
                lineTo(w * 0.50f, h * 0.35f)
                close()

                moveTo(w * 0.50f, h * 0.47f)
                lineTo(w * 0.72f, h * 0.47f)
                lineTo(w * 0.69f, h * 0.68f)
                lineTo(w * 0.50f, h * 0.73f)
                lineTo(w * 0.50f, h * 0.60f)
                lineTo(w * 0.60f, h * 0.57f)
                lineTo(w * 0.61f, h * 0.52f)
                lineTo(w * 0.50f, h * 0.52f)
                close()
            }
            drawPath(whiteRight, Color.White)

            val grayLeft = Path().apply {
                moveTo(w * 0.50f, h * 0.22f)
                lineTo(w * 0.25f, h * 0.22f)
                lineTo(w * 0.28f, h * 0.47f)
                lineTo(w * 0.50f, h * 0.47f)
                lineTo(w * 0.50f, h * 0.35f)
                lineTo(w * 0.37f, h * 0.35f)
                lineTo(w * 0.36f, h * 0.30f)
                lineTo(w * 0.50f, h * 0.30f)
                close()

                moveTo(w * 0.50f, h * 0.60f)
                lineTo(w * 0.50f, h * 0.73f)
                lineTo(w * 0.33f, h * 0.68f)
                lineTo(w * 0.32f, h * 0.58f)
                lineTo(w * 0.42f, h * 0.58f)
                lineTo(w * 0.43f, h * 0.63f)
                close()
            }
            drawPath(grayLeft, Color(0xFFEBEBEB))
        }
    }

    fun drawCss(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Official CSS3 Shield
            val left = Path().apply {
                moveTo(w * 0.12f, h * 0.08f)
                lineTo(w * 0.50f, h * 0.08f)
                lineTo(w * 0.50f, h * 0.92f)
                lineTo(w * 0.20f, h * 0.83f)
                close()
            }
            drawPath(left, Color(0xFF1572B6))
            val right = Path().apply {
                moveTo(w * 0.50f, h * 0.08f)
                lineTo(w * 0.88f, h * 0.08f)
                lineTo(w * 0.80f, h * 0.83f)
                lineTo(w * 0.50f, h * 0.92f)
                close()
            }
            drawPath(right, Color(0xFF33A9DC))

            // White 3 facets
            val whiteRight = Path().apply {
                moveTo(w * 0.50f, h * 0.22f)
                lineTo(w * 0.77f, h * 0.22f)
                lineTo(w * 0.74f, h * 0.35f)
                lineTo(w * 0.50f, h * 0.35f)
                close()

                moveTo(w * 0.50f, h * 0.46f)
                lineTo(w * 0.72f, h * 0.46f)
                lineTo(w * 0.69f, h * 0.68f)
                lineTo(w * 0.50f, h * 0.73f)
                lineTo(w * 0.50f, h * 0.60f)
                lineTo(w * 0.60f, h * 0.57f)
                lineTo(w * 0.61f, h * 0.52f)
                lineTo(w * 0.50f, h * 0.52f)
                close()
            }
            drawPath(whiteRight, Color.White)

            val grayLeft = Path().apply {
                moveTo(w * 0.50f, h * 0.22f)
                lineTo(w * 0.25f, h * 0.22f)
                lineTo(w * 0.26f, h * 0.35f)
                lineTo(w * 0.50f, h * 0.35f)
                close()

                moveTo(w * 0.50f, h * 0.46f)
                lineTo(w * 0.37f, h * 0.46f)
                lineTo(w * 0.38f, h * 0.52f)
                lineTo(w * 0.50f, h * 0.52f)
                close()

                moveTo(w * 0.50f, h * 0.60f)
                lineTo(w * 0.50f, h * 0.73f)
                lineTo(w * 0.33f, h * 0.68f)
                lineTo(w * 0.32f, h * 0.58f)
                lineTo(w * 0.42f, h * 0.58f)
                lineTo(w * 0.43f, h * 0.63f)
                close()
            }
            drawPath(grayLeft, Color(0xFFEBEBEB))
        }
    }

    fun drawJs(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Official JavaScript Yellow badge with crisp black JS lettering in bottom-right
            drawRoundRect(
                color = Color(0xFFF7DF1E),
                size = Size(w, h),
                cornerRadius = CornerRadius(w * 0.16f)
            )

            val stroke = Stroke(width = w * 0.088f, cap = StrokeCap.Square, join = StrokeJoin.Miter)

            // Crisp black "J" in lower-right
            val jPath = Path().apply {
                moveTo(w * 0.44f, h * 0.42f)
                lineTo(w * 0.44f, h * 0.74f)
                cubicTo(w * 0.44f, h * 0.85f, w * 0.28f, h * 0.85f, w * 0.26f, h * 0.76f)
            }
            drawPath(jPath, color = Color.Black, style = stroke)

            // Crisp black "S" in lower-right
            val sPath = Path().apply {
                moveTo(w * 0.82f, h * 0.50f)
                cubicTo(w * 0.78f, h * 0.42f, w * 0.56f, h * 0.42f, w * 0.56f, h * 0.54f)
                cubicTo(w * 0.56f, h * 0.65f, w * 0.82f, h * 0.62f, w * 0.82f, h * 0.73f)
                cubicTo(w * 0.82f, h * 0.85f, w * 0.56f, h * 0.85f, w * 0.52f, h * 0.78f)
            }
            drawPath(sPath, color = Color.Black, style = stroke)
        }
    }

    fun drawTs(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Official TypeScript Blue badge with crisp white TS lettering in bottom-right
            drawRoundRect(
                color = Color(0xFF3178C6),
                size = Size(w, h),
                cornerRadius = CornerRadius(w * 0.16f)
            )

            val stroke = Stroke(width = w * 0.085f, cap = StrokeCap.Square, join = StrokeJoin.Miter)

            // Crisp White "T"
            drawLine(Color.White, Offset(w * 0.22f, h * 0.44f), Offset(w * 0.52f, h * 0.44f), stroke.width)
            drawLine(Color.White, Offset(w * 0.37f, h * 0.44f), Offset(w * 0.37f, h * 0.82f), stroke.width)

            // Crisp White "S"
            val sPath = Path().apply {
                moveTo(w * 0.82f, h * 0.50f)
                cubicTo(w * 0.78f, h * 0.42f, w * 0.56f, h * 0.42f, w * 0.56f, h * 0.54f)
                cubicTo(w * 0.56f, h * 0.65f, w * 0.82f, h * 0.62f, w * 0.82f, h * 0.73f)
                cubicTo(w * 0.82f, h * 0.85f, w * 0.56f, h * 0.85f, w * 0.52f, h * 0.78f)
            }
            drawPath(sPath, color = Color.White, style = stroke)
        }
    }

    fun drawReact(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Modern dark badge for React contrast
            drawRoundRect(
                color = Color(0xFF20232A),
                size = Size(w, h),
                cornerRadius = CornerRadius(w * 0.20f)
            )

            val cyan = Color(0xFF61DAFB)
            drawCircle(cyan, radius = w * 0.09f, center = Offset(w * 0.5f, h * 0.5f))

            val orbitStroke = Stroke(width = w * 0.055f)
            drawOval(cyan, topLeft = Offset(w * 0.14f, h * 0.37f), size = Size(w * 0.72f, h * 0.26f), style = orbitStroke)

            rotate(60f, Offset(w * 0.5f, h * 0.5f)) {
                drawOval(cyan, topLeft = Offset(w * 0.14f, h * 0.37f), size = Size(w * 0.72f, h * 0.26f), style = orbitStroke)
            }

            rotate(-60f, Offset(w * 0.5f, h * 0.5f)) {
                drawOval(cyan, topLeft = Offset(w * 0.14f, h * 0.37f), size = Size(w * 0.72f, h * 0.26f), style = orbitStroke)
            }
        }
    }

    fun drawVue(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val outer = Path().apply {
                moveTo(w * 0.08f, h * 0.16f); lineTo(w * 0.50f, h * 0.88f); lineTo(w * 0.92f, h * 0.16f)
                lineTo(w * 0.74f, h * 0.16f); lineTo(w * 0.50f, h * 0.58f); lineTo(w * 0.26f, h * 0.16f); close()
            }
            drawPath(outer, Color(0xFF42B883), style = Fill)
            val inner = Path().apply {
                moveTo(w * 0.26f, h * 0.16f); lineTo(w * 0.50f, h * 0.58f); lineTo(w * 0.74f, h * 0.16f)
                lineTo(w * 0.58f, h * 0.16f); lineTo(w * 0.50f, h * 0.32f); lineTo(w * 0.42f, h * 0.16f); close()
            }
            drawPath(inner, Color(0xFF35495E), style = Fill)
        }
    }

    fun drawSvelte(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val sPath = Path().apply {
                moveTo(w * 0.78f, h * 0.24f)
                cubicTo(w * 0.62f, h * 0.10f, w * 0.26f, h * 0.14f, w * 0.26f, h * 0.38f)
                cubicTo(w * 0.26f, h * 0.56f, w * 0.74f, h * 0.50f, w * 0.74f, h * 0.70f)
                cubicTo(w * 0.74f, h * 0.90f, w * 0.36f, h * 0.90f, w * 0.22f, h * 0.76f)
            }
            drawPath(sPath, Color(0xFFFF3E00), style = Stroke(width = w * 0.15f, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
            // Official Go Cyan rounded rectangle badge
            drawRoundRect(
                color = Color(0xFF00ADD8),
                size = Size(w, h),
                cornerRadius = CornerRadius(w * 0.18f)
            )

            val stroke = Stroke(width = w * 0.088f, cap = StrokeCap.Round, join = StrokeJoin.Round)

            // Crisp White "G"
            val gPath = Path().apply {
                moveTo(w * 0.44f, h * 0.40f)
                cubicTo(w * 0.38f, h * 0.34f, w * 0.20f, h * 0.34f, w * 0.20f, h * 0.54f)
                cubicTo(w * 0.20f, h * 0.74f, w * 0.38f, h * 0.74f, w * 0.44f, h * 0.68f)
                lineTo(w * 0.44f, h * 0.54f)
                lineTo(w * 0.32f, h * 0.54f)
            }
            drawPath(gPath, color = Color.White, style = stroke)

            // Crisp White "O"
            drawOval(
                color = Color.White,
                topLeft = Offset(w * 0.54f, h * 0.36f),
                size = Size(w * 0.28f, h * 0.36f),
                style = stroke
            )
        }
    }

    fun drawJava(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Background subtle rounded container for Java coffee cup
            drawRoundRect(
                color = Color(0xFF1B2A32),
                size = Size(w, h),
                cornerRadius = CornerRadius(w * 0.20f)
            )

            // Official Java Coffee Cup with double steam
            val cyanSteam = Path().apply {
                moveTo(w * 0.40f, h * 0.34f)
                cubicTo(w * 0.48f, h * 0.24f, w * 0.34f, h * 0.16f, w * 0.44f, h * 0.08f)
                cubicTo(w * 0.52f, h * 0.16f, w * 0.42f, h * 0.22f, w * 0.44f, h * 0.34f)
                close()
            }
            drawPath(cyanSteam, Color(0xFF5382A1), style = Fill)

            val orangeSteam = Path().apply {
                moveTo(w * 0.56f, h * 0.34f)
                cubicTo(w * 0.66f, h * 0.24f, w * 0.52f, h * 0.16f, w * 0.62f, h * 0.09f)
                cubicTo(w * 0.70f, h * 0.17f, w * 0.58f, h * 0.23f, w * 0.62f, h * 0.34f)
                close()
            }
            drawPath(orangeSteam, Color(0xFFE76F00), style = Fill)

            // Cup Body
            val cup = Path().apply {
                moveTo(w * 0.24f, h * 0.42f)
                lineTo(w * 0.76f, h * 0.42f)
                cubicTo(w * 0.76f, h * 0.70f, w * 0.24f, h * 0.70f, w * 0.24f, h * 0.42f)
                close()
            }
            drawPath(cup, Color(0xFFEA2D2E), style = Fill)

            // Saucer
            val saucer = Path().apply {
                moveTo(w * 0.16f, h * 0.74f)
                cubicTo(w * 0.36f, h * 0.86f, w * 0.64f, h * 0.86f, w * 0.84f, h * 0.74f)
                lineTo(w * 0.78f, h * 0.80f)
                cubicTo(w * 0.60f, h * 0.90f, w * 0.40f, h * 0.90f, w * 0.22f, h * 0.80f)
                close()
            }
            drawPath(saucer, Color(0xFF5382A1), style = Fill)
        }
    }

    fun drawC(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Deep blue circle for C
            drawCircle(Color(0xFF00599C), radius = w * 0.46f, center = Offset(w * 0.50f, h * 0.50f))

            // White stylized geometric C
            val stroke = Stroke(width = w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val cPath = Path().apply {
                moveTo(w * 0.70f, h * 0.34f)
                cubicTo(w * 0.62f, h * 0.24f, w * 0.28f, h * 0.24f, w * 0.28f, h * 0.50f)
                cubicTo(w * 0.28f, h * 0.76f, w * 0.62f, h * 0.76f, w * 0.70f, h * 0.66f)
            }
            drawPath(cPath, Color.White, style = stroke)
        }
    }

    fun drawCpp(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Hexagon badge for C++
            val hex = Path().apply {
                moveTo(w * 0.50f, h * 0.05f)
                lineTo(w * 0.92f, h * 0.26f)
                lineTo(w * 0.92f, h * 0.74f)
                lineTo(w * 0.50f, h * 0.95f)
                lineTo(w * 0.08f, h * 0.74f)
                lineTo(w * 0.08f, h * 0.26f)
                close()
            }
            drawPath(hex, Color(0xFF00599C), style = Fill)

            // Crisp White "C"
            val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val cPath = Path().apply {
                moveTo(w * 0.46f, h * 0.38f)
                cubicTo(w * 0.38f, h * 0.30f, w * 0.20f, h * 0.34f, w * 0.20f, h * 0.54f)
                cubicTo(w * 0.20f, h * 0.74f, w * 0.38f, h * 0.78f, w * 0.46f, h * 0.70f)
            }
            drawPath(cPath, Color.White, style = stroke)

            // Two ++ in cyan
            val pStroke = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
            // Plus 1
            drawLine(Color(0xFF00D2FF), Offset(w * 0.52f, h * 0.48f), Offset(w * 0.66f, h * 0.48f), pStroke.width)
            drawLine(Color(0xFF00D2FF), Offset(w * 0.59f, h * 0.41f), Offset(w * 0.59f, h * 0.55f), pStroke.width)
            // Plus 2
            drawLine(Color(0xFF00D2FF), Offset(w * 0.72f, h * 0.58f), Offset(w * 0.86f, h * 0.58f), pStroke.width)
            drawLine(Color(0xFF00D2FF), Offset(w * 0.79f, h * 0.49f), Offset(w * 0.79f, h * 0.65f), pStroke.width)
        }
    }

    fun drawCSharp(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Hexagon badge for C#
            val hex = Path().apply {
                moveTo(w * 0.50f, h * 0.05f)
                lineTo(w * 0.92f, h * 0.26f)
                lineTo(w * 0.92f, h * 0.74f)
                lineTo(w * 0.50f, h * 0.95f)
                lineTo(w * 0.08f, h * 0.74f)
                lineTo(w * 0.08f, h * 0.26f)
                close()
            }
            drawPath(hex, Color(0xFF68217A), style = Fill)

            // Crisp White "C"
            val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val cPath = Path().apply {
                moveTo(w * 0.46f, h * 0.38f)
                cubicTo(w * 0.38f, h * 0.30f, w * 0.20f, h * 0.34f, w * 0.20f, h * 0.54f)
                cubicTo(w * 0.20f, h * 0.74f, w * 0.38f, h * 0.78f, w * 0.46f, h * 0.70f)
            }
            drawPath(cPath, Color.White, style = stroke)

            // Lavender #
            val hashStroke = Stroke(width = w * 0.05f, cap = StrokeCap.Round)
            drawLine(Color(0xFFE1BEE7), Offset(w * 0.58f, h * 0.38f), Offset(w * 0.58f, h * 0.70f), hashStroke.width)
            drawLine(Color(0xFFE1BEE7), Offset(w * 0.70f, h * 0.38f), Offset(w * 0.70f, h * 0.70f), hashStroke.width)
            drawLine(Color(0xFFE1BEE7), Offset(w * 0.52f, h * 0.48f), Offset(w * 0.76f, h * 0.48f), hashStroke.width)
            drawLine(Color(0xFFE1BEE7), Offset(w * 0.52f, h * 0.60f), Offset(w * 0.76f, h * 0.60f), hashStroke.width)
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
            // Official yellow document with folded corner
            val doc = Path().apply {
                moveTo(w * 0.16f, h * 0.12f)
                lineTo(w * 0.62f, h * 0.12f)
                lineTo(w * 0.84f, h * 0.34f)
                lineTo(w * 0.84f, h * 0.88f)
                lineTo(w * 0.16f, h * 0.88f)
                close()
            }
            drawPath(doc, Color(0xFFF5A623), style = Fill)
            // Fold flap
            val fold = Path().apply {
                moveTo(w * 0.62f, h * 0.12f)
                lineTo(w * 0.62f, h * 0.34f)
                lineTo(w * 0.84f, h * 0.34f)
                close()
            }
            drawPath(fold, Color(0xFFD48812), style = Fill)

            // Inner dark braces { }
            val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
            val braces = Path().apply {
                moveTo(w * 0.44f, h * 0.44f); lineTo(w * 0.36f, h * 0.44f); lineTo(w * 0.36f, h * 0.58f)
                lineTo(w * 0.28f, h * 0.62f); lineTo(w * 0.36f, h * 0.66f); lineTo(w * 0.36f, h * 0.78f); lineTo(w * 0.44f, h * 0.78f)
                moveTo(w * 0.56f, h * 0.44f); lineTo(w * 0.64f, h * 0.44f); lineTo(w * 0.64f, h * 0.58f)
                lineTo(w * 0.72f, h * 0.62f); lineTo(w * 0.64f, h * 0.66f); lineTo(w * 0.64f, h * 0.78f); lineTo(w * 0.56f, h * 0.78f)
            }
            drawPath(braces, Color(0xFF1E232A), style = stroke)
        }
    }

    fun drawXml(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFE65100), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // White clean XML tag brackets
            val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val tags = Path().apply {
                moveTo(w * 0.30f, h * 0.34f); lineTo(w * 0.16f, h * 0.50f); lineTo(w * 0.30f, h * 0.66f)
                moveTo(w * 0.70f, h * 0.34f); lineTo(w * 0.84f, h * 0.50f); lineTo(w * 0.70f, h * 0.66f)
            }
            drawPath(tags, Color.White, style = stroke)
            // Slash in center
            drawLine(Color.White, Offset(w * 0.58f, h * 0.30f), Offset(w * 0.42f, h * 0.70f), stroke.width, cap = StrokeCap.Round)
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
            // 3 spiral staircase steps
            for (i in 0..2) {
                val y = h * (0.16f + i * 0.24f)
                val step = Path().apply {
                    moveTo(w * (0.14f + i * 0.10f), y)
                    lineTo(w * (0.86f - (2 - i) * 0.05f), y)
                    lineTo(w * (0.80f - (2 - i) * 0.05f), y + h * 0.18f)
                    lineTo(w * (0.10f + i * 0.10f), y + h * 0.18f); close()
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

    fun drawR(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Official R grey oval loop
            val haloStroke = Stroke(width = w * 0.10f)
            drawOval(
                color = Color(0xFFB0B7BF),
                topLeft = Offset(w * 0.24f, h * 0.12f),
                size = Size(w * 0.68f, h * 0.52f),
                style = haloStroke
            )
            // Official dark blue R
            val rBlue = Color(0xFF276DC3)
            val stemWidth = w * 0.16f
            // Vertical stem
            drawRect(rBlue, topLeft = Offset(w * 0.14f, h * 0.14f), size = Size(stemWidth, h * 0.72f))
            // Loop of R
            val rLoop = Path().apply {
                moveTo(w * 0.30f, h * 0.14f)
                lineTo(w * 0.56f, h * 0.14f)
                cubicTo(w * 0.82f, h * 0.14f, w * 0.82f, h * 0.50f, w * 0.56f, h * 0.50f)
                lineTo(w * 0.30f, h * 0.50f)
                close()
            }
            drawPath(rLoop, rBlue, style = Fill)
            // Diagonal leg of R
            val rLeg = Path().apply {
                moveTo(w * 0.44f, h * 0.48f)
                lineTo(w * 0.78f, h * 0.86f)
                lineTo(w * 0.58f, h * 0.86f)
                lineTo(w * 0.32f, h * 0.52f)
                close()
            }
            drawPath(rLeg, rBlue, style = Fill)
        }
    }

    fun drawPerl(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF0073A1), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Official Perl Camel silhouette in white
            val camel = Path().apply {
                // Head and neck
                moveTo(w * 0.22f, h * 0.28f)
                cubicTo(w * 0.20f, h * 0.22f, w * 0.28f, h * 0.22f, w * 0.30f, h * 0.26f)
                lineTo(w * 0.35f, h * 0.40f)
                // First hump
                cubicTo(w * 0.42f, h * 0.32f, w * 0.52f, h * 0.32f, w * 0.56f, h * 0.44f)
                // Second hump
                cubicTo(w * 0.62f, h * 0.34f, w * 0.74f, h * 0.34f, w * 0.78f, h * 0.46f)
                // Rump & tail
                lineTo(w * 0.82f, h * 0.58f)
                // Back leg
                lineTo(w * 0.76f, h * 0.78f)
                lineTo(w * 0.70f, h * 0.78f)
                lineTo(w * 0.70f, h * 0.58f)
                // Belly
                lineTo(w * 0.48f, h * 0.58f)
                // Front leg
                lineTo(w * 0.44f, h * 0.78f)
                lineTo(w * 0.38f, h * 0.78f)
                lineTo(w * 0.36f, h * 0.52f)
                // Neck front
                lineTo(w * 0.26f, h * 0.42f)
                close()
            }
            drawPath(camel, Color.White, style = Fill)
        }
    }

    fun drawScss(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Official Sass / SCSS Pink badge
            drawRoundRect(Color(0xFFCD6799), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Elegant white cursive Sass ribbon 'S'
            val sPath = Path().apply {
                moveTo(w * 0.72f, h * 0.26f)
                cubicTo(w * 0.54f, h * 0.16f, w * 0.26f, h * 0.24f, w * 0.28f, h * 0.42f)
                cubicTo(w * 0.30f, h * 0.58f, w * 0.74f, h * 0.52f, w * 0.72f, h * 0.68f)
                cubicTo(w * 0.70f, h * 0.84f, w * 0.40f, h * 0.86f, w * 0.26f, h * 0.74f)
            }
            drawPath(
                sPath,
                Color.White,
                style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }

    fun drawLess(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF1D365D), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // LESS chevron / bold L
            val stroke = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val lPath = Path().apply {
                moveTo(w * 0.30f, h * 0.26f)
                lineTo(w * 0.30f, h * 0.74f)
                lineTo(w * 0.70f, h * 0.74f)
            }
            drawPath(lPath, Color.White, style = stroke)
            // Accent dot/slash in cyan
            drawCircle(Color(0xFF00A2E8), radius = w * 0.08f, center = Offset(w * 0.62f, h * 0.40f))
        }
    }

    fun drawGroovy(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF4298B8), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Official Groovy blue/white star logo
            val star = Path().apply {
                moveTo(w * 0.50f, h * 0.14f)
                lineTo(w * 0.61f, h * 0.36f)
                lineTo(w * 0.86f, h * 0.36f)
                lineTo(w * 0.66f, h * 0.52f)
                lineTo(w * 0.73f, h * 0.76f)
                lineTo(w * 0.50f, h * 0.62f)
                lineTo(w * 0.27f, h * 0.76f)
                lineTo(w * 0.34f, h * 0.52f)
                lineTo(w * 0.14f, h * 0.36f)
                lineTo(w * 0.39f, h * 0.36f)
                close()
            }
            drawPath(star, Color.White, style = Fill)
            // Star center hole
            drawCircle(Color(0xFF4298B8), radius = w * 0.10f, center = Offset(w * 0.50f, h * 0.48f))
        }
    }

    fun drawClojure(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            val stroke = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
            // Clojure Green Outer Arc (left half yin-yang)
            drawArc(
                color = Color(0xFF62B132),
                startAngle = 100f,
                sweepAngle = 200f,
                useCenter = false,
                topLeft = Offset(w * 0.10f, h * 0.10f),
                size = Size(w * 0.80f, h * 0.80f),
                style = stroke
            )
            // Clojure Blue Inner Arc (right half yin-yang)
            drawArc(
                color = Color(0xFF5881D8),
                startAngle = 280f,
                sweepAngle = 200f,
                useCenter = false,
                topLeft = Offset(w * 0.22f, h * 0.22f),
                size = Size(w * 0.56f, h * 0.56f),
                style = stroke
            )
        }
    }

    fun drawErlang(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFA90533), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Stylized interlocking 'e' and 'r' for Erlang
            val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
            // e arc
            val ePath = Path().apply {
                moveTo(w * 0.44f, h * 0.50f)
                lineTo(w * 0.22f, h * 0.50f)
                cubicTo(w * 0.22f, h * 0.32f, w * 0.44f, h * 0.32f, w * 0.44f, h * 0.50f)
                cubicTo(w * 0.44f, h * 0.68f, w * 0.22f, h * 0.68f, w * 0.26f, h * 0.62f)
            }
            drawPath(ePath, Color.White, style = stroke)
            // r stem & branch
            drawLine(Color.White, Offset(w * 0.56f, h * 0.38f), Offset(w * 0.56f, h * 0.68f), stroke.width, cap = StrokeCap.Round)
            val rArc = Path().apply {
                moveTo(w * 0.56f, h * 0.48f)
                cubicTo(w * 0.64f, h * 0.38f, w * 0.74f, h * 0.38f, w * 0.78f, h * 0.44f)
            }
            drawPath(rArc, Color.White, style = stroke)
        }
    }

    fun drawZig(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Zig iconic Z with angular cutouts
            val zigZ = Path().apply {
                moveTo(w * 0.12f, h * 0.14f)
                lineTo(w * 0.88f, h * 0.14f)
                lineTo(w * 0.88f, h * 0.30f)
                lineTo(w * 0.46f, h * 0.66f)
                lineTo(w * 0.88f, h * 0.66f)
                lineTo(w * 0.88f, h * 0.86f)
                lineTo(w * 0.12f, h * 0.86f)
                lineTo(w * 0.12f, h * 0.70f)
                lineTo(w * 0.54f, h * 0.34f)
                lineTo(w * 0.12f, h * 0.34f)
                close()
            }
            drawPath(zigZ, Color(0xFFF7A41D), style = Fill)
            // Sharp triangular cutouts
            val tri1 = Path().apply {
                moveTo(w * 0.66f, h * 0.34f); lineTo(w * 0.82f, h * 0.34f); lineTo(w * 0.74f, h * 0.46f); close()
            }
            drawPath(tri1, Color(0xFF1E232A), style = Fill)
            val tri2 = Path().apply {
                moveTo(w * 0.18f, h * 0.66f); lineTo(w * 0.34f, h * 0.66f); lineTo(w * 0.26f, h * 0.54f); close()
            }
            drawPath(tri2, Color(0xFF1E232A), style = Fill)
        }
    }

    fun drawNim(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Nim 3-pointed golden crown
            val crown = Path().apply {
                moveTo(w * 0.16f, h * 0.40f)
                lineTo(w * 0.28f, h * 0.14f)
                lineTo(w * 0.50f, h * 0.28f)
                lineTo(w * 0.72f, h * 0.14f)
                lineTo(w * 0.84f, h * 0.40f)
                lineTo(w * 0.76f, h * 0.50f)
                lineTo(w * 0.24f, h * 0.50f)
                close()
            }
            drawPath(crown, Color(0xFFFFD700), style = Fill)
            // Bold N below crown
            val stroke = Stroke(width = w * 0.11f, cap = StrokeCap.Square)
            drawLine(Color(0xFF00C853), Offset(w * 0.28f, h * 0.56f), Offset(w * 0.28f, h * 0.88f), stroke.width)
            drawLine(Color(0xFF00C853), Offset(w * 0.72f, h * 0.56f), Offset(w * 0.72f, h * 0.88f), stroke.width)
            drawLine(Color(0xFF00C853), Offset(w * 0.28f, h * 0.56f), Offset(w * 0.72f, h * 0.88f), stroke.width)
        }
    }

    fun drawCrystal(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Faceted crystal polygon
            val outerHex = Path().apply {
                moveTo(w * 0.50f, h * 0.10f)
                lineTo(w * 0.88f, h * 0.32f)
                lineTo(w * 0.88f, h * 0.68f)
                lineTo(w * 0.50f, h * 0.90f)
                lineTo(w * 0.12f, h * 0.68f)
                lineTo(w * 0.12f, h * 0.32f)
                close()
            }
            drawPath(outerHex, Color(0xFF00E5FF).copy(alpha = 0.35f), style = Fill)
            drawPath(outerHex, Color(0xFF00E5FF), style = Stroke(width = w * 0.06f))
            // Inner facets
            val center = Offset(w * 0.50f, h * 0.50f)
            drawLine(Color.White, center, Offset(w * 0.50f, h * 0.10f), w * 0.045f)
            drawLine(Color.White, center, Offset(w * 0.88f, h * 0.32f), w * 0.045f)
            drawLine(Color.White, center, Offset(w * 0.88f, h * 0.68f), w * 0.045f)
            drawLine(Color.White, center, Offset(w * 0.50f, h * 0.90f), w * 0.045f)
            drawLine(Color.White, center, Offset(w * 0.12f, h * 0.68f), w * 0.045f)
            drawLine(Color.White, center, Offset(w * 0.12f, h * 0.32f), w * 0.045f)
        }
    }

    fun drawSolidity(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Official Solidity double-diamond isometric shapes
            val leftRhombus = Path().apply {
                moveTo(w * 0.32f, h * 0.16f)
                lineTo(w * 0.50f, h * 0.32f)
                lineTo(w * 0.32f, h * 0.48f)
                lineTo(w * 0.14f, h * 0.32f)
                close()
            }
            drawPath(leftRhombus, Color(0xFF757575), style = Fill)

            val rightRhombus = Path().apply {
                moveTo(w * 0.68f, h * 0.16f)
                lineTo(w * 0.86f, h * 0.32f)
                lineTo(w * 0.68f, h * 0.48f)
                lineTo(w * 0.50f, h * 0.32f)
                close()
            }
            drawPath(rightRhombus, Color(0xFF9E9E9E), style = Fill)

            val bottomRhombus1 = Path().apply {
                moveTo(w * 0.32f, h * 0.48f)
                lineTo(w * 0.50f, h * 0.64f)
                lineTo(w * 0.32f, h * 0.84f)
                lineTo(w * 0.14f, h * 0.64f)
                close()
            }
            drawPath(bottomRhombus1, Color(0xFFBDBDBD), style = Fill)

            val bottomRhombus2 = Path().apply {
                moveTo(w * 0.68f, h * 0.48f)
                lineTo(w * 0.86f, h * 0.64f)
                lineTo(w * 0.68f, h * 0.84f)
                lineTo(w * 0.50f, h * 0.64f)
                close()
            }
            drawPath(bottomRhombus2, Color(0xFFE0E0E0), style = Fill)
        }
    }

    fun drawFSharp(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // F# cyan & blue chevron arrows
            val topChevron = Path().apply {
                moveTo(w * 0.18f, h * 0.18f); lineTo(w * 0.56f, h * 0.44f); lineTo(w * 0.18f, h * 0.70f)
            }
            drawPath(topChevron, Color(0xFF30B9DB), style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            val midChevron = Path().apply {
                moveTo(w * 0.46f, h * 0.30f); lineTo(w * 0.80f, h * 0.50f); lineTo(w * 0.46f, h * 0.72f)
            }
            drawPath(midChevron, Color(0xFF207198), style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    fun drawOCaml(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFEE6A1A), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // OCaml two-hump camel silhouette
            val camel = Path().apply {
                moveTo(w * 0.20f, h * 0.34f)
                cubicTo(w * 0.22f, h * 0.24f, w * 0.32f, h * 0.26f, w * 0.34f, h * 0.36f)
                cubicTo(w * 0.42f, h * 0.26f, w * 0.54f, h * 0.28f, w * 0.56f, h * 0.42f)
                cubicTo(w * 0.64f, h * 0.30f, w * 0.76f, h * 0.32f, w * 0.78f, h * 0.48f)
                lineTo(w * 0.80f, h * 0.74f); lineTo(w * 0.72f, h * 0.74f); lineTo(w * 0.68f, h * 0.56f)
                lineTo(w * 0.48f, h * 0.56f); lineTo(w * 0.44f, h * 0.74f); lineTo(w * 0.36f, h * 0.74f)
                lineTo(w * 0.32f, h * 0.50f); lineTo(w * 0.22f, h * 0.44f)
                close()
            }
            drawPath(camel, Color.White, style = Fill)
        }
    }

    fun drawFortran(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF734F96), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Fortran bold geometric 3D 'F'
            val stroke = Stroke(width = w * 0.12f, cap = StrokeCap.Square)
            drawLine(Color.White, Offset(w * 0.32f, h * 0.24f), Offset(w * 0.32f, h * 0.76f), stroke.width)
            drawLine(Color.White, Offset(w * 0.32f, h * 0.24f), Offset(w * 0.72f, h * 0.24f), stroke.width)
            drawLine(Color(0xFFFFD54F), Offset(w * 0.32f, h * 0.48f), Offset(w * 0.64f, h * 0.48f), stroke.width)
        }
    }

    fun drawCobol(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF003366), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Mainframe Gear & C
            drawCircle(Color(0xFF336699), radius = w * 0.34f, center = Offset(w * 0.50f, h * 0.50f), style = Stroke(width = w * 0.08f))
            val cPath = Path().apply {
                moveTo(w * 0.64f, h * 0.36f)
                cubicTo(w * 0.36f, h * 0.28f, w * 0.36f, h * 0.72f, w * 0.64f, h * 0.64f)
            }
            drawPath(cPath, Color.White, style = Stroke(width = w * 0.11f, cap = StrokeCap.Round))
        }
    }

    fun drawD(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFFB03931), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Bold D with 3D angle
            val dPath = Path().apply {
                moveTo(w * 0.28f, h * 0.24f)
                lineTo(w * 0.52f, h * 0.24f)
                cubicTo(w * 0.78f, h * 0.24f, w * 0.78f, h * 0.76f, w * 0.52f, h * 0.76f)
                lineTo(w * 0.28f, h * 0.76f)
                close()
            }
            drawPath(dPath, Color.White, style = Fill)
            val dHole = Path().apply {
                moveTo(w * 0.42f, h * 0.38f)
                lineTo(w * 0.50f, h * 0.38f)
                cubicTo(w * 0.64f, h * 0.38f, w * 0.64f, h * 0.62f, w * 0.50f, h * 0.62f)
                lineTo(w * 0.42f, h * 0.62f)
                close()
            }
            drawPath(dHole, Color(0xFFB03931), style = Fill)
        }
    }

    fun drawBallerina(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF20B6B0), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Ballerina ribbon swirl
            val swirl = Path().apply {
                moveTo(w * 0.28f, h * 0.72f)
                cubicTo(w * 0.22f, h * 0.42f, w * 0.50f, h * 0.24f, w * 0.72f, h * 0.36f)
                cubicTo(w * 0.86f, h * 0.46f, w * 0.64f, h * 0.66f, w * 0.46f, h * 0.54f)
                cubicTo(w * 0.34f, h * 0.44f, w * 0.48f, h * 0.34f, w * 0.54f, h * 0.38f)
            }
            drawPath(swirl, Color.White, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
            drawCircle(Color.White, radius = w * 0.06f, center = Offset(w * 0.62f, h * 0.22f))
        }
    }

    fun drawMakefile(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF3F51B5), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Crossed wrench & hammer silhouette
            val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(w * 0.26f, h * 0.74f), Offset(w * 0.74f, h * 0.26f), stroke.width)
            drawLine(Color.White, Offset(w * 0.74f, h * 0.74f), Offset(w * 0.26f, h * 0.26f), stroke.width)
            // Center nut
            drawCircle(Color(0xFFFFC107), radius = w * 0.12f, center = Offset(w * 0.50f, h * 0.50f))
        }
    }

    fun drawIni(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            drawRoundRect(Color(0xFF546E7A), size = Size(w, h), cornerRadius = CornerRadius(w * 0.22f))
            // Equalizer / settings sliders
            val stroke = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
            // Line 1
            drawLine(Color.White.copy(alpha = 0.5f), Offset(w * 0.24f, h * 0.34f), Offset(w * 0.76f, h * 0.34f), stroke.width)
            drawCircle(Color.White, radius = w * 0.08f, center = Offset(w * 0.40f, h * 0.34f))
            // Line 2
            drawLine(Color.White.copy(alpha = 0.5f), Offset(w * 0.24f, h * 0.66f), Offset(w * 0.76f, h * 0.66f), stroke.width)
            drawCircle(Color.White, radius = w * 0.08f, center = Offset(w * 0.64f, h * 0.66f))
        }
    }

    fun drawGenericCode(scope: DrawScope, size: Float) {
        with(scope) {
            val w = size
            val h = size
            // Clean vector code tag brackets < / >
            val stroke = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            // Left bracket < (Cyan)
            val leftBracket = Path().apply {
                moveTo(w * 0.34f, h * 0.28f); lineTo(w * 0.14f, h * 0.50f); lineTo(w * 0.34f, h * 0.72f)
            }
            drawPath(leftBracket, Color(0xFF00E5FF), style = stroke)
            // Slash / (Purple)
            drawLine(Color(0xFFB388FF), Offset(w * 0.58f, h * 0.24f), Offset(w * 0.42f, h * 0.76f), stroke.width, cap = StrokeCap.Round)
            // Right bracket > (Pink)
            val rightBracket = Path().apply {
                moveTo(w * 0.66f, h * 0.28f); lineTo(w * 0.86f, h * 0.50f); lineTo(w * 0.66f, h * 0.72f)
            }
            drawPath(rightBracket, Color(0xFFFF4081), style = stroke)
        }
    }
}
