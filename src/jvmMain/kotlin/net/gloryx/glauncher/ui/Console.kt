package net.gloryx.glauncher.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
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
import cat.ui.dlg.*
import net.gloryx.glauncher.util.Static
import org.apache.logging.log4j.util.StackLocatorUtil
import org.slf4j.event.Level

object Console {
    val textState = State(
        "Gloryx Launcher by nothen@gloryx"
    )
    var text by textState


    val dialog = State(false)

    fun send(message: Any) {
        text += "\n$message"
    }

    fun log(message: Any, level: Level = Level.INFO) =
        if (level != Level.TRACE)
            send("$level [${try_ { StackLocatorUtil.getCallerClass(3) }?.simpleName ?: "STDOUT"}/] $message")
        else send("$level [${try_ { StackLocatorUtil.getCallerClass(3) }?.simpleName ?: "STDOUT"}/] $message\n${Exception().stackTrace.joinToString { "\tat $it" }}")

    fun info(message: Any) = log(message)
    fun warn(message: Any) = log(message, Level.WARN)
    fun error(message: Any) = log(message, Level.ERROR)
    fun debug(message: Any) = log(message, Level.DEBUG)

    fun trace(message: Any) = log(message, Level.TRACE)
}

@Composable
fun ConsoleComponent() {
    val scroll = rememberScrollState(0)

    var dialog by Console.dialog

    if (dialog) Window({ dialog = false }, title = "Gloryx Launcher - Console") {
        MaterialTheme(colors = Static.colors) {
            Box(
                Modifier.fillMaxSize().background(MaterialTheme.colors.background).border(
                    3.dp, MaterialTheme.colors.primarySurface
                ).fillMaxWidth()
            ) {
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