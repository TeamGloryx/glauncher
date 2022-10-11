package net.gloryx.glauncher.util.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.ui.ComposableFn
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.set

object GCheckboxDefaults {
    val spacing = 4.dp
}

@Composable
fun GCheckbox(state: MutableState<Boolean>, modifier: Modifier = Modifier, checkboxModifier: Modifier = Modifier, content: ComposableFn) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    Checkbox(state.value, state::set, checkboxModifier)

    Spacer(GCheckboxDefaults.spacing)

    content()
}