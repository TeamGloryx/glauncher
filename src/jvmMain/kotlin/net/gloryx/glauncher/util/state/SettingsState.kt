package net.gloryx.glauncher.util.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.f
import cat.i
import cat.ui.CFnWithArgs
import cat.ui.ComposableFn
import cat.ui.dlg.State
import cat.ui.dlg.useState
import cat.void
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.w1
import kotlin.math.roundToInt

object SettingsState {
    val oldSMP = State(false)
    val ram = State(1024)

    val settings = settings {
        group("Launch") {
            setting("RAM", ram) { currRAM ->
                val (state, setState) = useState(0f)
                val range = 1024..Static.physicalMemory
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceEvenly) {

                    Column(Modifier.align(Alignment.Top)) {
                        Slider(
                            state, {
                                setState(it)
                                ram.value = (it.roundToInt() * 512).coerceIn(range)
                            }, valueRange = 0f..range.last.f.div(512f)
                        )
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceEvenly) {
                            Text("${range.first}MB")
                            Spacer(1)
                            Text("${range.last}MB")
                        }
                        Text("${currRAM}MB", Modifier.align(Alignment.CenterHorizontally))
                    }

                }
            }
        }
    }
}

