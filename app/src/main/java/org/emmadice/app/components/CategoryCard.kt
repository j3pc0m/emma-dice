package org.emmadice.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.emmadice.app.ui.theme.Fredoka
import org.emmadice.app.design.Dimensions
import androidx.compose.ui.unit.dp

@Composable
fun CategoryCard(
    title: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.ButtonHeight)
            .clickable { onClick() },

        shape = RoundedCornerShape(Dimensions.CardRadius),

        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.CardElevation
        )
    ) {

        Box(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth(),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = title,
                fontFamily = Fredoka,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}