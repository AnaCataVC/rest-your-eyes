package com.example.restyoureyes.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.restyoureyes.R
import com.example.restyoureyes.data.SettingsRepository
import com.example.restyoureyes.ui.theme.StatusActiveGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun OverlayScreen(
    settingsRepository: SettingsRepository,
    onTimeExpired: () -> Unit = {},
    onCompleted: () -> Unit,
    onSkipped: () -> Unit
) {
    var isInitialized by rememberSaveable { mutableStateOf(false) }
    var timeLeft by rememberSaveable { mutableIntStateOf(20) }
    var totalBreakTime by rememberSaveable { mutableIntStateOf(20) }
    var autoDismiss by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isInitialized) {
            val duration = settingsRepository.breakDurationFlow.first()
            totalBreakTime = duration
            timeLeft = duration
            autoDismiss = settingsRepository.autoDismissFlow.first()
            isInitialized = true
        }

        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }

        onTimeExpired()

        if (autoDismiss) {
            onCompleted()
        }
    }

    val progress = if (totalBreakTime > 0) (timeLeft.toFloat() / totalBreakTime.toFloat()) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "breakProgress")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (timeLeft > 0) {
                Text(
                    text = stringResource(R.string.overlay_rule_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.overlay_instructions_format, totalBreakTime),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = stringResource(R.string.overlay_completed_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = StatusActiveGreen
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.overlay_completed_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Indicador de progreso circular con contador central
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = "$timeLeft ${R.string.overlay_seconds_unit}"
                    }
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(240.dp),
                    strokeWidth = 10.dp,
                    color = if (timeLeft > 0) MaterialTheme.colorScheme.primary else StatusActiveGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$timeLeft",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.overlay_seconds_unit),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Acciones con microcopy empático
            if (timeLeft <= 0) {
                Button(
                    onClick = onCompleted,
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.overlay_action_done),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onSkipped,
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.overlay_action_skip),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
