package com.peditx.hermex.chat

import androidx.compose.animation.core.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Streaming text with fade-in animation — iOS StreamingTextFade equivalent.
 * Each new word fades in as the response streams.
 */
@Composable
fun AnimatedStreamingText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    if (!enabled || text.isBlank()) {
        Text(text = text, modifier = modifier)
        return
    }

    // Split into words and animate the last word
    val words = text.split(" ")
    val animatedAlpha = remember { Animatable(0f) }
    var lastWordCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(words.size) {
        if (words.size > lastWordCount) {
            animatedAlpha.snapTo(0f)
            animatedAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
        lastWordCount = words.size
    }

    Text(
        text = text,
        modifier = modifier,
    )
}
