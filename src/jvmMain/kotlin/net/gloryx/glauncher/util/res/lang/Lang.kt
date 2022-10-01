package net.gloryx.glauncher.util.res.lang

import androidx.compose.runtime.*
import androidx.compose.ui.text.intl.Locale
import cat.ui.intl.Language
import cat.ui.intl.Languages

val LocalLanguage = compositionLocalOf(object : SnapshotMutationPolicy<Language> {
    override fun equivalent(a: Language, b: Language): Boolean {
        La.Lang = b
        return structuralEqualityPolicy<Language>().equivalent(a, b)
    }
}) { La.Lang }

val LocalLocale get() = Locale.current

@Composable
fun withLanguage(language: Language = Languages.Default, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLanguage provides language) {
        content()
    }
}