package com.example.editor.io

import android.net.Uri

/**
 * Encapsulates an item to be renamed, either an internal workspace file or an external SAF document.
 */
sealed class RenameTarget {
    abstract val name: String

    data class Workspace(val item: WorkspaceItem) : RenameTarget() {
        override val name: String get() = item.name
    }

    data class SafDocument(val uri: Uri, override val name: String) : RenameTarget()
}
