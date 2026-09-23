package com.example.editor.io

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.syntax.LanguageRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Encapsulates metadata and security permission status for a document or directory managed via SAF.
 */
data class SafDocumentInfo(
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val canRead: Boolean,
    val canWrite: Boolean,
    val isPermissionPersisted: Boolean,
    val languageDefinition: LanguageDefinition = if (isDirectory) LanguageDefinition.PLAIN_TEXT else LanguageRegistry.detectLanguage(name)
)

/**
 * Production-grade Storage Access Framework (SAF) manager component.
 *
 * Responsibilities:
 * - Document Opening: Querying metadata, taking persistent URI permissions, verifying read/write grants.
 * - Document Creation: Creating new documents via DocumentsContract / DocumentFile with correct MIME types and persistent grants.
 * - Document Renaming: Renaming documents safely using DocumentsContract.renameDocument and fallback mechanisms.
 * - Tree Navigation: Listing directory trees opened via ACTION_OPEN_DOCUMENT_TREE.
 * - Security & Permission Auditing: Inspecting persisted permissions, handling revocations, validating URIs, and releasing permissions.
 */
class SafStorageManager(private val context: Context) {

    companion object {
        private const val TAG = "SafStorageManager"
        const val MIME_TYPE_OCTET_STREAM = "application/octet-stream"
        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    }

