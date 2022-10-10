package net.gloryx.glauncher.logic

import cat.async.await
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.ui.Console
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.state.AuthState
import org.jetbrains.skiko.hostOs

object Launcher {
    suspend fun launch(target: LaunchTarget) {
        Console.info("Launching $target")
        if (!AuthState.isAuthenticated) {
            snackbar("You are not authenticated!", "Authenticate") {
                AuthState.authDialog = true
            }
            return
        }
        if (!target.dir.exists() || target.dir.list().isNullOrEmpty()) {
            target.install()
            snackbar("Installing ${target.normalName}...")
        }

        snackbar("Launching ${target.normalName}...")

        //Assets.prepare(target)
        target.run()


    }

    @OptIn(DelicateCoroutinesApi::class)
    fun play(target: LaunchTarget) {
        GlobalScope.launch {
            launch(target)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun start(target: LaunchTarget) {
        val dir = target.dir.absolutePath
        val args = mutableListOf(Jre.javaExecOf(target).absolutePath)

        if (System.getProperty("os.name")
                .let { it.startsWith("Windows") && it.endsWith("10") }
        ) args += listOf("-Dos.name=Windows 10", "-Dos.version=10.0")
        if (hostOs.isMacOS) args += "-XstartOnFirstThread"
        if (Static.is32Bit) args += "-Xss1M"

        args += "-Djava.library.path=\"${target.natives}\""
        args += "-Dorg.lwjgl.librarypath=${target.natives}"

        args += target.jvmArgs

        args += listOf(
            "-Dminecraft.launcher.brand=gloryx",
            "-Dminecraft.launcher.version=${Static.version}"
        )

        args += "-cp"
        val cp = target.classpath

        val classpath = if (hostOs.isWindows) cp.joinToString(";") else cp.joinToString(":")

        args += classpath

        args += target.main

        args += target.mcArgs


        val proc = ProcessBuilder().command(args).directory(target.dir).inheritIO()

        Console.debug(args)

        withContext(Dispatchers.IO) {
            Static.process = proc.start().also {
                launch {
                    it.inputReader().asFlow().flowOn(Dispatchers.IO).writeTo(Console.stream.writer())
                }
                launch {
                    it.onExit().await()
                    Static.process = null
                }
            }
        }
    }
}