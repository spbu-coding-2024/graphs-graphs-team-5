package boyaan.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

data class FloatingWindow(
    val id: String,
    val title: String,
    val position: Offset,
    val size: IntSize = IntSize(300, 200),
    val content: @Composable (() -> Unit)? = null,
)
