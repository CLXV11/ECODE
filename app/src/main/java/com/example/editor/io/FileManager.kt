package com.example.editor.io

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.example.editor.syntax.LanguageRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * File system manager handling local workspace, SAF documents, and atomic file saving.
 */
class FileManager(private val context: Context) {

    val safManager: SafStorageManager by lazy { SafStorageManager(context) }

    val workspaceRoot: File by lazy {
        val root = File(context.filesDir, "workspace")
        if (!root.exists()) {
            root.mkdirs()
            initializeDefaultWorkspace(root)
        }
        root
    }

    private fun initializeDefaultWorkspace(root: File) {
        try {
            // Python sample
            File(root, "main.py").writeText(
                """# CLXV11 Code Editor
# Python Demo

def greet(name: str) -> str:
    message = f"Hello, {name}!"
    print(message)
    return message

if __name__ == "__main__":
    greet("Developer")
""".trimIndent()
            )

            // JavaScript sample
            File(root, "app.js").writeText(
                """// JavaScript Demo
const appName = "CLXV11 Code Editor";

function calculateStats(items) {
    console.log(`Processing ${'$'}{items.length} items...`);
    return items.reduce((acc, curr) => acc + curr, 0);
}

const scores = [95, 88, 76, 100];
const total = calculateStats(scores);
console.log("Total score:", total);
""".trimIndent()
            )

            // HTML sample
            File(root, "index.html").writeText(
                """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CLXV11 Code Editor</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <header>
        <h1>Welcome to CLXV11</h1>
        <p>A fast, native mobile code editor.</p>
    </header>
</body>
</html>
""".trimIndent()
            )

            // CSS sample
            File(root, "styles.css").writeText(
                """/* CSS Demo */
body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    margin: 0;
    padding: 24px;
    background-color: #0f1117;
    color: #e6edf3;
}

h1 {
    color: #58a6ff;
    font-size: 2rem;
}
""".trimIndent()
            )

            // JSON sample
            File(root, "config.json").writeText(
                """{
  "name": "CLXV11 Code Editor",
  "version": "1.0.0",
  "theme": "dark",
  "editor": {
    "fontSize": 14,
    "tabSize": 4,
    "autoIndent": true
  }
}""".trimIndent()
            )
        } catch (_: Throwable) {
            // Fail silently on initial template creation
        }
    }

    /**
     * Lists files and directories in the specified workspace directory.
     */
    suspend fun listWorkspace(dir: File = workspaceRoot): List<WorkspaceItem> = withContext(Dispatchers.IO) {
        val target = if (dir.exists() && dir.isDirectory) dir else workspaceRoot
        val files = target.listFiles() ?: return@withContext emptyList()

        files.map { file ->
            val isDir = file.isDirectory
            val ext = if (isDir) "" else file.extension
            val langDef = if (isDir) com.example.editor.syntax.LanguageDefinition.PLAIN_TEXT else LanguageRegistry.detectLanguage(file.name)
            val lang = if (isDir) "Folder" else langDef.name
            WorkspaceItem(
                file = file,
                name = file.name,
                isDirectory = isDir,
                extension = ext,
                languageDefinition = langDef,
                languageName = lang,
                sizeBytes = if (isDir) 0L else file.length(),
                lastModified = file.lastModified()
            )
        }.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    }

    /**
     * Creates a new file in the target directory.
     */
    suspend fun createFile(parentDir: File, name: String, initialContent: String = ""): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val safeName = name.trim()
            if (safeName.isEmpty()) throw IllegalArgumentException("File name cannot be empty")
            val newFile = File(parentDir, safeName)
            if (newFile.exists()) throw IllegalStateException("File already exists")
            newFile.writeText(initialContent, Charsets.UTF_8)
            newFile
        }
    }

    /**
     * Creates a new subfolder in the target directory.
     */
    suspend fun createFolder(parentDir: File, name: String): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val safeName = name.trim()
            if (safeName.isEmpty()) throw IllegalArgumentException("Folder name cannot be empty")
            val newFolder = File(parentDir, safeName)
            if (newFolder.exists()) throw IllegalStateException("Folder already exists")
            if (!newFolder.mkdirs()) throw IllegalStateException("Failed to create folder")
            newFolder
        }
    }

    /**
     * Renames a workspace file or directory.
     */
    suspend fun renameItem(target: File, newName: String): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val safeName = newName.trim()
            if (safeName.isEmpty()) throw IllegalArgumentException("New name cannot be empty")
            val parent = target.parentFile ?: workspaceRoot
            val dest = File(parent, safeName)
            if (dest.exists()) throw IllegalStateException("Destination already exists")
            if (!target.renameTo(dest)) throw IllegalStateException("Rename failed")
            dest
        }
    }

    /**
     * Deletes a file or directory recursively.
     */
    suspend fun deleteItem(target: File): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            if (target.isDirectory) {
                target.deleteRecursively()
            } else {
                target.delete()
            }
        }
    }

    /**
     * Reads a workspace file and detects its encoding.
     */
    suspend fun readFile(file: File): Result<Pair<EditorFile, String>> = withContext(Dispatchers.IO) {
        runCatching {
            if (!file.exists()) throw IllegalArgumentException("File does not exist")
            val bytes = file.readBytes()
            val detected = EncodingDetector.detect(bytes)
            val content = EncodingDetector.decode(bytes, detected)

            val editorFile = EditorFile(
                uri = Uri.fromFile(file),
                absolutePath = file.absolutePath,
                name = file.name,
                isInternalWorkspace = true,
                encodingName = detected.name,
                hasBom = detected.hasBom,
                fileSize = file.length(),
                lastModified = file.lastModified()
            )
            Pair(editorFile, content)
        }
    }

    /**
     * Atomically saves a workspace file to avoid corruption during potential crashes.
     */
    suspend fun saveWorkspaceFile(
        file: File,
        content: String,
        encodingName: String = "UTF-8",
        hasBom: Boolean = false
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val parent = file.parentFile ?: workspaceRoot
            val tempFile = File(parent, ".${file.name}.${System.currentTimeMillis()}.tmp")

            val charset = when (encodingName) {
                "UTF-16 BE" -> EncodingDetector.UTF_16BE
                "UTF-16 LE" -> EncodingDetector.UTF_16LE
                "ASCII" -> EncodingDetector.US_ASCII
                "Windows-1252" -> EncodingDetector.WINDOWS_1252
                else -> EncodingDetector.UTF_8
            }
            val detected = DetectedEncoding(charset, hasBom, encodingName)
            val bytes = EncodingDetector.encode(content, detected)

            // Write to temp file and sync to disk
            FileOutputStream(tempFile).use { fos ->
                fos.write(bytes)
                fos.flush()
                fos.fd.sync()
            }

            // Atomic rename
            if (file.exists()) {
                file.delete()
            }
            if (!tempFile.renameTo(file)) {
                // Fallback copy if rename fails
                tempFile.copyTo(file, overwrite = true)
                tempFile.delete()
            }
        }
    }

    /**
     * Reads a file chosen through Storage Access Framework (SAF).
     */
    suspend fun readSafUri(uri: Uri): Result<Pair<EditorFile, String>> = safManager.openDocument(uri)

    /**
     * Saves content back to a SAF URI.
     */
    suspend fun saveSafUri(
        uri: Uri,
        content: String,
        encodingName: String = "UTF-8",
        hasBom: Boolean = false
    ): Result<Unit> = safManager.writeDocument(uri, content, encodingName, hasBom)
}
