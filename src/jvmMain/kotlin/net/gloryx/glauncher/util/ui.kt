package net.gloryx.glauncher.util

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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
fun GButton(click: () -> Unit = {}, icon: (@Composable () -> Unit)? = null, content: @Composable () -> Unit = {}) {
}