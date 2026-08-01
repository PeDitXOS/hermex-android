package com.hermex.android.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermex.android.ui.theme.HermexRadii

/**
 * Tool activity group view — groups multiple tool calls into a collapsible card.
 * iOS ToolActivityGroupView equivalent.
 */
@Composable
fun ToolActivityGroupView(
    toolCalls: List<ToolCallUi>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
) {
    if (toolCalls.isEmpty()) return
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val completedCount = toolCalls.count { it.isComplete }
    val totalCount = toolCalls.size

    Surface(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(HermexRadii.Card),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Build,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$completedCount/$totalCount tools",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                toolCalls.forEach { tool ->
                    ToolCallCard(toolCall = tool, initiallyExpanded = false)
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
