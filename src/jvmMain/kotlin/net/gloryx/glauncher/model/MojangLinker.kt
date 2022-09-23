package net.gloryx.glauncher.model

import kotlinx.serialization.Serializable

@Serializable
data class MojangLinker(val versions: Map<String, Version>) {
    @Serializable
    data class Version(val version: String, val clientJar: String)
}