package net.gloryx.glauncher.logic.target

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cat.ui.dlg.*
import catfish.winder.colors.*
import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.download.Downloading
import net.gloryx.glauncher.ui.nav.TargetState
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.state.MainScreen
import net.gloryx.glauncher.util.ui.GCheckbox
import net.gloryx.glauncher.util.ui.Spacing
import cat.map

@Suppress("nothing_to_inline")
object Instance : TargetState.Entry("Instance") {
    val scroll = ScrollState(0)
    val text = mutableStateListOf<String>()

    val autoscroll = State(true)
    val wrap = State(true)

    var currNav: Cfn by State(Nav.console)

    val Cfn.isSelected get() = currNav === this

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
                            TextField(
                                search.value,
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

                            Spacer(Spacing.Between.buttons)

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
                    Row(Modifier.padding(padding).fillMaxWidth()) {
                        SelectionContainer(Modifier.verticalScroll(scroll)) {
                            OutlinedTextField(TextFieldValue(ct), {}, readOnly = true)
                        }

                        VerticalScrollbar(
                            rememberScrollbarAdapter(scroll),
                            Modifier.fillMaxHeight(),
                            style = defaultScrollbarStyle().copy(
                                thickness = 10.dp,
                                unhoverColor = Stone300,
                                hoverColor = Stone700
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
                Text("Assets")
                Row(Modifier.verticalSplitter(4.dp, 1.dp, 8.dp, Teal400)) {
                    Button({
                        coro.launch {
                            Assets.check(selected)
                        }
                    }) {
                        Text("Check assets")
                    }
                    Spacer(Spacing.Between.buttons)
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
            Spacer(2.dp)
            Divider(color = Slate600)
        }

    @Composable
    inline fun nav() {
        val clr = ButtonDefaults.buttonColors(Emerald400, Black, Lime600, Black)
        Row(Modifier.width(120.dp)) {
            Column {
                Button(
                    {
                        currNav = Nav.console
                    },
                    Modifier.fillMaxWidth(),
                    !Nav.console.isSelected,
                    colors = clr
                ) {
                    Text("Console")
                }

                divide

                Button({
                    currNav = Nav.actions
                }, Modifier.fillMaxWidth(), !Nav.actions.isSelected, colors = clr) {
                    Text("Actions")
                }
            }

            Spacer(10.dp)

            VerticalEndSplitter(3.dp, Slate300)
        }
    }
}