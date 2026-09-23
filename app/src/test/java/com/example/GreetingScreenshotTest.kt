package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.editor.io.EditorFile
import com.example.editor.syntax.LanguageDefinition
import com.example.editor.tabs.EditorTab
import com.example.editor.ui.components.EditorTabBar
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun editor_tab_bar_screenshot() {
    val sampleTabs = listOf(
      EditorTab(
        file = EditorFile(name = "main.py"),
        content = "print('Hello')",
        language = com.example.editor.syntax.LanguageRegistry.detectLanguage("main.py")
      ),
      EditorTab(
        file = EditorFile(name = "index.html"),
        content = "<html></html>",
        language = com.example.editor.syntax.LanguageRegistry.detectLanguage("index.html")
      )
    )

    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        EditorTabBar(
          tabs = sampleTabs,
          activeIndex = 0,
          onTabSelected = {},
          onTabClose = {},
          onNewTabClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

