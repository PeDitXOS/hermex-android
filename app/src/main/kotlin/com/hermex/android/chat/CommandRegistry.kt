package com.hermex.android.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Continue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector

sealed class CommandAction {
    data class Steer(val message: String) : CommandAction()
    data object Continue : CommandAction() { val prompt = "Continue" }
    data object Summarize : CommandAction() { val prompt = "Summarize this conversation" }
    data object Edit : CommandAction()
    data object Search : CommandAction()
    data object Stop : CommandAction()
    data object Retry : CommandAction()
    data object Undo : CommandAction()
    data object Clear : CommandAction()
    data object Fork : CommandAction()
    data object Branch : CommandAction()
    data object New : CommandAction()
    data object Interrupt : CommandAction()
    data object Status : CommandAction()
    data object Skills : CommandAction()
    data object Compress : CommandAction()
    data object Compact : CommandAction()
    data object Queue : CommandAction()
    data object Goal : CommandAction()
    data object Btw : CommandAction()
    data object Background : CommandAction()
    data object Model : CommandAction()
    data object Workspace : CommandAction()
    data object Reasoning : CommandAction()
    data object Title : CommandAction()
    data object Personality : CommandAction()
}

data class CommandSuggestion(
    val command: String,
    val description: String,
    val icon: ImageVector,
    val action: CommandAction? = null,
    /** If true, the command takes a text argument after the command name. */
    val takesArgument: Boolean = false,
)

object CommandRegistry {
    val commands = listOf(
        CommandSuggestion("/help", "Show available commands", Icons.Filled.HelpOutline, CommandAction.Status),
        CommandSuggestion("/stop", "Stop the current response", Icons.Filled.Cancel, CommandAction.Stop),
        CommandSuggestion("/steer", "Steer the active response", Icons.Filled.Flight, takesArgument = true),
        CommandSuggestion("/edit", "Edit the last message", Icons.Filled.Edit, CommandAction.Edit),
        CommandSuggestion("/retry", "Retry the last response", Icons.Filled.Refresh, CommandAction.Retry),
        CommandSuggestion("/undo", "Undo last message", Icons.Filled.Undo, CommandAction.Undo),
        CommandSuggestion("/fork", "Fork conversation from here", Icons.Filled.AccountTree, CommandAction.Fork),
        CommandSuggestion("/branch", "Branch session", Icons.Filled.AccountTree, CommandAction.Branch),
        CommandSuggestion("/continue", "Continue the last response", Icons.Filled.Continue, CommandAction.Continue),
        CommandSuggestion("/summarize", "Summarize the conversation", Icons.Filled.Summarize, CommandAction.Summarize),
        CommandSuggestion("/compress", "Compress conversation context", Icons.Filled.Compress, CommandAction.Compress),
        CommandSuggestion("/compact", "Compact the conversation", Icons.Filled.Compress, CommandAction.Compact),
        CommandSuggestion("/new", "Start a new session", Icons.Filled.FiberNew, CommandAction.New),
        CommandSuggestion("/clear", "Clear the chat", Icons.Filled.Delete, CommandAction.Clear),
        CommandSuggestion("/model", "Switch model", Icons.Filled.SmartToy, CommandAction.Model),
        CommandSuggestion("/workspace", "Change workspace", Icons.Filled.Code, CommandAction.Workspace),
        CommandSuggestion("/reasoning", "Toggle reasoning display", Icons.Filled.Visibility, CommandAction.Reasoning),
        CommandSuggestion("/title", "Set session title", Icons.Filled.Title, takesArgument = true),
        CommandSuggestion("/personality", "Change personality", Icons.Filled.FormatColorText, takesArgument = true),
        CommandSuggestion("/skills", "Browse agent skills", Icons.Filled.Category, CommandAction.Skills),
        CommandSuggestion("/status", "Show session status", Icons.Filled.Speed, CommandAction.Status),
        CommandSuggestion("/goal", "Set a goal", Icons.Filled.NewReleases, takesArgument = true),
        CommandSuggestion("/interrupt", "Interrupt current run", Icons.Filled.Pause, CommandAction.Interrupt),
        CommandSuggestion("/queue", "Queue a message", Icons.Filled.HourglassBottom, takesArgument = true),
        CommandSuggestion("/btw", "Send a side note", Icons.Filled.Description, takesArgument = true),
        CommandSuggestion("/bg", "Background the agent", Icons.Filled.Backup, CommandAction.Background),
        CommandSuggestion("/background", "Background the agent", Icons.Filled.Backup, CommandAction.Background),
        CommandSuggestion("/search", "Search past sessions", Icons.Filled.Search, CommandAction.Search),
    )

    fun filter(query: String): List<CommandSuggestion> {
        if (!query.startsWith("/")) return emptyList()
        val search = query.substringAfter("/").lowercase()
        if (search.isEmpty()) return commands
        return commands.filter { it.command.substringAfter("/").contains(search, ignoreCase = true) }
    }

    fun matchCommand(text: String): Pair<CommandSuggestion, String>? {
        val trimmed = text.trim()
        if (!trimmed.startsWith("/")) return null
        val parts = trimmed.split(" ", limit = 2)
        val cmdName = parts[0].lowercase()
        val arg = parts.getOrElse(1) { "" }.trim()
        val cmd = commands.find { it.command == cmdName } ?: return null
        return cmd to arg
    }
}
