package com.example.editor.settings

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Utility to manage dynamic application locales, RTL layout direction,
 * and language persistence across system configurations.
 */
object LocaleHelper {

    fun applyLocale(context: Context, languageCode: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                if (languageCode == "system") {
                    localeManager?.applicationLocales = LocaleList.getEmptyLocaleList()
                } else {
                    localeManager?.applicationLocales = LocaleList.forLanguageTags(languageCode)
                }
            }

            val localeListCompat = if (languageCode == "system") {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(languageCode)
            }
            AppCompatDelegate.setApplicationLocales(localeListCompat)

            // Direct Resources configuration fallback for instant UI recomposition
            val targetLocale = when (languageCode) {
                "ar" -> Locale("ar")
                "en" -> Locale("en")
                else -> Resources.getSystem().configuration.locales[0] ?: Locale.getDefault()
            }
            Locale.setDefault(targetLocale)

            val config = Configuration(context.resources.configuration)
            config.setLocale(targetLocale)
            config.setLayoutDirection(targetLocale)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    fun isRtl(languageCode: String): Boolean {
        return when (languageCode) {
            "ar" -> true
            "en" -> false
            else -> {
                val current = Locale.getDefault().language
                current == "ar" || current.startsWith("ar_")
            }
        }
    }
}
