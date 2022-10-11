package net.gloryx.glauncher.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import java.io.File
import java.io.StringWriter
import java.nio.charset.Charset

val Charsets.Default: Charset
    get() = UTF_8

fun File.textAsFlow(charset: Charset = Charsets.Default) = reader(charset).asFlow().flowOn(Dispatchers.IO)

suspend fun File.readLinesAsync(charset: Charset = Charsets.Default) = textAsFlow(charset).toList()
suspend fun File.readAsync(charset: Charset = Charsets.Default) = StringWriter().also { textAsFlow(charset).writeTo(it) }.toString()

fun File.nullIfEmpty(): File? = if (isEmpty()) null else this