package com.example.editor.ui.dialogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.settings.EditorFont
import com.example.editor.settings.EditorSettings
import com.example.editor.settings.LocaleHelper
import com.example.editor.settings.ThemeMode

@Composable
fun SettingsDialog(
    currentSettings: EditorSettings,
    onSaveSettings: (EditorSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var settings by remember { mutableStateOf(currentSettings) }
    var selectedCategory by remember { mutableIntStateOf(0) } // 0: Editor, 1: Appearance, 2: Behavior

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .widthIn(max = 520.dp),
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory,
                    edgePadding = 8.dp
                ) {
                    Tab(
                        selected = selectedCategory == 0,
                        onClick = { selectedCategory = 0 },
                        text = { Text("Editor", fontSize = 13.sp, maxLines = 1, softWrap = false) }
                    )
                    Tab(
                        selected = selectedCategory == 1,
                        onClick = { selectedCategory = 1 },
                        text = { Text("Appearance", fontSize = 13.sp, maxLines = 1, softWrap = false) }
                    )
                    Tab(
                        selected = selectedCategory == 2,
                        onClick = { selectedCategory = 2 },
                        text = { Text("Behavior", fontSize = 13.sp, maxLines = 1, softWrap = false) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedCategory) {
                        0 -> {
                            // Editor Category
                            Text(
                                text = "Font Size: ${settings.fontSizeSp} sp",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Slider(
                                value = settings.fontSizeSp.toFloat(),
                                onValueChange = { settings = settings.copy(fontSizeSp = it.toInt()) },
                                valueRange = 10f..26f,
                                steps = 15
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                            Text("Tab Size", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(2, 4, 8).forEach { size ->
                                    FilterChip(
                                        selected = settings.tabSize == size,
                                        onClick = { settings = settings.copy(tabSize = size) },
                                        label = { Text("$size spaces", maxLines = 1, softWrap = false) }
                                    )
                                }
                            }

                            SettingToggle("Use Spaces for Tab", settings.useSpacesForTab) {
                                settings = settings.copy(useSpacesForTab = it)
                            }
                            SettingToggle("Word Wrap", settings.wordWrap) {
                                settings = settings.copy(wordWrap = it)
                            }
                            SettingToggle("Show Line Numbers", settings.showLineNumbers) {
                                settings = settings.copy(showLineNumbers = it)
                            }
                            SettingToggle("Highlight Current Line", settings.highlightCurrentLine) {
                                settings = settings.copy(highlightCurrentLine = it)
                            }
                            SettingToggle("Auto Indent", settings.autoIndent) {
                                settings = settings.copy(autoIndent = it)
                            }
                            SettingToggle("Auto Close Brackets", settings.autoCloseBrackets) {
                                settings = settings.copy(autoCloseBrackets = it)
                            }
                            SettingToggle("Syntax Highlighting", settings.syntaxHighlightingEnabled) {
                                settings = settings.copy(syntaxHighlightingEnabled = it)
                            }
                        }
                        1 -> {
                            // Appearance Category
                            Text("Theme", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    ThemeMode.BLACK to "Pitch Black (AMOLED)",
                                    ThemeMode.DARK to "Dark",
                                    ThemeMode.LIGHT to "Light",
                                    ThemeMode.SYSTEM to "System"
                                ).forEach { (mode, labelText) ->
                                    FilterChip(
                                        selected = settings.themeMode == mode,
                                        onClick = { settings = settings.copy(themeMode = mode) },
                                        label = { Text(labelText, maxLines = 1, softWrap = false) }
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Text("Editor Font", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EditorFont.entries.forEach { font ->
                                    FilterChip(
                                        selected = settings.editorFont == font,
                                        onClick = { settings = settings.copy(editorFont = font) },
                                        label = {
                                            Text(
                                                font.name.lowercase().replaceFirstChar { it.uppercase() },
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // UI Language with clean side-by-side chips and no vertical character breaking
                            Text("UI Language / لغة التطبيق", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "system" to "System / النظام",
                                    "en" to "English",
                                    "ar" to "العربية"
                                ).forEach { (code, label) ->
                                    FilterChip(
                                        selected = settings.uiLanguage == code,
                                        onClick = { settings = settings.copy(uiLanguage = code) },
                                        label = {
                                            Text(
                                                text = label,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        2 -> {
                            // Behavior Category
                            SettingToggle("Auto Save to Local Storage", settings.autoSave) {
                                settings = settings.copy(autoSave = it)
                            }
                            if (settings.autoSave) {
                                SettingToggle("Auto-Sync Changes to File", settings.autoSaveToDisk) {
                                    settings = settings.copy(autoSaveToDisk = it)
                                }

                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Text(
                                        "Auto-Save Interval",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                    ) {
                                        listOf(5 to "5s", 10 to "10s", 30 to "30s", 60 to "1m").forEach { (sec, label) ->
                                            FilterChip(
                                                selected = settings.autoSaveIntervalSeconds == sec,
                                                onClick = { settings = settings.copy(autoSaveIntervalSeconds = sec) },
                                                label = { Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                                            )
                                        }
                                    }
                                }
                            }

                            SettingToggle("Restore Open Tabs on Launch", settings.restoreOpenTabs) {
                                settings = settings.copy(restoreOpenTabs = it)
                            }
                            SettingToggle("Confirm Before Delete", settings.confirmDelete) {
                                settings = settings.copy(confirmDelete = it)
                            }
                            SettingToggle("Confirm Before Closing Modified", settings.confirmCloseModified) {
                                settings = settings.copy(confirmCloseModified = it)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    LocaleHelper.applyLocale(context, settings.uiLanguage)
                    onSaveSettings(settings)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
