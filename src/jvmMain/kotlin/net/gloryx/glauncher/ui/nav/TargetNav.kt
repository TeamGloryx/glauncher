package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.state.MainScreen

@Composable
fun RowScope.TargetNav() {
    LaunchTarget.values().map {
        val isSelected by derivedStateOf { MainScreen.selected == it }
        val rip = rememberRipple(true, 30.dp, MaterialTheme.colors.secondary)
        val int = remember { MutableInteractionSource() }
        val text = "${it.name.uppercase()} ${it.version.replace("_", ".")}"
        Row(
            modifier = Modifier.clickable(int, rip, !isSelected) {
                MainScreen.selected = it
            }.background(
                if (isSelected) Color.Transparent else MaterialTheme.colors.primary,
                MaterialTheme.shapes.small
            ).height(30.dp).width(
                180.dp
            )
        ) {
            Text(text, Modifier.align(Alignment.CenterVertically), textAlign = TextAlign.Center)
        }
        Spacer(20.dp)
    }
}