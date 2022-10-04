package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cat.ui.ComposableFn
import cat.ui.dlg.*
import catfish.winder.shapes.Round10
import net.gloryx.glauncher.ui.settings.Settings
import net.gloryx.glauncher.util.GButton

object TargetState {
    val entries = listOf(SelectTarget, Settings)
    var selected by State<Entry>(SelectTarget)

    abstract class Entry(val name: String) {
        @Composable
        abstract fun render()

        open val icon: ComposableFn? = null

        @Composable
        open fun renderWrapping(modifier: Modifier = Modifier, contentPadding: PaddingValues? = null) = Box(modifier.then(contentPadding?.let(Modifier::padding) ?: Modifier)) { render() }

        @Composable
        open fun button() = GButton({
            select()
        }, icon) {
            Text(name)
        }

        protected fun select() {
            selected = this
        }
    }
}

@Composable
fun RowScope.TargetNav() {
    LazyRow(userScrollEnabled = false) {
        items(TargetState.entries, TargetState.Entry::name) {
            Card(Modifier, Round10) {
                it.button()
            }
        }
    }
}