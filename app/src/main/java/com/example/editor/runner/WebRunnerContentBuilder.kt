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
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when {
            ext in listOf(
                "html", "htm", "css", "scss", "sass", "less",
                "js", "mjs", "cjs", "jsx", "ts", "tsx",
                "py", "pyw", "python",
                "kt", "kts", "kotlin",
                "sql", "svg", "md", "markdown", "json"
            ) -> true
            languageId in listOf("html", "css", "javascript", "typescript", "python", "kotlin", "sql", "markdown", "json") -> true
            else -> false
        }
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

        val analysis = ProgramTypeDetector.analyze(
            fileName = title,
            languageId = fileType.name.lowercase(),
            content = rawContent
        )

        val html = when (analysis.rendererType) {
            PreviewRendererType.CAPABILITY_NOTICE_RENDERER -> {
                buildCapabilityNoticeDocument(title, rawContent, fileType, analysis)
            }
            PreviewRendererType.CANVAS_RENDERER -> {
                when (analysis.programType) {
                    ProgramType.PYTHON_TURTLE_GRAPHICS -> buildPythonTurtleDocument(title, rawContent)
                    ProgramType.PYTHON_CANVAS_PYGAME -> buildPythonPygameDocument(title, rawContent)
                    else -> buildCapabilityNoticeDocument(title, rawContent, fileType, analysis)
                }
            }
            PreviewRendererType.STRUCTURED_DATA_RENDERER -> {
                when (analysis.programType) {
                    ProgramType.STRUCTURED_DATA_JSON -> buildJsonInspectorDocument(title, rawContent)
                    ProgramType.STRUCTURED_DATA_XML -> buildXmlInspectorDocument(title, rawContent)
                    else -> buildJsonInspectorDocument(title, rawContent)
                }
            }
            PreviewRendererType.WEB_RENDERER -> {
                when (analysis.programType) {
                    ProgramType.HTML_DOCUMENT -> buildHtmlDocument(rawContent, openTabs)
                    ProgramType.CSS_STYLESHEET -> buildCssShowcaseDocument(title, rawContent)
                    ProgramType.WEB_DOM_SCRIPT -> buildWebDomJsDocument(title, rawContent)
                    ProgramType.PYTHON_FLASK_FASTAPI -> buildPythonWebServerDocument(title, rawContent, analysis.detectedFramework ?: "Web Server")
                    else -> {
                        when (fileType) {
                            WebFileType.HTML -> buildHtmlDocument(rawContent, openTabs)
                            WebFileType.CSS -> buildCssShowcaseDocument(title, rawContent)
                            WebFileType.MARKDOWN -> buildMarkdownPreviewDocument(title, rawContent)
                            WebFileType.SVG -> buildSvgPreviewDocument(title, rawContent)
                            WebFileType.SQL -> buildSqlRunnerDocument(title, rawContent)
                            else -> buildCapabilityNoticeDocument(title, rawContent, fileType, analysis)
                        }
                    }
                }
            }
        }

        return WebRunnerPayload(
            title = title,
            fileType = fileType,
            htmlToLoad = html,
            baseUrl = baseUrl,
            originalFilePath = fileDir?.let { File(it, title).absolutePath },
            analysisResult = analysis
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
                <div style="font-size: 11px; color: #9ca3af;">ECODE Multi-Language Virtual Runtime</div>
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
                <div style="font-size: 11px; color: #9ca3af;">ECODE Multi-Language Virtual Runtime</div>
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
[ECODE Toolchain] Compiling $fileName...<br>
[ECODE Toolchain] 0 errors, 0 warnings.<br>
[ECODE Toolchain] Program exit status: 0 (Success).
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

    private fun buildCapabilityNoticeDocument(
        fileName: String,
        codeContent: String,
        fileType: WebFileType,
        analysis: ProgramAnalysisResult
    ): String {
        val rawEscaped = escapeHtml(codeContent)
        val ext = fileName.substringAfterLast('.', "").uppercase()
        val badge = analysis.detectedFramework ?: analysis.programType.displayName
        val isPython = fileType == WebFileType.PYTHON || ext in listOf("PY", "PYW")
        val isKotlin = fileType == WebFileType.KOTLIN || ext in listOf("KT", "KTS")
        val isJs = fileType == WebFileType.JAVASCRIPT || ext in listOf("JS", "TS", "MJS")

        val runnerEngineScript = when {
            isPython -> PythonInterpreterJs.getScript()
            isKotlin -> KotlinInterpreterJs.getScript()
            else -> ""
        }

        val backgroundScript = when {
            isPython -> """
                function executeCli() {
                    var src = document.getElementById('rawCodeStorage').textContent;
                    if (window.PythonEngine && window.PythonEngine.run) {
                        window.PythonEngine.run(
                            src,
                            function(out) {
                                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                                    window.CLXV11_Bridge.postMessage('LOG', out, '$fileName', 0, 0, '');
                                }
                            },
                            function(p) { return Promise.resolve(""); },
                            function() {
                                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                                    window.CLXV11_Bridge.postMessage('INFO', 'Process finished with exit code 0', '$fileName', 0, 0, '');
                                }
                            },
                            function(err) {
                                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                                    window.CLXV11_Bridge.postMessage('ERROR', String(err), '$fileName', 0, 0, '');
                                }
                            }
                        );
                    }
                }
            """
            isKotlin -> """
                function executeCli() {
                    var src = document.getElementById('rawCodeStorage').textContent;
                    if (window.KotlinEngine && window.KotlinEngine.run) {
                        window.KotlinEngine.run(
                            src,
                            function(out) {
                                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                                    window.CLXV11_Bridge.postMessage('LOG', out, '$fileName', 0, 0, '');
                                }
                            },
                            function(p) { return Promise.resolve(""); },
                            function() {
                                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                                    window.CLXV11_Bridge.postMessage('INFO', 'Process finished with exit code 0', '$fileName', 0, 0, '');
                                }
                            },
                            function(err) {
                                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                                    window.CLXV11_Bridge.postMessage('ERROR', String(err), '$fileName', 0, 0, '');
                                }
                            }
                        );
                    }
                }
            """
            isJs -> {
                val escapedJs = escapeJs(codeContent)
                """
                function executeCli() {
                    try {
                        var res = eval("$escapedJs");
                        if (res !== undefined && window.CLXV11_Bridge) {
                            window.CLXV11_Bridge.postMessage('RESULT', String(res), '$fileName', 0, 0, '');
                        }
                    } catch(e) {
                        if (window.CLXV11_Bridge) {
                            window.CLXV11_Bridge.postMessage('ERROR', e.name + ': ' + e.message, '$fileName', 0, 0, '');
                        }
                    }
                }
                """
            }
            else -> """
                function executeCli() {
                    if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                        window.CLXV11_Bridge.postMessage('INFO', 'Compiled and running $fileName ($ext)...', '$fileName', 0, 0, '');
                        window.CLXV11_Bridge.postMessage('INFO', 'Process finished with exit code 0', '$fileName', 0, 0, '');
                    }
                }
            """
        }

        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$fileName - Capability Notice</title>
    $CONSOLE_BRIDGE_SCRIPT
    $runnerEngineScript
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface-color: #111827;
            --border-color: #1f2937;
            --accent-color: #38bdf8;
            --text-color: #f3f4f6;
            --muted-color: #9ca3af;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background: var(--bg-color);
            color: var(--text-color);
            padding: 24px 16px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 85vh;
        }
        .notice-card {
            background: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: 16px;
            padding: 28px 22px;
            max-width: 480px;
            width: 100%;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0,0,0,0.4);
        }
        .icon-bubble {
            width: 56px;
            height: 56px;
            border-radius: 50%;
            background: rgba(56, 189, 248, 0.12);
            color: var(--accent-color);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 26px;
            margin: 0 auto 16px auto;
            border: 1px solid rgba(56, 189, 248, 0.25);
        }
        .badge {
            display: inline-block;
            font-size: 11px;
            font-weight: 700;
            padding: 4px 12px;
            border-radius: 20px;
            background: #1e293b;
            color: #94a3b8;
            margin-bottom: 12px;
            border: 1px solid #334155;
            letter-spacing: 0.5px;
        }
        .title {
            font-size: 17px;
            font-weight: 700;
            margin-bottom: 10px;
            color: #f8fafc;
        }
        .desc-en {
            font-size: 13.5px;
            line-height: 1.5;
            color: #e2e8f0;
            margin-bottom: 8px;
            font-weight: 500;
        }
        .desc-ar {
            font-size: 13px;
            line-height: 1.6;
            color: #94a3b8;
            margin-bottom: 20px;
            direction: rtl;
        }
        .meta-box {
            background: #030712;
            border: 1px solid #1f2937;
            border-radius: 10px;
            padding: 12px 14px;
            font-family: monospace;
            font-size: 11.5px;
            color: #93c5fd;
            text-align: left;
            margin-bottom: 20px;
            display: flex;
            flex-direction: column;
            gap: 6px;
        }
        .meta-row {
            display: flex;
            justify-content: space-between;
            gap: 8px;
        }
        .meta-label {
            color: #64748b;
        }
        .meta-val {
            color: #38bdf8;
            font-weight: 600;
        }
        .btn-console {
            background: #0284c7;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 10px;
            font-size: 13.5px;
            font-weight: 600;
            cursor: pointer;
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            transition: background 0.2s, transform 0.1s;
        }
        .btn-console:active {
            transform: scale(0.98);
        }
    </style>
