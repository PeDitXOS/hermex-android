package com.peditx.hermex.core.network

import com.peditx.hermex.core.storage.CookieStore

/** Shared in-memory [CookieStore] test double -- avoids needing a real Android Context/DataStore. */
internal class FakeCookieStore : CookieStore {
    var stored: String? = null
    override suspend fun save(serializedCookies: String) { stored = serializedCookies }
    override suspend fun load(): String? = stored
    override suspend fun clear() { stored = null }
}
