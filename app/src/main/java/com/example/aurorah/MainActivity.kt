package com.example.aurorah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aurorah.ui.shell.AurorahAppShell
import com.example.aurorah.ui.theme.AurorahTheme
import com.example.aurorah.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AurorahApplication
        val repository = app.repository

        setContent {
            val themeMode by repository.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)

            AurorahTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AurorahAppShell()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppShellPreview() {
    AurorahTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            // Preview placeholder
        }
    }
}
