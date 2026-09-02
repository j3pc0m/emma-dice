package org.emmadice.app.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.emmadice.app.audio.AudioPlayer
import org.emmadice.app.components.CommunicationCard
import org.emmadice.app.design.CategoryNeeds
import org.emmadice.app.design.CategoryPeople
import org.emmadice.app.design.Dimensions
import org.emmadice.app.model.CommunicationVisual
import java.io.File

private data class MvpCommunicationCard(
    val title: String,
    val cardKey: String,
    val initial: String,
    val usesNeedsColor: Boolean = false
)

@Composable
fun CommunicationScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val audioPlayer = remember {
        AudioPlayer()
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    val cards = remember {
        listOf(
            MvpCommunicationCard(
                title = "Papá",
                cardKey = "papa",
                initial = "P"
            ),
            MvpCommunicationCard(
                title = "Mamá",
                cardKey = "mama",
                initial = "M"
            ),
            MvpCommunicationCard(
                title = "Agua",
                cardKey = "agua",
                initial = "A",
                usesNeedsColor = true
            )
        )
    }

    fun visualFor(
        cardKey: String,
        initial: String
    ): CommunicationVisual {
        val photoFile = File(
            context.filesDir,
            "images/${cardKey}_photo.jpg"
        )

        return if (
            photoFile.exists() &&
            photoFile.length() > 0L
        ) {
            CommunicationVisual.LocalPhoto(
                absolutePath = photoFile.absolutePath
            )
        } else {
            CommunicationVisual.Placeholder(
                initial = initial
            )
        }
    }

    fun playAudio(card: MvpCommunicationCard) {
        val audioFile = File(
            context.filesDir,
            "audio/${card.cardKey}_audio.m4a"
        )

        val started = audioPlayer.play(audioFile)

        if (!started) {
            Toast.makeText(
                context,
                "La tarjeta ${card.title} todavía no tiene audio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Composable
    fun CardContent(
        card: MvpCommunicationCard,
        modifier: Modifier = Modifier
    ) {
        CommunicationCard(
            title = card.title,
            visual = visualFor(
                cardKey = card.cardKey,
                initial = card.initial
            ),
            backgroundColor = if (card.usesNeedsColor) {
                CategoryNeeds
            } else {
                CategoryPeople
            },
            onClick = {
                playAudio(card)
            },
            modifier = modifier
        )
    }

    when {
        isLandscape -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimensions.ScreenPadding),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        Dimensions.MediumSpacing
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    cards.forEach { card ->
                        CardContent(
                            card = card,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimensions.ScreenPadding),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val cardSpacing = Dimensions.MediumSpacing
                    val cardExtraHeight = 82.dp

                    val availableHeightPerCard =
                        (maxHeight - cardSpacing * 2) / 3

                    val cardWidth =
                        (availableHeightPerCard - cardExtraHeight)
                            .coerceAtLeast(80.dp)
                            .coerceAtMost(maxWidth)

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(cardSpacing)
                    ) {
                        cards.forEach { card ->
                            CardContent(
                                card = card,
                                modifier = Modifier.widthIn(
                                    min = cardWidth,
                                    max = cardWidth
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
