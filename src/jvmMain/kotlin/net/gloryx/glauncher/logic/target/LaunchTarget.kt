package net.gloryx.glauncher.logic.target

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.capitalize
import cat.collections.findOneAndReplace
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import net.gloryx.glauncher.logic.Launcher
import net.gloryx.glauncher.logic.auth.NotAuthenticatedException
import net.gloryx.glauncher.model.Mods
import net.gloryx.glauncher.model.MojangLinker
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.db.DB
import net.gloryx.glauncher.util.db.sql.AuthTable
import net.gloryx.glauncher.util.res.lang.LocalLocale
import net.gloryx.glauncher.util.res.paintable.P
import net.gloryx.glauncher.util.state.AuthState
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileInputStream
import java.util.UUID

private val json = Json {
    isLenient = true
    coerceInputValues = true
    ignoreUnknownKeys = true
}

enum class LaunchTarget(
    val version: String = "1_16_5", normalName: String? = null, val painting: ImageBitmap = P.Main.skyblock,
    val mod: String = "forge"
) {
    SKYBLOCK(normalName = "SkyBlock"),
    DAYZ(normalName = "DayZ", painting = P.Main.dayz),
    SMP("1_19_1", "Survival", P.Main.skyblock, "fabric") {
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
                    it.name.split(':')
                        .let { ls -> "${ls[1]}-${ls[2]}${if (ls.size == 4) "-${ls[3]}" else ""}.jar" }
                }" // net.fabricmc:fabric-loader:0.14.9 -> net/fabricmc/fabric-loader/fabric-loader-0.1.49.jar
            })
            add("versions/$verDir/$verDir.jar")
        }

        override val mcArgs: MutableList<String> get() = run {
            val args = mutableListOf<String>()

            // Auth
            args += "--username ${AuthState.ign}"
            args += "--accessToken ${Secret.accessToken}" // constant
            args += "--uuid ${getUUID(AuthState.ign)}"


            args
        }
    };

    open suspend fun run() {}

    protected fun checkPassword() {

    }

    val normalName = normalName ?: name.capitalize(LocalLocale)

    val dir = Static.root.resolve(".strangeness/${name.lowercase()}").also(File::mkdirs)

    val natives: String = dir.resolve("natives").absolutePath
    val mods: String = dir.resolve("mods").absolutePath

    val libraries = dir.resolve("libraries")
    val ver = version.replace('_', '.')

    open val verDir = ver

    val main = when (mod) {
        "forge" -> "cpw.mods.modlauncher.Launcher"
        "fabric" -> "net.fabricmc.loader.impl.launch.knot.KnotClient"
        else -> ""
    }

    suspend fun argPattern() =
        fetch("https://launchermeta.mojang.com/mc/game/version_manifest.json").json().asJson<MojangLinker>()

    open val mcArgs = run {
        val list = mutableListOf<String>()
        coro.launch {
            val args = fetch(argPattern().versions.first { it.id == ver && it.type == "release" }.url).json()
                .let {
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

    open val verFile = dir.resolve("versions/$ver/$ver.jar")

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

    fun getUUID(ign: String? = AuthState.ign): UUID {
        if (!AuthState.isAuthenticated) throw NotAuthenticatedException()
        var id = UUID.randomUUID()
        val uuids = DB.users.associate { it.nickname to it.uuid }

        if (ign != null && uuids.containsKey(ign)) {
            return uuids[ign]!!
        }

        while (uuids.containsValue(id)) {
            id = UUID.randomUUID()
        }

        return id
    }
}