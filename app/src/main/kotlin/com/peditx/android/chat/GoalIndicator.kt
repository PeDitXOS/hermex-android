package com.peditx.hermex.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Goal controls in chat — shows active goal and allows setting/changing it.
 * iOS ChatGoalControls equivalent.
 */
@Composable
fun GoalIndicator(
    goalText: String?,
    onClearGoal: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (goalText.isNullOrBlank()) return

    Surface(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.Flag,
                contentDescription = "Goal",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = goalText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = onClearGoal,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier.height(24.dp),
            ) {
                Text("Clear", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
