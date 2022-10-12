package net.gloryx.glauncher.model

@kotlinx.serialization.Serializable
data class InstanceConf(
    val url: String,
    val mods: List<String>
)
