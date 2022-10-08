@file:Suppress("nothing_to_inline")

package net.gloryx.glauncher.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.zIndex
import cat.try_
import cat.ui.Suspend
import cat.ui.dlg.*
import catfish.winder.colors.Gray700
import net.gloryx.glauncher.ui.Console.sb
import net.gloryx.glauncher.util.GButton
import net.gloryx.glauncher.util.Static
import org.apache.logging.log4j.util.StackLocatorUtil
import org.slf4j.event.Level

inline fun info(message: Any?) = Console.info(message)
inline fun warn(message: Any?) = Console.warn(message)
inline fun err(message: Any?) = Console.error(message)
inline fun debug(message: Any?) = Console.debug(message)
inline fun trace(message: Any?) = Console.trace(message)

object Console {

    val textState = State(
        "Gloryx Launcher by nothen@gloryx"
    )
    var text by textState


    val dialog = State(false)

    val doAutoscroll = State(true)
    var autoscroll by doAutoscroll

    internal var sb by State(false)

    fun send(message: Any?) {
        text += "\n$message"
    }

    fun log(message: Any?, level: Level = Level.INFO) =
        if (level != Level.TRACE)
            send("$level [${locateClass()}/] $message")
        else send("$level [${locateClass()}/] $message\n${Exception().stackTrace.joinToString { "\tat $it" }}")

    fun info(message: Any?) = log(message)
    fun warn(message: Any?) = log(message, Level.WARN)
    fun error(message: Any?) = log(message, Level.ERROR)
    fun debug(message: Any?) = log(message, Level.DEBUG)

    fun trace(message: Any?) = log(message, Level.TRACE)

    @Suppress("nothing_to_inline") // stack length increases without inlining
    private inline fun locateClass() =
        try_ { StackLocatorUtil.getCallerClass(3) }?.simpleName?.takeUnless(String::isEmpty) // try at depth 3 (e.g. normal class)
            ?: (try_ { StackLocatorUtil.getCallerClass(4) }?.simpleName?.takeUnless(String::isEmpty)
                ?: "STDOUT") // try at depth 4 (anon class)

    fun scrollToBottom() {
        sb = true
    }
}

@Composable
fun ConsoleComponent() {
    val scroll = rememberScrollState(0)

    suspend fun scrollBack() {
        scroll.animateScrollTo(scroll.maxValue)
    }

    if (sb) Suspend { scrollBack() }

    var dialog by Console.dialog

    if (dialog) Window({ dialog = false }, title = "Gloryx Launcher - Console") {
        MaterialTheme(colors = Static.colors) {
            Column(
                Modifier.fillMaxSize().background(
                    Gray700
                ).border(
                    3.dp, MaterialTheme.colors.primarySurface
                ).fillMaxWidth()
            ) {
                Row {
                    Column {
                        Text("Autoscroll?")
                        Console.doAutoscroll.let { (it, change) -> Switch(it, change) }
                    }
                    GButton({

                    }) {

                    }
                }
                Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                    SelectionContainer(Modifier.verticalScroll(scroll).fillMaxSize().zIndex(1f)) {
                        BasicText(
                            Console.text,
                            modifier = Modifier.padding(6.dp).wrapContentWidth(Alignment.Start, false),
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(MaterialTheme.colors.onSurface)
                        )
                    }
                    VerticalScrollbar(
                        rememberScrollbarAdapter(scroll),
                        Modifier.align(Alignment.CenterEnd).zIndex(2f).fillMaxHeight().offset(4.dp)
                            .drawBehind { drawRect(Color.DarkGray, size = size.copy(30f), alpha = 1f) },
                        false,
                        defaultScrollbarStyle().copy(
                            thickness = 20.dp,
                            unhoverColor = Color.LightGray,
                            hoverColor = MaterialTheme.colors.onBackground
                        )
                    )
                }
            }
        }
    }

    if (Console.autoscroll) {
        LaunchedEffect(Console.textState) {
            scrollBack()
        }
    }
}