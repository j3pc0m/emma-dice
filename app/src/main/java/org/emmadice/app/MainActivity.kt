package org.emmadice.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.emmadice.app.navigation.NavigationGraph
import org.emmadice.app.ui.theme.EmmaDiceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmmaDiceTheme {
                NavigationGraph()
            }
        }
    }
}