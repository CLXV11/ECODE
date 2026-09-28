package com.example.editor.runner

import java.util.UUID

/**
 * Log severity levels supported by the Web Console.
 */
enum class LogLevel {
    LOG,
    INFO,
    WARN,
    ERROR,
    DEBUG,
    RESULT
}

/**
 * Supported file types for web preview and multi-language execution.
 */
enum class WebFileType(val displayName: String) {
    HTML("HTML Preview"),
    CSS("CSS Showcase"),
    JAVASCRIPT("JavaScript / TypeScript"),
    PYTHON("Python Runner"),
    KOTLIN("Kotlin Runner"),
    MARKDOWN("Markdown Preview"),
    JSON("JSON Inspector"),
    SVG("SVG Vector"),
    SQL("SQL Query Runner"),
    CODE("Code & Terminal Runner"),
    UNKNOWN("Document Preview")
}

/**
 * Represents a single log or error message intercepted from the WebView JavaScript engine.
 */
data class WebConsoleMessage(
    val id: String = UUID.randomUUID().toString(),
    val level: LogLevel,
    val message: String,
    val source: String? = null,
    val lineNumber: Int? = null,
    val columnNumber: Int? = null,
    val stack: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Encapsulates the assembled HTML payload and file metadata to be loaded into the WebView.
 */
data class WebRunnerPayload(
    val title: String,
    val fileType: WebFileType,
    val htmlToLoad: String,
    val baseUrl: String?,
    val originalFilePath: String? = null,
    val analysisResult: ProgramAnalysisResult = ProgramAnalysisResult(
        programType = ProgramType.HTML_DOCUMENT,
        rendererType = PreviewRendererType.WEB_RENDERER,
        explanationEn = "",
        explanationAr = ""
    )
)

/**
 * Tabs available in the Web Runner dialog.
 */
enum class WebRunnerTab {
    PREVIEW,
    CONSOLE,
    SPLIT
}

/**
 * Viewport simulation modes.
 */
enum class ViewportMode(val displayName: String, val widthDp: Int?) {
    RESPONSIVE("Responsive", null),
    MOBILE("Mobile (375px)", 375),
    TABLET("Tablet (768px)", 768)
}

/**
 * State of the Web Runner screen/dialog.
 */
data class WebRunnerUiState(
    val isVisible: Boolean = false,
    val payload: WebRunnerPayload? = null,
    val logs: List<WebConsoleMessage> = emptyList(),
    val activeTab: WebRunnerTab = WebRunnerTab.PREVIEW,
    val viewportMode: ViewportMode = ViewportMode.RESPONSIVE,
    val isLoading: Boolean = false,
    val loadProgress: Int = 0,
    val pageTitle: String = "",
    val jsEvalInput: String = "",
    val logFilter: LogLevel? = null,
    val logSearchQuery: String = ""
) {
    val errorCount: Int get() = logs.count { it.level == LogLevel.ERROR }
    val warningCount: Int get() = logs.count { it.level == LogLevel.WARN }

    val filteredLogs: List<WebConsoleMessage>
        get() {
            var result = logs
            if (logFilter != null) {
                result = result.filter { it.level == logFilter }
            }
            if (logSearchQuery.isNotBlank()) {
                val q = logSearchQuery.trim().lowercase()
                result = result.filter { msg ->
                    msg.message.lowercase().contains(q) ||
                            (msg.source?.lowercase()?.contains(q) == true) ||
                            (msg.stack?.lowercase()?.contains(q) == true)
                }
            }
            return result
        }
}
