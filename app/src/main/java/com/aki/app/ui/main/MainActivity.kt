package com.aki.app.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import com.aki.app.navigation.AppNavigation
import com.aki.app.theme.AkiProxyTheme
import com.aki.app.ui.LocalAppViewModel
import com.aki.app.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity for the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalAppViewModel provides appViewModel) {
                AkiProxyTheme {
                    AppNavigation()
                }
            }
        }
    }
}
