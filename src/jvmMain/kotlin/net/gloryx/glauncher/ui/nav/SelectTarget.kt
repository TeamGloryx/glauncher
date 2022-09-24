package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.Spacer
import net.gloryx.glauncher.util.state.Auth
import net.gloryx.glauncher.util.state.MainScreen
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageFilter

@OptIn(ExperimentalUnitApi::class)
@Composable
fun SelectTarget() {
    val cardBorder = 4.dp
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        LaunchTarget.values().map {
            val name = it.normalName
            val isSelected by derivedStateOf { MainScreen.selected == it }
            Card(
                Modifier
                    .clickable(Auth.isAuthenticated && !isSelected, "Launch $name!", Role.Button) {
                        MainScreen.selected = it
                    }
                    .indication(
                        remember { MutableInteractionSource() },
                        rememberRipple(true, 30.dp, MaterialTheme.colors.primaryVariant)
                    )
                    .wrapContentSize(Alignment.CenterStart, true)
                    .border(if (isSelected) 1.dp.unaryMinus() else cardBorder, MaterialTheme.colors.primaryVariant),
                RectangleShape,
                Color.Transparent
            ) {
                Box(Modifier.padding(cardBorder)) {
                    Image(it.painting, name, Modifier.zIndex(1f).size(200.dp), contentScale = ContentScale.Fit)
                    Text(
                        name,
                        Modifier.zIndex(2f).align(Alignment.BottomStart).offset(10.dp),
                        textAlign = TextAlign.Left,
                        fontSize = TextUnit(2f, TextUnitType.Em)
                    )
                    if (isSelected)
                        Box(Modifier.matchParentSize().zIndex(3f).paint(ColorPainter(Color.Green.copy(0.1f))))
                }
            }
        }
    }
}