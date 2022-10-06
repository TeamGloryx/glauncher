package net.gloryx.glauncher.logic.target

import cat.reflect.cast
import com.typesafe.config.Config
import com.typesafe.config.ConfigValueFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gloryx.glauncher.logic.download.DownloadJob
import net.gloryx.glauncher.logic.download.Downloader
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.conf
import net.gloryx.glauncher.util.fetch
import net.gloryx.glauncher.util.json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object Assets {
    val dir get() = Static.root.resolve(".strangeness").apply { mkdirs() }
    val assets = Static.root.resolve(".assets").apply { mkdirs(); resolve("indexes").mkdirs(); resolve("objects").mkdirs() }

    suspend fun assetIndex(target: LaunchTarget): Config {
        val indexURL = target.versionManifest().conf.getConfig("assetIndex").getString("url")
        return fetch(indexURL).json().conf.withValue(
            "assetIndex",
            indexURL.split('/').last().let(ConfigValueFactory::fromAnyRef)
        ).withValue("assetIndexURL", indexURL.let(ConfigValueFactory::fromAnyRef))
    }

    suspend fun prepare(target: LaunchTarget) {
        val name = target.name.lowercase()
        val td = dir.resolve("./$name")

        val a = assetIndex(target)
        assets.resolve("indexes/${a.getString("assetIndex")}").also(File::createNewFile).writeText(fetch(a.getString("assetIndexURL")).json())
        val ai = a.getConfig("objects")!!.root().toMap()

        downloading {
            ai.mapValues { (_, b) -> b.unwrapped().cast<Map<String, Any>>()["hash"]!!.cast<String>() }
                .onEach { (_, b) ->
                    val f2 = b.take(2)
                    assets.resolve("objects/$f2").mkdirs()
                    download(
                        DownloadJob(
                            URL("http://resources.download.minecraft.net/$f2/$b"),
                            assets.resolve("objects/$f2/$b").also(File::createNewFile)
                        )
                    )
                }
        }
    }

    suspend fun unzip(url: String, dest: String) {
        val url = URL(url)
        Downloader.download(DownloadJob(url, dest))

        withContext(Dispatchers.IO) {
            val file = File(dest)
            val buf = byteArrayOf()
            val zip = ZipInputStream(FileInputStream(file))
            var entry = zip.nextEntry
            while (entry != null) {
                val newFile: File = newFile(file, entry)
                if (entry.isDirectory) {
                    if (!newFile.isDirectory && !newFile.mkdirs()) {
                        throw IOException("Failed to create directory $newFile")
                    }
                } else {
                    // fix for Windows-created archives
                    val parent = newFile.parentFile
                    if (!parent.isDirectory && !parent.mkdirs()) {
                        throw IOException("Failed to create directory $parent")
                    }

                    // write file content
                    val fos = FileOutputStream(newFile)
                    var len: Int
                    while (zip.read(buf).also { len = it } > 0) {
                        fos.write(buf, 0, len)
                    }
                    fos.close()
                }
                entry = zip.nextEntry
            }
            zip.closeEntry()
            zip.close()
        }
    }

    @Throws(IOException::class)
    fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
        val destFile = File(destinationDir, zipEntry.name)
        val destDirPath = destinationDir.canonicalPath
        val destFilePath = destFile.canonicalPath
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.name)
        }
        return destFile
    }
}