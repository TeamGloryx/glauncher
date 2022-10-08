package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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

object Actions : TargetState.Entry("Debug") {
    @Composable
    override fun render(padding: PaddingValues?) {
        val coro = rememberCoroutineScope()
        LazyVerticalGrid(GridCells.Adaptive(100.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Button({
                    coro.launch {
                        Jre.download(LaunchTarget.VANILLA)
                    }
                }) { Text("Vanilla Java") }
            }
            item {
                Button({
                    coro.launch {
                        downloading {
                            LaunchTarget.VANILLA.apply { doLibraries() }
                        }
                    }
                }) {
                    Text("Vanilla Libraries")
                }
            }
            item {
                Column {
                    Text("Auth:")

                }
            }
        }
    }
}