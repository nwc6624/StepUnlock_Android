package com.stepunlock.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stepunlock.app.R

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to StepUnlock",
            description = "Earn screen time on distracting apps by completing positive actions like walking, focusing, and staying hydrated.",
            icon = R.drawable.ic_launcher_foreground
        ),
        OnboardingPage(
            title = "Privacy First",
            description = "All your data stays on your device. No accounts required, no cloud sync, complete privacy.",
            icon = R.drawable.ic_launcher_foreground
        ),
        OnboardingPage(
            title = "Ready to Start?",
            description = "Let's set up permissions and choose which apps you'd like to lock.",
            icon = R.drawable.ic_launcher_foreground
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Page content
        Image(
            painter = painterResource(id = pages[currentPage].icon),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = pages[currentPage].title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = pages[currentPage].description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Page indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .weight(1f)
                        .height(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (index == currentPage) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .height(8.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .height(6.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentPage > 0) {
                TextButton(
                    onClick = { currentPage-- }
                ) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }
            
            if (currentPage < pages.size - 1) {
                Button(
                    onClick = { currentPage++ }
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = onNavigateToHome
                ) {
                    Text("Get Started")
                }
            }
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: Int
)
