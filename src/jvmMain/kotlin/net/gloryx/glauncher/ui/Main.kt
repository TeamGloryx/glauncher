package net.gloryx.glauncher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cat.ui.intl.Languages
import net.gloryx.glauncher.logic.Launcher
import net.gloryx.glauncher.logic.download.Downloader
import net.gloryx.glauncher.ui.auth.AuthDialog
import net.gloryx.glauncher.ui.nav.SelectTarget
import net.gloryx.glauncher.ui.nav.TargetNav
import net.gloryx.glauncher.ui.nav.TargetState
import net.gloryx.glauncher.ui.settings.Settings
import net.gloryx.glauncher.util.GButton
import net.gloryx.glauncher.util.GColors
import net.gloryx.glauncher.util.Static
import net.gloryx.glauncher.util.res.lang.L
import net.gloryx.glauncher.util.res.lang.from
import net.gloryx.glauncher.util.res.lang.withLanguage
import net.gloryx.glauncher.util.state.AuthState
import net.gloryx.glauncher.util.state.MainScreen
import net.gloryx.glauncher.util.state.SettingsState

@Composable
@Preview
fun Main() {
    val lang = Languages.from(Locale.current)
    val coro = rememberCoroutineScope()

    Static.scope = coro
    Downloader.init()

    val scaffold = rememberScaffoldState().also { MainScreen.scaffold = it }

    withLanguage(lang) {
        MaterialTheme(Static.colors) {
            Scaffold(scaffoldState = scaffold, snackbarHost = { sh ->
                SnackbarHost(sh) { data ->
                    Snackbar(
                        modifier = Modifier.shadow(4.dp),
                        backgroundColor = GColors.snackbar,
                        contentColor = contentColorFor(GColors.snackbar),
                        action = data.actionLabel?.let {
                            {
                                Button({
                                    data.dismiss()
                                }) {
                                    Text(it)
                                }
                            }
                        }
                    ) {
                        Text(data.message, softWrap = true, overflow = TextOverflow.Ellipsis)
                    }
                }
            }, topBar = {
                TopAppBar({ Text("Gloryx ${L.test("A")}") }, Modifier.height(50.dp), actions = {
                    TargetNav()
                }, backgroundColor = MaterialTheme.colors.secondary)
            }, bottomBar = {
                BottomAppBar(elevation = 20.dp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)) {
                        GButton({
                            AuthState.authDialog = true
                        }, {
                            Icon(Icons.Rounded.Person, "Log in")
                        }) {
                            Text(if (AuthState.isAuthenticated) "Logged in as ${AuthState.ign}" else "Log in")
                        }
                        GButton({
                            Console.dialog.value = true
                        }, {
                            Icon(Icons.Rounded.Warning, "Console")
                        }) {
                            Text("Console")
                        }
                        GButton({

                        }, {
                            Icon(Icons.Rounded.Settings, "Settings")
                        }) {
                            Text("Settings")
                        }
                    }
                }
            }, isFloatingActionButtonDocked = true, floatingActionButton = {
                GButton({
                    Launcher.play(MainScreen.selected)
                }, {
                    Icon(Icons.Rounded.PlayArrow, "Play!")
                }, shape = RoundedCornerShape(10)) {
                    Text("Play!")
                }
            }) { pad ->
                AuthDialog()
                Column(Modifier.padding(pad)) {
                    TargetState.selected.render()
                    ConsoleComponent()
                }
            }
        }
    }
}