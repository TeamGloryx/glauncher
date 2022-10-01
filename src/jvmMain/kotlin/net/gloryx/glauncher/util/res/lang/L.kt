package net.gloryx.glauncher.util.res.lang

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.ui.intl.Languages
import net.gloryx.glauncher.util.camelToSnake
import net.gloryx.oknamer.key.Key
import net.gloryx.oknamer.key.Namespaced
import net.gloryx.oknamer.key.dot
import net.gloryx.oknamer.key.kinds.LangKey
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object L : La(Namespaced("launcher")) {
    val test by null
    val logIn by null
}

sealed class La(namespace: Namespaced) : Namespaced by namespace {
    constructor(parent: La, prefix: String) : this(parent.dot(prefix)) // launcher -> launcher.x

    operator fun Nothing?.provideDelegate(that: La, prop: KProperty<*>): ReadOnlyProperty<La, LangComponent> = Delegate

    operator fun String.provideDelegate(that: La, prop: KProperty<*>): ReadOnlyProperty<La, LangComponent> =
        Delegate.Spec(Key.lang(this@La, this))

    private object Delegate : ReadOnlyProperty<La, LangComponent> {
        override fun getValue(thisRef: La, property: KProperty<*>): LangComponent = LangComponent(Key.lang(thisRef, property.name.camelToSnake()))

        class Spec(key: LangKey) : ReadOnlyProperty<La, LangComponent> {
            private val component = LangComponent(key)
            override fun getValue(thisRef: La, property: KProperty<*>): LangComponent = component
        }
    }

    companion object {
        var Lang by mutableStateOf(Languages.Default)
    }
}