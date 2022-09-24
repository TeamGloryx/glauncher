package net.gloryx.glauncher.util.state

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import net.gloryx.glauncher.logic.target.LaunchTarget

object MainScreen {
    var selected by mutableStateOf(LaunchTarget.SMP)

    var scaffold: ScaffoldState? = null
}