package com.example.restyoureyes.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restyoureyes.service.EyeRestService
import com.example.restyoureyes.ui.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    
    val workDuration by viewModel.workDuration.collectAsState()
    val breakDuration by viewModel.breakDuration.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val autoDismiss by viewModel.autoDismiss.collectAsState()
    
    var hasOverlayPermission by remember { 
        mutableStateOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(context) else true)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Rest Your Eyes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(32.dp))

            if (!hasOverlayPermission) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Se requiere permiso para mostrar sobre otras apps", color = MaterialTheme.colorScheme.primary)
                        Button(onClick = {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                            context.startActivity(intent)
                        }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Otorgar Permiso")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            var localWorkDuration by remember(workDuration) { mutableFloatStateOf(workDuration.toFloat()) }
            Text("Intervalo de aviso: ${localWorkDuration.toInt()} min", fontWeight = FontWeight.Medium)
            Slider(
                value = localWorkDuration,
                onValueChange = { localWorkDuration = it },
                onValueChangeFinished = {
                    viewModel.updateWorkDuration(localWorkDuration.toInt())
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Ajuste guardado", duration = SnackbarDuration.Short)
                    }
                },
                valueRange = 1f..60f,
                steps = 58
            )
            Spacer(modifier = Modifier.height(16.dp))

            var localBreakDuration by remember(breakDuration) { mutableFloatStateOf(breakDuration.toFloat()) }
            Text("Descanso: ${localBreakDuration.toInt()} seg", fontWeight = FontWeight.Medium)
            Slider(
                value = localBreakDuration,
                onValueChange = { localBreakDuration = it },
                onValueChangeFinished = {
                    viewModel.updateBreakDuration(localBreakDuration.toInt())
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Ajuste guardado", duration = SnackbarDuration.Short)
                    }
                },
                valueRange = 5f..60f,
                steps = 54
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Vibración al descansar", modifier = Modifier.weight(1f))
                Switch(
                    checked = vibrationEnabled, 
                    onCheckedChange = { 
                        viewModel.updateVibration(it)
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(if (it) "Vibración activada" else "Vibración desactivada", duration = SnackbarDuration.Short)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar automáticamente", modifier = Modifier.weight(1f))
                Switch(
                    checked = autoDismiss, 
                    onCheckedChange = { 
                        viewModel.updateAutoDismiss(it)
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(if (it) "Cierre automático activado" else "Cierre automático desactivado", duration = SnackbarDuration.Short)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val intent = Intent(context, EyeRestService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Rastreador activado y funcionando en segundo plano", duration = SnackbarDuration.Short)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = hasOverlayPermission
            ) {
                Text("APLICAR Y ACTIVAR RASTREO")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, EyeRestService::class.java)
                    context.stopService(intent)
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("El rastreo de tiempo se ha detenido", duration = SnackbarDuration.Short)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("DETENER RASTREO")
            }
        }
    }
}
