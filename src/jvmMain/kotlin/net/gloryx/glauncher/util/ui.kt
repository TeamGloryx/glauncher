package net.gloryx.glauncher.util

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cat.f
import kotlinx.coroutines.launch
import net.gloryx.glauncher.util.state.MainScreen

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

@Composable fun <T> Suspense(operation: @DisallowComposableCalls suspend () -> T, fallback: @Composable () -> Unit = { Text("Please wait...") }, content: @Composable (T) -> Unit) {
    val (it, set) = mutableStateOf<T?>(null)

    rememberCoroutineScope().launch {
        set(operation())
    }

    it?.let { value -> content(value) } ?: fallback()
}

object GColors {
    val snackbar = color(0x222222)
}

fun snackbar(message: String, dismissButton: String = "Dismiss", duration: SnackbarDuration = SnackbarDuration.Short, callback: suspend () -> Unit = {}) {
    coro.launch {
        MainScreen.scaffold?.snackbarHostState?.showSnackbar(message, dismissButton, duration)
        callback()
    }
}