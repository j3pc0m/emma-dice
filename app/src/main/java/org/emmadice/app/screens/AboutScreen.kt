package org.emmadice.app.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.emmadice.app.R
import org.emmadice.app.design.Dimensions
import org.emmadice.app.design.TextPrimary
import org.emmadice.app.ui.theme.Borel
import org.emmadice.app.ui.theme.Fredoka

@Composable
fun AboutScreen() {
    val configuration = LocalConfiguration.current

    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val contentWidthFraction = if (isLandscape) {
        0.72f
    } else {
        0.92f
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(contentWidthFraction)
                .verticalScroll(rememberScrollState())
                .padding(
                    vertical = Dimensions.ScreenPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Emma Dice",
                fontFamily = Borel,
                fontSize = 42.sp,
                color = Color(0xFF4A90E2),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Versión 0.1.0 – Birthday Edition",
                fontFamily = Fredoka,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.MediumSpacing))

            Image(
                painter = painterResource(
                    id = R.drawable.emma_birthday_edition
                ),
                contentDescription =
                    "Ilustración especial del cuarto cumpleaños de Emma",
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxHeight = 420.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(Dimensions.MediumSpacing))

            HorizontalDivider(
                color = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))

            AboutParagraph(
                text = "Emma Dice nace con un objetivo muy sencillo: " +
                        "ayudar a Emma a comunicar aquello que quiera decir."
            )

            AboutParagraph(
                text = "Mientras desarrollaba esta primera versión, junto a la IA, " +
                        "pensé que la necesidad de Emma no era única. Hay muchos niños " +
                        "y niñas que necesitan una forma más sencilla de expresar lo que " +
                        "sienten, lo que desean o lo que necesitan. Y también muchas " +
                        "familias que buscan una herramienta cercana, sencilla y adaptable " +
                        "a su forma de vivir."
            )

            AboutParagraph(
                text = "Por eso, Emma Dice no quiere ser solo una aplicación para Emma."
            )

            AboutParagraph(
                text = "Quiere convertirse en un comunicador libre, accesible y " +
                        "personalizable, diseñado para acompañar a cada niño o niña " +
                        "en su propio camino."
            )

            HighlightedText(
                text = "Hoy comienza con tres tarjetas:"
            )

            Text(
                text = "Papá · Mamá · Agua",
                modifier = Modifier.fillMaxWidth(),
                fontFamily = Fredoka,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A4C93),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.MediumSpacing))

            AboutParagraph(
                text = "Mañana podrán ser seis, ocho y hasta quince por pantalla..."
            )

            AboutParagraph(
                text = "Contará con categorías —personas, comidas, emociones y otras—, " +
                        "nuevas formas de comunicación y herramientas para que cada familia " +
                        "pueda construir un comunicador adaptado a su hogar, a su ritmo y a " +
                        "las necesidades de quien lo utiliza."
            )

            HighlightedText(
                text = "Porque cada niño aprende de una forma diferente y cada " +
                        "familia es diferente."
            )

            HighlightedText(
                text = "Creo que ninguna niña ni ningún niño debería tener que " +
                        "adaptarse a un comunicador. Debe ser el comunicador quien crezca " +
                        "y se adapte a la niña o al niño."
            )

            AboutParagraph(
                text = "Esta es solo la primera versión. Espero que Emma Dice siga " +
                        "creciendo con cada persona que la utilice, con cada familia que " +
                        "la necesite y con cada persona que se involucre en el proyecto; " +
                        "y que, con el tiempo, pueda ayudar a muchas más familias a descubrir " +
                        "que comunicarse puede ser un poco más fácil, un poco más libre y " +
                        "un poco más humano."
            )

            Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))

            HorizontalDivider(
                color = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))

            Text(
                text = "Emma Dice",
                fontFamily = Borel,
                fontSize = 32.sp,
                color = Color(0xFF4A90E2),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Comunicación accesible para todos",
                fontFamily = Fredoka,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.SmallSpacing))

            Text(
                text = "Software libre · Licencia AGPLv3",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))

            Text(
                text = "Con todo mi cariño para Emma,\n" +
                        "en su cuarto cumpleaños.",
                fontFamily = Fredoka,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6A4C93),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.SmallSpacing))

            Text(
                text = "5 de agosto de 2026",
                fontFamily = Fredoka,
                fontSize = 17.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimensions.LargeSpacing))
        }
    }
}

@Composable
private fun AboutParagraph(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.MediumSpacing),
        fontFamily = Fredoka,
        fontSize = 18.sp,
        lineHeight = 27.sp,
        color = TextPrimary,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun HighlightedText(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.MediumSpacing),
        fontFamily = Fredoka,
        fontSize = 19.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        textAlign = TextAlign.Start
    )
}
