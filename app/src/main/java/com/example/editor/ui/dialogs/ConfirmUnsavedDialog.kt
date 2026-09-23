package com.example.editor.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmUnsavedDialog(
    fileName: String,
    onSave: () -> Unit,
    onDontSave: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Unsaved Changes") },
        text = {
            Text("The file \"$fileName\" has unsaved changes. Do you want to save your changes before closing?")
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            FilledTonalButton(onClick = onDontSave) {
                Text("Don't Save")
            }
        },
        icon = {
            // Cancel button
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
