package com.example.pertemuan_12new

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.pertemuan_12new.ui.theme.Pertemuan_12NewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkThemeOverride by rememberSaveable { mutableStateOf<Boolean?>(null) }
            val isDarkTheme = darkThemeOverride ?: systemDark

            Pertemuan_12NewTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NotesScreen(
                        modifier = Modifier.padding(innerPadding),
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { darkThemeOverride = !isDarkTheme }
                    )
                }
            }
        }
    }
}

