package com.example.editor.runner

import com.example.editor.tabs.EditorTab
import java.io.File

/**
 * Builds ready-to-run HTML documents with injected console bridges, error boundaries,
 * and workspace resource resolution for HTML, CSS, and JavaScript files.
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
            ext in listOf("css") || languageId == "css" -> WebFileType.CSS
            ext in listOf("js", "mjs", "cjs") || languageId == "javascript" -> WebFileType.JAVASCRIPT
            else -> WebFileType.UNKNOWN
        }
    }

    fun isWebRunnable(fileName: String, languageId: String? = null): Boolean {
        val type = detectFileType(fileName, languageId)
        return type != WebFileType.UNKNOWN
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
            WebFileType.JAVASCRIPT -> buildJsRunnerDocument(title, rawContent)
            WebFileType.CSS -> buildCssShowcaseDocument(title, rawContent)
            WebFileType.UNKNOWN -> buildHtmlDocument(rawContent, openTabs)
        }

        return WebRunnerPayload(
            title = title,
            fileType = fileType,
            htmlToLoad = html,
            baseUrl = baseUrl,
            originalFilePath = fileDir?.let { File(it, title).absolutePath }
        )
    }

    private fun buildHtmlDocument(htmlContent: String, openTabs: List<EditorTab>): String {
        var processed = htmlContent

        // Inline any linked stylesheet or script if currently modified in open tabs
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

        // Inject the console bridge
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
            --bg-color: #0f1117;
            --surface-color: #161b22;
            --border-color: #30363d;
            --text-color: #e6edf3;
            --text-secondary: #8b949e;
            --accent-color: #58a6ff;
            --accent-hover: #79c0ff;
            --success-color: #3fb950;
        }
        @media (prefers-color-scheme: light) {
            :root {
                --bg-color: #f6f8fa;
                --surface-color: #ffffff;
                --border-color: #d0d7de;
                --text-color: #1f2328;
                --text-secondary: #656d76;
                --accent-color: #0969da;
                --accent-hover: #218bff;
                --success-color: #1a7f37;
            }
        }
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background-color: var(--bg-color);
            color: var(--text-color);
            padding: 16px;
            min-height: 100vh;
        }
        .header {
            background-color: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 12px 16px;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .header-title {
            font-size: 15px;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .badge {
            background-color: var(--accent-color);
            color: #fff;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
        }
        .runner-container {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }
        .card {
            background-color: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 16px;
        }
        .card-title {
            font-size: 13px;
            font-weight: 600;
            color: var(--text-secondary);
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 12px;
        }
        #app {
            min-height: 80px;
            word-break: break-word;
        }
        #canvas-wrapper {
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: rgba(0,0,0,0.05);
            border-radius: 6px;
            padding: 8px;
            overflow: auto;
        }
        canvas {
            background-color: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: 4px;
            max-width: 100%;
            height: auto;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .interactive-buttons {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
            margin-top: 12px;
        }
        button.demo-btn {
            background-color: var(--accent-color);
            color: #ffffff;
            border: none;
            padding: 8px 14px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
            transition: background 0.2s;
        }
        button.demo-btn:active {
            opacity: 0.8;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-title">
            <span>⚡ $fileName</span>
            <span class="badge">Running</span>
        </div>
        <div style="font-size: 12px; color: var(--text-secondary);">
            DOM & Canvas Ready
        </div>
    </div>

    <div class="runner-container">
        <!-- Interactive App Container -->
        <div class="card">
            <div class="card-title">DOM Output (#app)</div>
            <div id="app">
                <p style="color: var(--text-secondary); font-size: 13px; font-style: italic;">
                    (Script has access to document, window, #app, and canvas)
                </p>
            </div>
        </div>

        <!-- Canvas Playground -->
        <div class="card">
            <div class="card-title">Canvas (#canvas)</div>
            <div id="canvas-wrapper">
                <canvas id="canvas" width="480" height="260"></canvas>
            </div>
        </div>
    </div>

    <!-- User Script Execution with Safe Boundary -->
    <script>
    (function() {
        try {
            console.info("⚡ Executing $fileName...");
            $jsCode
            console.info("✔ $fileName executed successfully.");
        } catch (error) {
            console.error(error);
        }
    })();
    </script>
</body>
</html>
""".trimIndent()
    }

    private fun buildCssShowcaseDocument(fileName: String, cssCode: String): String {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CSS Showcase - $fileName</title>
    $CONSOLE_BRIDGE_SCRIPT
    <style id="__clxv11_user_stylesheet__">
$cssCode
    </style>
    <style>
        /* Host wrapper styling that does not interfere with user rules */
        .__host_status_bar {
            background-color: #21262d;
            color: #58a6ff;
            font-family: -apple-system, BlinkMacSystemFont, sans-serif;
            font-size: 12px;
            font-weight: 600;
            padding: 8px 16px;
            border-bottom: 1px solid #30363d;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .__host_container {
            max-width: 800px;
            margin: 0 auto;
            padding: 24px 16px;
        }
    </style>
