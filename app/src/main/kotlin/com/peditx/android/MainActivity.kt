package com.peditx.hermex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.peditx.hermex.navigation.HermexNavGraph
import com.peditx.hermex.navigation.HermexIntentDestination
import com.peditx.hermex.navigation.hermexDestination
import com.peditx.hermex.onboarding.OnboardingFlow
import com.peditx.hermex.onboarding.OnboardingManager
import com.peditx.hermex.ui.theme.HermexTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val externalIntentDestination = MutableStateFlow<HermexIntentDestination?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        externalIntentDestination.value = intent?.hermexDestination()
        enableEdgeToEdge()
        val appContainer = (application as HermexApplication).appContainer
        setContent {
            var showOnboarding by remember { mutableStateOf(!OnboardingManager.isCompleted(this)) }
            HermexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    if (showOnboarding) {
                        OnboardingFlow(onCompleted = {
                            showOnboarding = false
                            OnboardingManager.markCompleted(this)
                        })
                    } else {
                        HermexNavGraph(
                            appContainer = appContainer,
                            externalIntentDestination = externalIntentDestination,
                            onExternalIntentConsumed = { externalIntentDestination.value = null },
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (application as HermexApplication).appContainer.setAppInForeground(true)
    }

    override fun onStop() {
        super.onStop()
        (application as HermexApplication).appContainer.setAppInForeground(false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        externalIntentDestination.value = intent.hermexDestination()
    }
}
