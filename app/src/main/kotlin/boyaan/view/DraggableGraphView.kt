package boyaan.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boyaan.model.TabState
import boyaan.model.algorithms.modern.Vec2
import boyaan.model.core.base.Graph
import boyaan.model.core.internals.directed.Directed
import boyaan.model.core.internals.directed.DirectedGraph
import boyaan.model.core.internals.weighted.Weighted
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val VERTEX_RADIUS_DP = 15
private const val VERTEX_SIZE_DP = 30
private const val ARROW_SIZE = 20f
private const val CURVE_OFFSET = 40f
private const val EDGE_CLICK_THRESHOLD = 15f
private const val EDGE_STROKE_WIDTH = 3f
private const val TANGENT_SAMPLE_PARAMETER = 0.95f
private const val BEZIER_CURVE_SAMPLES = 20
private const val ANIMATION_FRAME_DELAY = 16L

@Composable
fun draggableGraphView(
    graph: Graph<String, String>,
    modifier: Modifier = Modifier,
    currentTab: TabState,
    onVertexSelected: (Int) -> Unit,
    onEdgeSelected: (Pair<Int, Int>) -> Unit,
) {
    fun Offset.normalize(): Offset {
        val len = sqrt(x * x + y * y)
        return if (len > 0f) Offset(x / len, y / len) else Offset.Zero
    }

    fun distanceToSegment(
        p: Offset,
        a: Offset,
        b: Offset,
    ): Float {
        val ab = b - a
        val ap = p - a
        val abLen = ab.getDistance()
        val segmentParameter = ((ap.x * ab.x + ap.y * ab.y) / (abLen * abLen)).coerceIn(0f, 1f)
        val closest = Offset(a.x + ab.x * segmentParameter, a.y + ab.y * segmentParameter)
        return (p - closest).getDistance()
    }

    fun calculateBezierPoint(
        t: Float,
        start: Offset,
        control: Offset,
        end: Offset,
    ): Offset {
        val oneMinusT = 1 - t
        return Offset(
            oneMinusT * oneMinusT * start.x + 2 * oneMinusT * t * control.x + t * t * end.x,
            oneMinusT * oneMinusT * start.y + 2 * oneMinusT * t * control.y + t * t * end.y,
        )
    }

    fun distanceToBezierCurve(
        point: Offset,
        start: Offset,
        control: Offset,
        end: Offset,
    ): Float =
        (0..BEZIER_CURVE_SAMPLES).minOf { i ->
            val t = i / BEZIER_CURVE_SAMPLES.toFloat()
            val curvePoint = calculateBezierPoint(t, start, control, end)
            (point - curvePoint).getDistance()
        }

    fun calculateCurveTangent(
        start: Offset,
        control: Offset,
        end: Offset,
    ): Offset {
        val pointNearEnd = calculateBezierPoint(TANGENT_SAMPLE_PARAMETER, start, control, end)
        return (end - pointNearEnd).normalize()
    }

    fun calculateControlPoint(
        start: Offset,
        end: Offset,
    ): Offset {
        val midPoint = (start + end) / 2f
        val direction = (end - start).normalize()
        val perpendicular = Offset(-direction.y, direction.x)
        return midPoint + perpendicular * CURVE_OFFSET
    }

    fun DrawScope.drawArrowHead(
        tip: Offset,
        direction: Offset,
        size: Float,
        color: Color,
    ) {
        val perpendicular = Offset(-direction.y, direction.x)
        val leftPoint = tip - direction * size + perpendicular * (size / 2)
        val rightPoint = tip - direction * size - perpendicular * (size / 2)

        drawPath(
            path =
                androidx.compose.ui.graphics.Path().apply {
                    moveTo(tip.x, tip.y)
                    lineTo(leftPoint.x, leftPoint.y)
                    lineTo(rightPoint.x, rightPoint.y)
                    close()
                },
            color = color,
        )
    }

    val vertexPositions = currentTab.vertexPositions
    val fa2 = currentTab.fa2
    val density = LocalDensity.current
    val radiusPx = with(density) { VERTEX_RADIUS_DP.dp.toPx() }

    LaunchedEffect(graph.vertices.size) {
        snapshotFlow { graph.vertices.map { it.key } }
            .collectLatest { keys ->
                keys.forEach { key ->
                    fa2.addVertex(key)
                    val fpos = fa2.positionsSnapshot()[key]
                    val initialPos =
                        fpos?.let { Offset(it.x.toFloat(), it.y.toFloat()) }
                            ?: Offset((300..800).random().toFloat(), (100..600).random().toFloat())

                    vertexPositions.putIfAbsent(key, initialPos)
                    fa2.setPosition(key, Vec2(initialPos.x.toDouble(), initialPos.y.toDouble()))
                }
            }
    }

    LaunchedEffect(currentTab) {
        while (true) {
            val dragged = currentTab.draggedVertex
            val dragPos =
                dragged?.let {
                    vertexPositions[it]?.let { pos -> Vec2(pos.x.toDouble(), pos.y.toDouble()) }
                }

            fa2.step(dragged, dragPos)

            fa2.positionsSnapshot().forEach { (id, pos) ->
                if (id != currentTab.draggedVertex) {
                    vertexPositions[id] = Offset(pos.x.toFloat(), pos.y.toFloat())
                }
            }

            delay(ANIMATION_FRAME_DELAY)
        }
    }

    val edgeColor = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
    val selectedEdgeColor = MaterialTheme.colors.primary

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            graph.edges.forEach { edge ->
                val (u, v) = edge.key
                val startPos = vertexPositions[u] ?: return@forEach
                val endPos = vertexPositions[v] ?: return@forEach

                val isSelected = edge.key == currentTab.selectedEdge.value
                val color = if (isSelected) selectedEdgeColor else edgeColor

                if (graph is DirectedGraph) {
                    val hasReverseEdge = graph.edges.any { it.key == Pair(v, u) }
                    val direction = (endPos - startPos).normalize()

                    if (hasReverseEdge) {
                        val controlPoint = calculateControlPoint(startPos, endPos)
                        val curvePath =
                            androidx.compose.ui.graphics.Path().apply {
                                moveTo(startPos.x, startPos.y)
                                quadraticBezierTo(controlPoint.x, controlPoint.y, endPos.x, endPos.y)
                            }

                        drawPath(
                            path = curvePath,
                            color = color,
                            style =
                                androidx.compose.ui.graphics.drawscope
                                    .Stroke(width = EDGE_STROKE_WIDTH),
                        )

                        val tangent = calculateCurveTangent(startPos, controlPoint, endPos)
                        drawArrowHead(endPos, tangent, ARROW_SIZE, color)
                    } else {
                        drawLine(color = color, start = startPos, end = endPos, strokeWidth = EDGE_STROKE_WIDTH)
                        drawArrowHead(endPos, direction, ARROW_SIZE, color)
                    }
                } else {
                    drawLine(color = color, start = startPos, end = endPos, strokeWidth = EDGE_STROKE_WIDTH)
                }
            }
        }

        Box(
            modifier =
                Modifier.matchParentSize().pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val clickPos = down.position

                        var closestEdge: Pair<Int, Int>? = null
                        var minDist = Float.MAX_VALUE

                        graph.edges.forEach { edge ->
                            val (u, v) = edge.key
                            val startPos = vertexPositions[u] ?: return@forEach
                            val endPos = vertexPositions[v] ?: return@forEach

                            val dist =
                                if (graph is DirectedGraph && graph.edges.any { it.key == Pair(v, u) }) {
                                    val controlPoint = calculateControlPoint(startPos, endPos)
                                    distanceToBezierCurve(clickPos, startPos, controlPoint, endPos)
                                } else {
                                    distanceToSegment(clickPos, startPos, endPos)
                                }

                            if (dist < EDGE_CLICK_THRESHOLD && dist < minDist) {
                                minDist = dist
                                closestEdge = edge.key
                            }
                        }

                        closestEdge?.let {
                            currentTab.selectedVertex.value = null
                            currentTab.selectedEdge.value = it
                            onEdgeSelected(it)
                            down.consume()
                        }
                    }
                },
        )

        graph.edges.forEach { edge ->
            val (u, v) = edge.key
            val startPos = vertexPositions[u] ?: return@forEach
            val endPos = vertexPositions[v] ?: return@forEach

            key("${currentTab.title}_edge_${edge.key}") {
                if (edge is Weighted) {
                    val hasReverseEdge = edge is Directed && graph.edges.any { it.key == Pair(v, u) }
                    val midPoint =
                        if (hasReverseEdge) {
                            val controlPoint = calculateControlPoint(startPos, endPos)
                            calculateBezierPoint(0.5f, startPos, controlPoint, endPos)
                        } else {
                            (startPos + endPos) / 2f
                        }

                    Box(
                        modifier =
                            Modifier.layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                layout(placeable.width, placeable.height) {
                                    placeable.place(
                                        x = midPoint.x.roundToInt() - placeable.width / 2,
                                        y = midPoint.y.roundToInt() - placeable.height / 2,
                                    )
                                }
                            },
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                                    .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = edge.weight.toString(),
                                color = Color.Black,
                                fontSize = 11.sp,
                                style = MaterialTheme.typography.body2,
                            )
                        }
                    }
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
                                    currentTab.selectedEdge.value = null
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
                                            val change =
                                                dragChange.changes.firstOrNull { it.id == pointerId } ?: continue
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
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(VERTEX_SIZE_DP.dp)) {
                                drawCircle(color = circleColor, radius = size.minDimension / 2f)
                            }
                            Text(
                                text = vertex.key.toString(),
                                color = MaterialTheme.colors.onPrimary,
                                fontSize = 16.sp,
                            )
                        }
                        Text(vertex.value, color = MaterialTheme.colors.onSurface, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
