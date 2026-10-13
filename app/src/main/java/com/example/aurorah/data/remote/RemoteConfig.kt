package com.example.aurorah.data.remote

import com.example.aurorah.BuildConfig

object RemoteConfig {
    /** Set per build type in app/build.gradle.kts (debug: emulator host, release: your HTTPS API). */
    val BASE_URL: String = BuildConfig.API_BASE_URL
    val SERVER_NAME: String = BASE_URL.removePrefix("https://").removePrefix("http://").trimEnd('/')
    const val DATABASE_NAME = "Aurorah API"
    const val AUTHENTICATION_TYPE = "Bearer token"
    const val ADMIN_LOGIN = "n/a"
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
}
