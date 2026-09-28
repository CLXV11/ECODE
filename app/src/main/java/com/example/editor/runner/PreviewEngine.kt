package com.example.editor.runner

/**
 * Program types detected by the Language-Aware Preview Engine.
 */
enum class ProgramType(val displayName: String, val isVisualCapable: Boolean) {
    HTML_DOCUMENT("HTML Document", true),
    CSS_STYLESHEET("CSS Stylesheet", true),
    WEB_DOM_SCRIPT("JavaScript (Web / DOM)", true),
    PYTHON_FLASK_FASTAPI("Python Web Server (Flask / FastAPI)", true),
    PYTHON_TURTLE_GRAPHICS("Python Turtle Graphics", true),
    PYTHON_CANVAS_PYGAME("Python 2D Canvas / Pygame", true),
    STRUCTURED_DATA_JSON("JSON Structured Data", true),
    STRUCTURED_DATA_XML("XML Structured Data", true),
    CLI_SCRIPT("CLI Console Script", false),
    DESKTOP_GUI_UNSUPPORTED("Desktop GUI (X11 / Wayland Required)", false)
}

/**
 * Concrete renderers dispatched by the Language-Aware Preview Engine.
 */
enum class PreviewRendererType {
    WEB_RENDERER,
    CANVAS_RENDERER,
    STRUCTURED_DATA_RENDERER,
    CAPABILITY_NOTICE_RENDERER
}

/**
 * Result of analyzing a source file before execution.
 */
data class ProgramAnalysisResult(
    val programType: ProgramType,
    val rendererType: PreviewRendererType,
    val explanationEn: String,
    val explanationAr: String,
    val detectedFramework: String? = null
)

/**
 * Intelligent detector that inspects file extension, language ID, and source code tokens
 * to determine the true program type and route it to the authentic renderer.
 *
 * Architecture:
 * Editor → Language Detection → Runtime → Program Type Detection → Renderer
 */
object ProgramTypeDetector {

    fun analyze(
        fileName: String,
        languageId: String? = null,
        content: String
    ): ProgramAnalysisResult {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        val lang = languageId?.lowercase() ?: ""
        val trimmed = content.trim()

        // 1. HTML Documents
        if (ext in listOf("html", "htm") || lang == "html") {
            return ProgramAnalysisResult(
                programType = ProgramType.HTML_DOCUMENT,
                rendererType = PreviewRendererType.WEB_RENDERER,
                explanationEn = "Interactive HTML document rendered in the WebView engine.",
                explanationAr = "مستند HTML تفاعلي يتم تصييره في محرك الويب."
            )
        }

        // 2. CSS Stylesheets
        if (ext in listOf("css", "scss", "sass", "less") || lang in listOf("css", "scss", "less")) {
            return ProgramAnalysisResult(
                programType = ProgramType.CSS_STYLESHEET,
                rendererType = PreviewRendererType.WEB_RENDERER,
                explanationEn = "CSS stylesheet applied to HTML components showcase.",
                explanationAr = "ورقة أنماط CSS مطبقة على معاينة عناصر الواجهة."
            )
        }

        // 3. Structured Data: JSON & XML
        if (ext == "json" || lang == "json") {
            return ProgramAnalysisResult(
                programType = ProgramType.STRUCTURED_DATA_JSON,
                rendererType = PreviewRendererType.STRUCTURED_DATA_RENDERER,
                explanationEn = "Interactive structured tree inspector for JSON data.",
                explanationAr = "مستعرض هيكلي تفاعلي لبيانات JSON."
            )
        }

        if (ext in listOf("xml", "svg") || lang in listOf("xml", "svg")) {
            return ProgramAnalysisResult(
                programType = ProgramType.STRUCTURED_DATA_XML,
                rendererType = PreviewRendererType.STRUCTURED_DATA_RENDERER,
                explanationEn = "Structured XML/SVG inspector and hierarchical tree view.",
                explanationAr = "مستعرض هيكلي تفاعلي لبيانات XML و SVG."
            )
        }

        // 4. Python Programs
        if (ext in listOf("py", "pyw", "python") || lang == "python") {
            return analyzePython(trimmed)
        }

        // 5. JavaScript / TypeScript
        if (ext in listOf("js", "mjs", "cjs", "jsx", "ts", "tsx") || lang in listOf("javascript", "typescript")) {
            return analyzeJavaScript(trimmed)
        }

        // 6. Compiled CLI languages (Kotlin, Java, C, C++, C#, Go, Rust, Swift, Dart)
        val compiledLangs = listOf("kt", "kts", "java", "c", "cpp", "cc", "cxx", "h", "hpp", "cs", "go", "rs", "swift", "dart", "sh", "bash")
        if (ext in compiledLangs || lang in listOf("kotlin", "java", "c", "cpp", "csharp", "go", "rust", "swift", "dart", "shell", "bash")) {
            val langName = when {
                ext in listOf("kt", "kts") || lang == "kotlin" -> "Kotlin"
                ext == "java" || lang == "java" -> "Java"
                ext in listOf("c", "h") || lang == "c" -> "C"
                ext in listOf("cpp", "cc", "cxx", "hpp") || lang == "cpp" -> "C++"
                ext == "cs" || lang == "csharp" -> "C#"
                ext == "go" || lang == "go" -> "Go"
                ext == "rs" || lang == "rust" -> "Rust"
                ext in listOf("sh", "bash") || lang in listOf("shell", "bash") -> "Bash / Shell"
                else -> "CLI"
            }

            return ProgramAnalysisResult(
                programType = ProgramType.CLI_SCRIPT,
                rendererType = PreviewRendererType.CAPABILITY_NOTICE_RENDERER,
                explanationEn = "Visual preview is unavailable for this runtime. The program executed successfully in Console.",
                explanationAr = "المعاينة المرئية غير متوفرة لبيئة التشغيل هذه. يتم تشغيل البرنامج بنجاح في وحدة التحكم/الطرفية.",
                detectedFramework = "$langName CLI Application"
            )
        }

        // Fallback for general text or unknown code
        return ProgramAnalysisResult(
            programType = ProgramType.CLI_SCRIPT,
            rendererType = PreviewRendererType.CAPABILITY_NOTICE_RENDERER,
            explanationEn = "Visual preview is unavailable for this runtime. The program executed successfully in Console.",
            explanationAr = "المعاينة المرئية غير متوفرة لبيئة التشغيل هذه. يتم تشغيل البرنامج بنجاح في وحدة التحكم/الطرفية."
        )
    }

