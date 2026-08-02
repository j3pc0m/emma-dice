package org.emmadice.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.emmadice.app.components.CategoryCard
import org.emmadice.app.data.CategoriesData
import org.emmadice.app.design.Dimensions

@Composable
fun CategoriesScreen(
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.ScreenPadding),

        verticalArrangement = Arrangement.spacedBy(Dimensions.MediumSpacing)
    ) {

        Text(
            text = "Categorías",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(Dimensions.SmallSpacing))

        CategoriesData.categories.forEach { category ->

            CategoryCard(

                title = category.name,

                backgroundColor = category.color,

                onClick = {

                    // Próximamente navegaremos
                    // navController.navigate(...)
                }
            )
        }
    }
}