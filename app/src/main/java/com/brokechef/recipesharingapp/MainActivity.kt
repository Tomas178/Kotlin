package com.brokechef.recipesharingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import com.brokechef.recipesharingapp.ui.components.toast.AppToast
import com.brokechef.recipesharingapp.ui.screens.MainScreen
import com.brokechef.recipesharingapp.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Box {
                    MainScreen()
                    AppToast()
                }
            }
        }
    }
}