    private fun analyzePython(content: String): ProgramAnalysisResult {
        // A. Flask / FastAPI / Local Web Server
        val isFlask = content.contains("from flask import", ignoreCase = true) ||
                content.contains("import flask", ignoreCase = true) ||
                content.contains("Flask(", ignoreCase = false) ||
                content.contains("@app.route", ignoreCase = false)

        val isFastApi = content.contains("from fastapi import", ignoreCase = true) ||
                content.contains("import fastapi", ignoreCase = true) ||
                content.contains("FastAPI(", ignoreCase = false) ||
                content.contains("@app.get", ignoreCase = false) ||
                content.contains("@app.post", ignoreCase = false)

        val isHttpServer = content.contains("http.server", ignoreCase = true) ||
                content.contains("HTTPServer", ignoreCase = true) ||
                content.contains("SimpleHTTPRequestHandler", ignoreCase = true)

        if (isFlask || isFastApi || isHttpServer) {
            val framework = when {
                isFlask -> "Flask"
                isFastApi -> "FastAPI"
                else -> "HTTP Server"
            }
            return ProgramAnalysisResult(
                programType = ProgramType.PYTHON_FLASK_FASTAPI,
                rendererType = PreviewRendererType.WEB_RENDERER,
                explanationEn = "Local Python $framework server simulation with live web endpoint preview.",
                explanationAr = "محاكاة خادم بايثون $framework مع معاينة تفاعلية حية لنقاط النهاية والصفحات.",
                detectedFramework = framework
            )
        }

        // B. Turtle Graphics
        val isTurtle = content.contains("import turtle", ignoreCase = true) ||
                content.contains("from turtle import", ignoreCase = true) ||
                content.contains("turtle.", ignoreCase = false) ||
                content.contains("Turtle()", ignoreCase = false)

        if (isTurtle) {
            return ProgramAnalysisResult(
                programType = ProgramType.PYTHON_TURTLE_GRAPHICS,
                rendererType = PreviewRendererType.CANVAS_RENDERER,
                explanationEn = "Authentic Python Turtle graphics surface rendered on high-DPI HTML5 Canvas.",
                explanationAr = "لوحة رسوميات سلحفاة بايثون (Turtle) حقيقية معروضة على سطح Canvas تفاعلي.",
                detectedFramework = "Turtle Graphics"
            )
        }

        // C. Pygame / 2D Canvas
        val isPygame = content.contains("import pygame", ignoreCase = true) ||
                content.contains("from pygame", ignoreCase = true) ||
                content.contains("pygame.", ignoreCase = false) ||
                content.contains("pgzero", ignoreCase = true)

        if (isPygame) {
            return ProgramAnalysisResult(
                programType = ProgramType.PYTHON_CANVAS_PYGAME,
                rendererType = PreviewRendererType.CANVAS_RENDERER,
                explanationEn = "Python 2D graphical game surface rendered onto HTML5 Canvas.",
                explanationAr = "سطح رسوميات ثنائي الأبعاد لبايثون معروض على HTML5 Canvas.",
                detectedFramework = "Pygame"
            )
        }

        // D. Desktop GUI frameworks requiring native X11 / Wayland windowing (Tkinter, PyQt, wx)
        val isDesktopGui = content.contains("import tkinter", ignoreCase = true) ||
                content.contains("from tkinter", ignoreCase = true) ||
                content.contains("import PyQt", ignoreCase = true) ||
                content.contains("from PyQt", ignoreCase = true) ||
                content.contains("import wx", ignoreCase = true)

        if (isDesktopGui) {
            val guiName = when {
                content.contains("tkinter", ignoreCase = true) -> "Tkinter"
                content.contains("PyQt", ignoreCase = true) -> "PyQt"
                else -> "Desktop GUI"
            }
            return ProgramAnalysisResult(
                programType = ProgramType.DESKTOP_GUI_UNSUPPORTED,
                rendererType = PreviewRendererType.CAPABILITY_NOTICE_RENDERER,
                explanationEn = "Visual preview is unavailable for this runtime. Desktop GUI frameworks ($guiName) require a native desktop windowing system (X11/Wayland). The program executed successfully in Console.",
                explanationAr = "المعاينة المرئية غير متوفرة لبيئة التشغيل هذه. تتطلب أطر الواجهات المكتبية ($guiName) نظام نوافذ مكتبي (X11/Wayland). يتم تشغيل البرنامج بنجاح في وحدة التحكم/الطرفية.",
                detectedFramework = guiName
            )
        }

        // E. Standard Python CLI Script
        return ProgramAnalysisResult(
            programType = ProgramType.CLI_SCRIPT,
            rendererType = PreviewRendererType.CAPABILITY_NOTICE_RENDERER,
            explanationEn = "Visual preview is unavailable for this runtime. The program executed successfully in Console.",
            explanationAr = "المعاينة المرئية غير متوفرة لبيئة التشغيل هذه. تم تنفيذ البرنامج بنجاح في وحدة التحكم/الطرفية.",
            detectedFramework = "Python CLI"
        )
    }

