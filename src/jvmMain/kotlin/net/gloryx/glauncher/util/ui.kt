@file:Suppress("unused", "nothing_to_inline")

package net.gloryx.glauncher.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import cat.f
import cat.map
import cat.ui.dlg.forget
import cat.ui.dlg.useState
import catfish.winder.colors.Black
import catfish.winder.colors.Transparent
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.gloryx.glauncher.util.state.MainScreen
import java.io.File

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
    colors: ButtonColors = ButtonDefaults.buttonColors(), enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(30),
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
fun GTextButton(
    click: () -> Unit = {}, icon: (@Composable () -> Unit)? = null, modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(Transparent, Black, Transparent, Black), enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(30),
    content: @Composable RowScope.() -> Unit = {}
) {
    TextButton(click, modifier, enabled, colors = colors, shape = shape) {
        icon?.let {
            it()
            Spacer(2.dp)
        }
        content()
    }
}

@Composable
fun <T : Any> suspendState(fn: suspend () -> T, recomposer: Any? = true): State<T?> =
    MutableStateFlow<T?>(null).also { LaunchedEffect(recomposer) { it.emit(fn()) } }.collectAsState()

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

@Composable
inline fun <T> useDensity(block: Density.() -> T) = with(LocalDensity.current, block)

@Composable
fun GSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    /*@IntRange(from = 0)*/
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors()
) = Slider(value, onValueChange, modifier, enabled, valueRange, steps, onValueChangeFinished, interactionSource, colors)

@Composable
fun GSlider(
    state: MutableState<Float>, modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    /*@IntRange(from = 0)*/
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors()
) = GSlider(
    state.component1(),
    state.component2(),
    modifier,
    enabled,
    valueRange,
    steps,
    onValueChangeFinished,
    interactionSource,
    colors
)

val String.conf: Config get() = let(ConfigFactory::parseString)

val File.rs get() = toRelativeString(Static.root)

@Composable
@Suppress("nothing_to_inline") // forget is inline.
inline fun forgetInteractionSource() = forget { MutableInteractionSource() }

@Composable
fun Modifier.verticalSplitter(width: Dp, take: Dp = 1.dp, padding: Dp = 3.dp, color: Color = MaterialTheme.colors.secondary) = composed {
    val (height, setHeight) = useState(0.dp)

    Modifier.useHeightRef(setHeight)
        .drawBehind { drawLine(color, Offset(0f, take.toPx()), Offset(0f, height.toPx() - take.toPx()), width.toPx()) }
        .padding(start = width + padding)
}

@Composable
fun RowScope.VerticalEndSplitter(width: Dp, color: Color = MaterialTheme.colors.secondary) = Box(Modifier.fillMaxHeight().width(width).background(color))

@Composable
fun Modifier.horizontalSplitter(
    height: Dp, take: Dp = 1.dp, after: Boolean = true, color: Color = MaterialTheme.colors.secondary
) = composed {
    val (width, setWidth) = useState(0.dp)
    val (h, setH) = useState(0.dp)

    Modifier.useWidthRef(setWidth).then(
        after.map(
            Modifier.useHeightRef(setH).padding(end = height - take),
            Modifier.padding(top = height + take)
        )
    )
        .drawBehind {
            drawLine(
                color,
                Offset(take.toPx(), after.map(h.toPx() + take.toPx(), 0f)),
                Offset(width.toPx() - take.toPx(), after.map(h.toPx() + take.toPx(), 0f)),
                height.toPx()
            )
        }
}