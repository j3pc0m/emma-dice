package org.emmadice.app.screens

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import org.emmadice.app.audio.AudioRecorder
import org.emmadice.app.design.Dimensions
import org.emmadice.app.design.TextPrimary
import org.emmadice.app.ui.theme.Fredoka
import androidx.core.content.ContextCompat

import java.io.File

private val SettingsCardBackground = Color(0xFFEDE7F6)
private val SettingsActionColor = Color(0xFF6A4C93)

@Composable
fun SettingsScreen() {
    val configuration = LocalConfiguration.current

    val isTablet = configuration.smallestScreenWidthDp >= 600
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val columnCount = when {
        isTablet && isLandscape -> 3
        isTablet -> 2
        isLandscape -> 2
        else -> 1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(
            Dimensions.MediumSpacing
        )
    ) {
        Text(
            text = "Configurar tarjetas",
            fontFamily = Fredoka,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(
                Dimensions.MediumSpacing
            ),
            verticalArrangement = Arrangement.spacedBy(
                Dimensions.MediumSpacing
            )
        ) {
            item {
                SettingsCard(
                    title = "Papá",
                    cardKey = "papa"
                )
            }

            item {
                SettingsCard(
                    title = "Mamá",
                    cardKey = "mama"
                )
            }

            item {
                SettingsCard(
                    title = "Agua",
                    cardKey = "agua"
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    cardKey: String
) {
    val context = LocalContext.current

    val photoFile = remember(context, cardKey) {
        File(
            context.filesDir,
            "images/${cardKey}_photo.jpg"
        ).also { file ->
            file.parentFile?.mkdirs()
        }
    }

    val audioFile = remember(context, cardKey) {
        File(
            context.filesDir,
            "audio/${cardKey}_audio.m4a"
        ).also { file ->
            file.parentFile?.mkdirs()
        }
    }

    val audioRecorder = remember(context) {
        AudioRecorder(context.applicationContext)
    }

    var isRecording by remember {
        mutableStateOf(false)
    }

    fun startAudioRecording() {
        try {
            audioRecorder.start(audioFile)
            isRecording = true

            Toast.makeText(
                context,
                "Grabando audio de $title",
                Toast.LENGTH_SHORT
            ).show()
        } catch (_: Exception) {
            isRecording = false

            Toast.makeText(
                context,
                "No se pudo iniciar la grabación",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun stopAudioRecording() {
        val savedCorrectly = audioRecorder.stop()

        isRecording = false

        val audioWasSaved =
            savedCorrectly &&
                    audioFile.exists() &&
                    audioFile.length() > 0L

        val message = if (audioWasSaved) {
            "Audio de $title guardado"
        } else {
            "No se pudo guardar el audio"
        }

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    val recordAudioPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startAudioRecording()
            } else {
                Toast.makeText(
                    context,
                    "Se necesita permiso de micrófono",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        val photoWasSaved =
            success &&
                    photoFile.exists() &&
                    photoFile.length() > 0L

        val message = if (photoWasSaved) {
            "Fotografía de $title guardada"
        } else {
            "No se pudo guardar la fotografía"
        }

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()

    }

    DisposableEffect(Unit) {
        onDispose {
            if (audioRecorder.isRecording()) {
                audioRecorder.cancel()
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SettingsCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.MediumSpacing),
            verticalArrangement = Arrangement.spacedBy(
                Dimensions.MediumSpacing
            )
        ) {
            Text(
                text = title,
                fontFamily = Fredoka,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimensions.SmallSpacing
                )
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        photoFile.parentFile?.mkdirs()

                        if (photoFile.exists()) {
                            photoFile.delete()
                        }

                        val photoUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )

                        takePictureLauncher.launch(photoUri)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsActionColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Fotografía")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (isRecording) {
                            stopAudioRecording()
                        } else {
                            val hasPermission =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                startAudioRecording()
                            } else {
                                recordAudioPermissionLauncher.launch(
                                    Manifest.permission.RECORD_AUDIO
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) {
                            Color(0xFFB3261E)
                        } else {
                            SettingsActionColor
                        },
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isRecording) {
                            "Detener"
                        } else if (audioFile.exists() && audioFile.length() > 0L) {
                            "Regrabar"
                        } else {
                            "Audio"
                        }
                    )
                }
            }
        }
    }
}