package org.emmadice.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import org.emmadice.app.audio.AudioPlayer
import org.emmadice.app.components.CommunicationCard
import org.emmadice.app.data.CardViewModel
import org.emmadice.app.data.db.CardEntity
import org.emmadice.app.design.CategoryNeeds
import org.emmadice.app.design.CategoryPeople
import org.emmadice.app.model.CommunicationVisual
import java.io.File

@Composable
fun CommunicationScreen(
    cardViewModel: CardViewModel = viewModel()
) {
    val context = LocalContext.current
    val allCards by cardViewModel.cards.collectAsState()
    val cards = allCards.filter { it.enabled }
    val audioPlayer = remember { AudioPlayer() }
    val resources = remember(cardViewModel) { cardViewModel.resources() }

    DisposableEffect(audioPlayer) {
        onDispose { audioPlayer.release() }
    }

    DisposableEffect(cards) {
        cards.forEach { card ->
            val audioFile = card.audioPath?.let(resources::absolutePath)
                ?.let(::File)
                ?: resources.audioFile(card.stableKey)
            audioPlayer.preload(card.stableKey, audioFile)
        }
        onDispose { }
    }

    if (cards.isEmpty()) {
        Text(
            text = "No hay tarjetas activas",
            modifier = Modifier.padding(24.dp)
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cards, key = { it.id }) { card ->
            CommunicationCard(
                title = card.title,
                visual = cardVisual(card, resources),
                backgroundColor = if (card.stableKey == "agua") {
                    CategoryNeeds
                } else {
                    CategoryPeople
                },
                onClick = {
                    val audioFile = card.audioPath?.let(resources::absolutePath)
                        ?.let(::File)
                        ?: resources.audioFile(card.stableKey)
                    val showError = {
                        Toast.makeText(
                            context,
                            "La tarjeta ${card.title} todavía no tiene audio",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (!audioPlayer.play(card.stableKey, audioFile, showError)) {
                        showError()
                    }
                },
                contentDescription = card.title
            )
        }
    }
}

private fun cardVisual(
    card: CardEntity,
    resources: org.emmadice.app.data.CardResources
): CommunicationVisual {
    val photoFile = card.imagePath?.let(resources::absolutePath)
        ?.let(::File)
        ?: resources.imageFile(card.stableKey)
    return if (photoFile.exists() && photoFile.length() > 0L) {
        CommunicationVisual.LocalPhoto(photoFile.absolutePath)
    } else {
        CommunicationVisual.Placeholder(
            card.title.trim().firstOrNull()?.uppercase() ?: "?"
        )
    }
}
