package com.peditx.hermex.chat

import android.content.Context
import android.content.Intent
import com.peditx.hermex.core.notifications.HermexNotificationRoutes

fun shareSession(context: Context, sessionId: String, sessionTitle: String) {
    val uri = HermexNotificationRoutes.session(sessionId)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, uri)
        putExtra(Intent.EXTRA_SUBJECT, sessionTitle)
    }
    context.startActivity(Intent.createChooser(intent, "Share Session"))
}