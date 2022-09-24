package net.gloryx.glauncher.logic.target

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gloryx.glauncher.logic.download.DownloadJob
import net.gloryx.glauncher.logic.download.Downloader
import net.gloryx.glauncher.util.Static
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object Assets {
    val dir get() = Static.root.resolve(".strangeness").apply { mkdirs() }
    suspend fun prepare(target: LaunchTarget) {
        val name = target.name.lowercase()
        val td = dir.resolve("./$name")
        /*
        if (td.exists() && td.listFiles()?.isNotEmpty() == true) {
            val hash =
                DigestUtils.md5Hex(td.listFiles()!!.asList().run {
                    subList(0, size.coerceAtLeast(0)).filter { !it.isDirectory && it.length() <= 5e+8 }
                        .map { DigestUtils.md5Hex(it.readText()) }
                }.ifEmpty { listOf(DigestUtils.md5Hex("1212jawiec12h3c1h2ionch31hnc2hncy1cn30123897123712")) }
                    .reduce { acc, it -> DigestUtils.sha1Hex("$acc$it") })
            val curHash = td.resolve("./hsh128").takeIf { it.exists() }?.readText()
            if (hash != curHash)
                dload(name)
            return
        }
        dload(name)
         */
    }

    suspend fun dload(name: String) {
        val dest = "./${dir.toRelativeString(Static.root)}/$name.zip"
        val url = URL("https://launch.gloryx.net/assets/$name.zip")
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