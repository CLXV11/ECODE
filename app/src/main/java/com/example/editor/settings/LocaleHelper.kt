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

    fun isSystemArabic(context: Context? = null): Boolean {
        try {
            val sysLocales = Resources.getSystem().configuration.locales
            for (i in 0 until sysLocales.size()) {
                val lang = sysLocales[i]?.language?.lowercase() ?: ""
                if (lang == "ar" || lang.startsWith("ar")) return true
            }
        } catch (_: Exception) {}

        try {
            if (context != null) {
                val appLocales = context.resources.configuration.locales
                for (i in 0 until appLocales.size()) {
                    val lang = appLocales[i]?.language?.lowercase() ?: ""
                    if (lang == "ar" || lang.startsWith("ar")) return true
                }
            }
        } catch (_: Exception) {}

        val defaultLang = Locale.getDefault().language.lowercase()
        return defaultLang == "ar" || defaultLang.startsWith("ar")
    }

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
                else -> {
                    if (isSystemArabic(context)) Locale("ar") else Locale("en")
                }
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

    fun isRtl(languageCode: String, context: Context? = null): Boolean {
        return when (languageCode.lowercase()) {
            "ar" -> true
            "en" -> false
            else -> isSystemArabic(context)
        }
    }
}
