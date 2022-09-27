package net.gloryx.glauncher.logic.download

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import net.gloryx.glauncher.ui.Console
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.plsCollect
import okhttp3.Dns
import okhttp3.OkHttpClient
import java.net.URL

object Downloader {
    val client = OkHttpClient.Builder().build()

    private val _downloads = MutableSharedFlow<DownloadJob>()
    val downloads = _downloads.asSharedFlow()
    internal val helper = Downloading()

    fun init() {
        val scope = Static.scope
        scope.launch { register() }
    }

    private suspend inline fun register() {
        downloads.plsCollect { job ->
            job.tryAwait()?.let {
                if (job.destination.exists()) return@plsCollect println(
                    "[Download] \"./${
                        job.destination.toRelativeString(
                            Static.root
                        )
                    }\" already exists, skipping."
                )

                println("[Download] Url: \"${job.url}\"; Dst: \"./${job.destination.toRelativeString(Static.root)}\"")
                it.body?.let { body ->
                    if (body.contentType()?.type?.startsWith("text") == true) job.destination.writeText(
                        body.string(), body.contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
                    )
                    else job.destination.writeBytes(body.bytes())
                }
            } ?: println("[Download] ${job.url} failed.".also { t -> Console.text += t })
        }
    }

    suspend fun download(job: DownloadJob) = _downloads.emit(job).also { job.maybe?.body?.close() }
}

suspend fun downloading(scope: suspend Downloading.() -> Unit) = scope(Downloader.helper)

class Downloading internal constructor() {
    val loader = Downloader

    suspend fun download(job: DownloadJob) = loader.download(job).let { job.file }
    suspend fun download(url: String, destination: String) = download(DownloadJob(destination, URL(url)))

    suspend fun library(url: String) = download(url, "./libraries")
}