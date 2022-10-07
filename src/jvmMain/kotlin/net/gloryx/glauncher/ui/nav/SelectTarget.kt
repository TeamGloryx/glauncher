package net.gloryx.glauncher.ui.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cat.map
import cat.ui.dlg.forget
import catfish.winder.colors.Green300
import net.gloryx.glauncher.logic.target.LaunchTarget
import net.gloryx.glauncher.util.*
import net.gloryx.glauncher.util.state.MainScreen

object SelectTarget : TargetState.Entry("Select") {
    @Composable
    override fun render(padding: PaddingValues?) {
        val cardBorder = 4.dp
        LazyVerticalGrid(GridCells.Fixed(3), padding?.let(Modifier.Companion::padding) ?: Modifier) {
            items(LaunchTarget.values(), LaunchTarget::ordinal) {
                val name = it.normalName
                val isSelected by forget { derivedStateOf { MainScreen.selected == it } }
                Card(
                    Modifier
                        .clickable(!isSelected, "Launch $name!", Role.Button) {
                            MainScreen.selected = it
                        }
                        .indication(
                            forget { MutableInteractionSource() },
                            rememberRipple(true, 30.dp, MaterialTheme.colors.primaryVariant)
                        )
                        .wrapContentSize(Alignment.CenterStart, true)
                        .border(cardBorder, isSelected.map(Green300, MaterialTheme.colors.primaryVariant)),
                    RectangleShape,
                    Color.Transparent,
                    elevation = 10.dp
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
}