package net.gloryx.glauncher.model

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.fabricmc.installer.LoaderVersion
import net.fabricmc.installer.client.ClientInstaller
import net.fabricmc.installer.util.InstallerProgress
import net.fabricmc.installer.util.MetaHandler
import net.gloryx.glauncher.logic.download.DownloadJob
import net.gloryx.glauncher.logic.download.Downloading
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.asJson
import net.gloryx.glauncher.util.conf
import net.gloryx.glauncher.util.mk
import java.net.URL

object Mods {
    interface Conf {
        suspend fun install(target: LaunchTarget)
    }

    @Serializable
    data class Fabric(val version: String, val loaderVersion: String = "0.14.9") : Conf {
        @kotlinx.serialization.Transient var versionData = ConfigFactory.empty()
        @kotlinx.serialization.Transient var fabData = ConfigFactory.empty()
        val fabricLoader inline get() = "fabric-loader-$loaderVersion-$version"
        override suspend fun install(target: LaunchTarget) {
            Jre.download(target)
            versionData = ConfigFactory.parseString(target.versionManifest())

            println("Fabric running with $version and $loaderVersion")
            val fn = target.dir.resolve("versions/$fabricLoader")
            withContext(Dispatchers.IO) {
                ClientInstaller.install(
                    target.dir.toPath(), version, LoaderVersion(
                        MetaHandler("v2/versions/loader").also(MetaHandler::load).getLatestVersion(false).version
                    ), InstallerProgress.CONSOLE
                )
            }

            val fl = fn.resolve("$fabricLoader.jar")
            fl.delete()

            downloading {
                download(DownloadJob(URL("https://maven.fabricmc.net/net/fabricmc/fabric-loader/$loaderVersion/fabric-loader-$loaderVersion.jar"), fl))
            }

            target.dir.resolve("libraries").deleteRecursively()

            fabData = fn.resolve("$fabricLoader.json").readText().conf

            val vd = target.dir.resolve("versions/$version").mk()

            downloading {
                download(DownloadJob(URL("https://piston-data.mojang.com/v1/objects/90d438c3e432add8848a9f9f368ce5a52f6bc4a7/client.jar"), vd.resolve("$version.jar"))) // 1.19.1.jar
                download(DownloadJob(URL(target.argPattern().versions.first { it.id == version && it.type == "release" }.url), vd.resolve("$version.json")))
                doLibraries()
            }
        }

        suspend fun Downloading.doLibraries() {
            LL.nineteen.mapped.mapValues { (_, b) -> URL(b) }.forEach { (loc, url) ->
                download(DownloadJob(url, Static.root.resolve("libraries/nineteen/$loc")))
            }
        }
    }
}