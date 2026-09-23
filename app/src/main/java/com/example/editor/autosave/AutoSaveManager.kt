package com.example.editor.autosave

import android.content.Context
import android.net.Uri
import com.example.editor.io.EditorFile
import com.example.editor.syntax.LanguageRegistry
import com.example.editor.tabs.EditorTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * Snapshot record of an auto-saved tab stored locally to prevent data loss.
 */
data class AutoSaveTabSnapshot(
    val id: String,
    val name: String,
    val filePath: String?,
    val uriString: String?,
    val isInternal: Boolean,
    val languageId: String,
    val cursorPosition: Int,
    val isModified: Boolean,
    val content: String,
    val timestamp: Long
) {
    fun toEditorTab(): EditorTab {
        val uri = uriString?.let { Uri.parse(it) }
        val file = EditorFile(
            uri = uri,
            absolutePath = filePath,
            name = name,
            isInternalWorkspace = isInternal,
            lastModified = timestamp
        )
        val lang = LanguageRegistry.findById(languageId)
        return EditorTab(
            id = id,
            file = file,
            content = content,
            isModified = isModified,
            language = lang,
            cursorPosition = cursorPosition
        )
    }
}

/**
 * AutoSaveManager periodically persists file state and snapshots to secure local storage.
 *
 * It uses atomic file writes and isolated storage to ensure zero data loss in scenarios
 * such as OS memory trims, process death, tab changes, or unexpected restarts.
 */
class AutoSaveManager(context: Context) {

    private val snapshotDir = File(context.filesDir, "autosave_snapshots").apply {
        if (!exists()) mkdirs()
    }
    private val manifestFile = File(snapshotDir, "autosave_manifest.json")
    private val writeMutex = Mutex()

    /**
     * Atomically saves snapshots of all open tabs and active index to local storage.
     * Returns the timestamp (ms) when the save completed.
     */
    suspend fun saveSnapshots(
        tabs: List<EditorTab>,
        activeIndex: Int
    ): Long = withContext(Dispatchers.IO) {
        writeMutex.withLock {
            val timestamp = System.currentTimeMillis()
            if (tabs.isEmpty()) {
                clearAll()
                return@withLock timestamp
            }

            val validIds = mutableSetOf<String>()
            val manifestArray = JSONArray()

            for (tab in tabs) {
                validIds.add(tab.id)
                val snapshotFile = File(snapshotDir, "${tab.id}.snapshot")
                val tmpFile = File(snapshotDir, "${tab.id}.snapshot.tmp")

                // Write tab content atomically
                try {
                    FileOutputStream(tmpFile).use { fos ->
                        OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                            writer.write(tab.content)
                        }
                    }
                    if (tmpFile.exists()) {
                        tmpFile.renameTo(snapshotFile)
                    }
                } catch (e: Exception) {
                    tmpFile.delete()
                }

                // Add to manifest
                val tabJson = JSONObject().apply {
                    put("id", tab.id)
                    put("name", tab.file.name)
                    put("filePath", tab.file.absolutePath)
                    put("uriString", tab.file.uri?.toString())
                    put("isInternal", tab.file.isInternalWorkspace)
                    put("languageId", tab.language.id)
                    put("cursorPosition", tab.cursorPosition)
                    put("isModified", tab.isModified)
                    put("timestamp", timestamp)
                }
                manifestArray.put(tabJson)
            }

            // Write manifest atomically
            val manifestJson = JSONObject().apply {
                put("version", 1)
                put("timestamp", timestamp)
                put("activeIndex", activeIndex)
                put("tabs", manifestArray)
            }

            val tmpManifest = File(snapshotDir, "autosave_manifest.json.tmp")
            try {
                FileOutputStream(tmpManifest).use { fos ->
                    OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                        writer.write(manifestJson.toString())
                    }
                }
                if (tmpManifest.exists()) {
                    tmpManifest.renameTo(manifestFile)
                }
            } catch (e: Exception) {
                tmpManifest.delete()
            }

            // Cleanup any orphaned snapshot files not in the active tabs
            val existingFiles = snapshotDir.listFiles { _, name -> name.endsWith(".snapshot") }
            existingFiles?.forEach { file ->
                val id = file.name.removeSuffix(".snapshot")
                if (id !in validIds) {
                    file.delete()
                }
            }

            timestamp
        }
    }

    /**
     * Checks if there are saved snapshots in local storage.
     */
    fun hasSnapshots(): Boolean {
        return manifestFile.exists() && manifestFile.length() > 0
    }

    /**
     * Loads all tab snapshots from local storage.
     * Returns the list of snapshots and the previously active index.
     */
    suspend fun loadSnapshots(): Pair<List<AutoSaveTabSnapshot>, Int>? = withContext(Dispatchers.IO) {
        writeMutex.withLock {
            if (!manifestFile.exists()) return@withLock null

            try {
                val manifestRaw = manifestFile.readText(StandardCharsets.UTF_8)
                val json = JSONObject(manifestRaw)
                val activeIndex = json.optInt("activeIndex", 0)
                val tabsArray = json.optJSONArray("tabs") ?: return@withLock null

                val list = mutableListOf<AutoSaveTabSnapshot>()
                for (i in 0 until tabsArray.length()) {
                    val obj = tabsArray.getJSONObject(i)
                    val id = obj.getString("id")
                    val snapshotFile = File(snapshotDir, "$id.snapshot")

                    val content = if (snapshotFile.exists()) {
                        snapshotFile.readText(StandardCharsets.UTF_8)
                    } else {
                        ""
                    }

                    list.add(
                        AutoSaveTabSnapshot(
                            id = id,
                            name = obj.getString("name"),
                            filePath = if (obj.has("filePath") && !obj.isNull("filePath")) obj.getString("filePath") else null,
                            uriString = if (obj.has("uriString") && !obj.isNull("uriString")) obj.getString("uriString") else null,
                            isInternal = obj.optBoolean("isInternal", true),
                            languageId = obj.optString("languageId", "plaintext"),
                            cursorPosition = obj.optInt("cursorPosition", 0),
                            isModified = obj.optBoolean("isModified", false),
                            content = content,
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }

                if (list.isNotEmpty()) {
                    Pair(list, activeIndex.coerceIn(0, list.size - 1))
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Discards snapshot files for a single tab when closed.
     */
    suspend fun discardTab(tabId: String) = withContext(Dispatchers.IO) {
        writeMutex.withLock {
            val file = File(snapshotDir, "$tabId.snapshot")
            if (file.exists()) file.delete()
        }
    }

    /**
     * Clears all snapshot files and manifest.
     */
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        writeMutex.withLock {
            val files = snapshotDir.listFiles()
            files?.forEach { it.delete() }
        }
    }
}