    private fun analyzeJavaScript(content: String): ProgramAnalysisResult {
        val domTokens = listOf(
            "document.", "window.", "addEventListener", "getElementById",
            "querySelector", "createElement", "innerHTML", "innerText",
            "canvas.getContext", "alert(", "prompt("
        )

        val hasDomAccess = domTokens.any { content.contains(it, ignoreCase = false) }

        return if (hasDomAccess) {
            ProgramAnalysisResult(
                programType = ProgramType.WEB_DOM_SCRIPT,
                rendererType = PreviewRendererType.WEB_RENDERER,
                explanationEn = "Interactive JavaScript/TypeScript manipulating browser DOM and visual elements.",
                explanationAr = "كود جافاسكريبت تفاعلي يتحكم في عناصر DOM والعرض المرئي."
            )
        } else {
            ProgramAnalysisResult(
                programType = ProgramType.CLI_SCRIPT,
                rendererType = PreviewRendererType.CAPABILITY_NOTICE_RENDERER,
                explanationEn = "Visual preview is unavailable for this runtime. The program executed successfully in Console.",
                explanationAr = "المعاينة المرئية غير متوفرة لبيئة التشغيل هذه. تم تنفيذ البرنامج بنجاح في وحدة التحكم/الطرفية.",
                detectedFramework = "JavaScript CLI (Node.js)"
            )
        }
    }
}
