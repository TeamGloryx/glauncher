package net.gloryx.glauncher.util.nbt

import kotlinx.serialization.Serializable
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.alsoCreate
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser
import org.jglrxavpok.hephaistos.nbt.NBTWriter

object ServersDatFile {
    fun install(target: LaunchTarget) {
        NBTWriter(target.dir.resolve("servers.dat").alsoCreate(), CompressedProcesser.NONE).write(
            when (target) {
                LaunchTarget.SMP -> ServersDat.smp
                else -> ServersDat.normal
            }
        )
    }
}

@Serializable
data class ServersDat(val servers: List<Server>) {
    @Serializable
    data class Server(val icon: String = "", val ip: String, val name: String, val acceptsTextures: Boolean = true)

    companion object {
        val smp = ServersDat(
            listOf(Server("", "smp.gloryx.net", "Gloryx SMP"), Server("", "gloryx.net", "Gloryx"))
        )

        val normal = ServersDat(
            listOf(Server("", "gloryx.net", "Gloryx"))
        )
    }
}