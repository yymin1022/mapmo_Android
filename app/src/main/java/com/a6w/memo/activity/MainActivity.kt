package com.a6w.memo.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.a6w.memo.navigation.MapmoNavHost
import com.a6w.memo.ui.theme.LocationMemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LocationMemoTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                ) { innerPadding ->
                    val navController = rememberNavController()

                    // Mapmo Nav Host
                    MapmoNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding),
                        navController = navController,
                    )
                }
            }
        }
    }
}