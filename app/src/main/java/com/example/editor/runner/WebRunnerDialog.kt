package com.example.editor.runner

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.ConsoleMessage
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Native JS Dialog state holder
private sealed class JsDialogState {
    data class Alert(val message: String, val result: JsResult) : JsDialogState()
    data class Confirm(val message: String, val result: JsResult) : JsDialogState()
    data class Prompt(val message: String, val defaultValue: String, val result: JsPromptResult) : JsDialogState()
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebRunnerDialog(
    state: WebRunnerUiState,
    onDismiss: () -> Unit,
    onActiveTabChanged: (WebRunnerTab) -> Unit,
    onViewportModeChanged: (ViewportMode) -> Unit,
    onLogFilterChanged: (LogLevel?) -> Unit,
    onLogSearchChanged: (String) -> Unit,
    onClearLogs: () -> Unit,
    onAddLogMessage: (WebConsoleMessage) -> Unit,
    onPageTitleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var reloadTrigger by remember { mutableIntStateOf(0) }
    var activeJsDialog by remember { mutableStateOf<JsDialogState?>(null) }
    var jsEvalCode by remember { mutableStateOf("") }
    var promptInputText by remember { mutableStateOf("") }
    var loadProgress by remember { mutableIntStateOf(0) }
    var isCurrentlyLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .testTag("web_runner_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Application Bar for Runner
                WebRunnerTopBar(
                    payload = state.payload,
                    pageTitle = state.pageTitle,
                    activeTab = state.activeTab,
                    viewportMode = state.viewportMode,
                    errorCount = state.errorCount,
                    warningCount = state.warningCount,
                    onActiveTabChanged = onActiveTabChanged,
                    onViewportModeChanged = onViewportModeChanged,
                    onReload = {
                        reloadTrigger++
                        webViewInstance?.reload()
                    },
                    onOpenInBrowser = {
                        state.payload?.let { payload ->
                            openInExternalBrowser(context, payload)
                        }
                    },
                    onDismiss = onDismiss
                )

                // Loading progress indicator
                if (isCurrentlyLoading && loadProgress < 100) {
                    LinearProgressIndicator(
                        progress = { loadProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }

                // Main Content View (Preview, Console, or Split)
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (state.activeTab) {
                        WebRunnerTab.PREVIEW -> {
                            WebPreviewContainer(
                                viewportMode = state.viewportMode,
                                state = state,
                                reloadTrigger = reloadTrigger,
                                onWebViewCreated = { webViewInstance = it },
                                onProgress = { progress, loading ->
                                    loadProgress = progress
                                    isCurrentlyLoading = loading
                                },
                                onJsDialog = { activeJsDialog = it },
                                onAddLogMessage = onAddLogMessage,
                                onPageTitleChanged = onPageTitleChanged
                            )
                        }

                        WebRunnerTab.CONSOLE -> {
                            WebConsoleView(
                                logs = state.filteredLogs,
                                activeFilter = state.logFilter,
                                searchQuery = state.logSearchQuery,
                                evalCode = jsEvalCode,
                                onEvalCodeChange = { jsEvalCode = it },
                                onExecuteEval = { code ->
                                    if (code.isNotBlank() && webViewInstance != null) {
                                        onAddLogMessage(
                                            WebConsoleMessage(
                                                level = LogLevel.DEBUG,
                                                message = "> $code"
                                            )
                                        )
                                        webViewInstance?.evaluateJavascript(code) { result ->
                                            val cleanResult = if (result == "null" || result == null) "undefined" else result
                                            onAddLogMessage(
                                                WebConsoleMessage(
                                                    level = LogLevel.RESULT,
                                                    message = "< $cleanResult"
                                                )
                                            )
                                        }
                                        jsEvalCode = ""
                                    }
                                },
                                onFilterChanged = onLogFilterChanged,
                                onSearchChanged = onLogSearchChanged,
                                onClearLogs = onClearLogs
                            )
                        }

                        WebRunnerTab.SPLIT -> {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .fillMaxHeight()
                                ) {
                                    WebPreviewContainer(
                                        viewportMode = ViewportMode.RESPONSIVE,
                                        state = state,
                                        reloadTrigger = reloadTrigger,
                                        onWebViewCreated = { webViewInstance = it },
                                        onProgress = { progress, loading ->
                                            loadProgress = progress
                                            isCurrentlyLoading = loading
                                        },
                                        onJsDialog = { activeJsDialog = it },
                                        onAddLogMessage = onAddLogMessage,
                                        onPageTitleChanged = onPageTitleChanged
                                    )
                                }

                                VerticalDivider()

                                Box(
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .fillMaxHeight()
                                ) {
                                    WebConsoleView(
                                        logs = state.filteredLogs,
                                        activeFilter = state.logFilter,
                                        searchQuery = state.logSearchQuery,
                                        evalCode = jsEvalCode,
                                        onEvalCodeChange = { jsEvalCode = it },
                                        onExecuteEval = { code ->
                                            if (code.isNotBlank() && webViewInstance != null) {
                                                onAddLogMessage(
                                                    WebConsoleMessage(
                                                        level = LogLevel.DEBUG,
                                                        message = "> $code"
                                                    )
                                                )
                                                webViewInstance?.evaluateJavascript(code) { result ->
                                                    val cleanResult = if (result == "null" || result == null) "undefined" else result
                                                    onAddLogMessage(
                                                        WebConsoleMessage(
                                                            level = LogLevel.RESULT,
                                                            message = "< $cleanResult"
                                                        )
                                                    )
                                                }
                                                jsEvalCode = ""
                                            }
                                        },
                                        onFilterChanged = onLogFilterChanged,
                                        onSearchChanged = onLogSearchChanged,
                                        onClearLogs = onClearLogs
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // JavaScript Dialog Handling (Alert, Confirm, Prompt)
            when (val dialog = activeJsDialog) {
                is JsDialogState.Alert -> {
                    AlertDialog(
                        onDismissRequest = {
                            dialog.result.confirm()
                            activeJsDialog = null
                        },
                        title = { Text("JavaScript Alert") },
                        text = { Text(dialog.message) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    dialog.result.confirm()
                                    activeJsDialog = null
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }

                is JsDialogState.Confirm -> {
                    AlertDialog(
                        onDismissRequest = {
                            dialog.result.cancel()
                            activeJsDialog = null
                        },
                        title = { Text("JavaScript Confirm") },
                        text = { Text(dialog.message) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    dialog.result.confirm()
                                    activeJsDialog = null
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    dialog.result.cancel()
                                    activeJsDialog = null
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                is JsDialogState.Prompt -> {
                    AlertDialog(
                        onDismissRequest = {
                            dialog.result.cancel()
                            activeJsDialog = null
                        },
                        title = { Text("JavaScript Prompt") },
                        text = {
                            Column {
                                Text(dialog.message)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = promptInputText,
                                    onValueChange = { promptInputText = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    dialog.result.confirm(promptInputText)
                                    promptInputText = ""
                                    activeJsDialog = null
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    dialog.result.cancel()
                                    promptInputText = ""
                                    activeJsDialog = null
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                null -> {}
            }
        }
    }
}

@Composable
private fun WebRunnerTopBar(
    payload: WebRunnerPayload?,
    pageTitle: String,
    activeTab: WebRunnerTab,
    viewportMode: ViewportMode,
    errorCount: Int,
    warningCount: Int,
    onActiveTabChanged: (WebRunnerTab) -> Unit,
    onViewportModeChanged: (ViewportMode) -> Unit,
    onReload: () -> Unit,
    onOpenInBrowser: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Title and live badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3FB950))
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = payload?.title ?: "Web Runner",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val subText = if (pageTitle.isNotBlank() && pageTitle != payload?.title) {
                            "$pageTitle • ${payload?.fileType?.displayName ?: "Web"}"
                        } else {
                            payload?.fileType?.displayName ?: "HTML/CSS/JS"
                        }
                        Text(
                            text = subText,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Actions: Reload, Open in Browser, Close
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onReload) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload Page")
                    }
                    IconButton(onClick = onOpenInBrowser) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in External Browser")
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Runner")
                    }
                }
            }

            HorizontalDivider()

            // Mode and Viewport selector bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tab Selection
                FilterChip(
                    selected = activeTab == WebRunnerTab.PREVIEW,
                    onClick = { onActiveTabChanged(WebRunnerTab.PREVIEW) },
                    leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Preview", fontSize = 12.sp) }
                )

                FilterChip(
                    selected = activeTab == WebRunnerTab.CONSOLE,
                    onClick = { onActiveTabChanged(WebRunnerTab.CONSOLE) },
                    leadingIcon = {
                        if (errorCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text("$errorCount", fontSize = 9.sp)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        } else {
                            Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    },
                    label = {
                        val labelText = if (errorCount > 0) "Console ($errorCount)" else "Console"
                        Text(labelText, fontSize = 12.sp)
                    }
                )

                FilterChip(
                    selected = activeTab == WebRunnerTab.SPLIT,
                    onClick = { onActiveTabChanged(WebRunnerTab.SPLIT) },
                    leadingIcon = { Icon(Icons.Default.VerticalSplit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Split View", fontSize = 12.sp) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Viewport selection chips
                ViewportMode.entries.forEach { mode ->
                    FilterChip(
                        selected = viewportMode == mode,
                        onClick = { onViewportModeChanged(mode) },
                        label = { Text(mode.displayName, fontSize = 11.sp) }
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebPreviewContainer(
    viewportMode: ViewportMode,
    state: WebRunnerUiState,
    reloadTrigger: Int,
    onWebViewCreated: (WebView) -> Unit,
    onProgress: (Int, Boolean) -> Unit,
    onJsDialog: (JsDialogState) -> Unit,
    onAddLogMessage: (WebConsoleMessage) -> Unit,
    onPageTitleChanged: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val payload = state.payload

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceDim),
        contentAlignment = Alignment.Center
    ) {
        val widthModifier = when (viewportMode) {
            ViewportMode.RESPONSIVE -> Modifier.fillMaxSize()
            ViewportMode.MOBILE -> Modifier
                .width(375.dp)
                .fillMaxHeight()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            ViewportMode.TABLET -> Modifier
                .width(768.dp)
                .fillMaxHeight()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
        }

        Surface(
            modifier = widthModifier,
            color = Color.White
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }

                        val bridge = WebConsoleBridge(
                            scope = coroutineScope,
                            onMessage = { onAddLogMessage(it) },
                            onTitleChanged = { onPageTitleChanged(it) }
                        )
                        addJavascriptInterface(bridge, "CLXV11_Bridge")

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                onProgress(newProgress, newProgress < 100)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                title?.let { onPageTitleChanged(it) }
                            }

                            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                consoleMessage?.let { msg ->
                                    val level = when (msg.messageLevel()) {
                                        ConsoleMessage.MessageLevel.ERROR -> LogLevel.ERROR
                                        ConsoleMessage.MessageLevel.WARNING -> LogLevel.WARN
                                        ConsoleMessage.MessageLevel.LOG -> LogLevel.LOG
                                        ConsoleMessage.MessageLevel.TIP -> LogLevel.INFO
                                        ConsoleMessage.MessageLevel.DEBUG -> LogLevel.DEBUG
                                        else -> LogLevel.LOG
                                    }
                                    onAddLogMessage(
                                        WebConsoleMessage(
                                            level = level,
                                            message = msg.message() ?: "",
                                            source = msg.sourceId()?.substringAfterLast('/'),
                                            lineNumber = msg.lineNumber()
                                        )
                                    )
                                }
                                return true
                            }

                            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                                if (message != null && result != null) {
                                    onJsDialog(JsDialogState.Alert(message, result))
                                    return true
                                }
                                return false
                            }

                            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                                if (message != null && result != null) {
                                    onJsDialog(JsDialogState.Confirm(message, result))
                                    return true
                                }
                                return false
                            }

                            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                                if (message != null && result != null) {
                                    onJsDialog(JsDialogState.Prompt(message, defaultValue ?: "", result))
                                    return true
                                }
                                return false
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                onProgress(10, true)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                onProgress(100, false)
                            }

                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                onAddLogMessage(
                                    WebConsoleMessage(
                                        level = LogLevel.ERROR,
                                        message = "Resource load error: ${error?.description ?: "Unknown error"} on ${request?.url}"
                                    )
                                )
                            }
                        }

                        onWebViewCreated(this)

                        if (payload != null) {
                            loadDataWithBaseURL(
                                payload.baseUrl,
                                payload.htmlToLoad,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    }
                },
                update = { webView ->
                    if (reloadTrigger > 0 || webView.url == null) {
                        if (payload != null) {
                            webView.loadDataWithBaseURL(
                                payload.baseUrl,
                                payload.htmlToLoad,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun WebConsoleView(
    logs: List<WebConsoleMessage>,
    activeFilter: LogLevel?,
    searchQuery: String,
    evalCode: String,
    onEvalCodeChange: (String) -> Unit,
    onExecuteEval: (String) -> Unit,
    onFilterChanged: (LogLevel?) -> Unit,
    onSearchChanged: (String) -> Unit,
    onClearLogs: () -> Unit
) {
    val listState = rememberLazyListState()
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()) }

    // Auto-scroll to latest log on new additions
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Filter & search header
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                // Search bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChanged,
                        placeholder = { Text("Filter console logs...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChanged("") }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onClearLogs,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Console", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Severity Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = activeFilter == null,
                        onClick = { onFilterChanged(null) },
                        label = { Text("All (${logs.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = activeFilter == LogLevel.LOG,
                        onClick = { onFilterChanged(if (activeFilter == LogLevel.LOG) null else LogLevel.LOG) },
                        label = { Text("Log", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = activeFilter == LogLevel.INFO,
                        onClick = { onFilterChanged(if (activeFilter == LogLevel.INFO) null else LogLevel.INFO) },
                        label = { Text("Info", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = activeFilter == LogLevel.WARN,
                        onClick = { onFilterChanged(if (activeFilter == LogLevel.WARN) null else LogLevel.WARN) },
                        label = { Text("Warn", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = activeFilter == LogLevel.ERROR,
                        onClick = { onFilterChanged(if (activeFilter == LogLevel.ERROR) null else LogLevel.ERROR) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        label = { Text("Errors", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Logs Output List
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Console is empty",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Messages from console.log and errors will appear here.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items(logs, key = { it.id }) { msg ->
                        ConsoleLogRow(msg = msg, dateFormat = dateFormat)
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        // Interactive JavaScript REPL Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ">",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )

                OutlinedTextField(
                    value = evalCode,
                    onValueChange = onEvalCodeChange,
                    placeholder = { Text("Run JavaScript (e.g. 2+2, document.title)...", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onExecuteEval(evalCode) }),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { onExecuteEval(evalCode) },
                    enabled = evalCode.isNotBlank(),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Execute JS",
                        tint = if (evalCode.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsoleLogRow(
    msg: WebConsoleMessage,
    dateFormat: SimpleDateFormat
) {
    var isExpanded by remember { mutableStateOf(false) }

    val (icon, tint, bgColor) = when (msg.level) {
        LogLevel.ERROR -> Triple(
            Icons.Default.Error,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        )
        LogLevel.WARN -> Triple(
            Icons.Default.Warning,
            Color(0xFFD29922),
            Color(0xFFD29922).copy(alpha = 0.1f)
        )
        LogLevel.INFO -> Triple(
            Icons.Default.Info,
            Color(0xFF58A6FF),
            Color.Transparent
        )
        LogLevel.DEBUG -> Triple(
            Icons.Default.PlayArrow,
            MaterialTheme.colorScheme.primary,
            Color.Transparent
        )
        LogLevel.RESULT -> Triple(
            Icons.AutoMirrored.Filled.Send,
            Color(0xFF3FB950),
            Color(0xFF3FB950).copy(alpha = 0.08f)
        )
        LogLevel.LOG -> Triple(
            Icons.Default.Terminal,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Color.Transparent
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(15.dp)
                .padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source and line badge
                if (msg.source != null || msg.lineNumber != null) {
                    val location = buildString {
                        if (msg.source != null) append(msg.source)
                        if (msg.lineNumber != null) append(":${msg.lineNumber}")
                    }
                    Text(
                        text = location,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Timestamp
                Text(
                    text = dateFormat.format(Date(msg.timestamp)),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Main Message Content
            Text(
                text = msg.message,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = if (msg.level == LogLevel.ERROR) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                maxLines = if (isExpanded) Int.MAX_VALUE else 6,
                overflow = TextOverflow.Ellipsis
            )

            // Stack trace if expanded
            if (isExpanded && msg.stack != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg.stack,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Helper to export the generated HTML to a temporary cache file and launch the external web browser.
 */
private fun openInExternalBrowser(context: Context, payload: WebRunnerPayload) {
    try {
        val tempDir = File(context.cacheDir, "web_preview").apply { mkdirs() }
        val tempFile = File(tempDir, "preview.html")
        FileOutputStream(tempFile).use { it.write(payload.htmlToLoad.toByteArray(Charsets.UTF_8)) }

        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/html")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Open in Browser"))
    } catch (_: Exception) {
        // Fallback: try sharing HTML text directly if FileProvider is unavailable
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, payload.htmlToLoad)
                type = "text/html"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Web Preview"))
        } catch (_: Exception) {}
    }
}
