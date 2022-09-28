package net.gloryx.glauncher.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.zIndex
import net.gloryx.glauncher.util.Static

object Console {
    val textState = mutableStateOf(
        "Gloryx Launcher by nothen@gloryx\n"
    )
    var text by textState


    val dialog = mutableStateOf(false)
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