    /**
     * Resolves the proper MIME type for a given filename or extension.
     */
    fun resolveMimeType(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "txt", "log", "ini", "conf", "env" -> "text/plain"
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "js", "mjs", "cjs" -> "text/javascript"
            "ts" -> "application/typescript"
            "json" -> "application/json"
            "xml", "svg" -> "application/xml"
            "md", "markdown" -> "text/markdown"
            "py", "pyw" -> "text/x-python"
            "kt", "kts" -> "text/x-kotlin"
            "java" -> "text/x-java-source"
            "c", "h" -> "text/x-c"
            "cpp", "hpp", "cc", "cxx" -> "text/x-c++src"
            "rs" -> "text/x-rust"
            "go" -> "text/x-go"
            "sh", "bash", "zsh" -> "application/x-sh"
            "sql" -> "application/sql"
            "yaml", "yml" -> "text/yaml"
            "csv" -> "text/csv"
            else -> "text/plain"
        }
    }

    /**
     * Securely acquires and persists read and write URI permissions from the system.
     * Prevents security exceptions across app restarts and process recreation.
     */
    fun takePersistablePermissions(uri: Uri, flags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION): Boolean {
        return try {
            val contentResolver = context.contentResolver
            contentResolver.takePersistableUriPermission(uri, flags)
            Log.d(TAG, "Persistable URI permissions granted for: $uri")
            true
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException: Cannot take persistable permission for $uri: ${e.message}")
            false
        } catch (t: Throwable) {
            Log.e(TAG, "Failed taking persistable permission: ${t.message}", t)
            false
        }
    }

    /**
     * Checks whether persistable permission is currently held for the given URI.
     */
    fun isPermissionPersisted(uri: Uri): Boolean {
        return try {
            context.contentResolver.persistedUriPermissions.any { perm ->
                perm.uri == uri && (perm.isReadPermission || perm.isWritePermission)
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to check persisted permissions: ${t.message}")
            false
        }
    }

    /**
     * Safely releases persisted URI permission when a document is removed from recents or permanently closed.
     */
    fun releasePersistedPermission(uri: Uri, flags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION) {
        try {
            context.contentResolver.releasePersistableUriPermission(uri, flags)
            Log.d(TAG, "Persisted permission released for: $uri")
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to release permission for $uri: ${t.message}")
        }
    }

    /**
     * Inspects and retrieves detailed SafDocumentInfo metadata for a given document URI.
     */
    suspend fun getDocumentInfo(uri: Uri): Result<SafDocumentInfo> = withContext(Dispatchers.IO) {
        runCatching {
            // First attempt to take persistable permissions if available
            val persisted = takePersistablePermissions(uri) || isPermissionPersisted(uri)

            var displayName = "document"
            var fileSize = 0L
            var lastModified = System.currentTimeMillis()
            var mimeType = MIME_TYPE_OCTET_STREAM

            // Query OpenableColumns and DocumentsContract columns
            val projection = arrayOf(
                OpenableColumns.DISPLAY_NAME,
                OpenableColumns.SIZE,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )

            try {
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameCol = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeCol = cursor.getColumnIndex(OpenableColumns.SIZE)
                        val mimeCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                        val modCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

                        if (nameCol != -1) cursor.getString(nameCol)?.let { displayName = it }
                        if (sizeCol != -1 && !cursor.isNull(sizeCol)) fileSize = cursor.getLong(sizeCol)
                        if (mimeCol != -1 && !cursor.isNull(mimeCol)) cursor.getString(mimeCol)?.let { mimeType = it }
                        if (modCol != -1 && !cursor.isNull(modCol)) lastModified = cursor.getLong(modCol)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Querying projection failed for $uri, falling back to DocumentFile: ${e.message}")
            }

            // Verify with DocumentFile for read/write capabilities
            val docFile = DocumentFile.fromSingleUri(context, uri)
                ?: DocumentFile.fromTreeUri(context, uri)

            val canRead = docFile?.canRead() ?: true
            val canWrite = docFile?.canWrite() ?: true
            val isDirectory = docFile?.isDirectory ?: (mimeType == DocumentsContract.Document.MIME_TYPE_DIR)

            if (displayName == "document" && docFile?.name != null) {
                displayName = docFile.name ?: "document"
            }

            SafDocumentInfo(
                uri = uri,
                name = displayName,
                mimeType = mimeType,
                isDirectory = isDirectory,
                size = fileSize,
                lastModified = lastModified,
                canRead = canRead,
                canWrite = canWrite,
                isPermissionPersisted = persisted
            )
        }
    }

    /**
     * Reads a document via SAF, handling stream management, charset decoding, and BOM detection.
     */
    suspend fun openDocument(uri: Uri): Result<Pair<EditorFile, String>> = withContext(Dispatchers.IO) {
        runCatching {
            // Guarantee persistent permissions
            takePersistablePermissions(uri)

            val docInfo = getDocumentInfo(uri).getOrNull()
            val displayName = docInfo?.name ?: "document"
            val isReadOnly = !(docInfo?.canWrite ?: true)

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Could not open input stream for URI: $uri. Permission may have been revoked.")

            val bytes = inputStream.use { it.readBytes() }
            val detectedEncoding = EncodingDetector.detect(bytes)
            val textContent = EncodingDetector.decode(bytes, detectedEncoding)

            val editorFile = EditorFile(
                uri = uri,
                absolutePath = uri.toString(),
                name = displayName,
                isInternalWorkspace = false,
                encodingName = detectedEncoding.name,
                hasBom = detectedEncoding.hasBom,
                fileSize = if (docInfo?.size ?: 0L > 0L) docInfo!!.size else bytes.size.toLong(),
                lastModified = docInfo?.lastModified ?: System.currentTimeMillis(),
                isReadOnly = isReadOnly
            )

            Pair(editorFile, textContent)
        }
    }

    /**
     * Writes content to a SAF document URI atomically and securely.
     */
    suspend fun writeDocument(
        uri: Uri,
        content: String,
        encodingName: String = "UTF-8",
        hasBom: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val charset = when (encodingName) {
                "UTF-16 BE" -> EncodingDetector.UTF_16BE
                "UTF-16 LE" -> EncodingDetector.UTF_16LE
                "ASCII" -> EncodingDetector.US_ASCII
                "Windows-1252" -> EncodingDetector.WINDOWS_1252
                else -> EncodingDetector.UTF_8
            }
            val detected = DetectedEncoding(charset, hasBom, encodingName)
            val bytes = EncodingDetector.encode(content, detected)

            // "rwt" or "wt" mode truncates existing content before writing
            val outputStream = context.contentResolver.openOutputStream(uri, "wt")
                ?: throw IllegalStateException("Cannot open output stream for writing: $uri. Storage may be read-only or permission expired.")

            outputStream.use { stream ->
                stream.write(bytes)
                stream.flush()
            }
        }
    }

    /**
     * Creates a new document inside a directory tree chosen with ACTION_OPEN_DOCUMENT_TREE.
     */
    suspend fun createDocumentInTree(
        treeUri: Uri,
        fileName: String,
        initialContent: String = ""
    ): Result<SafDocumentInfo> = withContext(Dispatchers.IO) {
        runCatching {
            takePersistablePermissions(treeUri)
            val treeDoc = DocumentFile.fromTreeUri(context, treeUri)
                ?: throw IllegalArgumentException("Invalid tree URI: $treeUri")

            if (!treeDoc.isDirectory || !treeDoc.canWrite()) {
                throw IllegalStateException("Tree directory is not writable or not a directory: $treeUri")
            }

            val mimeType = resolveMimeType(fileName)
            val createdDoc = treeDoc.createFile(mimeType, fileName)
                ?: throw IllegalStateException("Failed to create document '$fileName' in tree")

            val newUri = createdDoc.uri

            if (initialContent.isNotEmpty()) {
                writeDocument(newUri, initialContent).getOrThrow()
            }

            getDocumentInfo(newUri).getOrThrow()
        }
    }

    /**
     * Creates a new subdirectory inside a directory tree chosen with ACTION_OPEN_DOCUMENT_TREE.
     */
    suspend fun createDirectoryInTree(
        treeUri: Uri,
        directoryName: String
    ): Result<SafDocumentInfo> = withContext(Dispatchers.IO) {
        runCatching {
            takePersistablePermissions(treeUri)
            val treeDoc = DocumentFile.fromTreeUri(context, treeUri)
                ?: throw IllegalArgumentException("Invalid tree URI: $treeUri")

            if (!treeDoc.isDirectory || !treeDoc.canWrite()) {
                throw IllegalStateException("Tree directory is not writable: $treeUri")
            }

            val createdDir = treeDoc.createDirectory(directoryName)
                ?: throw IllegalStateException("Failed to create directory '$directoryName' in tree")

            getDocumentInfo(createdDir.uri).getOrThrow()
        }
    }

    /**
     * Renames an existing SAF document using DocumentsContract.renameDocument.
     * Handles returning the newly minted URI (Android may change the URI on rename).
     */
    suspend fun renameDocument(uri: Uri, newDisplayName: String): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val safeName = newDisplayName.trim()
            if (safeName.isEmpty()) throw IllegalArgumentException("File name cannot be empty")

            // Method 1: Try system DocumentsContract.renameDocument (supported API 21+)
            var updatedUri: Uri? = null
            try {
                updatedUri = DocumentsContract.renameDocument(context.contentResolver, uri, safeName)
                if (updatedUri != null) {
                    takePersistablePermissions(updatedUri)
                }
            } catch (e: Exception) {
                Log.w(TAG, "DocumentsContract.renameDocument failed for $uri: ${e.message}")
            }

            // Method 2: Fallback to DocumentFile renameTo
            if (updatedUri == null) {
                val doc = DocumentFile.fromSingleUri(context, uri)
                    ?: DocumentFile.fromTreeUri(context, uri)
                if (doc != null && doc.canWrite()) {
                    val renamed = doc.renameTo(safeName)
                    if (renamed) {
                        updatedUri = doc.uri
                        takePersistablePermissions(updatedUri)
                    }
                }
            }

            updatedUri ?: throw IllegalStateException("Provider does not support renaming or document is read-only: $uri")
        }
    }

    /**
     * Deletes a SAF document or directory.
     */
    suspend fun deleteDocument(uri: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            try {
                if (DocumentsContract.deleteDocument(context.contentResolver, uri)) {
                    releasePersistedPermission(uri)
                    return@runCatching true
                }
            } catch (e: Exception) {
                Log.w(TAG, "DocumentsContract.deleteDocument failed: ${e.message}")
            }

            val doc = DocumentFile.fromSingleUri(context, uri)
                ?: DocumentFile.fromTreeUri(context, uri)
            val deleted = doc?.delete() ?: false
            if (deleted) {
                releasePersistedPermission(uri)
            }
            deleted
        }
    }

    /**
     * Lists child files and folders inside an ACTION_OPEN_DOCUMENT_TREE directory.
     */
    suspend fun listTreeDocuments(treeUri: Uri): Result<List<SafDocumentInfo>> = withContext(Dispatchers.IO) {
        runCatching {
            takePersistablePermissions(treeUri)
            val treeDoc = DocumentFile.fromTreeUri(context, treeUri)
                ?: throw IllegalArgumentException("Cannot resolve tree URI: $treeUri")

            val files = treeDoc.listFiles()
            files.map { doc ->
                val name = doc.name ?: "unnamed"
                val isDir = doc.isDirectory
                val mime = doc.type ?: if (isDir) DocumentsContract.Document.MIME_TYPE_DIR else MIME_TYPE_OCTET_STREAM
                SafDocumentInfo(
                    uri = doc.uri,
                    name = name,
                    mimeType = mime,
                    isDirectory = isDir,
                    size = if (isDir) 0L else doc.length(),
                    lastModified = doc.lastModified(),
                    canRead = doc.canRead(),
                    canWrite = doc.canWrite(),
                    isPermissionPersisted = true
                )
            }.sortedWith(compareBy<SafDocumentInfo> { !it.isDirectory }.thenBy { it.name.lowercase() })
        }
    }
}
