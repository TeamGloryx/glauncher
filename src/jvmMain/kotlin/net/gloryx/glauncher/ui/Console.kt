package net.gloryx.glauncher.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import net.gloryx.glauncher.util.Static

object Console {
    var text by mutableStateOf(
        "GLauncher by nothen@gloryx\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaa\n${
            List(
                40
            ) { "A" }.joinToString("\n")
        }"
    )
}

@Composable
fun ConsoleComponent() {
    val scroll = rememberScrollState(0)

    var dialog by remember { mutableStateOf(true) }

    if (dialog)
        Dialog({ dialog = false }) {
            MaterialTheme(colors = Static.colors) {
                Row(
                    Modifier.border(
                        3.dp,
                        MaterialTheme.colors.primarySurface
                    ).fillMaxWidth().requiredHeight(200.dp)
                ) {
                    SelectionContainer(Modifier.verticalScroll(scroll).fillMaxSize()) {
                        BasicText(
                            Console.text,
                            modifier = Modifier.padding(6.dp).wrapContentWidth(Alignment.Start, false),
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(MaterialTheme.colors.onSurface)
                        )
                    }
                    VerticalScrollbar(
                        rememberScrollbarAdapter(scroll),
                        Modifier.weight(1f),
                        false,
                        defaultScrollbarStyle().copy(
                            thickness = 30.dp,
                            unhoverColor = Color.White,
                            hoverColor = MaterialTheme.colors.primarySurface
                        )
                    )
                }
            }
        }


}