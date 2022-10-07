package net.gloryx.glauncher.logic.target

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.capitalize
import cat.collections.findOneAndReplace
import cat.reflect.cast
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import net.gloryx.glauncher.logic.Launcher
import net.gloryx.glauncher.logic.auth.NotAuthenticatedException
import net.gloryx.glauncher.logic.download.DownloadJob
import net.gloryx.glauncher.logic.download.Downloading
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.model.Mods
import net.gloryx.glauncher.model.MojangLinker
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.db.DB
import net.gloryx.glauncher.util.db.sql.AuthTable
import net.gloryx.glauncher.util.res.lang.LocalLocale
import net.gloryx.glauncher.util.res.paintable.P
import net.gloryx.glauncher.util.state.AuthState
import net.gloryx.glauncher.util.state.AuthState.getUUID
import net.gloryx.glauncher.util.state.AuthState.ign
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.util.UUID

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
        "1_19_1",
        "Survival",
        P.Main.skyblock,
        "fabric"
    ) {
        override suspend fun run() {
            println("launch target running")
            Mods.Fabric("1.19.1").install(this)

            Launcher.start(this)
        }

        override val verDir: String = "fabric-loader-0.14.9-1.19.1"

        override val verFile: File = dir.resolve("$verDir/$verDir.jar")

        @OptIn(ExperimentalSerializationApi::class)
        override val classpath: MutableList<String> = mutableListOf<String>().apply {
            val mf =
                Json.decodeFromStream<Mods.Fabric.Manifest>(FileInputStream(dir.resolve("versions/$verDir/$verDir.json")))
            addAll(mf.libraries.map {
                "libraries/${it.name.split(':').joinToString("/")}/${
                    it.name.split(':').let { ls -> "${ls[1]}-${ls[2]}${if (ls.size == 4) "-${ls[3]}" else ""}.jar" }
                }" // net.fabricmc:fabric-loader:0.14.9 -> net/fabricmc/fabric-loader/fabric-loader-0.1.49.jar
            })
            add("versions/$verDir/$verDir.jar")
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
    },
    VANILLA(normalName = "Vanilla", mod = "vanilla") {
        private suspend fun vm() = versionManifest().conf
        private suspend fun Downloading.libraries() {
            val c = vm().getConfigList("libraries")

            c.forEach { cnf ->
                if (cnf.getConfigList("rules").any {
                        it.getString("action") == "allow" && if (it.hasPath("os.name")) Static.osName == it.getString("os.name") else true
                    } || cnf.getConfigList("rules").isEmpty())
                    library(cnf.getString("downloads.artifact.url"), cnf.getString("name"))
            }
        }

        override suspend fun install() {
            Jre.download(this)

            downloading {
                download(DownloadJob(URL(vm().getConfig("downloads.client").getString("url")!!), verFile)) // 1.16.5.jar

                download(
                    DownloadJob(
                        URL(argPattern().versions.first { it.id == ver && it.type == "release" }.url),
                        vdf.resolve("$ver.json")
                    )
                )

                libraries()
            }
        }

        override suspend fun run() {
            Launcher.start(this)
        }

        override val mcArgs: MutableList<String> get() = run {
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
    };

    open suspend fun run() {}

    open suspend fun install() {}

    val normalName = normalName ?: name.capitalize(LocalLocale)

    val dir = Static.root.resolve(".strangeness/${name.lowercase()}").also(File::mkdirs)

    val natives: String = dir.resolve("natives").absolutePath

    val libraries = dir.resolve("libraries")
    val ver = version.replace('_', '.')

    open val verDir = ver
    open val vdf = dir.resolve("versions/$ver").also(File::mkdirs)

    val main = when (mod) {
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

    open val classpath = mutableListOf<String>().also {
        getCp(it, libraries)
        it.add("versions/$verDir/$verDir.jar")
    }

    open val jvmArgs: List<String> = listOf()

    private fun getCp(list: MutableList<String>, file: File) {
        if (file.isDirectory) {
            for (i in file.listFiles()!!) {
                if (i.isDirectory) getCp(list, i)
            }
        } else {
            if (file.extension == "jar") {
                list.add(file.toRelativeString(Static.root))
            }
        }
    }
}