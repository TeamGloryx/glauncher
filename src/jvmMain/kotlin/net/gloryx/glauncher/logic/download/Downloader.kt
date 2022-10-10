package net.gloryx.glauncher.logic.download

import io.ktor.util.cio.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val downloader = CoroutineScope(Dispatchers.IO.limitedParallelism(30))

    private suspend inline fun register() {
        downloads.plsCollect { job ->
            job.tryAwait()?.let {
                if (job.destination.length() != 0L) return@plsCollect Console.warn("./${job.destination.rs} already exists")
                Console.info("Url: \"${job.url}\"; Dst: \"./${job.destination.toRelativeString(Static.root)}\"")
                downloader.launch {
                    it.body?.let { body ->
                        if (body.contentType()?.type?.startsWith("text") == true) body.charStream()
                            .use { job.destination.writer().use { fs -> it.copyTo(fs) } }
                        else body.byteStream().use { FileOutputStream(job.destination).use { fs -> it.copyTo(fs) } }
                    }
                }
            } ?: Console.warn("${job.url} failed.")
        }
    }

    suspend fun download(job: DownloadJob) = _downloads.emit(job)
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