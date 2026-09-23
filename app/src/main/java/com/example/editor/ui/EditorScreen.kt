package com.example.editor.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.engine.EditorState
import com.example.editor.io.RenameTarget
import com.example.editor.runner.WebRunnerContentBuilder
import com.example.editor.runner.WebRunnerDialog
import com.example.editor.settings.ThemeMode
import com.example.editor.shortcuts.EditorShortcutActions
import com.example.editor.shortcuts.editorKeyboardShortcuts
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.LanguageIcon
import com.example.editor.syntax.SyntaxTheme
import com.example.editor.ui.components.AutocompletePopup
import com.example.editor.ui.components.CodeEditorView
import com.example.editor.ui.components.EditorBottomBar
import com.example.editor.ui.components.EditorStatusBar
import com.example.editor.ui.components.EditorTabBar
import com.example.editor.ui.components.FileExplorerDrawer
import com.example.editor.ui.components.SearchReplaceBar
import com.example.editor.ui.dialogs.ConfirmDeleteDialog
import com.example.editor.ui.dialogs.ConfirmUnsavedDialog
import com.example.editor.ui.dialogs.FilePropertiesDialog
import com.example.editor.ui.dialogs.GoToLineDialog
import com.example.editor.ui.dialogs.KeyboardShortcutsDialog
import com.example.editor.ui.dialogs.LanguageSelectorDialog
import com.example.editor.ui.dialogs.NewFileDialog
import com.example.editor.ui.dialogs.RenameDialog
import com.example.editor.ui.dialogs.SafTreeBrowserDialog
import com.example.editor.ui.dialogs.SettingsDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    // SAF Launchers
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.openSafUri(it) }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let { viewModel.createSafDocument(it) }
    }

    val openDocumentTreeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            viewModel.openSafTreeBrowser(it)
        }
    }

    // Handle snackbar notifications
    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.dismissInfo()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { err ->
            snackbarHostState.showSnackbar(err, duration = SnackbarDuration.Long)
            viewModel.dismissError()
        }
    }

    // Determine syntax theme
    val isSystemDark = isSystemInDarkTheme()
    val syntaxTheme = when (uiState.settings.themeMode) {
        ThemeMode.BLACK -> SyntaxTheme.PitchBlack
        ThemeMode.DARK -> SyntaxTheme.Dark
        ThemeMode.LIGHT -> SyntaxTheme.Light
        ThemeMode.SYSTEM -> if (isSystemDark) SyntaxTheme.PitchBlack else SyntaxTheme.Light
    }

    val activeTab = uiState.openTabs.getOrNull(uiState.activeTabIndex)
    val activeLanguage = activeTab?.language ?: LanguageDefinition.PLAIN_TEXT

    val shortcutActions = remember(viewModel, activeTab, uiState.activeTabIndex, uiState.openTabs.size) {
        EditorShortcutActions(
            onSave = { viewModel.saveActiveFile() },
            onSaveAs = {
                createDocumentLauncher.launch(activeTab?.file?.name ?: "untitled.txt")
            },
            onUndo = { viewModel.undo() },
            onRedo = { viewModel.redo() },
            onSearch = { viewModel.toggleSearch(true) },
            onGoToLine = { viewModel.setShowGoToLineDialog(true) },
            onCloseTab = {
                if (uiState.openTabs.isNotEmpty()) {
                    viewModel.requestCloseTab(uiState.activeTabIndex)
                }
            },
            onNewFile = { viewModel.setShowNewFileDialog(true) },
            onSelectAll = { viewModel.selectAll() },
            onDuplicateLine = { viewModel.duplicateLineOrSelection() },
            onDeleteLine = { viewModel.deleteCurrentLine() },
            onToggleComment = { viewModel.toggleComment() },
            onIndent = { viewModel.indentSelection() },
            onDedent = { viewModel.dedentSelection() }
        )
    }

    var topMenuExpanded by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FileExplorerDrawer(
                currentDir = uiState.currentDirectory ?: context.filesDir,
                isRoot = uiState.currentDirectory?.absolutePath == context.filesDir.resolve("workspace").absolutePath,
                items = uiState.workspaceItems,
                recentFiles = uiState.recentFiles,
                onItemClick = { item ->
                    viewModel.openWorkspaceItem(item)
                    coroutineScope.launch { drawerState.close() }
                },
                onNavigateUp = { viewModel.navigateWorkspaceUp() },
                onNewFileClick = { viewModel.setShowNewFileDialog(true) },
                onNewFolderClick = { viewModel.setShowNewFileDialog(true) },
                onOpenSafDocument = {
                    openDocumentLauncher.launch(arrayOf("*/*"))
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenSafTree = {
                    openDocumentTreeLauncher.launch(null)
                    coroutineScope.launch { drawerState.close() }
                },
                onOpenSafCreateDocument = {
                    createDocumentLauncher.launch("untitled.txt")
                    coroutineScope.launch { drawerState.close() }
                },
                onRefresh = { viewModel.refreshWorkspace() },
                onRenameItem = { item -> viewModel.setItemToRename(item) },
                onDeleteItem = { item -> viewModel.setItemToDelete(item) },
                onShareItem = { item ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_STREAM, android.net.Uri.fromFile(item.file))
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share ${item.name}"))
                },
                onPropertiesItem = { viewModel.setShowFilePropertiesDialog(true) },
                onRecentFileClick = { recent ->
                    viewModel.openRecentFile(recent)
                    coroutineScope.launch { drawerState.close() }
                },
                onRenameRecent = { recent ->
                    if (recent.isInternal) {
                        val item = uiState.workspaceItems.firstOrNull { it.file.absolutePath == recent.pathOrUri }
                            ?: com.example.editor.io.WorkspaceItem(
                                file = java.io.File(recent.pathOrUri),
                                name = recent.name,
                                isDirectory = false,
                                extension = "",
                                languageName = recent.languageId,
                                sizeBytes = 0L,
                                lastModified = recent.lastOpened
                            )
                        viewModel.setRenameTarget(RenameTarget.Workspace(item))
                    } else {
                        viewModel.setRenameTarget(RenameTarget.SafDocument(android.net.Uri.parse(recent.pathOrUri), recent.name))
                    }
                },
                onRemoveRecentFile = { recent -> viewModel.removeRecentFile(recent) },
                onRunWebFile = { item ->
                    viewModel.runFileWebPreview(item.file)
                    coroutineScope.launch { drawerState.close() }
                },
                onRunRecentWebFile = { recent ->
                    if (recent.isInternal) {
                        viewModel.runFileWebPreview(java.io.File(recent.pathOrUri))
                    } else {
                        viewModel.runSafUriWebPreview(android.net.Uri.parse(recent.pathOrUri), recent.name)
                    }
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize().editorKeyboardShortcuts(shortcutActions),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (activeTab != null) {
                                LanguageIcon(
                                    iconDef = activeLanguage.icon,
                                    size = 20.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Text(
                                text = activeTab?.title ?: "CodeXCroc",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (activeTab != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.setShowLanguageDialog(true) },
                                    leadingIcon = {
                                        LanguageIcon(
                                            iconDef = activeLanguage.icon,
                                            size = 14.dp
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = activeLanguage.name,
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    },
                                    modifier = Modifier.testTag("language_selector_chip")
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
                        }
                    },
                    actions = {
                        // Run Web (HTML / CSS / JS) button
                        val isWebRunnable = activeTab != null && WebRunnerContentBuilder.isWebRunnable(activeTab.file.name, activeTab.language.id)
                        IconButton(
                            onClick = { viewModel.runActiveFileWebPreview() },
                            modifier = Modifier.testTag("run_web_preview_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run Web Preview",
                                tint = if (isWebRunnable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Format code button
                        IconButton(
                            onClick = { viewModel.formatCurrentCode() },
                            modifier = Modifier.testTag("format_code_button")
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Format Code")
                        }

                        // Save file button
                        IconButton(
                            onClick = { viewModel.saveActiveFile() },
                            modifier = Modifier.testTag("save_file_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save File")
                        }

                        // Overflow menu
                        Box {
                            IconButton(
                                onClick = { topMenuExpanded = true },
                                modifier = Modifier.testTag("top_menu_overflow")
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }

                            DropdownMenu(
                                expanded = topMenuExpanded,
                                onDismissRequest = { topMenuExpanded = false },
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Run / Preview Code") },
                                    leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        topMenuExpanded = false
                                        viewModel.runActiveFileWebPreview()
                                    }
                                )
                                if (activeTab != null) {
                                    DropdownMenuItem(
                                        text = { Text("Rename File") },
                                        onClick = {
                                            topMenuExpanded = false
                                            viewModel.requestRenameActiveFile()
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("New External File (SAF)") },
                                    onClick = {
                                        topMenuExpanded = false
                                        createDocumentLauncher.launch("untitled.txt")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("File Properties") },
                                    onClick = {
                                        topMenuExpanded = false
                                        viewModel.setShowFilePropertiesDialog(true)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = {
                                        topMenuExpanded = false
                                        viewModel.setShowSettingsDialog(true)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Keyboard Shortcuts") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Keyboard,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    onClick = {
                                        topMenuExpanded = false
                                        viewModel.setShowShortcutsDialog(true)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Share Code") },
                                    onClick = {
                                        topMenuExpanded = false
                                        val content = uiState.editorValue.text
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, content)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Code"))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Close Current Tab") },
                                    onClick = {
                                        topMenuExpanded = false
                                        viewModel.requestCloseTab(uiState.activeTabIndex)
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .navigationBarsPadding()
                ) {
                    // Line & Column and Auto-Save Status Bar
                    val lineOffsets = remember(uiState.editorValue.text) {
                        EditorState.getLineStartOffsets(uiState.editorValue.text)
                    }
                    val currentLine = remember(uiState.editorValue.selection.start, lineOffsets) {
                        EditorState.getLineNumberForOffset(lineOffsets, uiState.editorValue.selection.start)
                    }
                    val currentColumn = remember(uiState.editorValue.selection.start, lineOffsets) {
                        EditorState.getColumnNumberForOffset(lineOffsets, uiState.editorValue.selection.start)
                    }

                    EditorStatusBar(
                        activeTab = activeTab,
                        line = currentLine,
                        column = currentColumn,
                        isAutoSaving = uiState.isAutoSaving,
                        autoSaveStatusText = uiState.autoSaveStatusText
                    )

                    // Autocomplete bar if suggestions exist
                    if (uiState.autocompleteSuggestions.isNotEmpty()) {
                        AutocompletePopup(
                            suggestions = uiState.autocompleteSuggestions,
                            onSuggestionClick = { item -> viewModel.applyAutocomplete(item) }
                        )
                    }

                    // Mobile Developer Quick Bar
                    EditorBottomBar(
                        canUndo = uiState.canUndo,
                        canRedo = uiState.canRedo,
                        onUndo = { viewModel.undo() },
                        onRedo = { viewModel.redo() },
                        onTab = { viewModel.insertTab() },
                        onIndent = { viewModel.indentSelection() },
                        onOutdent = { viewModel.dedentSelection() },
                        onInsertSymbol = { sym -> viewModel.insertSymbol(sym) },
                        onSearchClick = { viewModel.toggleSearch() },
                        onGoToLineClick = { viewModel.setShowGoToLineDialog(true) }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tab Bar
                EditorTabBar(
                    tabs = uiState.openTabs,
                    activeIndex = uiState.activeTabIndex,
                    onTabSelected = { idx -> viewModel.selectTab(idx) },
                    onTabClose = { idx -> viewModel.requestCloseTab(idx) },
                    onNewTabClick = { viewModel.setShowNewFileDialog(true) }
                )

                // Search & Replace Bar
                if (uiState.isSearchVisible) {
                    SearchReplaceBar(
                        query = uiState.searchQuery,
                        replaceText = uiState.replaceQuery,
                        matchCase = uiState.matchCase,
                        wholeWord = uiState.wholeWord,
                        useRegex = uiState.useRegex,
                        currentMatchIndex = uiState.activeMatchIndex,
                        totalMatches = uiState.searchMatches.size,
                        onQueryChange = { q -> viewModel.setSearchQuery(q) },
                        onReplaceChange = { r -> viewModel.setReplaceQuery(r) },
                        onToggleMatchCase = { viewModel.toggleMatchCase() },
                        onToggleWholeWord = { viewModel.toggleWholeWord() },
                        onToggleRegex = { viewModel.toggleRegex() },
                        onNextMatch = { viewModel.nextMatch() },
                        onPreviousMatch = { viewModel.previousMatch() },
                        onReplaceCurrent = { viewModel.replaceCurrentMatch() },
                        onReplaceAll = { viewModel.replaceAllMatches() },
                        onClose = { viewModel.toggleSearch(false) }
                    )
                }

                // Code Editor View
                CodeEditorView(
                    value = uiState.editorValue,
                    onValueChange = { newVal -> viewModel.onEditorValueChanged(newVal) },
                    language = activeLanguage,
                    settings = uiState.settings,
                    theme = syntaxTheme,
                    searchRanges = uiState.searchMatches.map { it.range },
                    activeSearchRangeIndex = uiState.activeMatchIndex,
                    shortcutActions = shortcutActions,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Dialogs
    if (uiState.showNewFileDialog) {
        NewFileDialog(
            onConfirm = { name, isFolder -> viewModel.createWorkspaceItem(name, isFolder) },
            onDismiss = { viewModel.setShowNewFileDialog(false) }
        )
    }

    if (uiState.showGoToLineDialog) {
        val totalLines = EditorState.getLineStartOffsets(uiState.editorValue.text).size
        val currentLine = EditorState.getLineNumberForOffset(
            EditorState.getLineStartOffsets(uiState.editorValue.text),
            uiState.editorValue.selection.start
        )
        GoToLineDialog(
            totalLines = totalLines,
            currentLine = currentLine,
            onConfirm = { line -> viewModel.goToLine(line) },
            onDismiss = { viewModel.setShowGoToLineDialog(false) }
        )
    }

    if (uiState.showFilePropertiesDialog && activeTab != null) {
        val lines = EditorState.getLineStartOffsets(uiState.editorValue.text).size
        val chars = uiState.editorValue.text.length
        FilePropertiesDialog(
            file = activeTab.file,
            language = activeLanguage,
            lineCount = lines,
            characterCount = chars,
            onDismiss = { viewModel.setShowFilePropertiesDialog(false) }
        )
    }

    if (uiState.showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = activeLanguage,
            onSelectLanguage = { lang -> viewModel.setLanguage(lang) },
            onDismiss = { viewModel.setShowLanguageDialog(false) }
        )
    }

    if (uiState.showSettingsDialog) {
        SettingsDialog(
            currentSettings = uiState.settings,
            onSaveSettings = { updated -> viewModel.updateSettings(updated) },
            onDismiss = { viewModel.setShowSettingsDialog(false) }
        )
    }

    if (uiState.showShortcutsDialog) {
        KeyboardShortcutsDialog(
            onDismiss = { viewModel.setShowShortcutsDialog(false) }
        )
    }

    if (uiState.tabToCloseIndex != null) {
        val closingTab = uiState.openTabs.getOrNull(uiState.tabToCloseIndex!!)
        ConfirmUnsavedDialog(
            fileName = closingTab?.title ?: "Document",
            onSave = { viewModel.confirmCloseTab(saveChanges = true) },
            onDontSave = { viewModel.confirmCloseTab(saveChanges = false) },
            onCancel = { viewModel.dismissCloseTabDialog() }
        )
    }

    uiState.itemToDelete?.let { item ->
        ConfirmDeleteDialog(
            itemName = item.name,
            isDirectory = item.isDirectory,
            onConfirm = { viewModel.deleteWorkspaceItem(item) },
            onDismiss = { viewModel.setItemToDelete(null) }
        )
    }

    uiState.itemToRename?.let { item ->
        RenameDialog(
            initialName = item.name,
            onConfirm = { newName -> viewModel.renameWorkspaceItem(item, newName) },
            onDismiss = { viewModel.setItemToRename(null) }
        )
    }

    uiState.renameTarget?.let { target ->
        RenameDialog(
            initialName = target.name,
            onConfirm = { newName ->
                when (target) {
                    is RenameTarget.Workspace -> viewModel.renameWorkspaceItem(target.item, newName)
                    is RenameTarget.SafDocument -> viewModel.renameSafDocument(target.uri, newName)
                }
            },
            onDismiss = { viewModel.setRenameTarget(null) }
        )
    }

    uiState.activeSafTreeUri?.let { treeUri ->
        SafTreeBrowserDialog(
            treeUri = treeUri,
            safStorageManager = viewModel.safStorageManager,
            onOpenDocument = { uri -> viewModel.openSafUri(uri) },
            onDismiss = { viewModel.dismissSafTreeBrowser() }
        )
    }

    if (uiState.webRunnerState.isVisible) {
        WebRunnerDialog(
            state = uiState.webRunnerState,
            onDismiss = { viewModel.dismissWebRunner() },
            onActiveTabChanged = { viewModel.setWebRunnerTab(it) },
            onViewportModeChanged = { viewModel.setWebRunnerViewport(it) },
            onLogFilterChanged = { viewModel.setWebRunnerLogFilter(it) },
            onLogSearchChanged = { viewModel.setWebRunnerLogSearch(it) },
            onClearLogs = { viewModel.clearWebRunnerLogs() },
            onAddLogMessage = { viewModel.addWebRunnerLog(it) },
            onPageTitleChanged = { viewModel.setWebRunnerPageTitle(it) }
        )
    }
}
