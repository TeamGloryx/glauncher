package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.ui.trace
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.state.AuthState

object Actions : TargetState.Entry("Debug") {
    @Composable
    override fun render(padding: PaddingValues?) {
        val coro = rememberCoroutineScope()
        LazyVerticalGrid(GridCells.Adaptive(100.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Button({
                    coro.launch {
                        Jre.download(LaunchTarget.SMP_1_16)
                    }
                }) { Text("Vanilla Java") }
            }
            item {
                Button({
                    coro.launch {
                        downloading {
                            LaunchTarget.SMP_1_16.apply { doLibraries() }
                        }
                    }
                }) {
                    Text("Vanilla Libraries")
                }
            }
            item {
                Button({
                    coro.launch {
                        downloading {
                            LaunchTarget.SMP_1_16.apply { doNatives() }
                        }
                    }
                }) {
                    Text("Vanilla Natives")
                }
            }
            item {
                Column {
                    Text("Auth:")
                    Row {
                        Spacer(3.dp)
                        Column {
                            Text("IGN: ${AuthState.ign}")
                            Text("Premium: ${AuthState.premium}")
                            Button({
                                trace("bye bye database")
                            }) {
                                Text("Database go brr")
                            }
                        }
                    }
                }
            }
        }
    }
}