package boyaan.view

import androidx.compose.ui.geometry.Offset

fun isPointInCircle(
    point: Offset,
    center: Offset,
    radius: Float,
): Boolean {
    val dx = point.x - center.x
    val dy = point.y - center.y
    return dx * dx + dy * dy <= radius * radius
}
