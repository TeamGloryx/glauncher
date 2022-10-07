package net.gloryx.glauncher.logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.logic.target.Assets
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.ui.Console
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.rs
import net.gloryx.glauncher.util.snackbar
import net.gloryx.glauncher.util.state.AuthState
import net.gloryx.glauncher.util.state.MainScreen
import org.jetbrains.skiko.hostOs

object Launcher {
    suspend fun launch(target: LaunchTarget) {
        Console.info("Launching $target")
        if (!AuthState.isAuthenticated) {
            snackbar("You are not authenticated!", "Authenticate")
            AuthState.authDialog = true
            return
        }
        if (!target.dir.exists() || target.dir.list().isNullOrEmpty()) target.install()

        Assets.prepare(target)
        target.run()

        Console.info(target.mcArgs)

        snackbar("Launching ${target.normalName}...", "Dismiss")
    }

    fun play(target: LaunchTarget) {
        GlobalScope.launch {
            launch(target)
        }
    }

    suspend fun start(target: LaunchTarget) {
        val dir = target.dir.absolutePath
        val args = mutableListOf(Jre.j(target).rs)

        if (System.getProperty("os.name")
                .let { it.startsWith("Windows") && it.endsWith("10") }
        ) args += listOf("-Dos.name=Windows 10", "-Dos.version=10.0")
        if (hostOs.isMacOS) args += "-XstartOnFirstThread"
        if (Static.is32Bit) args += "-Xss1M"

        args += "-Djava.library.path=\"${target.natives}\""

        args += listOf(
            "-Dminecraft.launcher.brand=gloryx",
            "-Dminecraft.launcher.version=${Static.version}",
        )

        args += "-cp"
        val cp = target.classpath

        val classpath = if (hostOs.isWindows) cp.joinToString(";") else cp.joinToString(":")

        args += classpath

        args += target.main

        args += target.mcArgs


        val proc = ProcessBuilder().command(args).inheritIO()

        println(args)

        withContext(Dispatchers.IO) {
            proc.start()
        }
    }
}