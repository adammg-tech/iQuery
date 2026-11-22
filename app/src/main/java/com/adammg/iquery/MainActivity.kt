package com.adammg.iquery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.adammg.iquery.ui.navigation.MainScreen
import com.adammg.iquery.ui.theme.IQueryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IQueryTheme {
                MainScreen()
            }
        }
    }
}
