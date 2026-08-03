package org.emmadice.app.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
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
import androidx.exifinterface.media.ExifInterface
import org.emmadice.app.design.Dimensions
import org.emmadice.app.design.TextPrimary
import org.emmadice.app.model.CommunicationVisual
import org.emmadice.app.ui.theme.Fredoka
import java.io.File

private val PlaceholderBackground = Color(0xFFF3F0F7)
private val PlaceholderTextColor = Color(0xFF6A4C93)

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
            defaultElevation = 0.dp
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
                    .aspectRatio(1f)
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
            val photoFile = remember(visual.absolutePath) {
                File(visual.absolutePath)
            }

            val imageBitmap = remember(
                visual.absolutePath,
                photoFile.lastModified(),
                photoFile.length()
            ) {
                decodePhotoWithCorrectOrientation(
                    absolutePath = photoFile.absolutePath
                )?.asImageBitmap()
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                PlaceholderImage(
                    initial = titleInitial(contentDescription),
                    modifier = modifier
                )
            }
        }

        is CommunicationVisual.Placeholder -> {
            PlaceholderImage(
                initial = visual.initial,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun PlaceholderImage(
    initial: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = PlaceholderBackground,
                shape = RoundedCornerShape(Dimensions.CardRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontFamily = Fredoka,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = PlaceholderTextColor
        )
    }
}

private fun titleInitial(title: String): String {
    return title
        .trim()
        .firstOrNull()
        ?.uppercase()
        ?: "?"
}

private fun decodePhotoWithCorrectOrientation(
    absolutePath: String
): Bitmap? {
    val options = BitmapFactory.Options().apply {
        inSampleSize = 4
    }

    val originalBitmap =
        BitmapFactory.decodeFile(absolutePath, options)
            ?: return null

    val exif = ExifInterface(absolutePath)

    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val rotationDegrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    if (rotationDegrees == 0f) {
        return originalBitmap
    }

    val matrix = Matrix().apply {
        postRotate(rotationDegrees)
    }

    val rotatedBitmap = Bitmap.createBitmap(
        originalBitmap,
        0,
        0,
        originalBitmap.width,
        originalBitmap.height,
        matrix,
        true
    )

    if (rotatedBitmap !== originalBitmap) {
        originalBitmap.recycle()
    }

    return rotatedBitmap
}