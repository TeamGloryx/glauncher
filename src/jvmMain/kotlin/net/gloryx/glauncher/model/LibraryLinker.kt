package net.gloryx.glauncher.model

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.Serializable
import net.gloryx.glauncher.util.Static

@Serializable
@JvmInline
value class LibraryLinker(val array: List<String>) {
    val mapped
        get() = array.associateWith { "https://libraries.minecraft.net/$it" }
}

object LL {
    private val cnf: Config = ConfigFactory.parseResources("static/libraries.conf")
    val sixteen = cnf.getStringList("sixteen")
        .filter { if (it.contains("windows")) Static.osName == "windows" else if (it.contains("linux")) Static.osName == "linux" else true }
        .let(::LibraryLinker)
}