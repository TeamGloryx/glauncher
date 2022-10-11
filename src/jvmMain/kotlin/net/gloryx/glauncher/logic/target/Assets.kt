package net.gloryx.glauncher.logic.target

import cat.reflect.cast
import com.typesafe.config.Config
import com.typesafe.config.ConfigValueFactory
import kotlinx.coroutines.*
import net.gloryx.glauncher.logic.download.DownloadJob
import net.gloryx.glauncher.logic.download.Downloader
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.util.*
import java.awt.SystemColor.info
import java.io.File
import java.net.URL


object Assets {
    val dir get() = Static.root.resolve(".strangeness").apply { mkdirs() }
    val assets =
        Static.root.resolve(".assets").apply { mkdirs(); resolve("indexes").mkdirs(); resolve("objects").mkdirs() }

    val indexes = assets.resolve("indexes")
    val objs = assets.resolve("objects")

    suspend fun assetIndex(target: LaunchTarget): Config {
        val indexURL = target.versionManifest().conf.getConfig("assetIndex").getString("url")
        return (indexes.resolve(
            "${
                target.ver
                    .take(4)
                    .trim('.')
            }.json"
        ).nullIfEmpty()?.readAsync() ?: fetch(indexURL).json()).conf.withValue(
            "assetIndex",
            indexURL.split('/').last().let(ConfigValueFactory::fromAnyRef)
        ).withValue("assetIndexURL", indexURL.let(ConfigValueFactory::fromAnyRef))
    }

    suspend fun prepare(target: LaunchTarget) {
        val name = target.name.lowercase()
        val td = dir.resolve("./$name")

        val a = assetIndex(target)
        assets.resolve("indexes/${a.getString("assetIndex")}").also(File::createNewFile)
            .writeText(fetch(a.getString("assetIndexURL")).json())
        val ai = a.getConfig("objects")!!.root().toMap()

        downloading {
            ai.mapValues { (_, b) -> b.unwrapped().cast<Map<String, Any>>()["hash"]!!.cast<String>() }
                .onEach { (_, b) ->
                    val f2 = b.take(2)
                    objs.resolve(f2).mkdirs()
                    download(
                        DownloadJob(
                            URL("http://resources.download.minecraft.net/$f2/$b"),
                            objs.resolve("$f2/$b").also(File::createNewFile)
                        )
                    )
                }
        }
    }

    suspend fun check(target: LaunchTarget) {
        withContext(Dispatchers.IO) {
            launch {
                val objects = assetIndex(target).getConfig("objects").root()

                for ((_, _info) in objects) {
                    val info = _info.unwrapped().cast<Map<String, Any>>()

                    val (h2, hash) = info["hash"]!!.toString().let { listOf(it.take(2), it) }
                    val size = info["size"]!!.toString().toLong()

                    val file = objs.resolve("$h2/$hash")
                    if (file.isEmpty() || (file.length() != size).also { if (it) file.delete() }) Downloader.download(
                        DownloadJob(URL("https://resources.download.minecraft.net/$h2/$hash"), file)
                    )
                }
            }
        }
    }
}