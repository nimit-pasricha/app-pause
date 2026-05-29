package com.nimitpasricha.pause.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * The app's home and only setup surface: choose which apps to watch, and see
 * the one number worth celebrating (opens you backed out of). Deliberately not
 * a dashboard — there is nothing here to compulsively check.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SetupScreen() }
    }
}
