package net.gloryx.glauncher.logic.jre

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gloryx.glauncher.logic.download.DownloadJob
import net.gloryx.glauncher.logic.download.Downloader
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.ui.*
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.isNotEmpty
import net.gloryx.glauncher.util.rs
import net.gloryx.glauncher.util.waitFor
import net.lingala.zip4j.ZipFile
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.net.URL
import kotlin.system.exitProcess

object Jre {
    inline val dir get() = Static.jresDir

    val windows32 = mapOf(
        "8" to "https://cdn.azul.com/zulu/bin/zulu8.64.0.19-ca-jre8.0.345-win_i686.zip",
        "17" to "https://cdn.azul.com/zulu/bin/zulu17.36.19-ca-jre17.0.4.1-win_i686.zip"
    )
    val windows64 = mapOf(
        "8" to "https://cdn.azul.com/zulu/bin/zulu8.64.0.19-ca-jre8.0.345-win_x64.zip",
        "17" to "https://cdn.azul.com/zulu/bin/zulu17.36.17-ca-jre17.0.4.1-win_x64.zip"
    )

    val linux = mapOf(
        "8" to "https://cdn.azul.com/zulu/bin/zulu8.64.0.19-ca-jre8.0.345-linux_x64.zip",
        "17" to "https://cdn.azul.com/zulu/bin/zulu17.36.17-ca-jre17.0.4.1-linux_x64.zip"
    )

    fun j(target: LaunchTarget) = dir.resolve(
        when (target.javaVersion) {
            "8" -> "eight"
            "17" -> "seventeen"
            else -> inlineThrow()
        }
    )

    fun javaExecOf(target: LaunchTarget) = j(target).resolve("bin").resolve(if (Static.osName == "windows") "java.exe" else "java")

    suspend fun download(target: LaunchTarget) {
        val j = target.javaVersion
        val dl = (when (hostOs) {
            OS.Windows -> if (Static.is32Bit) windows32[j] else windows64[j]
            OS.Linux -> linux[j]
            else -> windows64[j]
        } ?: inlineThrow()).let(::URL)

        val dest = j(target)

        if (dest.exists() && dest.isDirectory && !dest.listFiles()
                .isNullOrEmpty()
        ) return warn("Java $j already exists.")

        dest.delete()

        val zf = dest.resolveSibling("${dest.name}.zip")
        Downloader.download(DownloadJob(dl, zf))

        debug("Downloaded ${zf.rs}")

        waitFor { zf.isNotEmpty() }

        debug("I am sure zf does exist")

        val zip = ZipFile(zf)

        // fixme single directory zip-archive workaround
        val d = dl.toString().split('/').last().removeSuffix(".zip") + "/"
        zip.renameFile(d, dest.name + "/")
        debug("Renamed $d to ${dest.name}")
        dest.mkdirs()

        withContext(Dispatchers.IO) {
            zip.extractFile(dest.name + "/", dir.absolutePath + "/")
        }

        waitFor { dest.isNotEmpty() }
        zip.fileHeaders.also(Console::info)
        info("Successfully extracted java ${target.javaVersion} to ${dir.absolutePath}")

        zf.delete()
    }

    @Suppress("nothing_to_inline") // inline throw
    inline fun inlineThrow(): Nothing = try {
        throw IllegalArgumentException("It is not possible, you modded the launcher and you should get the fuck out of here.")
    } catch (a: IllegalArgumentException) {
        err(a.message!!)
        exitProcess(91)
    }
}