@file:Suppress("MemberVisibilityCanBePrivate")

package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.map
import cat.ui.ComposableFn
import cat.ui.dlg.State
import cat.ui.dlg.getValue
import cat.ui.dlg.setValue
import catfish.winder.colors.Blue200
import catfish.winder.colors.Blue500
import catfish.winder.colors.Emerald200
import catfish.winder.shapes.Round
import net.gloryx.glauncher.ui.settings.Settings
import net.gloryx.glauncher.util.GTextButton
import net.gloryx.glauncher.util.forgetInteractionSource

object TargetState {
    val entries = listOf(SelectTarget, Settings, Actions)
    var selected by State<Entry>(SelectTarget)

    abstract class Entry(val name: String) {
        val isSelected by derivedStateOf { selected == this }

        @Composable
        abstract fun render(padding: PaddingValues?)

        open val icon: ComposableFn? = null

        @Composable
        open fun renderWrapping(modifier: Modifier = Modifier, contentPadding: PaddingValues? = null) = Box(modifier) { render(contentPadding) }

        @Composable
        open fun button() = GTextButton({
            select()
        }, icon, Modifier.indication(forgetInteractionSource(), rememberRipple(false, 10.dp, Blue500))) {
            Text(name)
        }

        protected fun select() {
            selected = this
        }
    }
}

@Composable
fun RowScope.TargetNav() {
    LazyRow(userScrollEnabled = false, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(TargetState.entries, TargetState.Entry::name) {
            val bg = it.isSelected.map(Emerald200, Blue200)
            Card(Modifier.padding(4.dp), Round(10), bg) {
                it.button()
            }
        }
    }
}