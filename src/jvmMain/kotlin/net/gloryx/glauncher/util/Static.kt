package net.gloryx.glauncher.util

import androidx.compose.material.darkColors
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import cat.i
import cat.try_
import cat.ui.dlg.MaybeState
import cat.ui.dlg.getValue
import cat.ui.dlg.setValue
import com.sun.management.OperatingSystemMXBean
import kotlinx.coroutines.CoroutineScope
import net.gloryx.glauncher.logic.jre.Jre.inlineThrow
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.io.File
import java.io.PrintStream
import java.lang.management.ManagementFactory


object Static {
    val osArch: String = System.getProperty("sun.arch.data.model")
    val is32Bit = osArch.contains("86")
    val out: PrintStream = System.out
    val root get() = File(System.getProperty("user.dir"))
    val jresDir = root.resolve(".jre").also(File::mkdirs)
    var window: ComposeWindow? = null
    lateinit var scope: CoroutineScope

    const val doAuth = false
    val assetsDir get() = root.resolve("assets").absolutePath
    val physicalMemory = run {
        if (is32Bit) return@run 1024
        val os = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        os.totalMemorySize / 1e6
    }.also(out::println).i.also(out::println)

    val osName = when (hostOs) {
        OS.Linux -> "linux"
        OS.MacOS -> "osx"
        OS.Windows -> "windows"
        else -> inlineThrow()
    }

    val scopen get() = try_ { scope }

    val colors = darkColors(color(0x00ffaf), secondary = color(0x08afd), surface = Color.DarkGray)

    const val version = "28"

    var process by MaybeState<Process>()
}