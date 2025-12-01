package com.aki.app.ui.greeting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.aki.app.theme.AkiProxyTheme
import com.aki.app.viewmodel.MainViewModel

@Composable
fun GreetingScreen(viewModel: MainViewModel = hiltViewModel()) {
    val greeting by viewModel.greeting.collectAsState()
    Greeting(name = greeting)
}

/**
 * A composable that displays a greeting.
 *
 * @param name The name to greet.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = name)
    }
}

/**
 * A preview of the [Greeting] composable.
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AkiProxyTheme {
        Greeting("Hello from Preview!")
    }
}
