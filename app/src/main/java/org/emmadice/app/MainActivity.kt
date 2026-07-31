package org.emmadice.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.emmadice.app.ui.theme.EmmaDiceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmmaDiceTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        ) {
            Text("👩 Mamá", fontSize = 30.sp)
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        ) {
            Text("👨 Papá", fontSize = 30.sp)
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        ) {
            Text("💧 Agua", fontSize = 30.sp)
        }
    }
}