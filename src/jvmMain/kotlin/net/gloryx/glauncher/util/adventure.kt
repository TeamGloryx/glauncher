package net.gloryx.glauncher.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import cat.reflect.cast
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration as ADec
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor

fun TextComponent.annotate() = if (children().isEmpty()) annotateSingle() else buildAnnotatedString {
    val txt = this@annotate.children().filterNotNull().map { it.cast<TextComponent>() }

    txt.map(TextComponent::annotateSingle).forEach(this::append)
}

fun TextComponent.annotateSingle() = buildAnnotatedString {
    append(content())
    addStyle(style().span(), 0, (content().length - 1).coerceAtLeast(0))
}

fun Style.span() = SpanStyle((color() ?: NamedTextColor.WHITE).compose()).run {
    val textDec = listOf(ADec.UNDERLINED, ADec.STRIKETHROUGH).filter { hasDecoration(it) }.map {
        when (it) {
            ADec.UNDERLINED -> TextDecoration.Underline
            ADec.STRIKETHROUGH -> TextDecoration.LineThrough
            else -> TextDecoration.None
        }
    }.let(TextDecoration.Companion::combine)
    val fw = if (hasDecoration(ADec.BOLD)) FontWeight.Bold else FontWeight.Normal
    val fs = if (hasDecoration(ADec.ITALIC)) FontStyle.Italic else FontStyle.Normal


    copy(textDecoration = textDec, fontStyle = fs, fontWeight = fw)
}

fun TextColor.compose() = Color(value() or 0x000000ff)