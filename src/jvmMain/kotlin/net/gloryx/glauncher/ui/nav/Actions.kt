package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.l
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.ui.trace
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.coro
import net.gloryx.glauncher.util.state.AuthState
import java.math.BigInteger

object Actions : TargetState.Entry("Debug") {
    fun x(w: BigInteger): Lazy<List<BigInteger>> = lazy {
        var x = w
        val ls = mutableListOf<BigInteger>()

        while (x != BigInteger.ONE) {
            val y = if (x % BigInteger.TWO == BigInteger.ZERO) x / bi(2) else x * bi(3) + bi(1)
            x = y
            ls.add(y)
        }

        ls
    }

    @Suppress("nothing_to_inline")
    inline fun bi(number: Number): BigInteger = BigInteger.valueOf(number.l)

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
                        }
                    }
                }
            }
        }
    }
}