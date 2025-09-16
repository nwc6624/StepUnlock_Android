package com.stepunlock.app.ui.screens.apps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(
    viewModel: AppsViewModel = remember { AppsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadApps()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Manage Apps",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.lockedAppsCount} apps locked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Select All/None Button
            TextButton(
                onClick = { viewModel.toggleAllApps() }
            ) {
                Text(
                    text = if (uiState.allAppsLocked) "Unlock All" else "Lock All"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            placeholder = { Text("Search apps...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    onClick = { viewModel.updateFilter(AppFilter.ALL) },
                    label = { Text("All") },
                    selected = uiState.selectedFilter == AppFilter.ALL
                )
            }
            item {
                FilterChip(
                    onClick = { viewModel.updateFilter(AppFilter.LOCKED) },
                    label = { Text("Locked") },
                    selected = uiState.selectedFilter == AppFilter.LOCKED
                )
            }
            item {
                FilterChip(
                    onClick = { viewModel.updateFilter(AppFilter.SOCIAL) },
                    label = { Text("Social") },
                    selected = uiState.selectedFilter == AppFilter.SOCIAL
                )
            }
            item {
                FilterChip(
                    onClick = { viewModel.updateFilter(AppFilter.ENTERTAINMENT) },
                    label = { Text("Entertainment") },
                    selected = uiState.selectedFilter == AppFilter.ENTERTAINMENT
                )
            }
            item {
                FilterChip(
                    onClick = { viewModel.updateFilter(AppFilter.GAMES) },
                    label = { Text("Games") },
                    selected = uiState.selectedFilter == AppFilter.GAMES
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Apps List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Placeholder for app items - will be implemented later
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Apps Found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "App detection will be implemented soon",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                if (uiState.filteredApps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No apps found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppItem(
    app: com.stepunlock.app.data.entity.AppRuleEntity,
    onToggleLock: () -> Unit,
    onUpdateCost: (Int) -> Unit
) {
    var showCostDialog by remember { mutableStateOf(false) }
    var newCost by remember { mutableStateOf(app.unlockCost.toString()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon (placeholder)
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = app.appName,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = app.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Cost: ${app.unlockCost} credits",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Actions
            Row {
                // Cost Button
                OutlinedButton(
                    onClick = { showCostDialog = true },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("${app.unlockCost}")
                }
                
                // Lock Toggle
                Switch(
                    checked = app.isLocked,
                    onCheckedChange = { onToggleLock() }
                )
            }
        }
    }
    
    // Cost Dialog
    if (showCostDialog) {
        AlertDialog(
            onDismissRequest = { showCostDialog = false },
            title = { Text("Set Unlock Cost") },
            text = {
                Column {
                    Text("How many credits should ${app.appName} cost to unlock?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newCost,
                        onValueChange = { newCost = it },
                        label = { Text("Credits") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cost = newCost.toIntOrNull() ?: app.unlockCost
                        onUpdateCost(cost)
                        showCostDialog = false
                    }
                ) {
                    Text("Set")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCostDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

enum class AppFilter {
    ALL, LOCKED, SOCIAL, ENTERTAINMENT, GAMES
}
