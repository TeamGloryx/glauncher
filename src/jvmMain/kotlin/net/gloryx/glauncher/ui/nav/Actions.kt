package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import net.gloryx.glauncher.logic.jre.Jre
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.yeet

object Actions : TargetState.Entry("Actions") {
    @Composable
    override fun render(padding: PaddingValues?) {
        val coro = rememberCoroutineScope()
        Button({
            coro.yeet {
                Jre.download(LaunchTarget.VANILLA)
            }
        }) { Text("Vanilla Java") }
    }
}