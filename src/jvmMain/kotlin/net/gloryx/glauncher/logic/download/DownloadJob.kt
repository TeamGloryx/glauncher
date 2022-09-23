package net.gloryx.glauncher.logic.download

import cat.try_
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.await
import okhttp3.Request
import java.io.File
import java.net.URL

data class DownloadJob(val url: URL, val destination: File = Static.root.resolve("./assets")) {
    init {
        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
        if (!destination.exists())
            destination.createNewFile()
    }
    private var request = Request.Builder().get().url(url)
    fun req(scope: Request.Builder.() -> Unit = {}) = req(request.apply(scope).build())
    fun req(request: Request) { this.request = request.newBuilder() }

    constructor(url: URL, destination: String) : this(url, File(destination).relativeTo(Static.root))

    suspend fun get() = try_ { Downloader.client.newCall(request.build()).await() }

    val file get() = destination
}