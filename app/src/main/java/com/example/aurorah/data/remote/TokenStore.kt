package com.example.aurorah.data.remote

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/** Keeps the API bearer token encrypted at rest. */
object TokenStore {
    private const val FILE = "aurorah_secure_prefs"
    private const val KEY_TOKEN = "api_token"
    private const val KEY_EMAIL = "api_email"

    @Volatile
    private var cached: String? = null
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun prefs() = appContext?.let { ctx ->
        val key = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(
            ctx, FILE, key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun get(): String? {
        cached?.let { return it }
        return runCatching { prefs()?.getString(KEY_TOKEN, null) }.getOrNull().also { cached = it }
    }

    fun email(): String? = runCatching { prefs()?.getString(KEY_EMAIL, null) }.getOrNull()

    fun save(token: String?, email: String? = null) {
        cached = token
        runCatching {
            prefs()?.edit()?.apply {
                if (token == null) remove(KEY_TOKEN) else putString(KEY_TOKEN, token)
                if (token == null || email == null) remove(KEY_EMAIL) else putString(KEY_EMAIL, email)
            }?.apply()
        }
    }

    fun clear() = save(null)
}
