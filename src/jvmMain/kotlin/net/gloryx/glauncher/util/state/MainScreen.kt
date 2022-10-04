package net.gloryx.glauncher.util.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ScaffoldState
import cat.ui.dlg.*
import net.gloryx.glauncher.logic.target.LaunchTarget

object MainScreen {
    var lazyRow = LazyListState()
    var selected by State(LaunchTarget.SMP)

    var scaffold: ScaffoldState? = null
}