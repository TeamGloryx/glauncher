package net.gloryx.glauncher.model

import kotlinx.serialization.Serializable
import net.fabricmc.installer.LoaderVersion
import net.fabricmc.installer.client.ClientInstaller
import net.fabricmc.installer.util.InstallerProgress
import net.fabricmc.installer.util.MetaHandler
import net.gloryx.glauncher.logic.target.LaunchTarget

object Mods {
    interface Conf {
        suspend fun install(target: LaunchTarget)
    }

    @Serializable
    data class Fabric(val version: String, val loaderVersion: String? = null) : Conf {
        override suspend fun install(target: LaunchTarget) {
            println("Fabric running with $version and $loaderVersion")
            if (target.dir.resolve("versions").listFiles()!!.first().let { it.resolve("${it.name}.jar") }
                    .exists()) return println("Fabric is already installed.")
            ClientInstaller.install(
                target.dir.toPath(),
                version,
                LoaderVersion(
                    MetaHandler("v2/versions/loader").also(MetaHandler::load).getLatestVersion(false).version
                ),
                InstallerProgress.CONSOLE
            )
        }

        @Serializable
        data class Manifest(
            val arguments: Arguments,
            val id: String,
            val inheritsFrom: String,
            val libraries: List<Library>,
            val mainClass: String,
            val releaseTime: String,
            val time: String,
            val type: String
        )

        @Serializable
        data class Arguments(
            val game: List<String>,
            val jvm: List<String>
        )

        @Serializable
        data class Library(
            val name: String,
            val url: String
        )
    }
}