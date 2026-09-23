package com.example.editor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.editor.settings.LocalAppStrings

@Composable
fun SearchReplaceBar(
    query: String,
    replaceText: String,
    matchCase: Boolean,
    wholeWord: Boolean,
    useRegex: Boolean,
    currentMatchIndex: Int,
    totalMatches: Int,
    onQueryChange: (String) -> Unit,
    onReplaceChange: (String) -> Unit,
    onToggleMatchCase: () -> Unit,
    onToggleWholeWord: () -> Unit,
    onToggleRegex: () -> Unit,
    onNextMatch: () -> Unit,
    onPreviousMatch: () -> Unit,
    onReplaceCurrent: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Find Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(strings.findPlaceholder, fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onNextMatch() }),
                    modifier = Modifier
                        .testTag("search_query_input")
                        .weight(1f)
                        .height(48.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Match count indicator
                Text(
                    text = if (totalMatches > 0) "${currentMatchIndex + 1}/$totalMatches" else "0/0",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (totalMatches > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = onPreviousMatch,
                    enabled = totalMatches > 0,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = strings.previousMatch)
                }

                IconButton(
                    onClick = onNextMatch,
                    enabled = totalMatches > 0,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = strings.nextMatch)
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = strings.closeSearch)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Replace Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replaceText,
                    onValueChange = onReplaceChange,
                    placeholder = { Text(strings.replacePlaceholder, fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .testTag("replace_query_input")
                        .weight(1f)
                        .height(48.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                FilledTonalButton(
                    onClick = onReplaceCurrent,
                    enabled = totalMatches > 0,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(38.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                ) {
                    Text(strings.replace, fontSize = 12.sp, maxLines = 1, softWrap = false)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = onReplaceAll,
                    enabled = totalMatches > 0,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(38.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                ) {
                    Text(strings.replaceAll, fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Options Row (Match Case, Whole Word, Regex) with horizontal scrolling to prevent wrapping / stacking
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = matchCase,
                    onClick = onToggleMatchCase,
                    label = { Text(strings.matchCase, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                )

                FilterChip(
                    selected = wholeWord,
                    onClick = onToggleWholeWord,
                    label = { Text(strings.wholeWord, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                )

                FilterChip(
                    selected = useRegex,
                    onClick = onToggleRegex,
                    label = { Text(strings.regex, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                )
            }
        }
    }
}
