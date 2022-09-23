package net.gloryx.glauncher.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.target.LaunchTarget

object Mods {
    interface Conf {
        suspend fun run(target: LaunchTarget)
    }
    @Serializable data class Fabric(val version: String, val loaderVersion: String) : Conf {
        override suspend fun run(target: LaunchTarget) {
            downloading {
                val path = library("https://maven.fabricmc.net/net/fabricmc/fabric-installer/0.11.1/fabric-installer-0.11.1.jar").absolutePath
                val builder = ProcessBuilder("java", "-jar", path, "client", "-dir", "\"${target.dir.absolutePath}\"", "")
                val proc: Process = withContext(Dispatchers.IO) {
                    builder.start()
                }

                
            }
        }
    }
}