package net.gloryx.glauncher.util

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.platform.Font

object Fonts {
    val fw = FontWeight
    val Poppins = goof("poppins")

    fun goof(
        font: String, vararg weight: FontWeight = arrayOf(
            fw.W100,
            fw.W200,
            fw.W300,
            fw.W400,
            fw.W500,
            fw.W600,
            fw.W700,
            fw.W800,
            fw.W900
        )
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