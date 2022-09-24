package net.gloryx.glauncher.model

import kotlinx.serialization.Serializable

@Serializable
data class MojangLinker(val latest: Latest, val versions: List<Version>) {
    @Serializable
    data class Latest(
        val release: String,
        val snapshot: String
    )

    @Serializable
    data class Version(
        val id: String,
        val type: String,
        val url: String,
        val time: String,
        val releaseTime: String
    )

    @Serializable
    data class VersionManifest(val arguments: Arguments) {
        @Serializable
        data class Arguments(val game: List<String>, val jvm: List<String>)
    }
}