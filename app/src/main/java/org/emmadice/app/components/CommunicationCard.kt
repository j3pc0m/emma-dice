package org.emmadice.app.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.emmadice.app.design.Dimensions
import org.emmadice.app.design.TextPrimary
import org.emmadice.app.model.CommunicationVisual
import org.emmadice.app.ui.theme.Fredoka

@Composable
fun CommunicationCard(
    title: String,
    visual: CommunicationVisual,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = title
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimensions.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.CardElevation
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.MediumSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                Dimensions.MediumSpacing
            )
        ) {
            CommunicationCardImage(
                visual = visual,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .sizeIn(minHeight = 140.dp)
            )

            Text(
                text = title,
                fontFamily = Fredoka,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun CommunicationCardImage(
    visual: CommunicationVisual,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    when (visual) {
        is CommunicationVisual.DrawableResource -> {
            Image(
                painter = painterResource(visual.resourceId),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = ContentScale.Fit
            )
        }

        is CommunicationVisual.LocalPhoto -> {
            val imageBitmap = remember(visual.absolutePath) {
                BitmapFactory
                    .decodeFile(visual.absolutePath)
                    ?.asImageBitmap()
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}