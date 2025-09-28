package boyaan.view

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import boyaan.model.core.base.Graph

@Composable
fun graphView(
    graph: Graph<String, String>,
    vertexPositions: Map<Int, Offset>,
    selectedVertexId: Int? = null,
    modifier: Modifier = Modifier,
) {
    val textMeasurer: TextMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        graph.edges.forEach { edge ->
            val (u, v) = edge.key
            val uPos = vertexPositions[u] ?: return@forEach
            val vPos = vertexPositions[v] ?: return@forEach

            drawLine(
                color = Color.Gray,
                start = uPos,
                end = vPos,
                strokeWidth = 3f,
            )
        }

        graph.vertices.forEach { vertex ->
            val pos = vertexPositions[vertex.key] ?: return@forEach
            val isSelected = vertex.key == selectedVertexId

            drawCircle(
                color = if (isSelected) Color.Red else Color.Cyan,
                radius = 30f,
                center = pos,
            )

            val textLayoutResult: TextLayoutResult =
                textMeasurer.measure(
                    text =
                        androidx.compose.ui.text
                            .AnnotatedString(vertex.value.toString()),
                )
            drawText(
                textLayoutResult,
                topLeft =
                    Offset(
                        pos.x - textLayoutResult.size.width / 2f,
                        pos.y - textLayoutResult.size.height / 2f,
                    ),
            )
        }
    }
}
