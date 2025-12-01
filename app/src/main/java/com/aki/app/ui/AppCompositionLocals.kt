package com.aki.app.ui

import androidx.compose.runtime.compositionLocalOf
import com.aki.app.viewmodel.AppViewModel

/**
 * CompositionLocal để cung cấp AppViewModel cho các composable con.
 */
val LocalAppViewModel = compositionLocalOf<AppViewModel> {
    error("No AppViewModel provided")
}
