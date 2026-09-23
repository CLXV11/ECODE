package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.editor.settings.LocaleHelper
import com.example.editor.ui.EditorScreen
import com.example.editor.ui.EditorViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val editorViewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by editorViewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Dynamically synchronize system/app locale when language setting changes
            LaunchedEffect(uiState.settings.uiLanguage) {
                LocaleHelper.applyLocale(context, uiState.settings.uiLanguage)
            }

            // Dynamically compute RTL / LTR layout direction
            val isRtl = remember(uiState.settings.uiLanguage) {
                LocaleHelper.isRtl(uiState.settings.uiLanguage)
            }
            val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MyApplicationTheme(
                    themeMode = uiState.settings.themeMode,
                    dynamicColor = false
                ) {
                    EditorScreen(viewModel = editorViewModel)
                }
            }
        }
    }
}
