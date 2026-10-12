package com.example.aurorah

import android.app.Application
import com.example.aurorah.data.local.AppDatabase
import com.example.aurorah.data.preferences.UserPreferencesRepository
import com.example.aurorah.data.repository.AurorahRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AurorahApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val userPreferences by lazy { UserPreferencesRepository(this) }
    val repository by lazy { AurorahRepository(database, userPreferences) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AurorahApplication
            private set
    }
}
