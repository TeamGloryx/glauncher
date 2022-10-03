package net.gloryx.glauncher.util.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cat.i
import cat.ui.dlg.getValue
import net.gloryx.glauncher.util.color
import kotlin.time.Duration.Companion.seconds

@Composable
fun Spinner(spinning: Boolean, cycle: Int = 1000, modifier: Modifier = Modifier, color: Color = color(0xff), strokeWidth: Dp = 6.dp) {
    if (spinning) {
        val animation by rememberInfiniteTransition().animateFloat(
            0f,
            100f,
            InfiniteRepeatableSpec(
                KeyframesSpec(
                    KeyframesSpec.KeyframesSpecConfig<Float>()
                        .apply { durationMillis = cycle })
            )
        )
        CircularProgressIndicator(animation / 100f, modifier, color, strokeWidth)
    }
}

fun Modifier.drawWithoutRect(rect: RoundRect?) =
    drawWithContent {
        if (rect != null) {
            clipRect(
                left = rect.left,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                clipOp = ClipOp.Difference,
            ) {
                this@drawWithContent.drawContent()
            }
        } else {
            drawContent()
        }
    }