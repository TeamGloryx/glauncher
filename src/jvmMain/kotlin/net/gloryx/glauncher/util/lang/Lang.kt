package net.gloryx.glauncher.util.lang

import androidx.compose.runtime.*

val LocalLanguage = compositionLocalOf(object : SnapshotMutationPolicy<Language> {
    override fun equivalent(a: Language, b: Language): Boolean {
        La.Lang = b
        return structuralEqualityPolicy<Language>().equivalent(a, b)
    }
}) { La.Lang }

@Composable
fun withLanguage(language: Language = Language.Default, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLanguage provides language) {
        content()
    }
}