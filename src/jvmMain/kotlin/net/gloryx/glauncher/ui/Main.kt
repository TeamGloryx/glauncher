package net.gloryx.glauncher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.gloryx.glauncher.logic.Launcher
import net.gloryx.glauncher.logic.download.Downloader
import net.gloryx.glauncher.ui.nav.TargetNav
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.color
import net.gloryx.glauncher.util.state.MainScreen

@Composable
@Preview
fun Main() {
    val coro = rememberCoroutineScope()

    Static.scope = coro
    Downloader.init()

    val scaffold = rememberScaffoldState()

    MaterialTheme(darkColors(color(0x00ffaf), secondary = color(0x08afd), surface = Color.DarkGray)) {
        Scaffold(scaffoldState = scaffold, topBar = {
            TopAppBar({ Text("Gloryx") }, actions = {
                TargetNav()
            }, backgroundColor = MaterialTheme.colors.secondary)
        }, bottomBar = {
            BottomAppBar(elevation = 20.dp) {
                Button({
                    // TODO Login
                }) {
                    Icon(Icons.Rounded.Person, "Log in")
                    Spacer(2.dp)
                    Text("Log in")
                }
            }
        }, isFloatingActionButtonDocked = true, floatingActionButton = {
            Button({
                Launcher.play(MainScreen.selected)
            }, shape = RoundedCornerShape(10)) {
                Icon(Icons.Rounded.PlayArrow, "Play!")
                Spacer(2.dp)
                Text("Play!")
            }
        }) { pad ->
            Column(Modifier.padding(pad)) {
                ConsoleComponent()
            }
        }
    }
}