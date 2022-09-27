package net.gloryx.glauncher.logic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.state.AuthState
import net.gloryx.glauncher.util.state.MainScreen
import org.jetbrains.skiko.hostOs

object Launcher {
    suspend fun launch(target: LaunchTarget) {
        println("Launching $target")
        if (!AuthState.isAuthenticated) {
            MainScreen.scaffold?.snackbarHostState?.showSnackbar("You are not authenticated!", "Authenticate")
            AuthState.authDialog = true
            return
        }
        //Assets.prepare(target)
        target.run()

        println(target.mcArgs)

        //! DO LAST, IT SUSPENDS UNTIL THE SNACKBAR IS DISMISSED!!!
        MainScreen.scaffold?.snackbarHostState?.showSnackbar("Launching ${target.normalName}...", "Dismiss")
    }

    fun play(target: LaunchTarget) {
        GlobalScope.launch {
            launch(target)
        }
    }

    suspend fun start(target: LaunchTarget) {
        val dir = target.dir.absolutePath
        val args = mutableListOf("java")
        args += "-Djava.library.path=\"${target.natives}\""

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