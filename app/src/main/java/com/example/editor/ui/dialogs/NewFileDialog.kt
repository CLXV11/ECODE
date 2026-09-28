package com.example.editor.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.editor.settings.LocalAppStrings

@Composable
fun NewFileDialog(
    initialIsFolder: Boolean = false,
    onConfirm: (name: String, isFolder: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    var isFolder by remember { mutableStateOf(initialIsFolder) }
    var nameInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val validateName: (String) -> Boolean = { name ->
        val trimmed = name.trim()
        when {
            trimmed.isEmpty() -> {
                isError = true
                errorMessage = strings.nameCannotBeEmpty
                false
            }
            trimmed.contains('/') || trimmed.contains('\\') -> {
                isError = true
                errorMessage = strings.nameCannotContainSlash
                false
            }
            else -> {
                isError = false
                errorMessage = ""
                true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isFolder) strings.createNewFolder else strings.createNewFile) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    FilterChip(
                        selected = !isFolder,
                        onClick = { isFolder = false },
                        label = { Text(strings.fileLabel) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = isFolder,
                        onClick = { isFolder = true },
                        label = { Text(strings.folderLabel) }
                    )
                }

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        validateName(it)
                    },
                    placeholder = {
                        Text(if (isFolder) "e.g. scripts" else "e.g. script.py")
                    },
                    isError = isError,
                    supportingText = {
                        if (isError) Text(errorMessage)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (validateName(nameInput)) {
                                onConfirm(nameInput.trim(), isFolder)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validateName(nameInput)) {
                        onConfirm(nameInput.trim(), isFolder)
                    }
                }
            ) {
                Text(strings.createButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
