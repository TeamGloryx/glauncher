package net.gloryx.glauncher.logic.jre

import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.target.Assets
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.ui.Console
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.rs
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

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
        "17" to "https://cdn.azul.com/zulu/bin/zulu17.36.19-ca-jre17.0.4.1-linux_i686.zip"
    )

    fun j(target: LaunchTarget) = dir.resolve(when (target.javaVersion) {
        "8" -> "eight"
        "17" -> "seventeen"
        else -> inlineThrow()
    })

    suspend fun download(target: LaunchTarget) = downloading {
        val j = target.javaVersion
        val dl = when (hostOs) {
            OS.Windows -> if (Static.is32Bit) windows32[j] else windows64[j]
            OS.Linux -> linux[j]
            else -> windows64[j]
        } ?: inlineThrow()

        val dest = j(target)

        if (dest.exists() && dest.isDirectory && !dest.listFiles().isNullOrEmpty()) return Console.warn("Java $j already exists.")

        val uz = Assets.unzip(dl, dest.rs)
        uz.listFiles()!![0].listFiles()!!.filter { it.isDirectory }.forEach { it.copyTo(uz) }
    }

    @Suppress("nothing_to_inline") // inline throw
    inline fun inlineThrow(): Nothing = throw IllegalArgumentException("It is not possible, you modded the launcher and you should get the fuck out of here.")
}