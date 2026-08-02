package org.emmadice.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.emmadice.app.R
import org.emmadice.app.components.CommunicationCard
import org.emmadice.app.design.CategoryNeeds
import org.emmadice.app.design.CategoryPeople
import org.emmadice.app.design.Dimensions
import org.emmadice.app.model.CommunicationVisual

@Composable
fun CommunicationScreen() {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimensions.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.MediumSpacing),
        verticalArrangement = Arrangement.spacedBy(Dimensions.MediumSpacing)
    ) {

        item {
            CommunicationCard(
                title = "Papá",
                visual = CommunicationVisual.DrawableResource(
                    resourceId = R.drawable.emma_dice_logo_v1
                ),
                backgroundColor = CategoryPeople,
                onClick = {
                    // El audio se conectará en OT-003
                }
            )
        }

        item {
            CommunicationCard(
                title = "Mamá",
                visual = CommunicationVisual.DrawableResource(
                    resourceId = R.drawable.emma_dice_logo_v1
                ),
                backgroundColor = CategoryPeople,
                onClick = {
                    // El audio se conectará en OT-003
                }
            )
        }

        item {
            CommunicationCard(
                title = "Agua",
                visual = CommunicationVisual.DrawableResource(
                    resourceId = R.drawable.emma_dice_logo_v1
                ),
                backgroundColor = CategoryNeeds,
                onClick = {
                    // El audio se conectará en OT-003
                }
            )
        }
    }
}