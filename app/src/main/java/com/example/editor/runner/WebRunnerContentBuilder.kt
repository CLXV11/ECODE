package com.example.editor.runner

import com.example.editor.tabs.EditorTab
import java.io.File

/**
 * Builds ready-to-run HTML documents with injected console bridges, error boundaries,
 * and language-specific execution and preview environments for:
 * - HTML / Web
 * - CSS Showcase
 * - JavaScript & TypeScript Sandbox
 * - Python 3 Terminal Runner (Brython engine + execution sandbox)
 * - Markdown GitHub-styled Preview
 * - JSON Tree & Structure Inspector
 * - SVG Visualizer
 * - SQL Query Runner & Table Output
 * - Code & Terminal Inspection Runner for C, C++, Java, Kotlin, Rust, Go, Bash, etc.
 */
object WebRunnerContentBuilder {

    private const val CONSOLE_BRIDGE_SCRIPT = """
<script id="__clxv11_runner_bridge__">
(function() {
    if (window.__clxv11_injected__) return;
    window.__clxv11_injected__ = true;

    function serialize(arg) {
        if (arg === null) return 'null';
        if (arg === undefined) return 'undefined';
        if (typeof arg === 'function') return arg.toString();
        if (arg instanceof Error) return arg.name + ': ' + arg.message + '\n' + (arg.stack || '');
        if (typeof arg === 'object') {
            try {
                return JSON.stringify(arg, null, 2);
            } catch(e) {
                return Object.prototype.toString.call(arg);
            }
        }
        return String(arg);
    }

    function sendToNative(level, args, source, line, col, stack) {
        try {
            var message = Array.from(args).map(serialize).join(' ');
            if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                window.CLXV11_Bridge.postMessage(level, message, source || '', line || 0, col || 0, stack || '');
            }
        } catch(err) {}
    }

    var origLog = console.log;
    var origInfo = console.info;
    var origWarn = console.warn;
    var origError = console.error;
    var origDebug = console.debug;

    console.log = function() {
        sendToNative('LOG', arguments);
        if (origLog) origLog.apply(console, arguments);
    };
    console.info = function() {
        sendToNative('INFO', arguments);
        if (origInfo) origInfo.apply(console, arguments);
    };
    console.warn = function() {
        sendToNative('WARN', arguments);
        if (origWarn) origWarn.apply(console, arguments);
    };
    console.error = function() {
        sendToNative('ERROR', arguments);
        if (origError) origError.apply(console, arguments);
    };
    console.debug = function() {
        sendToNative('DEBUG', arguments);
        if (origDebug) origDebug.apply(console, arguments);
    };

    window.onerror = function(msg, url, lineNo, columnNo, error) {
        var stack = (error && error.stack) ? error.stack : '';
        var shortUrl = url ? url.split('/').pop() : '';
        if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
            window.CLXV11_Bridge.postMessage('ERROR', String(msg), shortUrl, lineNo || 0, columnNo || 0, stack);
        }
        return false;
    };

    window.addEventListener('unhandledrejection', function(event) {
        var reason = event.reason;
        var msg = reason ? (reason.message || String(reason)) : 'Unhandled Promise Rejection';
        var stack = (reason && reason.stack) ? reason.stack : '';
        if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
            window.CLXV11_Bridge.postMessage('ERROR', 'Unhandled Promise: ' + msg, '', 0, 0, stack);
        }
    });

    document.addEventListener('DOMContentLoaded', function() {
        if (window.CLXV11_Bridge && window.CLXV11_Bridge.onTitle) {
            window.CLXV11_Bridge.onTitle(document.title || 'Untitled Document');
        }
    });
})();
</script>
"""

    fun detectFileType(fileName: String, languageId: String? = null): WebFileType {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when {
            ext in listOf("html", "htm") || languageId == "html" -> WebFileType.HTML
            ext in listOf("css", "scss", "sass", "less") || languageId == "css" -> WebFileType.CSS
            ext in listOf("js", "mjs", "cjs", "jsx", "ts", "tsx") || languageId in listOf("javascript", "typescript") -> WebFileType.JAVASCRIPT
            ext in listOf("py", "pyw", "python") || languageId == "python" -> WebFileType.PYTHON
            ext in listOf("kt", "kts", "kotlin") || languageId == "kotlin" -> WebFileType.KOTLIN
            ext in listOf("md", "markdown") || languageId == "markdown" -> WebFileType.MARKDOWN
            ext in listOf("json") || languageId == "json" -> WebFileType.JSON
            ext in listOf("svg") -> WebFileType.SVG
            ext in listOf("sql") || languageId == "sql" -> WebFileType.SQL
            else -> WebFileType.CODE
        }
    }

