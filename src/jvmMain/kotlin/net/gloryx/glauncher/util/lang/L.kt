package net.gloryx.glauncher.util.lang

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.gloryx.oknamer.key.Key
import net.gloryx.oknamer.key.Namespaced
import net.gloryx.oknamer.key.dot
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object L : La(Namespaced("launcher")) {
    val test by null
}

sealed class La(namespace: Namespaced) : Namespaced by namespace {
    constructor(parent: La, prefix: String) : this(parent.dot(prefix)) // launcher -> launcher.x

    operator fun Nothing?.provideDelegate(that: La, prop: KProperty<*>): ReadOnlyProperty<La, String> = Delegate(Key.lang(this@La, prop.name).asString())

    operator fun String.provideDelegate(that: La, prop: KProperty<*>): ReadOnlyProperty<La, String> = Delegate(Key.lang(this@La, this).asString())

    private class Delegate(val path: String) : ReadOnlyProperty<La, String> {
        override fun getValue(thisRef: La, property: KProperty<*>): String =
            Lang.conf[path] ?: path
    }

    companion object {
        var Lang by mutableStateOf(Language.Default)
    }
}