package com.peditx.hermex.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Adaptive glass/frosted effect modifier — iOS AdaptiveGlassModifier equivalent.
 * Uses semi-transparent surface colors to simulate a frosted glass look on Android.
 */
@Composable
fun Modifier.adaptiveGlass(
    cornerRadius: Dp = 16.dp,
    blurRadius: Dp = 0.dp, // blur not well-supported on all Android, use opacity fallback
    borderAlpha: Float = 0.15f,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .clip(shape)
        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
        .border(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = borderAlpha),
            shape = shape,
        )
}

/**
 * Frosted surface card — combines glass effect with standard card styling.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.adaptiveGlass(cornerRadius),
        content = content,
    )
}
