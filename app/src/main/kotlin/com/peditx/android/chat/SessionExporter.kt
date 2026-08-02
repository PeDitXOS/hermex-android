package com.peditx.hermex.chat

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.peditx.hermex.core.network.dto.ChatMessage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exports a chat session to markdown and shares via Android share sheet.
 * iOS SessionExport equivalent.
 */
object SessionExporter {

    fun exportToMarkdown(
        context: Context,
        sessionTitle: String,
        messages: List<ChatMessage>,
    ): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.appendLine("# $sessionTitle")
        sb.appendLine()
        sb.appendLine("Exported: ${dateFormat.format(Date())}")
        sb.appendLine("Messages: ${messages.size}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()

        for (msg in messages) {
            val role = when (msg.role) {
                "user" -> "**You**"
                "assistant" -> "**Assistant**"
                "system" -> "*System*"
                else -> msg.role?.uppercase() ?: "UNKNOWN"
            }
            val time = msg.timestamp?.let {
                dateFormat.format(Date((it * 1000).toLong()))
            } ?: ""
            sb.appendLine("### $role")
            if (time.isNotBlank()) sb.appendLine("_${time}_")
            sb.appendLine()
            sb.appendLine(msg.content.orEmpty())
            sb.appendLine()
            sb.appendLine("---")
            sb.appendLine()
        }

        val filename = "hermex-${sessionTitle.replace(Regex("[^a-zA-Z0-9]"), "_")}-${System.currentTimeMillis()}.md"
        val file = File(context.cacheDir, filename)
        file.writeText(sb.toString())
        return file
    }

    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/markdown"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Session"))
    }
}
