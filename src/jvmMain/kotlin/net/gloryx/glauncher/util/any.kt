@file:Suppress("NOTHING_TO_INLINE")

package net.gloryx.glauncher.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import cat.i
import cat.ui.dlg.getValue
import cat.ui.dlg.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.newCoroutineContext
import net.gloryx.glauncher.logic.download.Downloader
import okio.source
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
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

inline fun color(x: Int) = catfish.winder.colors.color(x)

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

/**
 * Waits for [expr] return value to become true, else delay for [step]ms
 * **BEWARE** [expr] is executed every [step]ms (e.g. very frequently)
 */
suspend fun waitFor(step: Long = 500, expr: () -> Boolean) {
    while (!expr()) delay(step)
}

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

fun File.isEmpty() = (if (isDirectory) length() == 0L || listFiles().isNullOrEmpty() else length() == 0L) || !exists()
fun File.isNotEmpty() = !isEmpty()

inline fun File.mk() = also(File::mkdirs)

inline fun <T> Flow<T>.io() = flowOn(Dispatchers.IO)
inline fun <T> Flow<T>.iod() = io()

fun InputStream.asFlow() = buffered().iterator().asFlow()
fun Reader.asFlow() = buffered().lineSequence().asFlow()

suspend fun Flow<String>.writeTo(writer: Writer, on: suspend (Int, String) -> Unit = { _, _ -> }) = collectIndexed { i, it -> writer.write(it); on(i, it) }
suspend fun Flow<Byte>.copyTo(out: OutputStream, on: suspend (Int, Byte) -> Unit = { _, _ -> }) = collectIndexed { i, it -> out.write(it.i); on(i, it) }

inline fun <T> MutableState<T>.get() = value
fun <T> MutableState<T>.set(value: T) = also { it.value = value }