package com.peditx.hermex.core.notifications

/**
 * Pure (no Android deps) decision of whether [HermexNotifier] should attempt to post a
 * notification -- extracted so the two independent gates (Hermex's own in-app preference, and the
 * OS runtime permission) can be unit tested without Robolectric/mocked framework classes, mirroring
 * [com.peditx.hermex.chat.ResponseCompletionGate]'s split of pure logic from Android side effects.
 */
object NotificationGate {
    fun shouldPost(notificationsEnabledInApp: Boolean, hasSystemPermission: Boolean): Boolean =
        notificationsEnabledInApp && hasSystemPermission
}
