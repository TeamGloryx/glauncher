package net.gloryx.glauncher.util

import androidx.compose.ui.awt.ComposeWindow
import cat.reflect.safeCast
import cat.try_
import kotlinx.coroutines.CoroutineScope
import java.io.File
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

object Static {
    const val ROOT_DIR = "."
    val root get() = File(ROOT_DIR)
    var window: ComposeWindow? = null
    lateinit var scope: CoroutineScope

    val scopen get() = try_ { scope }
}