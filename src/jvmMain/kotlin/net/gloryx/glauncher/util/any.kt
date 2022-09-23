package net.gloryx.glauncher.util

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow

val Any?.void get() = Unit

suspend fun <T> SharedFlow<T>.plsCollect(collector: FlowCollector<T>) {
    try {
        collect(collector)
    } catch (_: Exception) {
        return
    }
}

fun color(x: Int) = Color(x.toLong() or 0x00000000FF000000)