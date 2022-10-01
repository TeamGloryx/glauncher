package net.gloryx.glauncher.util.res.lang

import androidx.compose.ui.text.intl.Locale
import cat.ui.intl.Languages

val Languages.RU_RU
    get() = Russian
val Languages.EN_US get() = English

fun Languages.from(j: Locale) = new(j.toLanguageTag())