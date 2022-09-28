package net.gloryx.glauncher.util

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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
    click: () -> Unit = {}, modifier: Modifier = Modifier, icon: (@Composable () -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(), enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = {}
) {
    Button(click, enabled = enabled, colors = colors) {
        icon?.let {
            it()
            Spacer(2.dp)
        }
        content()
    }
}

@Composable
fun <T : Any> Suspense(fn: suspend () -> T, fallback: Cfn, content: Cafn<T>) {
    Suspense(sus(fn), fallback, content)
}

@Composable
fun <T : Any> Suspense(state: MutableState<T?>, fallback: Cfn, content: Cafn<T>) {
    if (state.value != null) println(state.value)
    state.value?.let { content(it) } ?: fallback()
}

@Composable
fun Suspend(fn: suspend CoroutineScope.() -> Unit): Job = rememberCoroutineScope().launch { fn() }

@Composable
fun <T> Async(fn: suspend CoroutineScope.() -> T): Deferred<T> = rememberCoroutineScope().async { fn() }

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