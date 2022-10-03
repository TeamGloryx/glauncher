package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cat.ui.dlg.*
import net.gloryx.glauncher.ui.settings.Settings
import net.gloryx.glauncher.util.GButton

object TargetState {
    val entries = listOf(SelectTarget, Settings)
    var selected by State<Entry>(SelectTarget)

    abstract class Entry(val name: String) {
        @Composable
        abstract fun render()
    }
}

@Composable
fun RowScope.TargetNav() {
    LazyRow {
        items(TargetState.entries, TargetState.Entry::name) {
            Card {
                GButton({
                    TargetState.selected = it
                }) {
                    Text(it.name)
                }
            }
        }
    }
}