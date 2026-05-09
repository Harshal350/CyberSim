package com.example.cybersim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.cybersim.ui.navigation.CyberSimNavGraph
import com.example.cybersim.ui.theme.AppGradientBackground
import com.example.cybersim.ui.theme.CyberSimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberSimTheme {
                AppGradientBackground {
                    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                        CyberSimNavGraph()
                    }
                }
            }
        }
    }
}
