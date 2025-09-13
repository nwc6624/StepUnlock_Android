package com.stepunlock.app.ui.lock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stepunlock.app.ui.theme.StepUnlockTheme

class LockActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_APP_PACKAGE = "extra_app_package"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_UNLOCK_COST = "extra_unlock_cost"
        const val EXTRA_UNLOCK_DURATION = "extra_unlock_duration"
        
        const val RESULT_UNLOCKED = 1
        const val RESULT_CANCELLED = 0
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val appPackage = intent.getStringExtra(EXTRA_APP_PACKAGE) ?: ""
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "App"
        val unlockCost = intent.getIntExtra(EXTRA_UNLOCK_COST, 10)
        val unlockDuration = intent.getLongExtra(EXTRA_UNLOCK_DURATION, 15 * 60 * 1000)
        
        setContent {
            StepUnlockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LockScreen(
                        appPackage = appPackage,
                        appName = appName,
                        unlockCost = unlockCost,
                        unlockDuration = unlockDuration,
                        onUnlock = { duration ->
                            val resultIntent = Intent().apply {
                                putExtra(EXTRA_APP_PACKAGE, appPackage)
                                putExtra(EXTRA_UNLOCK_DURATION, duration)
                            }
                            setResult(RESULT_UNLOCKED, resultIntent)
                            finish()
                        },
                        onCancel = {
                            setResult(RESULT_CANCELLED)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreen(
    appPackage: String,
    appName: String,
    unlockCost: Int,
    unlockDuration: Long,
    onUnlock: (Long) -> Unit,
    onCancel: () -> Unit,
    viewModel: LockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(appPackage) {
        viewModel.loadAppInfo(appPackage)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // Lock Icon
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // App Name
        Text(
            text = appName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Locked Message
        Text(
            text = "This app is locked!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Credit Balance
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Credits",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${uiState.creditBalance}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Unlock Options
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Unlock Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick Unlock
                Button(
                    onClick = { 
                        if (uiState.creditBalance >= unlockCost) {
                            onUnlock(unlockDuration)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.creditBalance >= unlockCost,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Unlock for 15 minutes",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Cost: $unlockCost credits",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Extended Unlock
                OutlinedButton(
                    onClick = { 
                        if (uiState.creditBalance >= unlockCost * 2) {
                            onUnlock(unlockDuration * 2)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.creditBalance >= unlockCost * 2,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Unlock for 30 minutes",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Cost: ${unlockCost * 2} credits",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Actions
        Text(
            text = "Earn credits quickly:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Steps
            QuickActionButton(
                icon = Icons.Default.DirectionsWalk,
                label = "Walk",
                credits = "+10",
                onClick = { /* TODO: Implement step tracking */ }
            )
            
            // Pomodoro
            QuickActionButton(
                icon = Icons.Default.Timer,
                label = "Focus",
                credits = "+15",
                onClick = { /* TODO: Implement pomodoro */ }
            )
            
            // Water
            QuickActionButton(
                icon = Icons.Default.LocalDrink,
                label = "Hydrate",
                credits = "+5",
                onClick = { /* TODO: Implement water tracking */ }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Cancel Button
        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Go Back",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    credits: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = credits,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}
