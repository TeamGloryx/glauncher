package net.gloryx.glauncher.util.lang

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.gloryx.oknamer.key.Key
import net.gloryx.oknamer.key.Namespaced
import net.gloryx.oknamer.key.dot
import net.gloryx.oknamer.key.kinds.LangKey
import java.util.*
import java.util.Locale.forLanguageTag
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object L : La(Namespaced("launcher")) {
    val test by null(1)
}

sealed class La(namespace: Namespaced) : Namespaced by namespace {
    constructor(parent: La, prefix: String) : this(parent.dot(prefix)) // launcher -> launcher.x

    operator fun Nothing?.provideDelegate(that: La, prop: KProperty<*>): ReadOnlyProperty<La, LangComponent> =
        Delegate(Key.lang(this@La, prop.name), false)

    operator fun String.provideDelegate(that: La, prop: KProperty<*>): ReadOnlyProperty<La, LangComponent> =
        Delegate(Key.lang(this@La, this), true)

    operator fun Nothing?.invoke(vararg args: Any?): ReadOnlyProperty<La, LangComponent> = Delegate(LangComponent(Key.lang(this@La, ""), args), false)

    private class Delegate(private var component: LangComponent, private val specified: Boolean = false) :
        ReadOnlyProperty<La, LangComponent> {
        constructor(key: LangKey, specified: Boolean = false) : this(LangComponent(key), specified)
        constructor(namespace: Namespaced) : this(LangComponent(LangKey(namespace.namespace, "")), false)

        override fun getValue(thisRef: La, property: KProperty<*>): LangComponent {
            component =
                LangComponent(component.key.run { if (specified) component.key else LangKey(namespace, property.name) }) // update component to reflect prop name

            return component
        }
    }

    companion object {
        var Lang by mutableStateOf(Language.Default)
    }
}