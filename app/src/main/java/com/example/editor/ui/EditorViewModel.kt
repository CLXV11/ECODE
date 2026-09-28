package com.example.editor.ui

import android.app.Application
import android.net.Uri
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.editor.autosave.AutoSaveManager
import com.example.editor.autosave.AutoSaveTabSnapshot
import com.example.editor.engine.AutocompleteEngine
import com.example.editor.engine.AutoIndent
import com.example.editor.engine.CompletionItem
import com.example.editor.engine.EditorState
import com.example.editor.engine.EditorTextActions
import com.example.editor.engine.SearchEngine
import com.example.editor.engine.SearchMatch
import com.example.editor.engine.SimpleCodeFormatter
import com.example.editor.io.EditorFile
import com.example.editor.io.FileManager
import com.example.editor.io.RecentFileItem
import com.example.editor.io.RecentFilesRepository
import com.example.editor.io.RenameTarget
import com.example.editor.io.SafStorageManager
import com.example.editor.io.WorkspaceItem
import com.example.editor.runner.LogLevel
import com.example.editor.runner.ViewportMode
import com.example.editor.runner.WebConsoleMessage
import com.example.editor.runner.WebRunnerContentBuilder
import com.example.editor.runner.WebRunnerTab
import com.example.editor.runner.WebRunnerUiState
import com.example.editor.settings.EditorSettings
import com.example.editor.settings.SettingsRepository
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.LanguageRegistry
import com.example.editor.tabs.EditorTab
import com.example.editor.tabs.TabManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class EditorUiState(
    val openTabs: List<EditorTab> = emptyList(),
    val activeTabIndex: Int = 0,
    val editorValue: TextFieldValue = TextFieldValue(""),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val replaceQuery: String = "",
    val matchCase: Boolean = false,
    val wholeWord: Boolean = false,
    val useRegex: Boolean = false,
    val searchMatches: List<SearchMatch> = emptyList(),
    val activeMatchIndex: Int = -1,
    val autocompleteSuggestions: List<CompletionItem> = emptyList(),
    val settings: EditorSettings = EditorSettings(),
    val currentDirectory: File? = null,
    val workspaceItems: List<WorkspaceItem> = emptyList(),
    val recentFiles: List<RecentFileItem> = emptyList(),
    val isDrawerOpen: Boolean = false,
    // Web Runner
    val webRunnerState: WebRunnerUiState = WebRunnerUiState(),
    // Auto-Save & Data Loss Prevention
    val lastAutoSaveTimestamp: Long? = null,
    val isAutoSaving: Boolean = false,
    val autoSaveStatusText: String? = null,
    // Dialogs
    val showNewFileDialog: Boolean = false,
    val showGoToLineDialog: Boolean = false,
    val showFilePropertiesDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showShortcutsDialog: Boolean = false,
    val showWelcomeScreen: Boolean = false,
    val tabToCloseIndex: Int? = null,
    val itemToDelete: WorkspaceItem? = null,
    val itemToRename: WorkspaceItem? = null,
    val renameTarget: RenameTarget? = null,
    val activeSafTreeUri: Uri? = null,
    // Feedback
    val infoMessage: String? = null,
    val errorMessage: String? = null
)

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    val fileManager = FileManager(application)
    val safStorageManager: SafStorageManager get() = fileManager.safManager
    val autoSaveManager = AutoSaveManager(application)
    private val tabManager = TabManager(application)
    private val settingsRepository = SettingsRepository(application)
    private val recentFilesRepository = RecentFilesRepository(application)

    private val editorState = EditorState()

    private val _uiState = MutableStateFlow(
        EditorUiState(
            settings = settingsRepository.loadSettings(),
            currentDirectory = fileManager.workspaceRoot
        )
    )
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var periodicAutoSaveJob: Job? = null
    private var autoSaveDebounceJob: Job? = null

    init {
        loadWorkspaceItems()
        loadRecentFiles()
        restorePersistedTabs()
        startAutoSaveWatcher()
    }

    private fun loadWorkspaceItems(dir: File = _uiState.value.currentDirectory ?: fileManager.workspaceRoot) {
        viewModelScope.launch {
            val items = fileManager.listWorkspace(dir)
            _uiState.update { it.copy(currentDirectory = dir, workspaceItems = items) }
        }
    }

    private fun loadRecentFiles() {
        _uiState.update { it.copy(recentFiles = recentFilesRepository.getRecentFiles()) }
    }

    private fun restorePersistedTabs() {
        viewModelScope.launch {
            var restoredFromSnapshot = false

            // Check if local storage has auto-saved snapshots to prevent data loss
            if (autoSaveManager.hasSnapshots()) {
                try {
                    val snapshotData = autoSaveManager.loadSnapshots()
                    if (snapshotData != null && snapshotData.first.isNotEmpty()) {
                        val (snapshots, savedIdx) = snapshotData
                        val restoredTabs = snapshots.map { it.toEditorTab() }
                        tabManager.restoreTabs(restoredTabs, savedIdx)
                        restoredFromSnapshot = true
                    }
                } catch (_: Throwable) {
                    // Fallback to persisted tabs
                }
            }

            if (!restoredFromSnapshot && _uiState.value.settings.restoreOpenTabs) {
                val (persisted, savedIdx) = tabManager.getPersistedTabs()
                if (persisted.isNotEmpty()) {
                    for (p in persisted) {
                        try {
                            if (p.isInternal && p.filePath != null) {
                                val file = File(p.filePath)
                                if (file.exists()) {
                                    val (editorFile, content) = fileManager.readFile(file).getOrThrow()
                                    val lang = LanguageRegistry.findById(p.languageId)
                                    tabManager.openTab(editorFile, content, lang)
                                }
                            } else if (!p.isInternal && p.uriString != null) {
                                val uri = Uri.parse(p.uriString)
                                val (editorFile, content) = fileManager.readSafUri(uri).getOrThrow()
                                val lang = LanguageRegistry.findById(p.languageId)
                                tabManager.openTab(editorFile, content, lang)
                            }
                        } catch (_: Throwable) {
                            // Skip broken persisted tab
                        }
                    }
                    if (tabManager.openTabs.isNotEmpty()) {
                        tabManager.selectTab(savedIdx.coerceIn(0, tabManager.openTabs.size - 1))
                    }
                }
            }

            // If still no tabs open, open default main.py from workspace
            if (tabManager.openTabs.isEmpty()) {
                val defaultFile = File(fileManager.workspaceRoot, "main.py")
                if (defaultFile.exists()) {
                    val (editorFile, content) = fileManager.readFile(defaultFile).getOrDefault(
                        Pair(EditorFile(name = "main.py", absolutePath = defaultFile.absolutePath), "# Python\n")
                    )
                    tabManager.openTab(editorFile, content)
                }
            }

            syncActiveTabToEditor()
        }
    }

    private fun syncActiveTabToEditor() {
        val active = tabManager.activeTab
        if (active != null) {
            editorState.reset(active.content)
            _uiState.update {
                it.copy(
                    openTabs = tabManager.openTabs.toList(),
                    activeTabIndex = tabManager.activeIndex,
                    editorValue = TextFieldValue(active.content, TextRange(active.cursorPosition.coerceIn(0, active.content.length))),
                    canUndo = editorState.canUndo,
                    canRedo = editorState.canRedo
                )
            }
            updateAutocompleteSuggestions(active.content, active.cursorPosition, active.language)
            performSearch(_uiState.value.searchQuery)
        } else {
            _uiState.update {
                it.copy(
                    openTabs = emptyList(),
                    activeTabIndex = 0,
                    editorValue = TextFieldValue(""),
                    canUndo = false,
                    canRedo = false
                )
            }
        }
    }

    fun onEditorValueChanged(newValue: TextFieldValue) {
        val prevText = _uiState.value.editorValue.text
        val isModified = prevText != newValue.text

        editorState.recordChange(newValue)

        _uiState.update {
            it.copy(
                editorValue = newValue,
                canUndo = editorState.canUndo,
                canRedo = editorState.canRedo
            )
        }

        tabManager.activeTab?.let { tab ->
            tab.content = newValue.text
            tab.cursorPosition = newValue.selection.start
            if (isModified) {
                tab.isModified = true
                _uiState.update {
                    it.copy(
                        openTabs = tabManager.openTabs.toList(),
                        autoSaveStatusText = "Unsaved changes"
                    )
                }
            }
            updateAutocompleteSuggestions(newValue.text, newValue.selection.start, tab.language)
        }

        if (isModified) {
            if (_uiState.value.isSearchVisible) {
                performSearch(_uiState.value.searchQuery)
            }
            // Fast debounced auto-save to local storage (1500ms after user pauses typing)
            autoSaveDebounceJob?.cancel()
            autoSaveDebounceJob = viewModelScope.launch {
                delay(1500)
                performAutoSave(silent = true)
            }
        }
    }

    fun undo() {
        val undone = editorState.undo() ?: return
        _uiState.update {
            it.copy(
                editorValue = undone,
                canUndo = editorState.canUndo,
                canRedo = editorState.canRedo
            )
        }
        tabManager.updateActiveTabContent(undone.text, isModified = true)
        _uiState.update { it.copy(openTabs = tabManager.openTabs.toList()) }
    }

    fun redo() {
        val redone = editorState.redo() ?: return
        _uiState.update {
            it.copy(
                editorValue = redone,
                canUndo = editorState.canUndo,
                canRedo = editorState.canRedo
            )
        }
        tabManager.updateActiveTabContent(redone.text, isModified = true)
        _uiState.update { it.copy(openTabs = tabManager.openTabs.toList()) }
    }

    fun insertTab() {
        val indent = AutoIndent.getIndentString(_uiState.value.settings.tabSize, _uiState.value.settings.useSpacesForTab)
        insertTextAtCursor(indent)
    }

    fun indentSelection() {
        val current = _uiState.value.editorValue
        val (newText, newSelection) = AutoIndent.indentLines(
            text = current.text,
            startOffset = current.selection.start,
            endOffset = current.selection.end,
            tabSize = _uiState.value.settings.tabSize,
            useSpaces = _uiState.value.settings.useSpacesForTab
        )
        onEditorValueChanged(TextFieldValue(newText, newSelection))
    }

    fun dedentSelection() {
        val current = _uiState.value.editorValue
        val (newText, newSelection) = AutoIndent.dedentLines(
            text = current.text,
            startOffset = current.selection.start,
            endOffset = current.selection.end,
            tabSize = _uiState.value.settings.tabSize,
            useSpaces = _uiState.value.settings.useSpacesForTab
        )
        onEditorValueChanged(TextFieldValue(newText, newSelection))
    }

    fun selectAll() {
        val current = _uiState.value.editorValue
        onEditorValueChanged(current.copy(selection = TextRange(0, current.text.length)))
    }

    fun duplicateLineOrSelection() {
        val current = _uiState.value.editorValue
        val (newText, newSelection) = EditorTextActions.duplicateLineOrSelection(
            text = current.text,
            selection = current.selection
        )
        onEditorValueChanged(TextFieldValue(newText, newSelection))
    }

    fun deleteCurrentLine() {
        val current = _uiState.value.editorValue
        val (newText, newSelection) = EditorTextActions.deleteCurrentLine(
            text = current.text,
            selection = current.selection
        )
        onEditorValueChanged(TextFieldValue(newText, newSelection))
    }

    fun toggleComment() {
        val current = _uiState.value.editorValue
        val activeLang = tabManager.activeTab?.language ?: LanguageRegistry.PLAIN_TEXT
        val (newText, newSelection) = EditorTextActions.toggleComment(
            text = current.text,
            selection = current.selection,
            language = activeLang
        )
        onEditorValueChanged(TextFieldValue(newText, newSelection))
    }

    fun insertSymbol(symbol: String) {
        insertTextAtCursor(symbol)
    }

    fun insertTextAtCursor(insert: String) {
        val current = _uiState.value.editorValue
        val text = current.text
        val sel = current.selection
        val min = minOf(sel.start, sel.end).coerceIn(0, text.length)
        val max = maxOf(sel.start, sel.end).coerceIn(0, text.length)

        val newText = text.substring(0, min) + insert + text.substring(max)
        val newCursor = min + insert.length
        onEditorValueChanged(TextFieldValue(newText, TextRange(newCursor)))
    }

    fun applyAutocomplete(item: CompletionItem) {
        val current = _uiState.value.editorValue
        val text = current.text
        val cursor = current.selection.start
        val (_, prefixStart) = AutocompleteEngine.extractPrefixAtCursor(text, cursor)

        val insert = item.insertText
        val newText = text.substring(0, prefixStart) + insert + text.substring(cursor)
        val newCursor = prefixStart + insert.length
        onEditorValueChanged(TextFieldValue(newText, TextRange(newCursor)))
    }

    private fun updateAutocompleteSuggestions(text: String, cursor: Int, language: LanguageDefinition) {
        val suggestions = AutocompleteEngine.getSuggestions(text, cursor, language)
        _uiState.update { it.copy(autocompleteSuggestions = suggestions) }
    }

    fun formatCurrentCode() {
        val current = _uiState.value.editorValue
        val active = tabManager.activeTab ?: return

        val formatted = if (active.language.isJson) {
            SimpleCodeFormatter.formatJson(current.text, _uiState.value.settings.tabSize)
        } else {
            SimpleCodeFormatter.formatCleanWhitespace(current.text)
        }

        if (formatted != null && formatted != current.text) {
            onEditorValueChanged(TextFieldValue(formatted, TextRange(0)))
            _uiState.update { it.copy(infoMessage = "Code formatted successfully") }
        } else {
            _uiState.update { it.copy(infoMessage = "Already formatted or unable to format") }
        }
    }

    fun saveActiveFile() {
        val active = tabManager.activeTab ?: return
        viewModelScope.launch {
            try {
                if (active.file.isInternalWorkspace && active.file.absolutePath != null) {
                    val file = File(active.file.absolutePath)
                    fileManager.saveWorkspaceFile(
                        file = file,
                        content = active.content,
                        encodingName = active.file.encodingName,
                        hasBom = active.file.hasBom
                    ).getOrThrow()

                    val updatedFile = active.file.copy(
                        fileSize = file.length(),
                        lastModified = file.lastModified()
                    )
                    tabManager.markActiveTabSaved(updatedFile)
                    autoSaveManager.saveSnapshots(tabManager.openTabs, tabManager.activeIndex)
                    _uiState.update {
                        it.copy(
                            openTabs = tabManager.openTabs.toList(),
                            infoMessage = "File saved successfully",
                            autoSaveStatusText = "Saved",
                            lastAutoSaveTimestamp = System.currentTimeMillis()
                        )
                    }
                    loadWorkspaceItems()
                } else if (!active.file.isInternalWorkspace && active.file.uri != null) {
                    fileManager.saveSafUri(
                        uri = active.file.uri,
                        content = active.content,
                        encodingName = active.file.encodingName,
                        hasBom = active.file.hasBom
                    ).getOrThrow()

                    tabManager.markActiveTabSaved(active.file.copy(lastModified = System.currentTimeMillis()))
                    autoSaveManager.saveSnapshots(tabManager.openTabs, tabManager.activeIndex)
                    _uiState.update {
                        it.copy(
                            openTabs = tabManager.openTabs.toList(),
                            infoMessage = "Document saved successfully",
                            autoSaveStatusText = "Saved",
                            lastAutoSaveTimestamp = System.currentTimeMillis()
                        )
                    }
                }
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "Unable to save file: ${e.localizedMessage ?: "Unknown error"}") }
            }
        }
    }

    fun selectTab(index: Int) {
        tabManager.selectTab(index)
        syncActiveTabToEditor()
    }

    fun requestCloseTab(index: Int) {
        val tab = tabManager.openTabs.getOrNull(index) ?: return
        if (tab.isModified && _uiState.value.settings.confirmCloseModified) {
            _uiState.update { it.copy(tabToCloseIndex = index) }
        } else {
            closeTabDirectly(index)
        }
    }

    fun confirmCloseTab(saveChanges: Boolean) {
        val index = _uiState.value.tabToCloseIndex ?: return
        _uiState.update { it.copy(tabToCloseIndex = null) }

        if (saveChanges) {
            saveActiveFile()
        }
        closeTabDirectly(index)
    }

    fun dismissCloseTabDialog() {
        _uiState.update { it.copy(tabToCloseIndex = null) }
    }

    private fun closeTabDirectly(index: Int) {
        val closed = tabManager.closeTab(index)
        closed?.let {
            viewModelScope.launch {
                autoSaveManager.discardTab(it.id)
                autoSaveManager.saveSnapshots(tabManager.openTabs, tabManager.activeIndex)
            }
        }
        syncActiveTabToEditor()
    }

    fun openWorkspaceItem(item: WorkspaceItem) {
        if (item.isDirectory) {
            loadWorkspaceItems(item.file)
        } else {
            viewModelScope.launch {
                try {
                    val (editorFile, content) = fileManager.readFile(item.file).getOrThrow()
                    tabManager.openTab(editorFile, content)
                    recentFilesRepository.addRecentFile(
                        RecentFileItem(
                            name = item.name,
                            pathOrUri = item.file.absolutePath,
                            isInternal = true,
                            languageId = LanguageRegistry.detectLanguage(item.name, content).id,
                            lastOpened = System.currentTimeMillis()
                        )
                    )
                    loadRecentFiles()
                    syncActiveTabToEditor()
                    _uiState.update { it.copy(isDrawerOpen = false) }
                } catch (e: Throwable) {
                    _uiState.update { it.copy(errorMessage = "Unable to open file: ${e.localizedMessage}") }
                }
            }
        }
    }

    fun openSafUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val (editorFile, content) = fileManager.readSafUri(uri).getOrThrow()
                tabManager.openTab(editorFile, content)
                recentFilesRepository.addRecentFile(
                    RecentFileItem(
                        name = editorFile.name,
                        pathOrUri = uri.toString(),
                        isInternal = false,
                        languageId = LanguageRegistry.detectLanguage(editorFile.name, content).id,
                        lastOpened = System.currentTimeMillis()
                    )
                )
                loadRecentFiles()
                syncActiveTabToEditor()
                _uiState.update { it.copy(isDrawerOpen = false, infoMessage = "Opened ${editorFile.name}") }
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "Failed to open document: ${e.localizedMessage}") }
            }
        }
    }

    fun createWorkspaceItem(name: String, isFolder: Boolean) {
        val current = _uiState.value.currentDirectory ?: fileManager.workspaceRoot
        viewModelScope.launch {
            try {
                if (isFolder) {
                    fileManager.createFolder(current, name).getOrThrow()
                    _uiState.update { it.copy(showNewFileDialog = false, infoMessage = "Folder created: $name") }
                } else {
                    val file = fileManager.createFile(current, name).getOrThrow()
                    _uiState.update { it.copy(showNewFileDialog = false, infoMessage = "File created: $name") }
                    val (editorFile, content) = fileManager.readFile(file).getOrThrow()
                    tabManager.openTab(editorFile, content)
                    syncActiveTabToEditor()
                }
                loadWorkspaceItems(current)
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "Unable to create item: ${e.localizedMessage}") }
            }
        }
    }

    fun renameWorkspaceItem(target: WorkspaceItem, newName: String) {
        viewModelScope.launch {
            try {
                val destFile = fileManager.renameItem(target.file, newName).getOrThrow()
                // Update any open tabs pointing to this file
                val openTab = tabManager.openTabs.firstOrNull { it.file.absolutePath == target.file.absolutePath }
                if (openTab != null) {
                    val updatedEditorFile = openTab.file.copy(
                        absolutePath = destFile.absolutePath,
                        name = destFile.name
                    )
                    tabManager.updateActiveTabFile(updatedEditorFile)
                    syncActiveTabToEditor()
                }
                _uiState.update { it.copy(itemToRename = null, renameTarget = null, infoMessage = "Renamed to $newName") }
                loadWorkspaceItems()
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "Rename failed: ${e.localizedMessage}") }
            }
        }
    }

    fun renameSafDocument(uri: Uri, newName: String) {
        viewModelScope.launch {
            try {
                val newUri = fileManager.safManager.renameDocument(uri, newName).getOrThrow()
                // Update active tab if it holds this URI
                val activeTab = tabManager.activeTab
                if (activeTab != null && activeTab.file.uri == uri) {
                    val updatedEditorFile = activeTab.file.copy(
                        uri = newUri,
                        absolutePath = newUri.toString(),
                        name = newName
                    )
                    tabManager.updateActiveTabFile(updatedEditorFile)
                    syncActiveTabToEditor()
                }
                // Update recent files
                recentFilesRepository.removeRecentFile(uri.toString())
                recentFilesRepository.addRecentFile(
                    RecentFileItem(
                        name = newName,
                        pathOrUri = newUri.toString(),
                        isInternal = false,
                        languageId = LanguageRegistry.detectLanguage(newName).id,
                        lastOpened = System.currentTimeMillis()
                    )
                )
                loadRecentFiles()
                _uiState.update { it.copy(renameTarget = null, infoMessage = "Renamed to $newName") }
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "Failed to rename document: ${e.localizedMessage}") }
            }
        }
    }

    fun createSafDocument(createdUri: Uri) {
        viewModelScope.launch {
            try {
                // Guarantee persistable permissions
                fileManager.safManager.takePersistablePermissions(createdUri)
                val (editorFile, content) = fileManager.safManager.openDocument(createdUri).getOrThrow()
                tabManager.openTab(editorFile, content)
                recentFilesRepository.addRecentFile(
                    RecentFileItem(
                        name = editorFile.name,
                        pathOrUri = createdUri.toString(),
                        isInternal = false,
                        languageId = LanguageRegistry.detectLanguage(editorFile.name, content).id,
                        lastOpened = System.currentTimeMillis()
                    )
                )
                loadRecentFiles()
                syncActiveTabToEditor()
                _uiState.update { it.copy(isDrawerOpen = false, infoMessage = "Created ${editorFile.name}") }
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "Failed to create document: ${e.localizedMessage}") }
            }
        }
    }

    fun requestRenameActiveFile() {
        val active = tabManager.activeTab ?: return
        val file = active.file
        if (file.isInternalWorkspace && file.absolutePath != null) {
            val item = _uiState.value.workspaceItems.firstOrNull { it.file.absolutePath == file.absolutePath }
                ?: WorkspaceItem(File(file.absolutePath), file.name, isDirectory = false, sizeBytes = file.fileSize, lastModified = file.lastModified)
            _uiState.update { it.copy(renameTarget = RenameTarget.Workspace(item)) }
        } else if (file.uri != null) {
            _uiState.update { it.copy(renameTarget = RenameTarget.SafDocument(file.uri, file.name)) }
        }
    }

    fun setRenameTarget(target: RenameTarget?) {
        _uiState.update { it.copy(renameTarget = target) }
    }

    fun openSafTreeBrowser(treeUri: Uri) {
        viewModelScope.launch {
            fileManager.safManager.takePersistablePermissions(treeUri)
            _uiState.update { it.copy(activeSafTreeUri = treeUri, isDrawerOpen = false) }
        }
    }

    fun dismissSafTreeBrowser() {
        _uiState.update { it.copy(activeSafTreeUri = null) }
    }

    fun deleteWorkspaceItem(target: WorkspaceItem) {
        viewModelScope.launch {
            try {
                fileManager.deleteItem(target.file).getOrThrow()
                // Close any open tab pointing to this file
                val openIndex = tabManager.openTabs.indexOfFirst { it.file.absolutePath == target.file.absolutePath }
                if (openIndex != -1) {
                    closeTabDirectly(openIndex)
                }
                _uiState.update { it.copy(itemToDelete = null, infoMessage = "Deleted ${target.name}") }
                loadWorkspaceItems()
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorMessage = "Delete failed: ${e.localizedMessage}") }
            }
        }
    }

    fun navigateWorkspaceUp() {
        val current = _uiState.value.currentDirectory ?: return
        val root = fileManager.workspaceRoot
        if (current.absolutePath != root.absolutePath) {
            val parent = current.parentFile ?: root
            loadWorkspaceItems(parent)
        }
    }

    fun refreshWorkspace() {
        loadWorkspaceItems()
    }

    fun setLanguage(language: LanguageDefinition) {
        tabManager.updateActiveTabLanguage(language)
        val active = tabManager.activeTab
        if (active != null) {
            val path = active.file.absolutePath ?: active.file.uri?.toString()
            if (path != null) {
                recentFilesRepository.addRecentFile(
                    RecentFileItem(
                        name = active.file.name,
                        pathOrUri = path,
                        isInternal = active.file.isInternalWorkspace,
                        languageId = language.id,
                        lastOpened = System.currentTimeMillis()
                    )
                )
                loadRecentFiles()
            }
            updateAutocompleteSuggestions(_uiState.value.editorValue.text, _uiState.value.editorValue.selection.start, language)
        }
        _uiState.update {
            it.copy(
                openTabs = tabManager.openTabs.toList(),
                showLanguageDialog = false,
                infoMessage = "Language set to ${language.name}"
            )
        }
    }

    fun goToLine(lineNumber: Int) {
        val text = _uiState.value.editorValue.text
        val offsets = EditorState.getLineStartOffsets(text)
        val targetOffset = EditorState.getOffsetForLine(offsets, lineNumber, text.length)
        onEditorValueChanged(
            _uiState.value.editorValue.copy(
                selection = TextRange(targetOffset)
            )
        )
        _uiState.update { it.copy(showGoToLineDialog = false) }
    }

    // Search & Replace
    fun toggleSearch(visible: Boolean? = null) {
        val target = visible ?: !_uiState.value.isSearchVisible
        _uiState.update { it.copy(isSearchVisible = target) }
        if (target) {
            performSearch(_uiState.value.searchQuery)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        performSearch(query)
    }

    fun setReplaceQuery(query: String) {
        _uiState.update { it.copy(replaceQuery = query) }
    }

    fun toggleMatchCase() {
        _uiState.update { it.copy(matchCase = !it.matchCase) }
        performSearch(_uiState.value.searchQuery)
    }

    fun toggleWholeWord() {
        _uiState.update { it.copy(wholeWord = !it.wholeWord) }
        performSearch(_uiState.value.searchQuery)
    }

    fun toggleRegex() {
        _uiState.update { it.copy(useRegex = !it.useRegex) }
        performSearch(_uiState.value.searchQuery)
    }

    private fun performSearch(query: String) {
        val text = _uiState.value.editorValue.text
        val matches = SearchEngine.findMatches(
            text = text,
            query = query,
            matchCase = _uiState.value.matchCase,
            wholeWord = _uiState.value.wholeWord,
            useRegex = _uiState.value.useRegex
        )

        val newIdx = if (matches.isNotEmpty()) 0 else -1
        _uiState.update {
            it.copy(
                searchMatches = matches,
                activeMatchIndex = newIdx
            )
        }

        if (newIdx != -1) {
            scrollToMatch(matches[0])
        }
    }

    fun nextMatch() {
        val matches = _uiState.value.searchMatches
        if (matches.isEmpty()) return
        val nextIdx = (_uiState.value.activeMatchIndex + 1) % matches.size
        _uiState.update { it.copy(activeMatchIndex = nextIdx) }
        scrollToMatch(matches[nextIdx])
    }

    fun previousMatch() {
        val matches = _uiState.value.searchMatches
        if (matches.isEmpty()) return
        val prevIdx = if (_uiState.value.activeMatchIndex <= 0) matches.size - 1 else _uiState.value.activeMatchIndex - 1
        _uiState.update { it.copy(activeMatchIndex = prevIdx) }
        scrollToMatch(matches[prevIdx])
    }

    private fun scrollToMatch(match: SearchMatch) {
        val current = _uiState.value.editorValue
        onEditorValueChanged(
            current.copy(
                selection = TextRange(match.range.first, match.range.last + 1)
            )
        )
    }

    fun replaceCurrentMatch() {
        val matches = _uiState.value.searchMatches
        val idx = _uiState.value.activeMatchIndex
        if (idx !in matches.indices) return

        val currentMatch = matches[idx]
        val (newText, newRange) = SearchEngine.replaceMatch(
            text = _uiState.value.editorValue.text,
            match = currentMatch,
            replacement = _uiState.value.replaceQuery
        )

        onEditorValueChanged(TextFieldValue(newText, TextRange(newRange.last + 1)))
        performSearch(_uiState.value.searchQuery)
    }

    fun replaceAllMatches() {
        val (newText, count) = SearchEngine.replaceAll(
            text = _uiState.value.editorValue.text,
            query = _uiState.value.searchQuery,
            replacement = _uiState.value.replaceQuery,
            matchCase = _uiState.value.matchCase,
            wholeWord = _uiState.value.wholeWord,
            useRegex = _uiState.value.useRegex
        )

        onEditorValueChanged(TextFieldValue(newText, TextRange(0)))
        performSearch(_uiState.value.searchQuery)
        _uiState.update { it.copy(infoMessage = "Replaced $count occurrences") }
    }

    // Settings
    fun updateSettings(newSettings: EditorSettings) {
        settingsRepository.saveSettings(newSettings)
        _uiState.update { it.copy(settings = newSettings) }
        startAutoSaveWatcher()
    }

    // Dialog state toggles
    fun setShowNewFileDialog(show: Boolean) { _uiState.update { it.copy(showNewFileDialog = show) } }
    fun setShowGoToLineDialog(show: Boolean) { _uiState.update { it.copy(showGoToLineDialog = show) } }
    fun setShowFilePropertiesDialog(show: Boolean) { _uiState.update { it.copy(showFilePropertiesDialog = show) } }
    fun setShowLanguageDialog(show: Boolean) { _uiState.update { it.copy(showLanguageDialog = show) } }
    fun setShowSettingsDialog(show: Boolean) { _uiState.update { it.copy(showSettingsDialog = show) } }
    fun setShowShortcutsDialog(show: Boolean) { _uiState.update { it.copy(showShortcutsDialog = show) } }
    fun setShowWelcomeScreen(show: Boolean) { _uiState.update { it.copy(showWelcomeScreen = show) } }
    fun setDrawerOpen(open: Boolean) { _uiState.update { it.copy(isDrawerOpen = open) } }
    fun setItemToDelete(item: WorkspaceItem?) { _uiState.update { it.copy(itemToDelete = item) } }
    fun setItemToRename(item: WorkspaceItem?) { _uiState.update { it.copy(itemToRename = item) } }
    fun dismissInfo() { _uiState.update { it.copy(infoMessage = null) } }
    fun dismissError() { _uiState.update { it.copy(errorMessage = null) } }

    fun removeRecentFile(item: RecentFileItem) {
        recentFilesRepository.removeRecentFile(item.pathOrUri)
        loadRecentFiles()
    }

    fun openRecentFile(recent: RecentFileItem) {
        if (recent.isInternal) {
            openWorkspaceItem(
                WorkspaceItem(
                    file = File(recent.pathOrUri),
                    name = recent.name,
                    isDirectory = false,
                    extension = File(recent.pathOrUri).extension,
                    languageName = recent.languageId,
                    sizeBytes = 0L,
                    lastModified = recent.lastOpened
                )
            )
        } else {
            openSafUri(Uri.parse(recent.pathOrUri))
        }
    }

    // Web Runner Operations
    fun runActiveFileWebPreview() {
        val active = tabManager.activeTab
        if (active == null) {
            val defaultIndex = File(fileManager.workspaceRoot, "index.html")
            if (defaultIndex.exists()) {
                runFileWebPreview(defaultIndex)
            } else {
                _uiState.update { it.copy(infoMessage = "Open an HTML, CSS, or JavaScript file to run") }
            }
            return
        }

        val fileName = active.file.name
        val languageId = active.language.id
        val fileType = WebRunnerContentBuilder.detectFileType(fileName, languageId)

        val parentDir = active.file.absolutePath?.let { File(it).parentFile }
            ?: _uiState.value.currentDirectory
            ?: fileManager.workspaceRoot

        val payload = WebRunnerContentBuilder.buildPayload(
            title = fileName,
            rawContent = _uiState.value.editorValue.text,
            fileType = fileType,
            fileDir = parentDir,
            openTabs = tabManager.openTabs
        )

        val isVisual = payload.analysisResult.programType.isVisualCapable
        val targetTab = if (isVisual) WebRunnerTab.PREVIEW else WebRunnerTab.CONSOLE

        val initialLogs = listOf(
            WebConsoleMessage(
                level = LogLevel.INFO,
                message = "Starting ${payload.analysisResult.programType.displayName} for $fileName..."
            )
        )

        _uiState.update {
            it.copy(
                webRunnerState = WebRunnerUiState(
                    isVisible = true,
                    payload = payload,
                    logs = initialLogs,
                    activeTab = targetTab,
                    pageTitle = fileName
                )
            )
        }
    }

    fun runFileWebPreview(file: File) {
        viewModelScope.launch {
            try {
                val content = file.readText()
                val fileType = WebRunnerContentBuilder.detectFileType(file.name)
                val payload = WebRunnerContentBuilder.buildPayload(
                    title = file.name,
                    rawContent = content,
                    fileType = fileType,
                    fileDir = file.parentFile ?: fileManager.workspaceRoot,
                    openTabs = tabManager.openTabs
                )

                val isVisual = payload.analysisResult.programType.isVisualCapable
                val targetTab = if (isVisual) WebRunnerTab.PREVIEW else WebRunnerTab.CONSOLE

                _uiState.update {
                    it.copy(
                        webRunnerState = WebRunnerUiState(
                            isVisible = true,
                            payload = payload,
                            logs = listOf(
                                WebConsoleMessage(
                                    level = LogLevel.INFO,
                                    message = "Running ${payload.analysisResult.programType.displayName}: ${file.name}..."
                                )
                            ),
                            activeTab = targetTab,
                            pageTitle = file.name
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to run file: ${e.message}") }
            }
        }
    }

    fun runSafUriWebPreview(uri: Uri, name: String) {
        viewModelScope.launch {
            try {
                val content = fileManager.readSafUri(uri).getOrThrow().second
                val fileType = WebRunnerContentBuilder.detectFileType(name)
                val payload = WebRunnerContentBuilder.buildPayload(
                    title = name,
                    rawContent = content,
                    fileType = fileType,
                    fileDir = null,
                    openTabs = tabManager.openTabs
                )

                val isVisual = payload.analysisResult.programType.isVisualCapable
                val targetTab = if (isVisual) WebRunnerTab.PREVIEW else WebRunnerTab.CONSOLE

                _uiState.update {
                    it.copy(
                        webRunnerState = WebRunnerUiState(
                            isVisible = true,
                            payload = payload,
                            logs = listOf(
                                WebConsoleMessage(
                                    level = LogLevel.INFO,
                                    message = "Running ${payload.analysisResult.programType.displayName}: $name"
                                )
                            ),
                            activeTab = targetTab,
                            pageTitle = name
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to run SAF document: ${e.message}") }
            }
        }
    }

    fun dismissWebRunner() {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(isVisible = false))
        }
    }

    fun setWebRunnerTab(tab: WebRunnerTab) {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(activeTab = tab))
        }
    }

    fun setWebRunnerViewport(mode: ViewportMode) {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(viewportMode = mode))
        }
    }

    fun setWebRunnerLogFilter(filter: LogLevel?) {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(logFilter = filter))
        }
    }

    fun setWebRunnerLogSearch(query: String) {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(logSearchQuery = query))
        }
    }

    fun clearWebRunnerLogs() {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(logs = emptyList()))
        }
    }

    fun addWebRunnerLog(msg: WebConsoleMessage) {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(logs = it.webRunnerState.logs + msg))
        }
    }

    fun setWebRunnerPageTitle(title: String) {
        _uiState.update {
            it.copy(webRunnerState = it.webRunnerState.copy(pageTitle = title))
        }
    }

    /**
     * Starts or restarts the periodic auto-save loop based on current user settings.
     */
    fun startAutoSaveWatcher() {
        periodicAutoSaveJob?.cancel()
        periodicAutoSaveJob = viewModelScope.launch {
            while (true) {
                val intervalSec = _uiState.value.settings.autoSaveIntervalSeconds.coerceAtLeast(3)
                delay(intervalSec * 1000L)
                performAutoSave(silent = true)
            }
        }
    }

    /**
     * Executes auto-save:
     * 1. Persists complete snapshot of all open tabs, cursor positions, and content to local storage.
     * 2. If auto-save to disk is enabled, atomically saves modified files directly to disk/SAF storage.
     */
    suspend fun performAutoSave(silent: Boolean = true) {
        val currentTabs = tabManager.openTabs.toList()
        if (currentTabs.isEmpty()) return

        _uiState.update { it.copy(isAutoSaving = true) }

        try {
            // Step 1: Persist snapshot to local storage to prevent data loss on OS kill / backgrounding
            val timestamp = autoSaveManager.saveSnapshots(currentTabs, tabManager.activeIndex)

            // Step 2: If autoSave to disk is active, also sync modified files to disk
            val settings = _uiState.value.settings
            if (settings.autoSave && settings.autoSaveToDisk) {
                for (tab in currentTabs) {
                    if (tab.isModified) {
                        try {
                            if (tab.file.isInternalWorkspace && tab.file.absolutePath != null) {
                                val file = File(tab.file.absolutePath)
                                if (file.exists() || file.parentFile?.exists() == true) {
                                    fileManager.saveWorkspaceFile(
                                        file = file,
                                        content = tab.content,
                                        encodingName = tab.file.encodingName,
                                        hasBom = tab.file.hasBom
                                    ).getOrThrow()
                                    val updated = tab.file.copy(fileSize = file.length(), lastModified = file.lastModified())
                                    tab.isModified = false
                                }
                            } else if (!tab.file.isInternalWorkspace && tab.file.uri != null) {
                                fileManager.saveSafUri(
                                    uri = tab.file.uri,
                                    content = tab.content,
                                    encodingName = tab.file.encodingName,
                                    hasBom = tab.file.hasBom
                                ).getOrThrow()
                                tab.isModified = false
                            }
                        } catch (_: Throwable) {
                            // File save error on one file must not cancel snapshots of other files
                        }
                    }
                }
            }

            _uiState.update {
                it.copy(
                    openTabs = tabManager.openTabs.toList(),
                    lastAutoSaveTimestamp = timestamp,
                    isAutoSaving = false,
                    autoSaveStatusText = "Saved"
                )
            }
        } catch (_: Throwable) {
            _uiState.update { it.copy(isAutoSaving = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        periodicAutoSaveJob?.cancel()
        autoSaveDebounceJob?.cancel()
    }
}
