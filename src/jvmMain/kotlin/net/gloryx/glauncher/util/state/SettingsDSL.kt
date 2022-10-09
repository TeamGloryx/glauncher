package net.gloryx.glauncher.util.state

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cat.void
import catfish.winder.colors.Gray200
import net.gloryx.glauncher.util.*

class SettingsDSL {
    private val groups = mutableListOf<Group>()

    data class Setting<T : Any>(
        val name: String, val state: MutableState<T>, val render: @Composable RowScope.(MutableState<T>) -> Unit
    ) {
        @Composable
        fun doRender() = Row(horizontalArrangement = Arrangement.SpaceBetween) { render(state) }
    }

    class Group(val name: String) {
        private val children = mutableListOf<Setting<out Any>>()

        fun <T : Any> setting(
            name: String, state: MutableState<T>, render: @Composable RowScope.(MutableState<T>) -> Unit
        ) =
            children.add(Setting(name, state, render)).void

        @Composable
        fun render() {
            Column(Modifier.border(1.dp, Color.LightGray).padding(10.dp)) {
                Text(name)
                Row(Modifier.verticalSplitter(3.dp, 3.dp)) {
                    Spacer(4.dp)
                    LazyColumn(
                        Modifier.absolutePadding(left = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(children, Setting<out Any>::name) {
                            Column {
                                Text(
                                    it.name,
                                    Modifier.align(Alignment.Start),
                                    fontSize = TextUnit(20),
                                    style = TextStyle(
                                        shadow = Shadow(
                                            Gray200, Offset(.5f, .5f), 0.2f
                                        )
                                    )
                                )
                                it.doRender()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun render() {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            groups.map { it.render() }
        }
    }

    fun group(name: String, builder: Group.() -> Unit) = groups.add(Group(name).apply(builder)).void
}

fun settings(scope: SettingsDSL.() -> Unit) = SettingsDSL().apply(scope)