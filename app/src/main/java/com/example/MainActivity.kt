package com.example
 
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

            MyApplicationTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = false
            ) {
                EditorScreen(viewModel = editorViewModel)
            }
        }
    }
}