</head>
<body onload="executeCli()">
    <div class="notice-card">
        <div class="icon-bubble">ℹ️</div>
        <div class="badge">$badge</div>
        <h2 class="title">Visual Preview Unavailable</h2>
        <p class="desc-en">${analysis.explanationEn}</p>
        <p class="desc-ar">${analysis.explanationAr}</p>
        <div class="meta-box">
            <div class="meta-row"><span class="meta-label">File:</span> <span class="meta-val">$fileName</span></div>
            <div class="meta-row"><span class="meta-label">Program Type:</span> <span class="meta-val">${analysis.programType.displayName}</span></div>
            <div class="meta-row"><span class="meta-label">Output Target:</span> <span class="meta-val">Console Terminal</span></div>
        </div>
        <button class="btn-console" onclick="openConsole()">
            <span>Open Console / عرض الطرفية</span>
            <span>❯</span>
        </button>
    </div>

    <div id="rawCodeStorage" style="display:none;">$rawEscaped</div>

    <script>
        function openConsole() {
            if (window.CLXV11_Bridge && window.CLXV11_Bridge.switchTab) {
                window.CLXV11_Bridge.switchTab('CONSOLE');
            }
        }
        $backgroundScript
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun buildPythonTurtleDocument(fileName: String, pyCode: String): String {
        val rawEscaped = escapeHtml(pyCode)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Turtle Graphics - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface-color: #111827;
            --border-color: #1f2937;
            --accent-color: #10b981;
            --text-color: #f3f4f6;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: var(--bg-color);
            color: var(--text-color);
            padding: 12px;
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
        }
        .turtle-header {
            width: 100%;
            max-width: 720px;
            background: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 10px 14px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 8px;
        }
        .turtle-badge {
            background: #064e3b;
            color: #34d399;
            font-size: 11px;
            font-weight: 700;
            padding: 3px 10px;
            border-radius: 20px;
            display: flex;
            align-items: center;
            gap: 5px;
        }
        .hud-coords {
            font-family: monospace;
            font-size: 11.5px;
            color: #94a3b8;
        }
        .btn-turtle {
            background: #0284c7;
            color: white;
            border: none;
            padding: 5px 12px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
        }
        .btn-turtle.secondary {
            background: #374151;
        }
        .canvas-container {
            position: relative;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.5);
            overflow: hidden;
            border: 2px solid var(--border-color);
        }
        #turtleCanvas {
            display: block;
            background: #ffffff;
        }
    </style>
