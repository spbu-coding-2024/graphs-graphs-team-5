package boyaan.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boyaan.model.TabState
import boyaan.model.core.base.Graph
import kotlin.math.roundToInt

@Composable
fun draggableGraphView(
    graph: Graph<String, String>,
    modifier: Modifier = Modifier,
    currentTab: TabState,
    onVertexSelected: (Int) -> Unit,
) {
    val vertexPositions = currentTab.vertexPositions

    LaunchedEffect(graph.vertices.size, currentTab.update) {
        graph.vertices.forEach { vertex ->
            if (vertex.key !in vertexPositions) {
                vertexPositions[vertex.key] =
                    Offset((100..800).random().toFloat(), (100..600).random().toFloat())
            }
        }
    }

    val edgeColor = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            graph.edges.forEach { edge ->
                val (u, v) = edge.key
                val uPos = vertexPositions[u] ?: return@forEach
                val vPos = vertexPositions[v] ?: return@forEach
                drawLine(color = edgeColor, start = uPos, end = vPos, strokeWidth = 3f)
            }
        }

        graph.vertices.forEach { vertex ->
            val pos = vertexPositions[vertex.key] ?: return@forEach
            val isSelected = vertex.key == currentTab.selectedVertex
            val density = LocalDensity.current
            val radius = with(density) { 20.dp.toPx() }
            key("${currentTab.title}_${vertex.key}") {
                Box(
                    modifier =
                        Modifier
                            .offset { IntOffset((pos.x - radius).roundToInt(), (pos.y - radius).roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = {
                                        currentTab.draggedVertex = vertex.key
                                        currentTab.selectedVertex = vertex.key
                                        onVertexSelected(vertex.key)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val id = currentTab.draggedVertex ?: return@detectDragGestures
                                        val oldPos = vertexPositions[id] ?: Offset.Zero
                                        vertexPositions[id] = oldPos + dragAmount
                                    },
                                    onDragEnd = { currentTab.draggedVertex = null },
                                    onDragCancel = { currentTab.draggedVertex = null },
                                )
                            },
                ) {
                    val circleColor =
                        if (isSelected) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.secondary
                        }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(modifier = Modifier.size(40.dp)) {
                            drawCircle(color = circleColor, radius = size.minDimension / 2f)
                        }
                        Text(vertex.value, color = MaterialTheme.colors.onSurface, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
