package com.peditx.hermex.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Context window indicator — shows how much context is left.
 * iOS ContextWindowIndicatorView equivalent.
 */
@Composable
fun ContextWindowIndicator(
    usedTokens: Int,
    maxTokens: Int,
    modifier: Modifier = Modifier,
) {
    if (maxTokens <= 0) return
    val fraction = (usedTokens.toFloat() / maxTokens).coerceIn(0f, 1f)
    val color = when {
        fraction > 0.9f -> MaterialTheme.colorScheme.error
        fraction > 0.7f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .weight(1f)
                .height(4.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${(fraction * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
