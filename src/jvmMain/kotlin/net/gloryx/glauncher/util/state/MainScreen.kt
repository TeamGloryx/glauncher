package net.gloryx.glauncher.util.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.gloryx.glauncher.logic.target.LaunchTarget

object MainScreen {
    var selected by mutableStateOf(LaunchTarget.SMP)


}