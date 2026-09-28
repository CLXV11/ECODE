package com.example.editor.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.editor.settings.LocalAppStrings

@Composable
fun GoToLineDialog(
    totalLines: Int,
    currentLine: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    var lineInput by remember { mutableStateOf(currentLine.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.goToLine) },
        text = {
            Column {
                Text(String.format(strings.goToLinePrompt, totalLines))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lineInput,
                    onValueChange = {
                        lineInput = it
                        val num = it.toIntOrNull()
                        isError = num == null || num < 1 || num > totalLines
                    },
                    isError = isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val line = lineInput.toIntOrNull()
                            if (line != null && line in 1..totalLines) {
                                onConfirm(line)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (isError) {
                            Text(strings.invalidLineNumber)
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val line = lineInput.toIntOrNull()
                    if (line != null && line in 1..totalLines) {
                        onConfirm(line)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text(strings.goButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
