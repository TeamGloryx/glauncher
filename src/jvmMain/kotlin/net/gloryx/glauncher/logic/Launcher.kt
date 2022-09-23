package net.gloryx.glauncher.logic

import kotlinx.coroutines.launch
import net.gloryx.glauncher.logic.target.Assets
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.Static

object Launcher {
    suspend fun launch(target: LaunchTarget) {
        Assets.prepare(target)
    }

    fun play(target: LaunchTarget) {
        Static.scope.launch {
            launch(target)
        }
    }
}