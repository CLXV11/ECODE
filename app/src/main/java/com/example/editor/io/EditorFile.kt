package com.example.editor.io

import android.net.Uri

/**
 * Representation of a file loaded in the editor.
 */
data class EditorFile(
    val uri: Uri? = null,
    val absolutePath: String? = null,
    val name: String,
    val isInternalWorkspace: Boolean = true,
    val encodingName: String = "UTF-8",
    val hasBom: Boolean = false,
    val fileSize: Long = 0L,
    val lastModified: Long = System.currentTimeMillis(),
    val isReadOnly: Boolean = false
)
