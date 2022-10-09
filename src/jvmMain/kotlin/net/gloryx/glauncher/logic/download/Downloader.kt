package net.gloryx.glauncher.logic.download

import io.ktor.util.cio.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import net.gloryx.glauncher.ui.Console
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.plsCollect
import net.gloryx.glauncher.util.rs
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

object Downloader {
    val client = OkHttpClient.Builder().build()

    private val _downloads = MutableSharedFlow<DownloadJob>()
    val downloads = _downloads.asSharedFlow()

    @PublishedApi
    internal val helper = Downloading()

    fun init() {
        val scope = Static.scope
        scope.launch { register() }
    }

    private suspend inline fun register() {
        downloads.plsCollect { job ->
            job.tryAwait()?.let {
                if (job.destination.length() != 0L) return@plsCollect Console.warn("./${job.destination.rs} already exists")
                Console.info("Url: \"${job.url}\"; Dst: \"./${job.destination.toRelativeString(Static.root)}\"")
                it.body?.let { body ->
                    if (body.contentType()?.type?.startsWith("text") == true) body.charStream().copyTo(job.destination.writer())
                    else body.byteStream().copyTo(FileOutputStream(job.destination))
                }
            } ?: Console.warn("${job.url} failed.")
        }
    }

    suspend fun download(job: DownloadJob) = _downloads.emit(job).also { job.maybe?.close() }
}

suspend inline fun downloading(scope: suspend Downloading.() -> Unit) = scope(Downloader.helper)

class Downloading internal constructor() {
    val loader = Downloader

    suspend fun download(job: DownloadJob) = loader.download(job).let { job.file }
    suspend fun download(url: String, destination: String) = download(DownloadJob(destination, URL(url)))

    suspend fun library(url: String, artifact: String? = null) = download(
        DownloadJob(
            URL(url), File(
                "./libraries${
                    if (artifact != null) "/${
                        artifact.split(":", ".").toMutableList().let {
                            it + (it.takeLast(2).joinToString("-"))
                        }.joinToString("/")
                    }.jar" else ""
                }"
            )
        )
    )
}