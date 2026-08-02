package org.emmadice.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.emmadice.app.ui.theme.Borel
import org.emmadice.app.navigation.AppScreen

@Composable
fun HomeScreen(
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.emma_dice_logo_v1),
            contentDescription = "Emma Dice",
            modifier = Modifier.size(220.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Emma Dice",
            fontFamily = Borel,
            fontSize = 44.sp,
            color = Color(0xFF4A90E2)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Comunicación accesible para todos",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(AppScreen.Communication.route)
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB388EB)
            )
        ) {
            Text(
                text = "Comenzar",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Próximamente:
                // navController.navigate("settings")
            },
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Configuración",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Próximamente:
                // navController.navigate("about")
            },
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Acerca de",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Versión 0.1.0",
            color = Color.Gray
        )
    }
}