package com.example.editor.runner

import android.webkit.JavascriptInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * JavaScript interface bridge exposed to WebView via window.CLXV11_Bridge.
 * Dispatches console logs, uncaught exceptions, unhandled promise rejections,
 * and page lifecycle events to Kotlin Coroutines on the Main thread.
 */
class WebConsoleBridge(
    private val scope: CoroutineScope,
    private val onMessage: (WebConsoleMessage) -> Unit,
    private val onTitleChanged: ((String) -> Unit)? = null
) {
    @JavascriptInterface
    fun postMessage(
        levelStr: String,
        messageStr: String,
        sourceStr: String?,
        line: Int,
        col: Int,
        stack: String?
    ) {
        val level = try {
            LogLevel.valueOf(levelStr.uppercase())
        } catch (_: Exception) {
            LogLevel.LOG
        }

        val msg = WebConsoleMessage(
            level = level,
            message = messageStr,
            source = sourceStr?.takeIf { it.isNotBlank() },
            lineNumber = line.takeIf { it > 0 },
            columnNumber = col.takeIf { it > 0 },
            stack = stack?.takeIf { it.isNotBlank() }
        )

        scope.launch(Dispatchers.Main) {
            onMessage(msg)
        }
    }

    @JavascriptInterface
    fun onTitle(title: String) {
        scope.launch(Dispatchers.Main) {
            onTitleChanged?.invoke(title)
        }
    }
}
