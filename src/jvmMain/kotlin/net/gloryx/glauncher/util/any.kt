package net.gloryx.glauncher.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import java.io.File
import java.io.OutputStream
import java.nio.ByteBuffer
import kotlin.reflect.KProperty

val Any?.void get() = Unit

suspend fun <T> SharedFlow<T>.plsCollect(collector: FlowCollector<T>) {
    try {
        collect(collector)
    } catch (_: Exception) {
        return
    }
}

fun color(x: Int) = Color(x.toLong() or 0x00000000FF000000)

interface ComposableDelegator<H, V> {
    @Composable
    operator fun getValue(that: H, prop: KProperty<*>): V
}

fun String.camelToSnake(): String {
    // Empty String
    var result = ""

    // Append first character(in lower case)
    // to result string
    val c = this[0]
    result += c.lowercaseChar()

    // Traverse the string from
    // ist index to last index
    for (i in 1 until this.length) {
        val ch = this[i]

        // Check if the character is upper case
        // then append '_' and such character
        // (in lower case) to result string
        if (Character.isUpperCase(ch)) {
            result += '_'
            result = (result + ch.lowercaseChar())
        } else {
            result += ch
        }
    }

    // return the result
    return result
}

val coro get() = Static.scope

suspend fun waitFor(step: Long = 500, expr: () -> Boolean) { while (!expr()) delay(step) }

operator fun File.getValue(that: Any?, prop: KProperty<*>) = readText()
operator fun File.setValue(that: Any?, prop: KProperty<*>, data: String) = writeText(data)

class VarOutputStream(state: MutableState<String>) : OutputStream() {
    var state by state
    override fun write(b: Int) {
        state += Charsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(byteArrayOf(b.toByte()))).toString()
    }

    override fun flush() {
        
    }
}