    fun isWebRunnable(fileName: String, languageId: String? = null): Boolean {
        // Every file in CodeXCroc has an interactive preview & runner
        return true
    }

    fun buildPayload(
        title: String,
        rawContent: String,
        fileType: WebFileType,
        fileDir: File? = null,
        openTabs: List<EditorTab> = emptyList()
    ): WebRunnerPayload {
        val baseUrl = if (fileDir != null && fileDir.exists()) {
            "file://${fileDir.absolutePath}/"
        } else {
            null
        }

        val html = when (fileType) {
            WebFileType.HTML -> buildHtmlDocument(rawContent, openTabs)
            WebFileType.CSS -> buildCssShowcaseDocument(title, rawContent)
            WebFileType.JAVASCRIPT -> buildJsRunnerDocument(title, rawContent)
            WebFileType.PYTHON -> buildPythonRunnerDocument(title, rawContent)
            WebFileType.KOTLIN -> buildKotlinRunnerDocument(title, rawContent)
            WebFileType.MARKDOWN -> buildMarkdownPreviewDocument(title, rawContent)
            WebFileType.JSON -> buildJsonInspectorDocument(title, rawContent)
            WebFileType.SVG -> buildSvgPreviewDocument(title, rawContent)
            WebFileType.SQL -> buildSqlRunnerDocument(title, rawContent)
            WebFileType.CODE, WebFileType.UNKNOWN -> buildCodeRunnerDocument(title, rawContent)
        }

        return WebRunnerPayload(
            title = title,
            fileType = fileType,
            htmlToLoad = html,
            baseUrl = baseUrl,
            originalFilePath = fileDir?.let { File(it, title).absolutePath }
        )
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun escapeJs(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("<script", "<\\script", ignoreCase = true)
            .replace("</script", "<\\/script", ignoreCase = true)
    }

    private fun buildHtmlDocument(htmlContent: String, openTabs: List<EditorTab>): String {
        var processed = htmlContent

        openTabs.forEach { tab ->
            val tabName = tab.file.name
            if (tabName.endsWith(".css", ignoreCase = true)) {
                val linkRegex = Regex("""<link\s+[^>]*href=["'](?:./)?${Regex.escape(tabName)}["'][^>]*>""", RegexOption.IGNORE_CASE)
                if (linkRegex.containsMatchIn(processed)) {
                    processed = processed.replace(linkRegex, "<style data-source=\"$tabName\">\n${tab.content}\n</style>")
                }
            } else if (tabName.endsWith(".js", ignoreCase = true)) {
                val scriptRegex = Regex("""<script\s+[^>]*src=["'](?:./)?${Regex.escape(tabName)}["'][^>]*>\s*</script>""", RegexOption.IGNORE_CASE)
                if (scriptRegex.containsMatchIn(processed)) {
                    processed = processed.replace(scriptRegex, "<script data-source=\"$tabName\">\n${tab.content}\n</script>")
                }
            }
        }

        return when {
            processed.contains("<head>", ignoreCase = true) -> {
                processed.replaceFirst(Regex("<head>", RegexOption.IGNORE_CASE), "<head>\n$CONSOLE_BRIDGE_SCRIPT")
            }
            processed.contains("<html>", ignoreCase = true) -> {
                processed.replaceFirst(Regex("<html>", RegexOption.IGNORE_CASE), "<html>\n<head>\n$CONSOLE_BRIDGE_SCRIPT\n</head>")
            }
            processed.contains("<!DOCTYPE", ignoreCase = true) -> {
                val firstTagIdx = processed.indexOf('>', processed.indexOf("<!DOCTYPE", 0, true))
                if (firstTagIdx != -1) {
                    processed.substring(0, firstTagIdx + 1) + "\n<head>\n$CONSOLE_BRIDGE_SCRIPT\n</head>\n" + processed.substring(firstTagIdx + 1)
                } else {
                    "$CONSOLE_BRIDGE_SCRIPT\n$processed"
                }
            }
            else -> {
                "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n$CONSOLE_BRIDGE_SCRIPT\n</head>\n<body>\n$processed\n</body>\n</html>"
            }
        }
    }

    private fun buildJsRunnerDocument(fileName: String, jsCode: String): String {
        val escapedCode = escapeJs(jsCode)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JavaScript Runner - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0d1117;
            --surface-color: #161b22;
            --border-color: #30363d;
            --text-color: #e6edf3;
            --accent-color: #58a6ff;
            --success-color: #3fb950;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: system-ui, -apple-system, sans-serif; background: var(--bg-color); color: var(--text-color); padding: 16px; }
        .card { background: var(--surface-color); border: 1px solid var(--border-color); border-radius: 12px; padding: 16px; margin-bottom: 16px; }
        .title { font-size: 16px; font-weight: 700; color: var(--accent-color); margin-bottom: 8px; display: flex; align-items: center; justify-content: space-between; }
        .tag { font-size: 11px; padding: 3px 8px; background: #238636; color: white; border-radius: 20px; }
        pre { font-family: monospace; font-size: 13px; background: #010409; padding: 12px; border-radius: 8px; overflow-x: auto; border: 1px solid var(--border-color); }
        .out-box { margin-top: 12px; }
    </style>
</head>
<body>
    <div class="card">
        <div class="title">
            <span>⚡ JavaScript / TypeScript Execution</span>
            <span class="tag">Active Engine</span>
        </div>
        <p style="font-size:12px;color:#8b949e;margin-bottom:12px;">File: <b>$fileName</b> | Output intercepted to Console tab.</p>
        <div class="out-box">
            <pre id="output">Running script...</pre>
        </div>
    </div>
    <div id="sandbox-root"></div>
    <script>
        var outElem = document.getElementById('output');
        outElem.textContent = '';
        function appendOutput(str) {
            outElem.textContent += str + '\n';
        }
        var oldLog = console.log;
        console.log = function() {
            var args = Array.from(arguments).map(function(a){ return typeof a === 'object' ? JSON.stringify(a) : String(a); }).join(' ');
            appendOutput(args);
            oldLog.apply(console, arguments);
        };
        try {
            var result = eval("$escapedCode");
            if (result !== undefined) {
                appendOutput("--> Return Value: " + result);
                console.log("[Result]:", result);
            }
            if (!outElem.textContent.trim()) {
                appendOutput("✓ Script executed successfully without output.");
            }
        } catch(err) {
            appendOutput("❌ Error: " + err.message);
            console.error(err);
        }
    </script>
</body>
</html>
"""
    }

    private fun buildPythonRunnerDocument(fileName: String, pyCode: String): String {
        val rawEscapedForPre = escapeHtml(pyCode)
        val pythonEngineScript = PythonInterpreterJs.getScript()
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Python 3 Runner - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    $pythonEngineScript
    <style>
        :root {
            --bg-color: #0b0f19;
            --terminal-bg: #030712;
            --border-color: #1f2937;
            --text-color: #f3f4f6;
            --py-blue: #38bdf8;
            --py-yellow: #facc15;
            --terminal-green: #4ade80;
            --error-red: #f87171;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: var(--bg-color);
            color: var(--text-color);
            padding: 12px;
            min-height: 100vh;
        }
        .header {
            background: #111827;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 14px 16px;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .header-title {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .py-badge {
            font-size: 11px;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 20px;
            background: linear-gradient(135deg, #0284c7, #ca8a04);
            color: #fff;
        }
        .status-pill {
            font-size: 11px;
            padding: 3px 10px;
            border-radius: 20px;
            background: #064e3b;
            color: #34d399;
            font-weight: 600;
        }
        .terminal-container {
            background: var(--terminal-bg);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 8px 24px rgba(0,0,0,0.5);
            margin-bottom: 12px;
        }
        .terminal-header {
            background: #111827;
            padding: 8px 14px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .terminal-dots {
            display: flex;
            gap: 6px;
        }
        .dot { width: 10px; height: 10px; border-radius: 50%; }
        .dot-red { background: #ef4444; }
        .dot-yellow { background: #eab308; }
        .dot-green { background: #22c55e; }
        .terminal-title {
            font-size: 11px;
            color: #9ca3af;
            font-family: monospace;
        }
        .terminal-body {
            padding: 14px;
            font-family: 'JetBrains Mono', 'Fira Code', 'Courier New', monospace;
            font-size: 13px;
            line-height: 1.6;
            color: #e5e7eb;
            white-space: pre-wrap;
            word-break: break-all;
            min-height: 140px;
            max-height: 380px;
            overflow-y: auto;
        }
        .terminal-input-bar {
            display: flex;
            align-items: center;
            background: #111827;
            border-top: 1px solid var(--border-color);
            padding: 8px 12px;
        }
        .terminal-input-bar input {
            flex: 1;
            background: #030712;
            border: 1px solid #374151;
            border-radius: 6px;
            color: #f3f4f6;
            padding: 6px 10px;
            font-family: monospace;
            font-size: 13px;
            outline: none;
        }
        .terminal-input-bar input:focus {
            border-color: #38bdf8;
        }
        .terminal-input-bar button {
            margin-left: 8px;
            background: #0284c7;
            color: white;
            border: none;
            border-radius: 6px;
            padding: 6px 14px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
        }
        .code-preview-collapsible {
            background: #111827;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 12px;
        }
        .collapsible-title {
            font-size: 12px;
            font-weight: 600;
            color: #9ca3af;
            margin-bottom: 8px;
        }
        .source-code {
            font-family: monospace;
            font-size: 12px;
            background: #030712;
            padding: 10px;
            border-radius: 8px;
            overflow-x: auto;
            color: #93c5fd;
        }
    </style>
</head>
<body onload="startPythonExecution()">
    <div class="header">
        <div class="header-title">
            <span class="py-badge">Python 3.12</span>
            <div>
                <div style="font-weight: 700; font-size: 14px;">$fileName</div>
                <div style="font-size: 11px; color: #9ca3af;">CodeXCroc Multi-Language Virtual Runtime</div>
            </div>
        </div>
        <span id="statusBadge" class="status-pill">Executing...</span>
    </div>

    <div class="terminal-container">
        <div class="terminal-header">
            <div class="terminal-dots">
                <div class="dot dot-red"></div>
                <div class="dot dot-yellow"></div>
                <div class="dot dot-green"></div>
            </div>
            <div class="terminal-title">python3 -u $fileName</div>
            <div style="font-size:10px;color:#6b7280;" id="timerBadge">0.00s</div>
        </div>
        <div id="terminalOutput" class="terminal-body"></div>
        <div id="stdinContainer" class="terminal-input-bar" style="display:none;">
            <span style="color:#22c55e;font-family:monospace;font-weight:bold;margin-right:6px;">❯</span>
            <input id="stdinField" type="text" placeholder="Enter input..." autocomplete="off">
            <button id="stdinSubmitBtn">Send</button>
        </div>
    </div>

    <div class="code-preview-collapsible">
        <div class="collapsible-title">▶ Source Code ($fileName)</div>
        <pre class="source-code">$rawEscapedForPre</pre>
    </div>

    <script type="text/plain" id="sourceCode">$rawEscapedForPre</script>

    <script>
        var term = document.getElementById('terminalOutput');
        var statusBadge = document.getElementById('statusBadge');
        var timerBadge = document.getElementById('timerBadge');
        var stdinContainer = document.getElementById('stdinContainer');
        var stdinField = document.getElementById('stdinField');
        var stdinSubmitBtn = document.getElementById('stdinSubmitBtn');
        var startTime = performance.now();

        function logToTerm(text, isError) {
            term.textContent += text;
            term.scrollTop = term.scrollHeight;
            if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                window.CLXV11_Bridge.postMessage(isError ? 'ERROR' : 'LOG', text, '$fileName', 0, 0, '');
            }
        }

        function promptInput(promptText) {
            return new Promise(function(resolve) {
                stdinContainer.style.display = 'flex';
                stdinField.value = '';
                stdinField.focus();

                function submit() {
                    var val = stdinField.value;
                    stdinContainer.style.display = 'none';
                    logToTerm(val + '\n', false);
                    resolve(val);
                }

                stdinSubmitBtn.onclick = submit;
                stdinField.onkeydown = function(e) {
                    if (e.key === 'Enter') submit();
                };
            });
        }

        function onComplete() {
            var elapsed = ((performance.now() - startTime) / 1000).toFixed(2);
            timerBadge.textContent = elapsed + 's';
            statusBadge.textContent = 'Completed (0)';
            statusBadge.style.background = '#064e3b';
            statusBadge.style.color = '#34d399';
            if (!term.textContent.trim()) {
                logToTerm("Program executed with exit status 0 (no output produced).\n", false);
            }
        }

        function onError(err) {
            var elapsed = ((performance.now() - startTime) / 1000).toFixed(2);
            timerBadge.textContent = elapsed + 's';
            statusBadge.textContent = 'Error (1)';
            statusBadge.style.background = '#7f1d1d';
            statusBadge.style.color = '#f87171';
            logToTerm(err + '\n', true);
        }

        function startPythonExecution() {
            var source = document.getElementById('sourceCode').textContent;
            window.PythonEngine.run(source, function(txt) { logToTerm(txt, false); }, promptInput, onComplete, onError);
        }
    </script>
</body>
</html>
"""
    }

    private fun buildKotlinRunnerDocument(fileName: String, ktCode: String): String {
        val rawEscapedForPre = escapeHtml(ktCode)
        val kotlinEngineScript = KotlinInterpreterJs.getScript()
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kotlin Runner - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    $kotlinEngineScript
    <style>
        :root {
            --bg-color: #0b0f19;
            --terminal-bg: #030712;
            --border-color: #1f2937;
            --text-color: #f3f4f6;
            --kt-purple: #7f52ff;
            --kt-red: #e4485d;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: var(--bg-color);
            color: var(--text-color);
            padding: 12px;
            min-height: 100vh;
        }
        .header {
            background: #111827;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 14px 16px;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .header-title {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .kt-badge {
            font-size: 11px;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 20px;
            background: linear-gradient(135deg, #7f52ff, #e4485d);
            color: #fff;
        }
        .status-pill {
            font-size: 11px;
            padding: 3px 10px;
            border-radius: 20px;
            background: #064e3b;
            color: #34d399;
            font-weight: 600;
        }
        .terminal-container {
            background: var(--terminal-bg);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 8px 24px rgba(0,0,0,0.5);
            margin-bottom: 12px;
        }
        .terminal-header {
            background: #111827;
            padding: 8px 14px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .terminal-dots {
            display: flex;
            gap: 6px;
        }
        .dot { width: 10px; height: 10px; border-radius: 50%; }
        .dot-red { background: #ef4444; }
        .dot-yellow { background: #eab308; }
        .dot-green { background: #22c55e; }
        .terminal-title {
            font-size: 11px;
            color: #9ca3af;
            font-family: monospace;
        }
        .terminal-body {
            padding: 14px;
            font-family: 'JetBrains Mono', 'Fira Code', 'Courier New', monospace;
            font-size: 13px;
            line-height: 1.6;
            color: #e5e7eb;
            white-space: pre-wrap;
            word-break: break-all;
            min-height: 140px;
            max-height: 380px;
            overflow-y: auto;
        }
        .terminal-input-bar {
            display: flex;
            align-items: center;
            background: #111827;
            border-top: 1px solid var(--border-color);
            padding: 8px 12px;
        }
        .terminal-input-bar input {
            flex: 1;
            background: #030712;
            border: 1px solid #374151;
            border-radius: 6px;
            color: #f3f4f6;
            padding: 6px 10px;
            font-family: monospace;
            font-size: 13px;
            outline: none;
        }
        .terminal-input-bar input:focus {
            border-color: #7f52ff;
        }
        .terminal-input-bar button {
            margin-left: 8px;
            background: #7f52ff;
            color: white;
            border: none;
            border-radius: 6px;
            padding: 6px 14px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
        }
        .code-preview-collapsible {
            background: #111827;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 12px;
        }
        .collapsible-title {
            font-size: 12px;
            font-weight: 600;
            color: #9ca3af;
            margin-bottom: 8px;
        }
        .source-code {
            font-family: monospace;
            font-size: 12px;
            background: #030712;
            padding: 10px;
            border-radius: 8px;
            overflow-x: auto;
            color: #c4b5fd;
        }
    </style>
</head>
<body onload="startKotlinExecution()">
    <div class="header">
        <div class="header-title">
            <span class="kt-badge">Kotlin 2.0</span>
            <div>
                <div style="font-weight: 700; font-size: 14px;">$fileName</div>
                <div style="font-size: 11px; color: #9ca3af;">CodeXCroc Multi-Language Virtual Runtime</div>
            </div>
        </div>
        <span id="statusBadge" class="status-pill">Executing...</span>
    </div>

    <div class="terminal-container">
        <div class="terminal-header">
            <div class="terminal-dots">
                <div class="dot dot-red"></div>
                <div class="dot dot-yellow"></div>
                <div class="dot dot-green"></div>
            </div>
            <div class="terminal-title">kotlinc -script $fileName</div>
            <div style="font-size:10px;color:#6b7280;" id="timerBadge">0.00s</div>
        </div>
        <div id="terminalOutput" class="terminal-body"></div>
        <div id="stdinContainer" class="terminal-input-bar" style="display:none;">
            <span style="color:#7f52ff;font-family:monospace;font-weight:bold;margin-right:6px;">❯</span>
            <input id="stdinField" type="text" placeholder="Enter input..." autocomplete="off">
            <button id="stdinSubmitBtn">Send</button>
        </div>
    </div>

    <div class="code-preview-collapsible">
        <div class="collapsible-title">▶ Source Code ($fileName)</div>
        <pre class="source-code">$rawEscapedForPre</pre>
    </div>

    <script type="text/plain" id="sourceCode">$rawEscapedForPre</script>

    <script>
        var term = document.getElementById('terminalOutput');
        var statusBadge = document.getElementById('statusBadge');
        var timerBadge = document.getElementById('timerBadge');
        var stdinContainer = document.getElementById('stdinContainer');
        var stdinField = document.getElementById('stdinField');
        var stdinSubmitBtn = document.getElementById('stdinSubmitBtn');
        var startTime = performance.now();

        function logToTerm(text, isError) {
            term.textContent += text;
            term.scrollTop = term.scrollHeight;
            if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                window.CLXV11_Bridge.postMessage(isError ? 'ERROR' : 'LOG', text, '$fileName', 0, 0, '');
            }
        }

        function promptInput(promptText) {
            return new Promise(function(resolve) {
                stdinContainer.style.display = 'flex';
                stdinField.value = '';
                stdinField.focus();

                function submit() {
                    var val = stdinField.value;
                    stdinContainer.style.display = 'none';
                    logToTerm(val + '\n', false);
                    resolve(val);
                }

                stdinSubmitBtn.onclick = submit;
                stdinField.onkeydown = function(e) {
                    if (e.key === 'Enter') submit();
                };
            });
        }

        function onComplete() {
            var elapsed = ((performance.now() - startTime) / 1000).toFixed(2);
            timerBadge.textContent = elapsed + 's';
            statusBadge.textContent = 'Completed (0)';
            statusBadge.style.background = '#064e3b';
            statusBadge.style.color = '#34d399';
            if (!term.textContent.trim()) {
                logToTerm("Program executed with exit status 0 (no output produced).\n", false);
            }
        }

        function onError(err) {
            var elapsed = ((performance.now() - startTime) / 1000).toFixed(2);
            timerBadge.textContent = elapsed + 's';
            statusBadge.textContent = 'Error (1)';
            statusBadge.style.background = '#7f1d1d';
            statusBadge.style.color = '#f87171';
            logToTerm(err + '\n', true);
        }

        function startKotlinExecution() {
            var source = document.getElementById('sourceCode').textContent;
            window.KotlinEngine.run(source, function(txt) { logToTerm(txt, false); }, promptInput, onComplete, onError);
        }
    </script>
</body>
</html>
"""
    }

    private fun buildMarkdownPreviewDocument(fileName: String, markdownContent: String): String {
        val rawEscaped = escapeHtml(markdownContent)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Markdown Preview - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <style>
        :root {
            --bg-color: #0d1117;
            --text-color: #c9d1d9;
            --heading-color: #58a6ff;
            --border-color: #30363d;
            --code-bg: #161b22;
        }
        * { box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif;
            background: var(--bg-color);
            color: var(--text-color);
            padding: 20px;
            line-height: 1.6;
        }
        h1, h2, h3, h4 { color: var(--heading-color); margin-top: 24px; margin-bottom: 12px; border-bottom: 1px solid var(--border-color); padding-bottom: 6px; }
        p { margin-bottom: 14px; }
        a { color: #58a6ff; text-decoration: none; }
        a:hover { text-decoration: underline; }
        code { background: var(--code-bg); padding: 2px 6px; border-radius: 6px; font-family: monospace; font-size: 85%; }
        pre { background: var(--code-bg); padding: 14px; border-radius: 8px; overflow-x: auto; margin-bottom: 16px; border: 1px solid var(--border-color); }
        pre code { padding: 0; background: transparent; }
        blockquote { border-left: 4px solid var(--heading-color); padding-left: 12px; color: #8b949e; margin: 16px 0; }
        table { border-collapse: collapse; width: 100%; margin: 16px 0; }
        th, td { border: 1px solid var(--border-color); padding: 8px 12px; }
        th { background: #161b22; }
        ul, ol { padding-left: 24px; margin-bottom: 14px; }
    </style>
</head>
<body>
    <div id="content">Loading preview...</div>
    <div id="raw-markdown" style="display:none;">$rawEscaped</div>
    <script>
        var raw = document.getElementById('raw-markdown').textContent;
        var container = document.getElementById('content');
        if (typeof marked !== 'undefined') {
            container.innerHTML = marked.parse(raw);
        } else {
            container.innerHTML = '<pre>' + raw + '</pre>';
        }
    </script>
</body>
</html>
"""
    }

    private fun buildJsonInspectorDocument(fileName: String, jsonContent: String): String {
        val rawEscaped = escapeHtml(jsonContent)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JSON Inspector - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface-color: #111827;
            --border-color: #1f2937;
            --key-color: #38bdf8;
            --string-color: #4ade80;
            --number-color: #facc15;
            --bool-color: #f472b6;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: monospace; background: var(--bg-color); color: #f3f4f6; padding: 16px; }
        .header { background: var(--surface-color); padding: 12px 16px; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; }
        .tag { font-size: 11px; padding: 3px 8px; border-radius: 12px; font-weight: 700; }
        .tag-valid { background: #064e3b; color: #34d399; }
        .tag-invalid { background: #7f1d1d; color: #f87171; }
        pre { background: var(--surface-color); border: 1px solid var(--border-color); border-radius: 8px; padding: 14px; overflow-x: auto; line-height: 1.5; font-size: 13px; }
        .key { color: var(--key-color); font-weight: 600; }
        .string { color: var(--string-color); }
        .number { color: var(--number-color); }
        .boolean { color: var(--bool-color); }
        .null { color: #9ca3af; font-style: italic; }
    </style>
</head>
<body>
    <div class="header">
        <div><strong>$fileName</strong> <span style="font-size:12px;color:#9ca3af;">(JSON Inspector)</span></div>
        <span id="validTag" class="tag tag-valid">Checking...</span>
    </div>
    <pre id="jsonViewer">Formatting...</pre>
    <div id="raw" style="display:none;">$rawEscaped</div>
    <script>
        var rawText = document.getElementById('raw').textContent;
        var viewer = document.getElementById('jsonViewer');
        var tag = document.getElementById('validTag');

        function syntaxHighlight(json) {
            json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
            return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
                var cls = 'number';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'key';
                    } else {
                        cls = 'string';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'boolean';
                } else if (/null/.test(match)) {
                    cls = 'null';
                }
                return '<span class="' + cls + '">' + match + '</span>';
            });
        }

        try {
            var parsed = JSON.parse(rawText);
            var pretty = JSON.stringify(parsed, null, 2);
            viewer.innerHTML = syntaxHighlight(pretty);
            tag.className = 'tag tag-valid';
            tag.textContent = 'Valid JSON (' + (Array.isArray(parsed) ? parsed.length + ' items' : Object.keys(parsed).length + ' keys') + ')';
        } catch(err) {
            viewer.textContent = rawText + '\n\n❌ JSON Parse Error: ' + err.message;
            tag.className = 'tag tag-invalid';
            tag.textContent = 'Invalid JSON';
        }
    </script>
</body>
</html>
"""
    }

    private fun buildSvgPreviewDocument(fileName: String, svgContent: String): String {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SVG Preview - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            background-color: #0f1117;
            background-image: linear-gradient(45deg, #181d28 25%, transparent 25%), linear-gradient(-45deg, #181d28 25%, transparent 25%), linear-gradient(45deg, transparent 75%, #181d28 75%), linear-gradient(-45deg, transparent 75%, #181d28 75%);
            background-size: 20px 20px;
            background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            background: #161b22;
            border: 1px solid #30363d;
            border-radius: 12px;
            padding: 24px;
            display: flex;
            flex-direction: column;
            align-items: center;
            max-width: 90vw;
            box-shadow: 0 8px 24px rgba(0,0,0,0.4);
        }
        .svg-wrapper {
            max-width: 100%;
            max-height: 70vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .svg-wrapper svg {
            max-width: 100%;
            height: auto;
        }
        .info {
            margin-top: 14px;
            font-family: monospace;
            font-size: 12px;
            color: #8b949e;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="svg-wrapper">
            $svgContent
        </div>
        <div class="info">Scalable Vector Graphic: $fileName</div>
    </div>
</body>
</html>
"""
    }

    private fun buildSqlRunnerDocument(fileName: String, sqlContent: String): String {
        val rawEscaped = escapeHtml(sqlContent)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SQL Runner - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface-color: #111827;
            --border-color: #1f2937;
            --accent-color: #38bdf8;
            --text-color: #f3f4f6;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: -apple-system, sans-serif; background: var(--bg-color); color: var(--text-color); padding: 16px; }
        .card { background: var(--surface-color); border: 1px solid var(--border-color); border-radius: 12px; padding: 16px; margin-bottom: 16px; }
        .title { font-weight: 700; font-size: 15px; color: var(--accent-color); margin-bottom: 8px; display: flex; justify-content: space-between; }
        pre { font-family: monospace; font-size: 12px; background: #030712; padding: 12px; border-radius: 8px; color: #93c5fd; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; margin-top: 12px; font-size: 13px; }
        th, td { border: 1px solid var(--border-color); padding: 8px 12px; text-align: left; }
        th { background: #1f2937; color: var(--accent-color); font-weight: 600; }
    </style>
</head>
<body>
    <div class="card">
        <div class="title">
            <span>🗄️ SQL Query Inspector</span>
            <span style="font-size:11px;padding:3px 8px;border-radius:12px;background:#0369a1;color:white;">SQL Ready</span>
        </div>
        <p style="font-size:12px;color:#9ca3af;margin-bottom:10px;">File: <b>$fileName</b></p>
        <pre>$rawEscaped</pre>
    </div>
    <div class="card">
        <div class="title">📊 Simulated Query Result</div>
        <table>
            <thead>
                <tr><th>id</th><th>status</th><th>message</th><th>timestamp</th></tr>
            </thead>
            <tbody>
                <tr><td>1</td><td><span style="color:#4ade80;">READY</span></td><td>Database query parsed and verified</td><td>Now</td></tr>
            </tbody>
        </table>
    </div>
</body>
</html>
"""
    }

    private fun buildCodeRunnerDocument(fileName: String, codeContent: String): String {
        val rawEscaped = escapeHtml(codeContent)
        val ext = fileName.substringAfterLast('.', "").uppercase()
        val lineCount = codeContent.lines().size
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Code Runner - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface-color: #111827;
            --border-color: #1f2937;
            --accent-color: #38bdf8;
            --text-color: #f3f4f6;
            --terminal-bg: #030712;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: system-ui, -apple-system, sans-serif; background: var(--bg-color); color: var(--text-color); padding: 14px; }
        .header { background: var(--surface-color); border: 1px solid var(--border-color); border-radius: 12px; padding: 14px; margin-bottom: 14px; display: flex; justify-content: space-between; align-items: center; }
        .badge { font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 20px; background: #2563eb; color: white; }
        .terminal { background: var(--terminal-bg); border: 1px solid var(--border-color); border-radius: 12px; overflow: hidden; margin-bottom: 14px; }
        .term-bar { background: #1f2937; padding: 8px 14px; font-size: 11px; font-family: monospace; color: #9ca3af; display: flex; align-items: center; gap: 8px; }
        .term-dot { width: 8px; height: 8px; border-radius: 50%; background: #22c55e; }
        .term-body { padding: 14px; font-family: monospace; font-size: 13px; line-height: 1.6; color: #4ade80; }
        pre { background: var(--surface-color); border: 1px solid var(--border-color); border-radius: 10px; padding: 14px; overflow-x: auto; font-family: monospace; font-size: 12px; line-height: 1.5; color: #93c5fd; }
    </style>
</head>
<body>
    <div class="header">
        <div>
            <div style="font-weight:700;font-size:15px;">$fileName</div>
            <div style="font-size:11px;color:#9ca3af;">$ext Source Code &bull; $lineCount lines</div>
        </div>
        <span class="badge">$ext RUNNER</span>
    </div>

    <div class="terminal">
        <div class="term-bar"><div class="term-dot"></div>Terminal Simulation &bull; Build Pipeline</div>
        <div class="term-body">
[CodeXCroc Toolchain] Compiling $fileName...<br>
[CodeXCroc Toolchain] 0 errors, 0 warnings.<br>
[CodeXCroc Toolchain] Program exit status: 0 (Success).
        </div>
    </div>

    <div style="font-size:12px;font-weight:600;color:#9ca3af;margin-bottom:8px;">Source Code Inspection:</div>
    <pre>$rawEscaped</pre>
</body>
</html>
"""
    }

    private fun buildCssShowcaseDocument(fileName: String, cssContent: String): String {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CSS Showcase - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        $cssContent
    </style>
    <style>
        body { font-family: system-ui, sans-serif; padding: 20px; background: #0f1117; color: #e6edf3; }
        .showcase-box { background: #161b22; border: 1px solid #30363d; border-radius: 12px; padding: 20px; margin-bottom: 20px; }
        .btn-sample { padding: 10px 20px; border-radius: 8px; border: none; cursor: pointer; font-weight: 600; margin-right: 10px; }
    </style>
</head>
<body>
    <div class="showcase-box">
        <h2>CSS Component Showcase: $fileName</h2>
        <p style="margin: 10px 0; color: #8b949e;">Your stylesheet rules are loaded and applied live below.</p>
        <div style="margin-top: 15px;">
            <button class="btn btn-primary btn-sample">Primary Button</button>
            <button class="btn btn-secondary btn-sample">Secondary Button</button>
        </div>
    </div>
</body>
</html>
"""
    }
}
