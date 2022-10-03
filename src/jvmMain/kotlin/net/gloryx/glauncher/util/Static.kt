package net.gloryx.glauncher.util

import androidx.compose.material.darkColors
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import cat.reflect.safeCast
import cat.try_
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.PrintStream
import kotlin.io.path.Path
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

object Static {
    val root get() = File(System.getProperty("user.dir"))
    var window: ComposeWindow? = null
    lateinit var scope: CoroutineScope

    const val doAuth = false
    val assetsDir get() = root.resolve("assets").absolutePath

    val scopen get() = try_ { scope }

    val colors = darkColors(color(0x00ffaf), secondary = color(0x08afd), surface = Color.DarkGray)

    val out: PrintStream = System.out
}