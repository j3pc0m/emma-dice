package org.emmadice.app

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.emmadice.app.components.CardItemView
import org.emmadice.app.design.CategoryNeeds
import org.emmadice.app.design.CategoryPeople
import org.emmadice.app.design.Dimensions
import org.emmadice.app.design.PurplePrimary
import org.emmadice.app.navigation.AppScreen
import org.emmadice.app.ui.theme.Borel

@Composable
fun HomeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.emma_dice_logo_v1),
            contentDescription = "Emma Dice",
            modifier = Modifier.size(220.dp)
        )

        Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))

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

        Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))

        HomeActionsGrid(
            onStartClick = {
                navController.navigate(AppScreen.Communication.route)
            },
            onSettingsClick = {
                navController.navigate(AppScreen.Settings.route)
            },
            onAboutClick = {
                navController.navigate(AppScreen.About.route)
            }
        )

        Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))

        Text(
            text = "Versión 0.1.0",
            color = Color.Gray
        )
    }
}

@Composable
private fun HomeActionsGrid(
    onStartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
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

    val rowCount = (3 + columnCount - 1) / columnCount

    val gridHeight =
        Dimensions.ButtonHeight * rowCount +
                Dimensions.MediumSpacing * (rowCount - 1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeight),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement =
            Arrangement.spacedBy(Dimensions.MediumSpacing),
        verticalArrangement =
            Arrangement.spacedBy(Dimensions.MediumSpacing)
    ) {
        item {
            CardItemView(
                title = "Comenzar",
                backgroundColor = CategoryPeople,
                onClick = onStartClick
            )
        }

        item {
            CardItemView(
                title = "Configuración",
                backgroundColor = PurplePrimary,
                onClick = onSettingsClick
            )
        }

        item {
            CardItemView(
                title = "Acerca de",
                backgroundColor = CategoryNeeds,
                onClick = onAboutClick
            )
        }
    }
}