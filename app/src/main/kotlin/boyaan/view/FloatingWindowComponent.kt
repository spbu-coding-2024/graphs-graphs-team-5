package boyaan.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import boyaan.model.FloatingWindow
import kotlin.math.roundToInt

@Composable
fun floatingWindowComponent(
    window: FloatingWindow,
    isActive: Boolean,
    onClose: () -> Unit,
    onMove: (Offset) -> Unit,
    onActivate: () -> Unit,
) {
    var offset by remember { mutableStateOf(window.position) }

    Box(
        modifier =
            Modifier
                .zIndex(if (isActive) 1000f else 1f)
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                .width(350.dp)
                .padding(4.dp)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    onActivate()
                },
    ) {
        Column(
            modifier =
                Modifier
                    .background(
                        if (isActive) {
                            MaterialTheme.colors.surface
                        } else {
                            MaterialTheme.colors.surface.copy(alpha = 0.95f)
                        },
                        shape = MaterialTheme.shapes.medium,
                    ).border(
                        2.dp,
                        if (isActive) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        },
                        shape = MaterialTheme.shapes.medium,
                    ),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            if (isActive) {
                                MaterialTheme.colors.primary
                            } else {
                                MaterialTheme.colors.primary.copy(alpha = 0.7f)
                            },
                        ).padding(8.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    onActivate()
                                },
                                onDrag = { _, dragAmount ->
                                    val newOffset = offset + dragAmount
                                    offset = newOffset
                                    onMove(newOffset)
                                },
                                onDragEnd = { },
                            )
                        },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    window.title,
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(1f),
                )

                IconButton(
                    onClick = {
                        println("Close button clicked")
                        onClose()
                    },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(MaterialTheme.colors.surface)
                        .verticalScroll(rememberScrollState()),
            ) {
                window.content?.invoke()
            }
        }
    }
}
