package org.emmadice.app.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import org.emmadice.app.audio.AudioRecorder
import org.emmadice.app.data.CardViewModel
import org.emmadice.app.data.db.CardEntity
import org.emmadice.app.design.Dimensions
import org.emmadice.app.design.TextPrimary
import org.emmadice.app.ui.theme.Fredoka
import kotlinx.coroutines.launch

private val SettingsCardBackground = Color(0xFFEDE7F6)
private val SettingsActionColor = Color(0xFF6A4C93)
private val SettingsDangerColor = Color(0xFFB3261E)

@Composable
fun SettingsScreen(
    cardViewModel: CardViewModel = viewModel()
) {
    val context = LocalContext.current
    val cards by cardViewModel.cards.collectAsState()
    val resources = remember(cardViewModel) { cardViewModel.resources() }
    val audioRecorder = remember(context) {
        org.emmadice.app.audio.AudioRecorder(context.applicationContext)
    }
    var newTitle by remember { mutableStateOf("") }
    var newTitleSubmitted by remember { mutableStateOf(false) }
    var recordingCard by remember { mutableStateOf<CardEntity?>(null) }
    var pendingAudioCard by remember { mutableStateOf<CardEntity?>(null) }
    var pendingPhotoCard by remember { mutableStateOf<CardEntity?>(null) }
    var cardToDelete by remember { mutableStateOf<CardEntity?>(null) }
    var titleSaveInFlight by remember { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showSaveFeedback(message: String) {
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(cardViewModel) {
        cardViewModel.messages.collect { showError(it) }
    }

    fun startRecording(card: CardEntity) {
        try {
            audioRecorder.start(resources.audioFile(card.stableKey))
            recordingCard = card
            Toast.makeText(context, "Grabando audio de ${card.title}", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            showError("No se pudo iniciar la grabación")
        }
    }

    fun stopRecording(card: CardEntity) {
        val saved = audioRecorder.stop()
        recordingCard = null
        if (saved) {
            cardViewModel.setAudioPath(
                card,
                "audio/${resources.audioFile(card.stableKey).name}",
                ::showErrorThrowable
            )
            Toast.makeText(context, "Audio de ${card.title} guardado", Toast.LENGTH_LONG).show()
        } else {
            showError("No se pudo guardar el audio")
        }
    }

    val recordPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val card = pendingAudioCard
        pendingAudioCard = null
        if (granted && card != null) startRecording(card)
        else if (!granted) showError("Se necesita permiso de micrófono")
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val card = pendingPhotoCard
        pendingPhotoCard = null
        if (card == null) return@rememberLauncherForActivityResult
        val file = resources.imageFile(card.stableKey)
        if (success && file.exists() && file.length() > 0L) {
            cardViewModel.setImagePath(
                card,
                "images/${file.name}",
                ::showErrorThrowable
            )
            Toast.makeText(context, "Fotografía de ${card.title} guardada", Toast.LENGTH_LONG).show()
        } else {
            showError("No se pudo guardar la fotografía")
        }
    }

    DisposableEffect(audioRecorder) {
        onDispose {
            if (audioRecorder.isRecording()) audioRecorder.cancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.MediumSpacing)
        ) {
        item {
            Text(
                text = "Configurar tarjetas",
                fontFamily = Fredoka,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SettingsCardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(Dimensions.MediumSpacing),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.SmallSpacing)
                ) {
                    Text(
                        "Nueva tarjeta",
                        fontFamily = Fredoka,
                        fontSize = 22.sp,
                        color = TextPrimary
                    )
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Título") },
                        singleLine = true,
                        isError = newTitleSubmitted && newTitle.isBlank(),
                        colors = titleFieldColors()
                    )
                    Button(
                        onClick = {
                            if (newTitle.isBlank()) {
                                newTitleSubmitted = true
                                showError("El título es obligatorio")
                            } else cardViewModel.create(newTitle) { created ->
                                if (created == null) showError("No se pudo crear la tarjeta")
                                else {
                                    newTitle = ""
                                    newTitleSubmitted = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SettingsActionColor,
                            contentColor = Color.White
                        )
                    ) { Text("Añadir tarjeta") }
                }
            }
        }
        items(cards, key = { it.id }) { card ->
            SettingsCard(
                card = card,
                isRecording = recordingCard?.id == card.id,
                isTitleSaveInFlight = titleSaveInFlight == card.id,
                hasAudio = card.audioPath != null || resources.audioFile(card.stableKey).let {
                    it.exists() && it.length() > 0L
                },
                onSave = { updated -> cardViewModel.update(updated, ::showErrorThrowable) },
                onTitleSave = { updated ->
                    if (updated.title.trim() != card.title && titleSaveInFlight == null) {
                        titleSaveInFlight = card.id
                        cardViewModel.update(
                            updated.copy(title = updated.title.trim()),
                            onError = {
                                titleSaveInFlight = null
                                showSaveFeedback("No se ha podido guardar el título")
                            },
                            onSuccess = {
                                titleSaveInFlight = null
                                showSaveFeedback("Título guardado correctamente")
                            },
                            reportMessage = false
                        )
                    }
                },
                onDelete = { cardToDelete = card },
                onPhoto = {
                    val file = resources.imageFile(card.stableKey)
                    file.parentFile?.mkdirs()
                    if (file.exists()) file.delete()
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    pendingPhotoCard = card
                    takePictureLauncher.launch(uri)
                },
                onAudio = {
                    if (recordingCard?.id == card.id) {
                        stopRecording(card)
                    } else {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) startRecording(card)
                        else {
                            pendingAudioCard = card
                            recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                onValidationError = ::showError
            )
        }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("Eliminar tarjeta") },
            text = { Text("¿Quieres eliminar ${card.title} y sus recursos?") },
            confirmButton = {
                TextButton(onClick = {
                    cardToDelete = null
                    cardViewModel.delete(card, ::showErrorThrowable)
                },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SettingsDangerColor
                    )
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(
                    onClick = { cardToDelete = null },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SettingsActionColor
                    )
                ) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    card: CardEntity,
    isRecording: Boolean,
    isTitleSaveInFlight: Boolean,
    hasAudio: Boolean,
    onSave: (CardEntity) -> Unit,
    onTitleSave: (CardEntity) -> Unit,
    onDelete: () -> Unit,
    onPhoto: () -> Unit,
    onAudio: () -> Unit,
    onValidationError: (String) -> Unit
) {
    var title by remember(card.id, card.title) { mutableStateOf(card.title) }
    var enabled by remember(card.id, card.enabled) { mutableStateOf(card.enabled) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SettingsCardBackground)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.MediumSpacing),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SmallSpacing)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Título") },
                singleLine = true,
                isError = title.isBlank(),
                colors = titleFieldColors()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (enabled) "Activa" else "Desactivada",
                    color = TextPrimary
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                        onSave(card.copy(title = title, enabled = it))
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SmallSpacing)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onPhoto,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsActionColor,
                        contentColor = Color.White
                    )
                ) { Text("Fotografía") }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onAudio,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) SettingsDangerColor else SettingsActionColor,
                        contentColor = Color.White
                    )
                ) { Text(if (isRecording) "Detener" else if (hasAudio) "Regrabar" else "Audio") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.SmallSpacing)) {
                TextButton(
                    enabled = !isTitleSaveInFlight,
                    onClick = {
                        if (title.isBlank()) onValidationError("El título es obligatorio")
                        else onTitleSave(card.copy(title = title.trim(), enabled = enabled))
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SettingsActionColor
                    )
                ) { Text("Guardar título") }
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SettingsDangerColor
                    )
                ) { Text("Eliminar") }
            }
        }
    }
}

@Composable
private fun titleFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF24202B),
    unfocusedTextColor = Color(0xFF24202B),
    disabledTextColor = Color(0xFF24202B),
    errorTextColor = Color(0xFF24202B),
    focusedLabelColor = Color(0xFF514563),
    unfocusedLabelColor = Color(0xFF514563),
    disabledLabelColor = Color(0xFF514563),
    errorLabelColor = Color(0xFFB3261E),
    focusedPlaceholderColor = Color(0xFF514563),
    unfocusedPlaceholderColor = Color(0xFF514563),
    disabledPlaceholderColor = Color(0xFF514563),
    errorPlaceholderColor = Color(0xFFB3261E),
    focusedBorderColor = Color(0xFF6A4C93),
    unfocusedBorderColor = Color(0xFF777080),
    disabledBorderColor = Color(0xFF777080),
    errorBorderColor = Color(0xFFB3261E),
    cursorColor = Color(0xFF6A4C93),
    errorCursorColor = Color(0xFFB3261E)
)

private fun showErrorThrowable(error: Throwable) {
    // Los callbacks de ViewModel se ejecutan fuera de la composición; la UI mantiene
    // el estado válido y los fallos de persistencia no se silencian en el log.
    error.printStackTrace()
}
