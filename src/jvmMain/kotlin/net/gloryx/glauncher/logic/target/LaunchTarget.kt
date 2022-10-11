package net.gloryx.glauncher.logic.target

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.capitalize
import cat.collections.findOneAndReplace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import net.gloryx.glauncher.logic.Launcher
import net.gloryx.glauncher.logic.download.DownloadJob
import net.gloryx.glauncher.logic.download.Downloading
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.model.LL
import net.gloryx.glauncher.model.Mods
import net.gloryx.glauncher.model.MojangLinker
import net.gloryx.glauncher.ui.debug
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.res.lang.LocalLocale
import net.gloryx.glauncher.util.res.paintable.P
import net.gloryx.glauncher.util.state.AuthState.getUUID
import net.gloryx.glauncher.util.state.AuthState.ign
import net.gloryx.glauncher.util.state.SettingsState
import net.lingala.zip4j.ZipFile
import java.io.File
import java.net.URL
import kotlin.collections.addAll as plsAddAll

private val json = Json {
    isLenient = true
    coerceInputValues = true
    ignoreUnknownKeys = true
}

enum class LaunchTarget(
    val version: String = "1_16_5", normalName: String? = null, val painting: ImageBitmap = P.Main.skyblock,
    val mod: String = "forge", val javaVersion: String = "8"
) {
    SKYBLOCK(normalName = "SkyBlock"), DAYZ(normalName = "DayZ", painting = P.Main.dayz), SMP(
        "1_19_1", "Survival", P.Main.survival, "fabric", "17"
    ) {
        private val mll = LL.nineteen.mapped.also(::debug)
        override suspend fun run() {
            Launcher.start(this)
        }

        override suspend fun install() {
            Mods.Fabric("1.19.1").install()
        }

        override val verDir: String = "fabric-loader-0.14.9-1.19.1"

        override val verFile: File = dir.resolve("versions/$verDir/$verDir.jar")

        override val classpath: MutableList<String> = run {
            val list = mutableSetOf<String>()

            libraries.walkBottomUp().filter { it.isFile && it.extension == "jar" }.map(File::getAbsolutePath).let(list::plsAddAll)

            list.add(dir.resolve("versions/$ver/$ver.jar").absolutePath)

            list.toMutableList()
        }

        override val mcArgs: MutableList<String>
            get() = run {

                val args = mutableListOf(
                    "--username",
                    ign ?: "Steve",
                    "--version",
                    ver,
                    "--gameDir",
                    dir.rs,
                    "--assetsDir",
                    Assets.assets.rs,
                    "--assetIndex",
                    "1.19",
                    "--uuid",
                    "${getUUID()}",
                    "--accessToken",
                    Secret.token,
                    "--userType",
                    "mojang",
                    "--versionType",
                    "release"
                )

                args
            }

        override val jvmArgs: List<String> = listOf("-DFabricMcEmu=net.minecraft.client.main.Main")
    },
    SMP_1_16(normalName = "SMP 1.16.5", mod = "vanilla", painting = P.Main.oldSMP) {
        override fun canBeDisplayed() = SettingsState.oldSMP.value
        private suspend fun vm() = versionManifest().conf

        private val mll = LL.sixteen.mapped.also(::debug)

        override suspend fun Downloading.doLibraries() {
            mll.forEach { (a, b) -> download(DownloadJob(URL(b), Static.root.resolve("libraries/sixteen/$a"))) }
        }

        override suspend fun Downloading.doNatives() {
            val ll = LL.sixteen.natives.also(::debug)

            ll.forEach { (a, b) ->
                val file = dir.resolve("natives/$a")
                download(DownloadJob(URL(b), file))
                waitFor { file.isNotEmpty() }
                withContext(Dispatchers.IO) {
                    ZipFile(file).use { zip ->
                        zip.fileHeaders.also(::debug)
                            .filter { it.fileName.endsWith(".so") || it.fileName.endsWith(".dll") }
                            .forEach { zip.extractFile(it, natives) }
                    }
                }

                File(natives).listFiles { it: File -> it.isDirectory }?.forEach(File::deleteRecursively)
            }

        }

        override suspend fun install() {
            Jre.download(this)

            downloading {
                doLibraries()
                doNatives()

                download(
                    DownloadJob(
                        URL("https://launcher.mojang.com/v1/objects/37fd3c903861eeff3bc24b71eed48f828b5269c8/client.jar"),
                        verFile
                    )
                ) // 1.16.5.jar

                download(
                    DownloadJob(
                        URL(argPattern().versions.first { it.id == ver && it.type == "release" }.url),
                        vdf.resolve("$ver.json")
                    )
                )
            }
        }

        override val classpath: MutableList<String> = run {
            val list = mutableListOf<String>()

            getCp(list, libraries)

            list.add(verFile.absolutePath)

            list.distinct().toMutableList()
        }

        override suspend fun run() {
            Launcher.start(this)
        }

        override val mcArgs: MutableList<String>
            get() = run {
                val args = mutableListOf(
                    "--username",
                    ign ?: "Steve",
                    "--version",
                    ver,
                    "--gameDir",
                    dir.absolutePath,
                    "--assetsDir",
                    Assets.assets.absolutePath,
                    "--assetIndex",
                    "1.16",
                    "--uuid",
                    "${getUUID()}",
                    "--accessToken",
                    Secret.token,
                    "--userType",
                    "mojang",
                    "--versionType",
                    "release"
                )

                args
            }

        override val main: String = "net.minecraft.client.main.Main"

        override fun getCp(list: MutableList<String>, file: File) { // faster
            list.addAll(mll.map { (a) -> file.resolve(a) }.filter { it.isNotEmpty() }.map(File::getAbsolutePath))
        }
    };

    open fun canBeDisplayed() = true

    open suspend fun run() {}

    open suspend fun install() {}

    open suspend fun Downloading.doLibraries() {}
    open suspend fun Downloading.doNatives() {}

    val normalName = normalName ?: name.capitalize(LocalLocale)

    val dir = Static.root.resolve(".strangeness/${name.lowercase()}").mk()

    val natives: String = dir.resolve("natives").mk().absolutePath

    val libraries = Static.root.resolve(
        "libraries/${
            if (version.contains("19")) "nineteen" else "sixteen"
        }"
    ).mk()
    val ver = version.replace('_', '.')

    open val verDir = ver
    open val vdf = dir.resolve("versions/$ver").mk()

    open val main = when (mod) {
        "forge" -> "cpw.mods.modlauncher.Launcher"
        "fabric" -> "net.fabricmc.loader.impl.launch.knot.KnotClient"
        "vanilla" -> "net.minecraft.client.main.Minecraft"
        else -> ""
    }

    suspend fun versionManifest() =
        fetch(argPattern().versions.first { it.id == ver && it.type == "release" }.url).json()

    suspend fun argPattern() =
        fetch("https://launchermeta.mojang.com/mc/game/version_manifest.json").json().asJson<MojangLinker>()

    open val mcArgs = run {
        val list = mutableListOf<String>()
        coro.launch {
            val args = fetch(argPattern().versions.first { it.id == ver && it.type == "release" }.url).json().let {
                json.parseToJsonElement(it).jsonObject["arguments"]!!.jsonObject["game"]!!.jsonArray.filterIsInstance<JsonPrimitive>()
                    .map(JsonPrimitive::toString)
            }.chunked(2).associate { it[0] to it[1] }.toMutableMap()

            val mapping = mutableMapOf<String, String>()

            mapping["version_type"] = "release"
            mapping["version_name"] = ver
            mapping["game_directory"] = dir.absolutePath

            val aad = dir.resolve("assets").absolutePath

            mapping.forEach { (a, b) ->
                args.findOneAndReplace({ (w, d) -> d.contains(a) }) { (q, it) -> q to it.replace("\${$q}", b) }
            }

            //list.addAll(args)
        }
        list
    }

    open val verFile = dir.resolve("versions/$ver/$ver.jar").also(File::createNewFile)

    open val classpath
        get() = mutableListOf<String>().also {
            getCp(it, libraries)
            it.add(verFile.absolutePath)
        }

    open val jvmArgs: List<String> = listOf()

    protected open fun getCp(list: MutableList<String>, file: File) {
        fun addIt() {
            if (file.extension == "jar") list.add(file.absolutePath)
        }
        if (file.isDirectory) for (i in file.listFiles()!!) getCp(list, i) else addIt()
    }
}