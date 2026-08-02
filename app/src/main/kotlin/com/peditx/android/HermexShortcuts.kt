package com.peditx.hermex

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon

/**
 * Android App Shortcuts — iOS AppIntents equivalent.
 * Registers dynamic shortcuts for quick actions.
 */
object HermexShortcuts {
    private const val NEW_CHAT_ID = "new_chat"
    private const val NEW_CHAT_VOICE_ID = "new_chat_voice"

    fun registerShortcuts(context: Context) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java) ?: return

        val newChatIntent = Intent(context, MainActivity::class.java).apply {
            action = "com.peditx.hermex.ACTION_NEW_CHAT"
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val newChatShortcut = ShortcutInfo.Builder(context, NEW_CHAT_ID)
            .setShortLabel("New Chat")
            .setLongLabel("Start New Chat")
            .setIcon(Icon.createWithResource(context, android.R.drawable.ic_menu_send))
            .setIntent(newChatIntent)
            .build()

        val newVoiceIntent = Intent(context, MainActivity::class.java).apply {
            action = "com.peditx.hermex.ACTION_NEW_CHAT_VOICE"
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val newVoiceShortcut = ShortcutInfo.Builder(context, NEW_CHAT_VOICE_ID)
            .setShortLabel("Voice Chat")
            .setLongLabel("New Chat with Voice")
            .setIcon(Icon.createWithResource(context, android.R.drawable.ic_btn_speak_now))
            .setIntent(newVoiceIntent)
            .build()

        shortcutManager.dynamicShortcuts = listOf(newChatShortcut, newVoiceShortcut)
    }

    fun handleIntent(intent: Intent?): String? {
        return when (intent?.action) {
            "com.peditx.hermex.ACTION_NEW_CHAT" -> "new_chat"
            "com.peditx.hermex.ACTION_NEW_CHAT_VOICE" -> "new_chat_voice"
            else -> null
        }
    }
}
