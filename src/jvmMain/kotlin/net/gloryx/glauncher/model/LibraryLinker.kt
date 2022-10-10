package net.gloryx.glauncher.model

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.Serializable
import net.gloryx.glauncher.util.Static

@Serializable
@JvmInline
value class LibraryLinker(val array: List<String>) {
    val mapped
        get() = array
            .filter { if (it.contains("x86")) Static.is32Bit else true }
            .associate { if (!it.startsWith("https://")) it to "https://libraries.minecraft.net/$it" else it.removePrefix("https://").removePrefix(it.removePrefix("https://").split('/')[0]) to it }

    val natives
        get() = array.filter { it.contains(Regex("windows|linux")) }
            .filter { if (it.contains("x86")) Static.is32Bit else true }
            .associateWith { "https://libraries.minecraft.net/$it" }
}

object LL {
    private val cnf: Config = ConfigFactory.parseResources("static/libraries.conf")
    val sixteen = cnf.getStringList("sixteen")
        .filter { if (it.contains("windows")) Static.osName == "windows" else if (it.contains("linux")) Static.osName == "linux" else true }
        .let(::LibraryLinker)

    val nineteen = cnf.getStringList("nineteen")
        .filter { if (it.contains("windows")) Static.osName == "windows" else if (it.contains("linux")) Static.osName == "linux" else true }
        .filter { if (it.contains("x86")) Static.is32Bit else true }
        .let(::LibraryLinker)
}