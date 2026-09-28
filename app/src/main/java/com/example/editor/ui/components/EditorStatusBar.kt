package com.example.editor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.settings.LocalAppStrings
import com.example.editor.tabs.EditorTab

@Composable
fun EditorStatusBar(
    activeTab: EditorTab?,
    line: Int,
    column: Int,
    isAutoSaving: Boolean,
    autoSaveStatusText: String?,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val containerBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(26.dp)
            .background(containerBg)
            .padding(horizontal = 10.dp)
            .testTag("editor_status_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Auto-save status and local storage state
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAutoSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(10.dp)
                        .testTag("auto_saving_indicator"),
                    strokeWidth = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = strings.autoSaving,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (activeTab != null && activeTab.isModified) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Unsaved Changes",
                    tint = Color(0xFFE5A00D), // Amber
                    modifier = Modifier.size(8.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = strings.autoSaving,
                    fontSize = 11.sp,
                    color = Color(0xFFE5A00D)
                )
            } else if (activeTab != null) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = strings.saved,
                    tint = Color(0xFF2EA043), // Green
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = autoSaveStatusText ?: strings.saved,
                    fontSize = 11.sp,
                    color = contentColor
                )
            }
        }

        // Right side: Line & Column, Encoding, Language
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${strings.line} $line, ${strings.col} $column",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = contentColor
            )

            if (activeTab != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = activeTab.file.encodingName,
                    fontSize = 11.sp,
                    color = contentColor
                )
            }
        }
    }
}
