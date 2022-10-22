package net.gloryx.glauncher.logic.target

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.ui.dlg.*
import catfish.winder.colors.*
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.download.Downloading
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.ui.nav.TargetState
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.nbt.ServersDatFile
import net.gloryx.glauncher.util.state.MainScreen
import net.gloryx.glauncher.util.ui.GCheckbox
import net.gloryx.glauncher.util.ui.GColors
import net.gloryx.glauncher.util.ui.Spacing

@Suppress("nothing_to_inline")
object Instance : TargetState.Entry("Instance") {
    val scroll = ScrollState(0)
    val text = mutableStateListOf<String>()

    val autoscroll = State(true)
    val wrap = State(true)

    var isConsoleAvailable by State(false, structuralEqualityPolicy())

    var currNav: Cfn by State(Nav.actions)

    val Cfn.isSelected get() = currNav === this

    fun onProc() {
        if (!isSelected) select()

        if (!Nav.console.isSelected) currNav = Nav.console

        isConsoleAvailable = true
    }

    fun onProcStop() {
        text.clear()
    }

    object Nav {
        val console = @Composable {
            Column(Modifier.fillMaxSize().fillMaxWidth()) {
                val ct by forget { derivedStateOf { text.joinToString("\n") } }
                val coro = currentCoroutine

                Scaffold(Modifier.fillMaxWidth().padding(bottom = 10.dp), topBar = {
                    Row {
                        GCheckbox(autoscroll) { Text("Autoscroll") }
                        GCheckbox(wrap) { Text("Wrap lines") }
                    }
                }, bottomBar = {
                    Row(Modifier.fillMaxWidth().border(4.dp, Stone400).padding(6.dp)) {
                        //fixme noop
                        val search = useState("")

                        //search
                        Row {
                            TextField(search.value,
                                search::set,
                                Modifier.requiredHeight(60.dp).width(100.dp),
                                singleLine = true,
                                maxLines = 1,
                                label = {
                                    Text("Search")
                                    BoxWithConstraints { }
                                })

                            Spacer(1)

                            //buttons
                            /// noop because multi-mc
                            Button({}) {
                                Text("Find")
                            }



                            Button({
                                coro.launch {
                                    scrollBack()
                                }
                            }) {
                                Text("Bottom")
                            }
                        }
                    }
                }) { padding ->
                    Row(Modifier.padding(padding).fillMaxSize()) {
                        SelectionContainer(Modifier.verticalScroll(scroll).weight(2f)) {
                            useFont(Fonts.JetbrainsMono) {
                                OutlinedTextField(
                                    TextFieldValue(ct), {}, Modifier.weight(1f).fillMaxWidth(), true, readOnly = true
                                )
                            }
                        }

                        VerticalScrollbar(
                            rememberScrollbarAdapter(scroll),
                            Modifier.fillMaxHeight().weight(1f),
                            style = defaultScrollbarStyle().copy(
                                thickness = 10.dp, unhoverColor = Stone300, hoverColor = Stone700
                            )
                        )
                    }
                }

                if (autoscroll.value) LaunchedEffect(text) {
                    scrollBack()
                }
            }
        }

        val actions = @Composable {
            val selected by forget { derivedStateOf(MainScreen::selected) }
            val coro = currentCoroutine

            Column {
                center {
                    Text(
                        "Settings: ${selected.normalName}",
                        Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                }
                Text("Revalidation")
                LazyVerticalGrid(
                    GridCells.Adaptive(300.dp),
                    Modifier.verticalSplitter(4.dp, 1.dp, 8.dp, Teal400),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Between.buttons)
                ) {
                    item {
                        Button({
                            coro.launch {
                                Assets.check(selected)
                            }
                        }) {
                            Text("Check assets")
                        }
                    }

                    item {
                        Button({
                            coro.launch {
                                selected.apply {
                                    Downloading().doLibraries()
                                }
                            }
                        }) {
                            Text("Check libraries")
                        }
                    }

                    item {

                        Button({
                            coro.launch {
                                selected.apply {
                                    Downloading().doMods()
                                }
                            }
                        }) {
                            Text("Check mods")
                        }
                    }

                    item {
                        GButton({
                            coro.launch {
                                ServersDatFile.install(selected)
                            }
                        }) {
                            Text("Install servers.dat")
                        }
                    }

                    item {

                        GButton({
                            coro.launch {
                                Jre.download(selected)
                            }
                        }) {
                            Text("Check & install Java {JRE v${selected.javaVersion}}")
                        }

                    }

                    item {

                        GButton({
                            coro.launch {
                                selected.install()
                            }
                        }, colors = ButtonDefaults.buttonColors(Red500), icon = {
                            Icon(Icons.Default.Warning, "Reinstall")
                        }) {
                            Text("Reinstall")
                        }
                    }
                }
            }
        }
    }

    suspend fun scrollBack() = scroll.animateScrollTo(scroll.maxValue)

    @Composable
    override fun render(padding: PaddingValues?) {
        Row {
            nav()

            Spacer(10.dp)

            currNav()
        }
    }

    val ColumnScope.divide: Unit
        @Composable get() {
            Divider(color = Slate600)
        }

    @Composable
    inline fun nav() {
        val clr = GColors.InstanceScreen.drawerButton
        val mod = Modifier.height(50.dp).fillMaxWidth().clipToBounds()
        Row(Modifier.width(120.dp)) {
            Column {
                if (isConsoleAvailable || (Static.process != null).useTruth { isConsoleAvailable = true }) {
                    Button(
                        {
                            currNav = Nav.console
                        }, mod, !Nav.console.isSelected, colors = clr, shape = RectangleShape
                    ) {
                        Text("Console")
                    }

                    divide
                }

                Button({
                    currNav = Nav.actions
                }, mod, !Nav.actions.isSelected, colors = clr, shape = RectangleShape) {
                    Text("Actions")
                }
            }

            Spacer(10.dp)

            VerticalEndSplitter(3.dp, Slate300)
        }
    }
}