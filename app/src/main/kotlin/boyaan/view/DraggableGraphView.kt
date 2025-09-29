package boyaan.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boyaan.model.TabState
import boyaan.model.algorithms.modern.Vec2
import boyaan.model.core.base.Graph
import boyaan.model.core.internals.directed.DirectedGraph
import boyaan.model.core.internals.weighted.Weighted
import boyaan.model.core.internals.weighted.WeightedGraph
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun draggableGraphView(
    graph: Graph<String, String>,
    modifier: Modifier = Modifier,
    currentTab: TabState,
    onVertexSelected: (Int) -> Unit,
) {
    val vertexPositions = currentTab.vertexPositions
    val fa2 = currentTab.fa2
    val density = LocalDensity.current
    val radiusPx = with(density) { 20.dp.toPx() }

    LaunchedEffect(graph.vertices.size) {
        snapshotFlow { graph.vertices.map { it.key } }
            .collectLatest { keys ->
                keys.forEach { key ->
                    fa2.addVertex(key)
                    val fpos = fa2.positionsSnapshot()[key]
                    if (fpos != null) {
                        vertexPositions.putIfAbsent(key, Offset(fpos.x.toFloat(), fpos.y.toFloat()))
                    } else {
                        val pos = Offset((300..800).random().toFloat(), (100..600).random().toFloat())
                        vertexPositions.putIfAbsent(key, pos)
                        fa2.setPosition(key, Vec2(pos.x.toDouble(), pos.y.toDouble()))
                    }
                }
            }
    }

    LaunchedEffect(currentTab) {
        while (true) {
            val dragged = currentTab.draggedVertex
            val dragPos =
                dragged?.let { vertexPositions[it]?.let { pos -> Vec2(pos.x.toDouble(), pos.y.toDouble()) } }

            fa2.step(dragged, dragPos)

            fa2.positionsSnapshot().forEach { (id, pos) ->
                if (id != currentTab.draggedVertex) {
                    vertexPositions[id] = Offset(pos.x.toFloat(), pos.y.toFloat())
                }
            }

            delay(16)
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
                if (graph is DirectedGraph) {
                    val dir = (vPos - uPos).normalize()
                    val arrowSize = 35f
                    val perp = Offset(-dir.y, dir.x)
                    val arrowTip = vPos
                    val arrowLeft = vPos - dir * arrowSize + perp * (arrowSize / 2)
                    val arrowRight = vPos - dir * arrowSize - perp * (arrowSize / 2)

                    drawLine(edgeColor, arrowTip, arrowLeft, strokeWidth = 3f)
                    drawLine(edgeColor, arrowTip, arrowRight, strokeWidth = 3f)
                }
            }
        }

        graph.edges.forEach { edge ->
            if (graph is WeightedGraph && edge is Weighted) {
                val uPos = vertexPositions[edge.key.first] ?: return@forEach
                val vPos = vertexPositions[edge.key.second] ?: return@forEach
                val mid = Offset((uPos.x + vPos.x) / 2, (uPos.y + vPos.y) / 2)

                Box(
                    modifier = Modifier.offset { IntOffset(mid.x.roundToInt(), mid.y.roundToInt()) },
                ) {
                    Text(text = edge.weight.toString(), color = Color.Black, fontSize = 14.sp)
                }
            }
        }

        graph.vertices.forEach { vertex ->
            val pos = vertexPositions[vertex.key] ?: return@forEach
            val isSelected = vertex.key == currentTab.selectedVertex.value

            key("${currentTab.title}_${vertex.key}") {
                Box(
                    modifier =
                        Modifier
                            .offset { IntOffset((pos.x - radiusPx).roundToInt(), (pos.y - radiusPx).roundToInt()) }
                            .pointerInput(vertex.key) {
                                awaitEachGesture {
                                    val down = awaitFirstDown()
                                    currentTab.selectedVertex.value = vertex.key
                                    onVertexSelected(vertex.key)

                                    val drag =
                                        awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                            change.consume()
                                        }

                                    if (drag != null) {
                                        currentTab.draggedVertex = vertex.key
                                        val pointerId = drag.id
                                        var dragChange: PointerEvent
                                        do {
                                            dragChange = awaitPointerEvent()
                                            val change = dragChange.changes.firstOrNull { it.id == pointerId } ?: continue
                                            val oldPos = vertexPositions[vertex.key] ?: Offset.Zero
                                            val newPos = oldPos + change.positionChange()
                                            vertexPositions[vertex.key] = newPos
                                            fa2.setPosition(vertex.key, Vec2(newPos.x.toDouble(), newPos.y.toDouble()))
                                            change.consume()
                                        } while (!dragChange.changes.all { it.changedToUpIgnoreConsumed() })
                                        currentTab.draggedVertex = null
                                    }
                                }
                            },
                ) {
                    val circleColor =
                        when {
                            currentTab.highlightedVertex[vertex.key] == true -> Color.Red
                            isSelected -> MaterialTheme.colors.primary
                            else -> MaterialTheme.colors.secondary
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

private fun Offset.normalize(): Offset {
    val len = sqrt(x * x + y * y)
    return if (len > 0f) Offset(x / len, y / len) else Offset.Zero
}
