package net.gloryx.glauncher.util

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.OkHttpClient

object HCL {
    val client = HttpClient(OkHttp) {
        engine {
            preconfigured = OkHttpClient.Builder().apply {

            }.build()
        }
    }
}