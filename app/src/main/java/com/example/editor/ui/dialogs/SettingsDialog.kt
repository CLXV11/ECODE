package com.example.editor.ui.dialogs

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.editor.settings.AppStrings
import com.example.editor.settings.EditorFont
import com.example.editor.settings.EditorSettings
import com.example.editor.settings.LocalAppStrings
import com.example.editor.settings.LocaleHelper
import com.example.editor.settings.ThemeMode

@Composable
fun SettingsDialog(
    currentSettings: EditorSettings,
    onSaveSettings: (EditorSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var settings by remember { mutableStateOf(currentSettings) }
    var selectedCategory by remember { mutableIntStateOf(0) } // 0: Editor, 1: Appearance, 2: Behavior

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .widthIn(max = 520.dp),
        title = { Text(strings.settings, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory,
                    edgePadding = 8.dp
                ) {
                    Tab(
                        selected = selectedCategory == 0,
                        onClick = { selectedCategory = 0 },
                        text = { Text(strings.editor, fontSize = 13.sp, maxLines = 1, softWrap = false) }
                    )
                    Tab(
                        selected = selectedCategory == 1,
                        onClick = { selectedCategory = 1 },
                        text = { Text(strings.appearance, fontSize = 13.sp, maxLines = 1, softWrap = false) }
                    )
                    Tab(
                        selected = selectedCategory == 2,
                        onClick = { selectedCategory = 2 },
                        text = { Text(strings.behavior, fontSize = 13.sp, maxLines = 1, softWrap = false) }
                    )
                    Tab(
                        selected = selectedCategory == 3,
                        onClick = { selectedCategory = 3 },
                        text = { Text(strings.community, fontSize = 13.sp, maxLines = 1, softWrap = false) }
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
                                text = "${strings.fontSize}: ${settings.fontSizeSp} sp",
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

                            Text(strings.tabSize, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
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
                                        label = { Text("$size", maxLines = 1, softWrap = false) }
                                    )
                                }
                            }

                            SettingToggle(strings.useSpacesForTab, settings.useSpacesForTab) {
                                settings = settings.copy(useSpacesForTab = it)
                            }
                            SettingToggle(strings.wordWrap, settings.wordWrap) {
                                settings = settings.copy(wordWrap = it)
                            }
                            SettingToggle(strings.showLineNumbers, settings.showLineNumbers) {
                                settings = settings.copy(showLineNumbers = it)
                            }
                            SettingToggle(strings.highlightCurrentLine, settings.highlightCurrentLine) {
                                settings = settings.copy(highlightCurrentLine = it)
                            }
                            SettingToggle(strings.autoIndent, settings.autoIndent) {
                                settings = settings.copy(autoIndent = it)
                            }
                            SettingToggle(strings.autoCloseBrackets, settings.autoCloseBrackets) {
                                settings = settings.copy(autoCloseBrackets = it)
                            }
                            SettingToggle(strings.syntaxHighlighting, settings.syntaxHighlightingEnabled) {
                                settings = settings.copy(syntaxHighlightingEnabled = it)
                            }
                        }
                        1 -> {
                            // Appearance Category
                            Text(strings.theme, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    ThemeMode.BLACK to strings.themeBlack,
                                    ThemeMode.DARK to strings.themeDark,
                                    ThemeMode.LIGHT to strings.themeLight,
                                    ThemeMode.SYSTEM to strings.themeSystem
                                ).forEach { (mode, labelText) ->
                                    FilterChip(
                                        selected = settings.themeMode == mode,
                                        onClick = { settings = settings.copy(themeMode = mode) },
                                        label = { Text(labelText, maxLines = 1, softWrap = false) }
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Text(strings.editorFont, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
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

                            // UI Language with clean side-by-side chips
                            Text(strings.uiLanguage, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "system" to strings.systemLang,
                                    "en" to strings.englishLang,
                                    "ar" to strings.arabicLang
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
                            SettingToggle(strings.autoSaveStorageToggle, settings.autoSave) {
                                settings = settings.copy(autoSave = it)
                            }
                            if (settings.autoSave) {
                                SettingToggle(strings.autoSyncDiskToggle, settings.autoSaveToDisk) {
                                    settings = settings.copy(autoSaveToDisk = it)
                                }

                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Text(
                                        strings.autoSaveInterval,
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

                            SettingToggle(strings.restoreOpenTabs, settings.restoreOpenTabs) {
                                settings = settings.copy(restoreOpenTabs = it)
                            }
                            SettingToggle(strings.confirmDelete, settings.confirmDelete) {
                                settings = settings.copy(confirmDelete = it)
                            }
                            SettingToggle(strings.confirmCloseModified, settings.confirmCloseModified) {
                                settings = settings.copy(confirmCloseModified = it)
                            }
                        }
                        3 -> {
                            // Community & Developer Contacts
                            CommunitySettingsSection(strings = strings, context = context)
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
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
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

@Composable
private fun CommunitySettingsSection(
    strings: AppStrings,
    context: android.content.Context
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = strings.communitySubtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Telegram Channel Card
        CommunityChannelCard(
            title = strings.telegramChannel,
            handle = strings.telegramHandle,
            iconRes = R.drawable.ic_telegram,
            iconTint = Color(0xFF2AABEE),
            url = "https://t.me/EPCD11",
            context = context
        )

        // Discord Server Card
        CommunityChannelCard(
            title = strings.discordServer,
            handle = strings.discordInvite,
            iconRes = R.drawable.ic_discord,
            iconTint = Color(0xFF5865F2),
            url = "https://discord.gg/FkssmYFY",
            context = context
        )
    }
}

@Composable
private fun CommunityChannelCard(
    title: String,
    handle: String,
    iconRes: Int,
    iconTint: Color,
    url: String,
    context: android.content.Context
) {
    Surface(
        onClick = {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(context, url, Toast.LENGTH_SHORT).show()
            }
        },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = handle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

