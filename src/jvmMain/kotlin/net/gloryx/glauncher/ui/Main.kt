package net.gloryx.glauncher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import net.gloryx.glauncher.logic.Launcher
import net.gloryx.glauncher.logic.download.Downloader
import net.gloryx.glauncher.ui.auth.AuthDialog
import net.gloryx.glauncher.ui.nav.SelectTarget
import net.gloryx.glauncher.ui.nav.TargetNav
import net.gloryx.glauncher.util.GColors
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.res.lang.L
import net.gloryx.glauncher.util.res.lang.Language
import net.gloryx.glauncher.util.res.lang.withLanguage
import net.gloryx.glauncher.util.state.AuthState
import net.gloryx.glauncher.util.state.MainScreen

@Composable
@Preview
fun Main() {
    val lang = Language.from(Locale.current)
    val coro = rememberCoroutineScope()

    Static.scope = coro
    Downloader.init()

    val scaffold = rememberScaffoldState().also { MainScreen.scaffold = it }

    withLanguage(Language.RU_RU) {
        MaterialTheme(Static.colors) {
            Scaffold(scaffoldState = scaffold, snackbarHost = { sh ->
                SnackbarHost(sh) { data ->
                    Snackbar(modifier = Modifier.shadow(4.dp), backgroundColor = GColors.snackbar, contentColor = contentColorFor(GColors.snackbar), snackbarData = data)
                }
            }, topBar = {
                TopAppBar({ Text("Gloryx ${L.test("A")}") }, actions = {
                    TargetNav()
                }, backgroundColor = MaterialTheme.colors.secondary)
            }, bottomBar = {
                BottomAppBar(elevation = 20.dp) {
                    Button({
                        AuthState.authDialog = true
                    }) {
                        Icon(Icons.Rounded.Person, "Log in")
                        Spacer(2.dp)
                        Text(if (AuthState.isAuthenticated) "Logged in as ${AuthState.ign}" else "Log in")
                    }
                    Button({
                        Console.dialog.value = true
                    }) {
                        Icon(Icons.Rounded.Settings, "Console")
                        Spacer(2.dp)
                        Text("Console")
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
                AuthDialog()
                Column(Modifier.padding(pad)) {
                    SelectTarget()
                    ConsoleComponent()
                }
            }
        }
    }
}