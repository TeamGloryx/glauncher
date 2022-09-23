package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import net.gloryx.glauncher.logic.target.LaunchTarget

@Composable
fun RowScope.TargetNav() {
    LaunchTarget.values().map {
        Button({

        }) {
            Text(it.name.uppercase())
        }
    }
}