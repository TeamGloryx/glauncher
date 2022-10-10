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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.l
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.download.downloading
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.ui.Console
import net.gloryx.glauncher.ui.debug
import net.gloryx.glauncher.ui.trace
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.conf
import net.gloryx.glauncher.util.coro
import net.gloryx.glauncher.util.set
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
            item {
                Button({
                    AuthState.ign = "NothinGG_"
                    AuthState.hash = AuthState.hasher.hashToString(10, "1".toCharArray())
                    Console.dialog.set(true)
                    TargetState.selected = SelectTarget
                }) { Text("Setup") }

                remember {
                    debug(
                        """
                    "https://maven.fabricmc.net/net/fabricmc/tiny-mappings-parser/0.3.0+build.17"
    "https://maven.fabricmc.net/net/fabricmc/sponge-mixin/0.11.4+mixin.0.8.5"
    "https://maven.fabricmc.net/net/fabricmc/tiny-remapper/0.8.2"
    "https://maven.fabricmc.net/net/fabricmc/access-widener/2.1.0"
    "https://maven.fabricmc.net/org/ow2/asm/asm/9.3"
    "https://maven.fabricmc.net/org/ow2/asm/asm-analysis/9.3"
    "https://maven.fabricmc.net/org/ow2/asm/asm-commons/9.3"
    "https://maven.fabricmc.net/org/ow2/asm/asm-tree/9.3"
    "https://maven.fabricmc.net/org/ow2/asm/asm-util/9.3"
    "https://maven.fabricmc.net/net/fabricmc/intermediary/1.19.1"
    "https://maven.fabricmc.net/net/fabricmc/fabric-loader/0.14.9"
                """.trimIndent().split("\n").toSet()
                            .map {
                                val ls = it.removeSurrounding("\"").removePrefix("https://").split("/")
                                val tl = ls.takeLast(2).toSet()
                                (ls - tl).joinToString("/") + "/" + tl.joinToString("-", "", ".jar")
                            }.joinToString("\n") { "\"$it\"" }
                    )
                }
            }
        }


    }
}