</head>
<body>
    <div class="__host_status_bar">
        <span>🎨 CSS Live Showcase: $fileName</span>
        <span style="color: #3fb950;">Stylesheet Active</span>
    </div>

    <div class="__host_container">
        <!-- Typography Showcase -->
        <header>
            <h1>Heading 1 (Main Title)</h1>
            <p class="subtitle">This is a subtitle or lead paragraph demonstrating your font and color styling.</p>
            <h2>Heading 2 (Section Title)</h2>
            <p>
                Lorem ipsum dolor sit amet, consectetur <strong>adipiscing elit</strong>. Vivamus 
                lacinia odio vitae vestibulum vestibulum. <a href="#test">Sample Hyperlink</a>.
            </p>
            <h3>Heading 3 (Sub-section)</h3>
            <blockquote>
                "Good design is as little design as possible." — Dieter Rams
            </blockquote>
        </header>

        <hr style="margin: 24px 0;">

        <!-- Buttons Showcase -->
        <section style="margin-bottom: 28px;">
            <h3>Buttons & Actions</h3>
            <div style="display: flex; gap: 12px; flex-wrap: wrap; margin-top: 12px;">
                <button class="btn btn-primary primary">Primary Button</button>
                <button class="btn btn-secondary secondary">Secondary Button</button>
                <button class="btn btn-outline outline">Outline Button</button>
                <button class="btn btn-danger danger">Danger Button</button>
                <button class="btn" disabled>Disabled Button</button>
            </div>
        </section>

        <!-- Form Elements Showcase -->
        <section style="margin-bottom: 28px;">
            <h3>Form Elements</h3>
            <form style="display: flex; flex-direction: column; gap: 12px; max-width: 400px; margin-top: 12px;" onsubmit="return false;">
                <label>
                    Text Input:
                    <input type="text" placeholder="Enter your text..." value="Sample text input" style="display: block; width: 100%; margin-top: 4px;">
                </label>
                <label>
                    Select Option:
                    <select style="display: block; width: 100%; margin-top: 4px;">
                        <option>Option 1</option>
                        <option>Option 2</option>
                        <option>Option 3</option>
                    </select>
                </label>
                <label style="display: flex; align-items: center; gap: 8px;">
                    <input type="checkbox" checked>
                    <span>Check me</span>
                </label>
            </form>
        </section>

        <!-- Cards / Container Showcase -->
        <section style="margin-bottom: 28px;">
            <h3>Card & Containers</h3>
            <div class="card" style="margin-top: 12px; padding: 16px; border: 1px solid #ccc; border-radius: 8px;">
                <h4>Card Component Title</h4>
                <p>Card body content illustrating your padding, borders, shadows, and background colors.</p>
                <button style="margin-top: 8px;">Action</button>
            </div>
        </section>
    </div>
</body>
</html>
""".trimIndent()
    }
}
