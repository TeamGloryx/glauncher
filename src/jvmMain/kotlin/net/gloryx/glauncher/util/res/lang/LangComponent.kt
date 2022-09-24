package net.gloryx.glauncher.util.res.lang

import net.gloryx.oknamer.key.kinds.LangKey

data class LangComponent(val key: LangKey, val args: Array<out Any?> = arrayOf()) {
    operator fun invoke(vararg args: Any?) = LangComponent(key, args)

    override fun toString(): String = (La.Lang.conf[key.asString()]?.format(*args) ?: key.asString())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LangComponent

        if (key != other.key) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}