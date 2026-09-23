package com.example.editor.io

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

data class DetectedEncoding(
    val charset: Charset,
    val hasBom: Boolean,
    val name: String
)

/**
 * Detects and handles various text file encodings including UTF-8, UTF-8 BOM, UTF-16, ASCII, Windows-1252.
 */
object EncodingDetector {

    val UTF_8: Charset = StandardCharsets.UTF_8
    val UTF_16LE: Charset = StandardCharsets.UTF_16LE
    val UTF_16BE: Charset = StandardCharsets.UTF_16BE
    val US_ASCII: Charset = StandardCharsets.US_ASCII
    val WINDOWS_1252: Charset = Charset.forName("windows-1252")

    fun detect(bytes: ByteArray): DetectedEncoding {
        if (bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
            return DetectedEncoding(UTF_8, true, "UTF-8 BOM")
        }
        if (bytes.size >= 2 && bytes[0] == 0xFE.toByte() && bytes[1] == 0xFF.toByte()) {
            return DetectedEncoding(UTF_16BE, true, "UTF-16 BE")
        }
        if (bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xFE.toByte()) {
            return DetectedEncoding(UTF_16LE, true, "UTF-16 LE")
        }

        // Test if bytes are valid UTF-8
        if (isValidUtf8(bytes)) {
            return DetectedEncoding(UTF_8, false, "UTF-8")
        }

        // Check for ASCII
        var allAscii = true
        for (b in bytes) {
            if (b.toInt() and 0x80 != 0) {
                allAscii = false
                break
            }
        }
        if (allAscii) {
            return DetectedEncoding(US_ASCII, false, "ASCII")
        }

        // Default fallback
        return DetectedEncoding(WINDOWS_1252, false, "Windows-1252")
    }

    fun decode(bytes: ByteArray, detected: DetectedEncoding): String {
        val offset = if (detected.hasBom) {
            when (detected.name) {
                "UTF-8 BOM" -> 3
                "UTF-16 BE", "UTF-16 LE" -> 2
                else -> 0
            }
        } else {
            0
        }
        val length = (bytes.size - offset).coerceAtLeast(0)
        return String(bytes, offset, length, detected.charset)
    }

    fun encode(text: String, detected: DetectedEncoding): ByteArray {
        val raw = text.toByteArray(detected.charset)
        if (!detected.hasBom) return raw

        val bom = when (detected.name) {
            "UTF-8 BOM" -> byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
            "UTF-16 BE" -> byteArrayOf(0xFE.toByte(), 0xFF.toByte())
            "UTF-16 LE" -> byteArrayOf(0xFF.toByte(), 0xFE.toByte())
            else -> byteArrayOf()
        }
        return bom + raw
    }

    private fun isValidUtf8(bytes: ByteArray): Boolean {
        var i = 0
        val len = bytes.size
        while (i < len) {
            val b = bytes[i].toInt() and 0xFF
            when {
                b and 0x80 == 0 -> i += 1 // 1-byte ASCII
                b and 0xE0 == 0xC0 -> {   // 2-byte sequence
                    if (i + 1 >= len || (bytes[i + 1].toInt() and 0xC0 != 0x80)) return false
                    i += 2
                }
                b and 0xF0 == 0xE0 -> {   // 3-byte sequence
                    if (i + 2 >= len || (bytes[i + 1].toInt() and 0xC0 != 0x80) || (bytes[i + 2].toInt() and 0xC0 != 0x80)) return false
                    i += 3
                }
                b and 0xF8 == 0xF0 -> {   // 4-byte sequence
                    if (i + 3 >= len || (bytes[i + 1].toInt() and 0xC0 != 0x80) || (bytes[i + 2].toInt() and 0xC0 != 0x80) || (bytes[i + 3].toInt() and 0xC0 != 0x80)) return false
                    i += 4
                }
                else -> return false
            }
        }
        return true
    }
}
