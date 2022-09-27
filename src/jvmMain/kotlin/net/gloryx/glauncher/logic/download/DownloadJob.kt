package net.gloryx.glauncher.logic.download

import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.async.LazyDeferred
import net.gloryx.glauncher.util.await
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.net.URL

data class DownloadJob(val url: URL, val destination: File = Static.root.resolve("./assets")) : LazyDeferred<Response> {
    constructor(destDir: String, url: URL) : this(url, Static.root.resolve(destDir).resolve(url.file.split('/').last()).also(::println))

    private var compl: Response? = null

    override val maybe: Response?
        get() = compl

    init {
        if (destination.parentFile?.exists() == false) destination.parentFile?.mkdirs()
        if (!destination.exists())
            destination.createNewFile()
    }
    private var request = Request.Builder().get().url(url)
    fun req(scope: Request.Builder.() -> Unit = {}) = req(request.apply(scope).build())
    fun req(request: Request) { this.request = request.newBuilder() }

    constructor(url: URL, destination: String) : this(url, File(destination).relativeTo(Static.root))

    override suspend fun await(): Response = Downloader.client.newCall(request.build()).await()


    val file get() = destination
}