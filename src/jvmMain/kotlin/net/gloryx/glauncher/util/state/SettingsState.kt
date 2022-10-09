package net.gloryx.glauncher.util.state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.KeyboardType
import cat.f
import cat.map
import cat.ui.dlg.State
import cat.ui.dlg.useState
import cat.ui.text.*
import catfish.winder.colors.*
import net.gloryx.glauncher.ui.Console
import net.gloryx.glauncher.util.GSlider
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.Static
import kotlin.math.roundToInt

object SettingsState {
    val oldSMP = State(true)
    val ram = State(4096.coerceAtMost(Static.physicalMemory))

    @OptIn(ExperimentalFoundationApi::class)
    val settings = settings {
        group("Launch") {
            setting("RAM", ram) { (currRAM, setRAM) ->
                val (state, setState) = useState(currRAM / 512f)
                val range = 512..Static.physicalMemory
                val valueRange = 1f..range.last.f.div(512f)
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(Modifier.align(Alignment.Top)) {
                        GSlider(
                            state, {
                                setState(it.coerceIn(valueRange))
                                setRAM((it.roundToInt() * 512).coerceIn(range))
                            }, valueRange = valueRange, steps = range.count { it % 512 == 0 } - 1
                        )
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceEvenly) {
                            Text("${range.first}MB")
                            Spacer(1)
                            Text("${range.last}MB")
                        }
                        TextField(
                            "$currRAM",
                            {
                                val i = it.trim().toIntOrNull() ?: range.first
                                setRAM(i)
                                setState(i / 512f)
                            },
                            Modifier.align(Alignment.CenterHorizontally),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(MaterialTheme.colors.onBackground),
                            isError = currRAM !in range,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        Text("MB", Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
            setting("Enable old SMP version (1.16.5)?", oldSMP) { (smp) ->
                Column {
                    Text("[Recommended on 32-bit Java]")
                    Row(Modifier.align(Alignment.Start).onClick { oldSMP.value = !smp }) {
                        Switch(smp, {
                            oldSMP.value = !smp
                        })
                        Text(
                            "1.16.5 SMP is " and smp.map("enabled", "disabled").withColor(smp.map(Green400, Red700)),
                            //buildAnnotatedString {
                            //    append("1.16.5 SMP is ")
                            //    append(
                            //      AnnotatedString(
                            //      if (smp) "enabled" else "disabled", SpanStyle(if (smp) Green400 else Red700))
                            //    )
                            //},
                            Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
        group("Console") {
            setting("Autoscroll", Console.doAutoscroll) { (it, change) ->
                Column {
                    Text("Makes the console automatically scroll to the bottom.")
                    Switch(it, change)
                }
            }
        }
    }
}

