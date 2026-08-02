package com.peditx.hermex.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * Wraps content in RTL layout direction when enabled.
 * iOS rtlChatLayoutEnabled equivalent.
 */
@Composable
fun WithRtlSupport(enabled: Boolean, content: @Composable () -> Unit) {
    if (enabled) {
        androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    } else {
        content()
    }
}
