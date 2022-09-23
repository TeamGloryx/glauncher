package net.gloryx.glauncher.util

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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