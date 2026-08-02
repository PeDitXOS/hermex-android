package com.peditx.hermex.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Multi-page onboarding flow — iOS OnboardingView equivalent.
 * Pages: Welcome → Features → Connect
 */

enum class OnboardingPage(val title: String, val description: String, val icon: ImageVector) {
    Welcome(
        "Welcome to Hermex",
        "Your AI agent, on your phone. Connect to your self-hosted Hermes server.",
        Icons.Filled.Rocket,
    ),
    Features(
        "What You Can Do",
        "Chat with your agent, manage tasks, browse skills, and explore your workspace — all from your phone.",
        Icons.Filled.Star,
    ),
    Connect(
        "Connect to Your Server",
        "Enter your server URL and password to get started. HTTPS recommended for security.",
        Icons.Filled.Link,
    ),
}

@Composable
fun OnboardingFlow(
    onCompleted: () -> Unit,
    onTailscaleGuide: () -> Unit = {},
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingPage.entries.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val entry = OnboardingPage.entries[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    entry.icon,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    entry.title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    entry.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Page indicators
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(OnboardingPage.entries.size) { i ->
                val isSelected = pagerState.currentPage == i
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(if (isSelected) 24.dp else 8.dp, 8.dp),
                ) {}
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (pagerState.currentPage > 0) {
                OutlinedButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Back") }
            }
            Button(
                onClick = {
                    if (pagerState.currentPage < OnboardingPage.entries.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onCompleted()
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(if (pagerState.currentPage < OnboardingPage.entries.size - 1) "Next" else "Get Started")
            }
        }
    }
}
