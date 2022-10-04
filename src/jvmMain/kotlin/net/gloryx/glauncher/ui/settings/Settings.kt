package net.gloryx.glauncher.ui.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import net.gloryx.glauncher.ui.nav.TargetState
import net.gloryx.glauncher.util.GButton
import net.gloryx.glauncher.util.state.SettingsState

private val state = SettingsState

object Settings : TargetState.Entry("Settings") {
    @Composable
    override fun render() {
        state.settings.render()
    }

    override val icon = @Composable { Icon(Icons.Default.Settings, "Settings") }
}