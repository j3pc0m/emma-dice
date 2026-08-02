package org.emmadice.app.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.emmadice.app.design.Dimensions
import org.emmadice.app.design.TextPrimary
import org.emmadice.app.ui.theme.Fredoka

private val SettingsCardBackground = Color(0xFFEDE7F6)
private val SettingsActionColor = Color(0xFF6A4C93)

@Composable
fun SettingsScreen() {
    val configuration = LocalConfiguration.current

    val isTablet = configuration.smallestScreenWidthDp >= 600
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val columnCount = when {
        isTablet && isLandscape -> 3
        isTablet -> 2
        isLandscape -> 2
        else -> 1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(
            Dimensions.MediumSpacing
        )
    ) {
        Text(
            text = "Configurar tarjetas",
            fontFamily = Fredoka,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(
                Dimensions.MediumSpacing
            ),
            verticalArrangement = Arrangement.spacedBy(
                Dimensions.MediumSpacing
            )
        ) {
            item {
                SettingsCard(title = "Papá")
            }

            item {
                SettingsCard(title = "Mamá")
            }

            item {
                SettingsCard(title = "Agua")
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SettingsCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.CardElevation
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.MediumSpacing),
            verticalArrangement = Arrangement.spacedBy(
                Dimensions.MediumSpacing
            )
        ) {
            Text(
                text = title,
                fontFamily = Fredoka,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimensions.SmallSpacing
                )
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // Cámara: siguiente incremento
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsActionColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Fotografía")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // Audio: siguiente incremento
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsActionColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Audio")
                }
            }
        }
    }
}