package com.example.editor.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.io.EditorFile
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.LanguageIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FilePropertiesDialog(
    file: EditorFile,
    language: LanguageDefinition,
    lineCount: Int,
    characterCount: Int,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LanguageIcon(
                    iconDef = language.icon,
                    size = 24.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("File Properties", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                PropertyRow("Name", file.name)
                PropertyRow("Storage", if (file.isInternalWorkspace) "Internal Workspace" else "External SAF Document")
                PropertyRow("Path / URI", file.absolutePath ?: file.uri?.toString() ?: "Workspace")
                PropertyRow("Access Mode", if (file.isReadOnly) "Read-Only" else "Read & Write")
                PropertyRow("Language", language.name)
                PropertyRow("Encoding", file.encodingName)
                PropertyRow("Lines", lineCount.toString())
                PropertyRow("Characters", characterCount.toString())
                PropertyRow("Size", formatBytes(file.fileSize))
                PropertyRow("Modified", dateFormat.format(Date(file.lastModified)))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            modifier = Modifier.weight(0.65f)
        )
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
    return String.format(Locale.US, "%.1f %s", value, units[digitGroups])
}