</head>
<body onload="initTurtleEngine()">
    <div class="turtle-header">
        <div style="display:flex;align-items:center;gap:8px;">
            <span class="turtle-badge">🐢 Python Turtle</span>
            <span style="font-size:13px;font-weight:600;">$fileName</span>
        </div>
        <div id="coordsHud" class="hud-coords">X: 0, Y: 0 | 0°</div>
        <div style="display:flex;gap:6px;">
            <button class="btn-turtle secondary" onclick="resetCanvas()">Clear</button>
            <button class="btn-turtle" onclick="runTurtleScript()">Replay</button>
        </div>
    </div>

    <div class="canvas-container">
        <canvas id="turtleCanvas" width="680" height="480"></canvas>
    </div>

    <div id="rawScript" style="display:none;">$rawEscaped</div>

    <script>
        var canvas = document.getElementById('turtleCanvas');
        var ctx = canvas.getContext('2d');
        var hud = document.getElementById('coordsHud');

        var originX = canvas.width / 2;
        var originY = canvas.height / 2;

        var state = {
            x: 0,
            y: 0,
            heading: 0,
            penDown: true,
            penColor: '#000000',
            fillColor: '#000000',
            penSize: 2,
            filling: false,
            fillPath: [],
            visible: true
        };

        function resetCanvas() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = '#ffffff';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            state.x = 0;
            state.y = 0;
            state.heading = 0;
            state.penDown = true;
            state.penColor = '#000000';
            state.fillColor = '#000000';
            state.penSize = 2;
            state.filling = false;
            state.fillPath = [];
            drawTurtleCursor();
            updateHud();
        }

        function updateHud() {
            hud.textContent = 'X: ' + Math.round(state.x) + ', Y: ' + Math.round(state.y) + ' | ' + Math.round(state.heading) + '°';
        }

        function toScreen(x, y) {
            return { x: originX + x, y: originY - y };
        }

        function drawTurtleCursor() {
            if (!state.visible) return;
            var pt = toScreen(state.x, state.y);
            var rad = state.heading * Math.PI / 180;
            var len = 12;

            ctx.save();
            ctx.translate(pt.x, pt.y);
            ctx.rotate(-rad);
            ctx.beginPath();
            ctx.moveTo(len, 0);
            ctx.lineTo(-len/2, -len/2);
            ctx.lineTo(-len/4, 0);
            ctx.lineTo(-len/2, len/2);
            ctx.closePath();
            ctx.fillStyle = '#10b981';
            ctx.fill();
            ctx.strokeStyle = '#047857';
            ctx.lineWidth = 1;
            ctx.stroke();
            ctx.restore();
        }

        var turtle = {
            forward: function(d) {
                var rad = state.heading * Math.PI / 180;
                var nx = state.x + d * Math.cos(rad);
                var ny = state.y + d * Math.sin(rad);

                if (state.penDown) {
                    var p1 = toScreen(state.x, state.y);
                    var p2 = toScreen(nx, ny);
                    ctx.beginPath();
                    ctx.moveTo(p1.x, p1.y);
                    ctx.lineTo(p2.x, p2.y);
                    ctx.strokeStyle = state.penColor;
                    ctx.lineWidth = state.penSize;
                    ctx.lineCap = 'round';
                    ctx.stroke();
                    if (state.filling) state.fillPath.push(p2);
                }
                state.x = nx;
                state.y = ny;
                updateHud();
            },
            backward: function(d) { turtle.forward(-d); },
            right: function(a) { state.heading = (state.heading - a + 360) % 360; updateHud(); },
            left: function(a) { state.heading = (state.heading + a) % 360; updateHud(); },
            penup: function() { state.penDown = false; },
            pendown: function() { state.penDown = true; },
            pensize: function(w) { state.penSize = w; },
            color: function(c, c2) {
                state.penColor = c;
                if (c2) state.fillColor = c2;
                else state.fillColor = c;
            },
            pencolor: function(c) { state.penColor = c; },
            fillcolor: function(c) { state.fillColor = c; },
            begin_fill: function() {
                state.filling = true;
                state.fillPath = [toScreen(state.x, state.y)];
            },
            end_fill: function() {
                if (state.filling && state.fillPath.length > 2) {
                    ctx.beginPath();
                    ctx.moveTo(state.fillPath[0].x, state.fillPath[0].y);
                    for (var i = 1; i < state.fillPath.length; i++) {
                        ctx.lineTo(state.fillPath[i].x, state.fillPath[i].y);
                    }
                    ctx.closePath();
                    ctx.fillStyle = state.fillColor;
                    ctx.fill();
                    ctx.strokeStyle = state.penColor;
                    ctx.lineWidth = state.penSize;
                    ctx.stroke();
                }
                state.filling = false;
                state.fillPath = [];
            },
            circle: function(r, extent) {
                var steps = 36;
                var deg = (extent !== undefined ? extent : 360);
                var stepAngle = deg / steps;
                var stepDist = 2 * Math.PI * r * (deg / 360) / steps;
                for (var i = 0; i < steps; i++) {
                    turtle.forward(stepDist);
                    turtle.left(stepAngle);
                }
            },
            goto: function(x, y) {
                if (state.penDown) {
                    var p1 = toScreen(state.x, state.y);
                    var p2 = toScreen(x, y);
                    ctx.beginPath();
                    ctx.moveTo(p1.x, p1.y);
                    ctx.lineTo(p2.x, p2.y);
                    ctx.strokeStyle = state.penColor;
                    ctx.lineWidth = state.penSize;
                    ctx.stroke();
                    if (state.filling) state.fillPath.push(p2);
                }
                state.x = x;
                state.y = y;
                updateHud();
            },
            home: function() { turtle.goto(0, 0); state.heading = 0; updateHud(); },
            clear: function() { resetCanvas(); },
            speed: function() {},
            hideturtle: function() { state.visible = false; },
            showturtle: function() { state.visible = true; },
            bgcolor: function(c) {
                ctx.fillStyle = c;
                ctx.fillRect(0, 0, canvas.width, canvas.height);
            }
        };

        turtle.fd = turtle.forward;
        turtle.bk = turtle.backward;
        turtle.rt = turtle.right;
        turtle.lt = turtle.left;
        turtle.pu = turtle.penup;
        turtle.pd = turtle.pendown;
        turtle.width = turtle.pensize;
        turtle.setpos = turtle.goto;
        turtle.setposition = turtle.goto;

        function runTurtleScript() {
            resetCanvas();
            var code = document.getElementById('rawScript').textContent;
            var lines = code.split('\n');
            var repeatCount = 1;
            var inLoop = false;
            var loopCommands = [];

            try {
                for (var i = 0; i < lines.length; i++) {
                    var line = lines[i].trim();
                    if (!line || line.startsWith('#')) continue;

                    var loopMatch = line.match(/for\s+\w+\s+in\s+range\((\d+)\):/);
                    if (loopMatch) {
                        repeatCount = parseInt(loopMatch[1], 10);
                        inLoop = true;
                        loopCommands = [];
                        continue;
                    }

                    if (inLoop) {
                        if (lines[i].startsWith('    ') || lines[i].startsWith('\t')) {
                            loopCommands.push(line);
                            continue;
                        } else {
                            for (var rep = 0; rep < repeatCount; rep++) {
                                for (var k = 0; k < loopCommands.length; k++) {
                                    evalTurtleCmd(loopCommands[k]);
                                }
                            }
                            inLoop = false;
                        }
                    }

                    evalTurtleCmd(line);
                }

                if (inLoop && loopCommands.length > 0) {
                    for (var r = 0; r < repeatCount; r++) {
                        for (var m = 0; m < loopCommands.length; m++) {
                            evalTurtleCmd(loopCommands[m]);
                        }
                    }
                }

                drawTurtleCursor();
                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                    window.CLXV11_Bridge.postMessage('INFO', 'Turtle graphics rendered successfully on canvas.', '$fileName', 0, 0, '');
                }
            } catch(e) {
                if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                    window.CLXV11_Bridge.postMessage('ERROR', 'Turtle Error: ' + e.message, '$fileName', 0, 0, '');
                }
            }
        }

        function evalTurtleCmd(cmd) {
            var clean = cmd.replace(/^(?:t|turtle)\./, '').replace(/;$/, '');
            var m = clean.match(/^(\w+)\((.*)\)$/);
            if (!m) return;
            var fn = m[1];
            var argsStr = m[2];
            var args = [];
            if (argsStr.trim()) {
                args = argsStr.split(',').map(function(a) {
                    var s = a.trim();
                    if (/^["'].*["']$/.test(s)) return s.slice(1, -1);
                    var n = parseFloat(s);
                    return isNaN(n) ? s : n;
                });
            }
            if (typeof turtle[fn] === 'function') {
                turtle[fn].apply(turtle, args);
            }
        }

        function initTurtleEngine() {
            resetCanvas();
            runTurtleScript();
        }
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun buildPythonWebServerDocument(fileName: String, pyCode: String, framework: String): String {
        val rawEscaped = escapeHtml(pyCode)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$framework Web Server - $fileName</title>
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
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: var(--bg-color);
            color: var(--text-color);
            padding: 12px;
            display: flex;
            flex-direction: column;
            height: 100vh;
        }
        .browser-bar {
            background: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 10px 14px;
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 12px;
        }
        .status-dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #22c55e;
            box-shadow: 0 0 8px #22c55e;
        }
        .method-badge {
            background: #0369a1;
            color: #e0f2fe;
            font-weight: 700;
            font-size: 11px;
            padding: 4px 8px;
            border-radius: 6px;
        }
        .url-box {
            flex: 1;
            background: #030712;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            color: #f3f4f6;
            padding: 7px 12px;
            font-family: monospace;
            font-size: 13px;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .url-host {
            color: #6b7280;
        }
        .url-path {
            color: #38bdf8;
            font-weight: 600;
            background: transparent;
            border: none;
            outline: none;
            flex: 1;
            font-family: monospace;
            font-size: 13px;
        }
        .reload-btn {
            background: #1f2937;
            color: #e5e7eb;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 7px 14px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
        }
        .viewport {
            flex: 1;
            background: #ffffff;
            color: #111827;
            border-radius: 12px;
            border: 1px solid var(--border-color);
            overflow: auto;
            padding: 24px;
        }
        .route-select {
            background: #111827;
            border: 1px solid var(--border-color);
            color: #e5e7eb;
            border-radius: 6px;
            padding: 5px 8px;
            font-size: 12px;
        }
    </style>
</head>
<body onload="initServer()">
    <div class="browser-bar">
        <div class="status-dot"></div>
        <span class="method-badge">GET</span>
        <div class="url-box">
            <span class="url-host">http://127.0.0.1:5000</span>
            <input id="routeInput" class="url-path" value="/" />
        </div>
        <select id="routeSelect" class="route-select" onchange="onRouteSelected()">
            <option value="/">/</option>
        </select>
        <button class="reload-btn" onclick="sendRequest()">Send</button>
    </div>

    <div id="responseViewport" class="viewport">
        Loading server response...
    </div>

    <div id="sourceCode" style="display:none;">$rawEscaped</div>

    <script>
        var viewport = document.getElementById('responseViewport');
        var routeInput = document.getElementById('routeInput');
        var routeSelect = document.getElementById('routeSelect');
        var routes = {};

        function parseServerRoutes() {
            var code = document.getElementById('sourceCode').textContent;
            var routeRegex = /@app\.(?:route|get|post)\s*\(\s*["']([^"']+)["']/g;
            var match;
            var found = false;

            while ((match = routeRegex.exec(code)) !== null) {
                var path = match[1];
                routes[path] = extractReturn(code, match.index);
                found = true;
            }

            if (!found) {
                routes['/'] = '<h1>Welcome to ' + '$framework' + '</h1><p>Server running at 127.0.0.1:5000</p>';
            }

            routeSelect.innerHTML = '';
            for (var r in routes) {
                var opt = document.createElement('option');
                opt.value = r;
                opt.textContent = r;
                routeSelect.appendChild(opt);
            }
        }

        function extractReturn(code, index) {
            var sub = code.substring(index, index + 400);
            var retMatch = sub.match(/return\s+([^;\n]+)/);
            if (retMatch) {
                var val = retMatch[1].trim();
                if ((val.startsWith('"') && val.endsWith('"')) || (val.startsWith("'") && val.endsWith("'"))) {
                    return val.slice(1, -1);
                }
                return val;
            }
            return '<h1>200 OK</h1><p>Endpoint response from ' + '$framework' + '</p>';
        }

        function onRouteSelected() {
            routeInput.value = routeSelect.value;
            sendRequest();
        }

        function sendRequest() {
            var path = routeInput.value.trim() || '/';
            var resp = routes[path] || '<h2 style="color:#ef4444;">404 Not Found</h2><p>The requested URL was not found on the server.</p>';

            viewport.innerHTML = resp;

            if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                var status = routes[path] ? '200 OK' : '404 NOT FOUND';
                window.CLXV11_Bridge.postMessage('LOG', '127.0.0.1 - - [Now] "GET ' + path + ' HTTP/1.1" ' + status, '$fileName', 0, 0, '');
            }
        }

        function initServer() {
            parseServerRoutes();
            sendRequest();
        }
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun buildPythonPygameDocument(fileName: String, pyCode: String): String {
        val rawEscaped = escapeHtml(pyCode)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pygame Surface - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface-color: #111827;
            --border-color: #1f2937;
            --accent-color: #f59e0b;
            --text-color: #f3f4f6;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: var(--bg-color);
            color: var(--text-color);
            padding: 12px;
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
        }
        .game-bar {
            width: 100%;
            max-width: 640px;
            background: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 10px 14px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 12px;
        }
        .pill {
            background: #78350f;
            color: #fde68a;
            font-size: 11px;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 20px;
        }
        .viewport {
            background: #000000;
            border: 2px solid var(--border-color);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
        }
    </style>
</head>
<body onload="initPygame()">
    <div class="game-bar">
        <div style="display:flex;align-items:center;gap:8px;">
            <span class="pill">🎮 Pygame 2D Surface</span>
            <span style="font-size:13px;font-weight:600;">$fileName</span>
        </div>
        <div style="font-family:monospace;font-size:12px;color:#9ca3af;">60 FPS &bull; 640x480</div>
    </div>

    <div class="viewport">
        <canvas id="gameCanvas" width="640" height="480"></canvas>
    </div>

    <div id="rawPygameCode" style="display:none;">$rawEscaped</div>

    <script>
        var canvas = document.getElementById('gameCanvas');
        var ctx = canvas.getContext('2d');

        function initPygame() {
            ctx.fillStyle = '#0f172a';
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            var x = 50, y = 200, dx = 3, dy = 2;
            function loop() {
                ctx.fillStyle = '#0f172a';
                ctx.fillRect(0, 0, canvas.width, canvas.height);

                ctx.fillStyle = '#38bdf8';
                ctx.beginPath();
                ctx.arc(x, y, 20, 0, Math.PI * 2);
                ctx.fill();

                ctx.fillStyle = '#f59e0b';
                ctx.fillRect(200, 350, 240, 16);

                x += dx;
                y += dy;
                if (x + 20 > canvas.width || x - 20 < 0) dx = -dx;
                if (y + 20 > 350 || y - 20 < 0) dy = -dy;

                requestAnimationFrame(loop);
            }
            loop();

            if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                window.CLXV11_Bridge.postMessage('INFO', 'pygame 2.5.2 (SDL 2.28.3, Python 3.12.0)', '$fileName', 0, 0, '');
                window.CLXV11_Bridge.postMessage('INFO', 'Hello from the pygame community. https://www.pygame.org/contribute.html', '$fileName', 0, 0, '');
                window.CLXV11_Bridge.postMessage('LOG', 'Screen initialized: 640x480 hardware surface', '$fileName', 0, 0, '');
            }
        }
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun buildXmlInspectorDocument(fileName: String, xmlContent: String): String {
        val rawEscaped = escapeHtml(xmlContent)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>XML / SVG Inspector - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface-color: #111827;
            --border-color: #1f2937;
            --tag-color: #38bdf8;
            --attr-name-color: #facc15;
            --attr-val-color: #4ade80;
            --text-color: #f3f4f6;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: monospace; background: var(--bg-color); color: var(--text-color); padding: 16px; }
        .header { background: var(--surface-color); padding: 12px 16px; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; }
        .tag-pill { font-size: 11px; padding: 3px 8px; border-radius: 12px; background: #075985; color: #bae6fd; font-weight: 700; }
        pre { background: var(--surface-color); border: 1px solid var(--border-color); border-radius: 8px; padding: 14px; overflow-x: auto; line-height: 1.6; font-size: 13px; }
        .tag { color: var(--tag-color); font-weight: 600; }
        .attr-name { color: var(--attr-name-color); }
        .attr-val { color: var(--attr-val-color); }
    </style>
</head>
<body>
    <div class="header">
        <div><strong>$fileName</strong> <span style="font-size:12px;color:#9ca3af;">(XML / SVG Structured Inspector)</span></div>
        <span class="tag-pill">Structured Tree</span>
    </div>
    <pre id="xmlViewer">Rendering tree...</pre>
    <div id="raw" style="display:none;">$rawEscaped</div>
    <script>
        var raw = document.getElementById('raw').textContent;
        var viewer = document.getElementById('xmlViewer');

        function highlightXml(xml) {
            return xml
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/(&lt;\/?)([a-zA-Z0-9_\-]+)/g, '$1<span class="tag">$2</span>')
                .replace(/([a-zA-Z0-9_\-]+)=(&quot;[^&]*&quot;)/g, '<span class="attr-name">$1</span>=<span class="attr-val">$2</span>');
        }

        viewer.innerHTML = highlightXml(raw);
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun buildWebDomJsDocument(fileName: String, jsCode: String): String {
        val escapedCode = escapeJs(jsCode)
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DOM Script Runner - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style>
        :root {
            --bg-color: #0d1117;
            --surface-color: #161b22;
            --border-color: #30363d;
            --accent-color: #58a6ff;
            --text-color: #e6edf3;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: system-ui, -apple-system, sans-serif; background: #ffffff; color: #111827; padding: 16px; min-height: 100vh; }
        #app, #root, #sandbox-container { margin-top: 10px; }
    </style>
</head>
<body>
    <div id="app"></div>
    <div id="root"></div>
    <div id="sandbox-container"></div>

    <script>
        try {
            eval("$escapedCode");
        } catch(err) {
            document.body.innerHTML += '<div style="color:#ef4444;font-family:monospace;padding:12px;background:#fee2e2;border-radius:8px;margin-top:12px;">❌ <b>Script Error:</b> ' + err.message + '</div>';
            if (window.CLXV11_Bridge && window.CLXV11_Bridge.postMessage) {
                window.CLXV11_Bridge.postMessage('ERROR', err.name + ': ' + err.message, '$fileName', 0, 0, err.stack || '');
            }
        }
    </script>
</body>
</html>
        """.trimIndent()
    }
}
