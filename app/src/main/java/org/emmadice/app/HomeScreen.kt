package org.emmadice.app

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
    val configuration = LocalConfiguration.current

    val isTablet = configuration.smallestScreenWidthDp >= 600
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val hasReducedHeight = configuration.screenHeightDp < 500

    val columnCount = when {
        isTablet && isLandscape -> 3
        isTablet -> 2
        isLandscape -> 2
        else -> 1
    }

    val logoSize = when {
        hasReducedHeight -> 96.dp
        isTablet -> 220.dp
        else -> 160.dp
    }

    val titleSize = when {
        hasReducedHeight -> 30.sp
        isTablet -> 44.sp
        else -> 36.sp
    }

    val sectionSpacing = when {
        hasReducedHeight -> Dimensions.SmallSpacing
        else -> Dimensions.LargeSpacing
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(
            Dimensions.MediumSpacing
        ),
        verticalArrangement = Arrangement.spacedBy(
            Dimensions.MediumSpacing
        )
    ) {
        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        R.drawable.emma_dice_logo_v1
                    ),
                    contentDescription = "Emma Dice",
                    modifier = Modifier.size(logoSize)
                )

                Spacer(modifier = Modifier.height(sectionSpacing))

                Text(
                    text = "Emma Dice",
                    fontFamily = Borel,
                    fontSize = titleSize,
                    color = Color(0xFF4A90E2)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Comunicación accesible para todos",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(sectionSpacing))
            }
        }

        item {
            CardItemView(
                title = "Comenzar",
                backgroundColor = CategoryPeople,
                onClick = {
                    navController.navigate(
                        AppScreen.Communication.route
                    )
                }
            )
        }

        item {
            CardItemView(
                title = "Configuración",
                backgroundColor = PurplePrimary,
                onClick = {
                    navController.navigate(
                        AppScreen.Settings.route
                    )
                }
            )
        }

        item {
            CardItemView(
                title = "Acerca de",
                backgroundColor = CategoryNeeds,
                onClick = {
                    navController.navigate(
                        AppScreen.About.route
                    )
                }
            )
        }

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(sectionSpacing))

                Text(
                    text = "Versión 0.1.0",
                    color = Color.Gray
                )
            }
        }
    }
}