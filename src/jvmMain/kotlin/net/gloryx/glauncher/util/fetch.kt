package net.gloryx.glauncher.util

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.gloryx.glauncher.logic.download.Downloader
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.HttpMethod
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun fetch(url: String, method: String = "GET", builder: Request.Builder.() -> Unit = {}) = Downloader.client.newCall(Request.Builder()
    .method(method, null)
    .url(url)
    .apply(builder)
    .build()
).await()

fun Response.json() = body?.string() ?: ""

inline fun <reified T> String.asJson(json: Json = Json) = json.decodeFromString<T>(this)

@Throws(IOException::class)
suspend fun Call.await() = suspendCancellableCoroutine<Response> { continuation ->
    enqueue(object : Callback {
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") // I DON'T CARE!!!
        override fun onFailure(call: Call, rethrow: IOException) {
            continuation.resumeWithException(rethrow)
        }

        override fun onResponse(call: Call, response: Response) {
            continuation.resume(response)
        }
    })
}