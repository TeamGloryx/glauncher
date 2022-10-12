package net.gloryx.glauncher.util

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.platform.Font

object Fonts {
    val fw = FontWeight
    val Poppins = goof("poppins", *fw.all())
    val JetbrainsMono = goof("jbm")

    fun goof(
        font: String, vararg weight: FontWeight = fw.almostAll()
    ) = FontFamily(weight.flatMap {
        listOf(
            Font("fonts/$font/${font.capitalize(Locale.current)}-${it.name}.ttf", it),
            Font("fonts/$font/${font.capitalize(Locale.current)}-${if (it == fw.Normal) "" else it.name}Italic.ttf", it)
        )
    })
}

val FontWeight.name get() = when (weight) {
    100 -> "Thin"
    200 -> "ExtraLight"
    300 -> "Light"
    400 -> "Regular"
    500 -> "Medium"
    600 -> "SemiBold"
    700 -> "Bold"
    800 -> "ExtraBold"
    900 -> "Black"
    else -> ""
}

inline fun FontWeight.Companion.almostAll() = arrayOf(
    W100,
    W200,
    W300,
    W400,
    W500,
    W600,
    W700,
    W800
)

inline fun FontWeight.Companion.all() = arrayOf(*almostAll(), W900)