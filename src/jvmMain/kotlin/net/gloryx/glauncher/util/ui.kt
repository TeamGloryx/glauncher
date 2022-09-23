package net.gloryx.glauncher.util

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cat.f

val RowScope.w1 get() = Modifier.weight(1f)
val ColumnScope.w1 get() = Modifier.weight(1f)

@Composable
fun RowScope.Spacer(weight: Number) = Spacer(Modifier.weight(weight.f))

@Composable
fun ColumnScope.Spacer(weight: Number) = Spacer(Modifier.weight(weight.f))

@Composable
fun RowScope.Spacer(width: Dp) = Spacer(Modifier.width(width))

@Composable
fun ColumnScope.Spacer(height: Dp) = Spacer(Modifier.height(height))

@Composable
fun GButton(click: () -> Unit = {}, modifier: Modifier = Modifier, icon: (@Composable () -> Unit)? = null, colors: ButtonColors = ButtonDefaults.buttonColors(), enabled: Boolean = true, content: @Composable RowScope.() -> Unit = {}) {
    Button(click, enabled = enabled, colors = colors) {
        icon?.let {
            it()
            Spacer(2.dp)
        }
        content()
    }
}