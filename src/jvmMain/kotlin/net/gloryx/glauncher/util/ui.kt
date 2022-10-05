package net.gloryx.glauncher.util

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cat.f
import cat.try_
import kotlinx.coroutines.*
import net.gloryx.glauncher.util.state.MainScreen

typealias Cfn = @Composable () -> Unit
typealias Cafn<T> = @Composable (T) -> Unit

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
fun GButton(
    click: () -> Unit = {}, icon: (@Composable () -> Unit)? = null, modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(), enabled: Boolean = true, shape: Shape = RoundedCornerShape(30),
    content: @Composable RowScope.() -> Unit = {}
) {
    Button(click, enabled = enabled, colors = colors, shape = shape, modifier = modifier) {
        icon?.let {
            it()
            Spacer(2.dp)
        }
        content()
    }
}

@Composable
fun <T : Any> sus(fn: suspend () -> T, recomposer: MutableState<out Any?> = mutableStateOf(null)): MutableState<T?> {
    val scope = rememberCoroutineScope()
    val state = remember(recomposer) { mutableStateOf<T?>(null) }
    scope.launch {
        state.value = fn()
    }

    return state
}

object GColors {
    val snackbar = color(0x222222)
}

fun snackbar(
    message: String, dismissButton: String = "Dismiss", duration: SnackbarDuration = SnackbarDuration.Short,
    then: suspend () -> Unit = {}
) {
    coro.launch {
        MainScreen.scaffold?.snackbarHostState?.showSnackbar(message, dismissButton, duration)
        then()
    }
}

@OptIn(ExperimentalUnitApi::class)
fun TextUnit(value: Number, type: TextUnitType = TextUnitType.Sp) = androidx.compose.ui.unit.TextUnit(value.f, type)

fun Modifier.useHeightRef(state: MutableState<Dp>) = onSizeChanged { (_, height) -> state.value = height.dp }
fun Modifier.useHeightRef(consumer: (Dp) -> Unit) = onSizeChanged { (_, height) -> consumer(height.dp) }
fun Modifier.useWidthRef(state: MutableState<Dp>) = onSizeChanged { (width, _) -> state.value = width.dp }
fun Modifier.useWidthRef(consumer: (Dp) -> Unit) = onSizeChanged { (width, _) -> consumer(width.dp) }