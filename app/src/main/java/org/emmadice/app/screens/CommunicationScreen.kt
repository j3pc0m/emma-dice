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

@Composable
fun CommunicationScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val audioPlayer = remember {
        AudioPlayer()
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

    fun playAudio(
        cardKey: String,
        title: String
    ) {
        val audioFile = File(
            context.filesDir,
            "audio/${cardKey}_audio.m4a"
        )

        val started = audioPlayer.play(audioFile)

        if (!started) {
            Toast.makeText(
                context,
                "La tarjeta $title todavía no tiene audio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.ScreenPadding),
        contentAlignment = Alignment.Center
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimensions.MediumSpacing
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommunicationCard(
                    title = "Papá",
                    visual = visualFor(
                        cardKey = "papa",
                        initial = "P"
                    ),
                    backgroundColor = CategoryPeople,
                    onClick = {
                        playAudio(
                            cardKey = "papa",
                            title = "Papá"
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                CommunicationCard(
                    title = "Mamá",
                    visual = visualFor(
                        cardKey = "mama",
                        initial = "M"
                    ),
                    backgroundColor = CategoryPeople,
                    onClick = {
                        playAudio(
                            cardKey = "mama",
                            title = "Mamá"
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                CommunicationCard(
                    title = "Agua",
                    visual = visualFor(
                        cardKey = "agua",
                        initial = "A"
                    ),
                    backgroundColor = CategoryNeeds,
                    onClick = {
                        playAudio(
                            cardKey = "agua",
                            title = "Agua"
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val cardSpacing = Dimensions.MediumSpacing

                /*
                 * Altura aproximada que añade CommunicationCard
                 * además de la imagen cuadrada:
                 * padding, separación y texto.
                 */
                val cardExtraHeight = 82.dp

                val availableHeightPerCard =
                    (maxHeight - cardSpacing * 2) / 3

                val cardWidth =
                    (availableHeightPerCard - cardExtraHeight)
                        .coerceAtLeast(80.dp)
                        .coerceAtMost(maxWidth)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(cardSpacing)
                ) {
                    CommunicationCard(
                        title = "Papá",
                        visual = visualFor(
                            cardKey = "papa",
                            initial = "P"
                        ),
                        backgroundColor = CategoryPeople,
                        onClick = {
                            playAudio(
                                cardKey = "papa",
                                title = "Papá"
                            )
                        },
                        modifier = Modifier.widthIn(
                            min = cardWidth,
                            max = cardWidth
                        )
                    )

                    CommunicationCard(
                        title = "Mamá",
                        visual = visualFor(
                            cardKey = "mama",
                            initial = "M"
                        ),
                        backgroundColor = CategoryPeople,
                        onClick = {
                            playAudio(
                                cardKey = "mama",
                                title = "Mamá"
                            )
                        },
                        modifier = Modifier.widthIn(
                            min = cardWidth,
                            max = cardWidth
                        )
                    )

                    CommunicationCard(
                        title = "Agua",
                        visual = visualFor(
                            cardKey = "agua",
                            initial = "A"
                        ),
                        backgroundColor = CategoryNeeds,
                        onClick = {
                            playAudio(
                                cardKey = "agua",
                                title = "Agua"
                            )
                        },
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