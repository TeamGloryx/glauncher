package net.gloryx.glauncher.util.ui

import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import catfish.winder.colors.*
import net.gloryx.glauncher.util.color
import cat.map

object GColors {
    val snackbar = color(0x222222)

    object InstanceScreen {
        val VibrantBlue = color(0x315bef)
        val drawerButton = ButtonDefaults.create(Slate500, White, VibrantBlue, White)
    }
}

data class GButtonColors(
    val background: Color, val content: Color, val disabledBackground: Color, val disabledContent: Color
) : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(enabled.map(background, disabledBackground))

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(enabled.map(content, disabledContent))
}

inline fun ButtonDefaults.create(background: Color, content: Color, disabledBackground: Color, disabledContent: Color) = GButtonColors(background, content, disabledBackground, disabledContent)