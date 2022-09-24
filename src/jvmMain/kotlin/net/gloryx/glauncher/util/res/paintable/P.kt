package net.gloryx.glauncher.util.res.paintable

import androidx.compose.ui.res.loadImageBitmap
import kotlin.reflect.KProperty

object P : Pa("paintable") {
    object Main : Pa(P, "main") {
        val skyblock by null
        val dayz by null
    }
}

sealed class Pa(val path: String) {
    constructor(parent: Pa, prefix: String) : this("${parent.path}/$prefix")

    operator fun String.getValue(that: Pa, prop: KProperty<*>) =
        loadImageBitmap(P.javaClass.getResourceAsStream("/$path/${if (this.split('.').size == 2) this else "$this.png"}")!!)

    operator fun Nothing?.getValue(that: Pa, prop: KProperty<*>) = prop.name.getValue(that, prop)
}