package com.bruitage.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bruitage.app.ui.SoundBoardScreen
import com.bruitage.app.ui.theme.BruitageTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SoundBoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.settings.collectAsState()

            LaunchedEffect(settings.keepScreenOn, settings.brightness) {
                if (settings.keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                window.attributes = window.attributes.apply {
                    screenBrightness = settings.brightness.coerceIn(0.05f, 1f)
                }
            }

            BruitageTheme(darkTheme = settings.darkTheme) {
                SoundBoardScreen(viewModel = viewModel)
            }
        }
    }
}
