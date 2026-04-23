package com.catalon.guard.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.catalon.guard.presentation.navigation.AppNavHost
import com.catalon.guard.presentation.theme.CatalonGuardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatalonGuardTheme {
                AppNavHost()
            }
        }
    }
}
