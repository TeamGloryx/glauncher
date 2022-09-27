package net.gloryx.glauncher.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.fabricmc.installer.LoaderVersion
import net.fabricmc.installer.client.ClientInstaller
import net.fabricmc.installer.util.InstallerProgress
import net.fabricmc.installer.util.MetaHandler
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.Static
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

object Mods {
    interface Conf {
        suspend fun install(target: LaunchTarget)
    }

    @Serializable
    data class Fabric(val version: String, val loaderVersion: String = "0.14.9") : Conf {
        val fabricLoader inline get() = "fabric-loader-$loaderVersion-$version"
        override suspend fun install(target: LaunchTarget) {
            println("Fabric running with $version and $loaderVersion")
            val fn = target.dir.resolve("versions").listFiles()!!.first().let { it.resolve("${it.name}.jar") }
            if (fn.let { it.exists() && it.length() != 0L }) return println("Fabric is already installed.")
            fn.delete()
            ClientInstaller.install(
                target.dir.toPath(), version, LoaderVersion(
                    MetaHandler("v2/versions/loader").also(MetaHandler::load).getLatestVersion(false).version
                ), InstallerProgress.CONSOLE
            )
            fn.delete()
            downloading {
                val fl =
                    library("https://maven.fabricmc.net/net/fabricmc/fabric-loader/$loaderVersion/$fabricLoader.jar").also {
                        it.renameTo(
                            it.resolveSibling("fabric-loader-$loaderVersion-$version.jar")
                        )
                    }
                withContext(Dispatchers.IO) {
                    fl.copyTo(fn, true)
                }
            }
        }

        @Serializable
        data class Manifest(
            val arguments: Arguments, val id: String, val inheritsFrom: String, val libraries: List<Library>,
            val mainClass: String, val releaseTime: String, val time: String, val type: String
        )

        @Serializable
        data class Arguments(
            val game: List<String>, val jvm: List<String>
        )

        @Serializable
        data class Library(
            val name: String, val url: String
        )
    }
}