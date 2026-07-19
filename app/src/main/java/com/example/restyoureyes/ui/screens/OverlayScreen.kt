package com.example.restyoureyes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restyoureyes.data.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun OverlayScreen(
    settingsRepository: SettingsRepository,
    onDismiss: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(20) }
    var totalBreakTime by remember { mutableStateOf(20) }
    var autoDismiss by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val duration = settingsRepository.breakDurationFlow.first()
        totalBreakTime = duration
        timeLeft = duration
        autoDismiss = settingsRepository.autoDismissFlow.first()

        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }

        if (autoDismiss) {
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Regla 20-20-20",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mira hacia otro lado (idealmente a 6 metros / 20 pies de distancia) por $totalBreakTime segundos.",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "$timeLeft",
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            if (timeLeft <= 0 && !autoDismiss) {
                Button(onClick = onDismiss, modifier = Modifier.padding(16.dp)) {
                    Text("CERRAR")
                }
            } else if (timeLeft > 0) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.padding(16.dp)) {
                    Text("FORZAR CIERRE")
                }
            }
        }
    }
}
