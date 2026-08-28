package com.bruitage.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.bruitage.app.ui.SoundBoardScreen
import com.bruitage.app.ui.theme.BruitageTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SoundBoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BruitageTheme {
                SoundBoardScreen(viewModel = viewModel)
            }
        }
    }
}
