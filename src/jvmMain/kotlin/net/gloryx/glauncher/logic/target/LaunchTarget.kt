package net.gloryx.glauncher.logic.target

import net.gloryx.glauncher.util.Static
import java.io.File

enum class LaunchTarget(val version: String = "1_16_5") {
    SKYBLOCK,
    DAYZ,
    SMP("1_19_1");

    val dir get() = Static.root.resolve("./${name.lowercase()}")
}