package boyaan.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import boyaan.model.FloatingWindow

@Composable
fun graphScreen(
    floatingWindows: List<FloatingWindow>,
    activeWindowId: String?,
    onCloseWindow: (String) -> Unit,
    onMoveWindow: (String, Offset) -> Unit,
    onActivateWindow: (String) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Здесь будут графы",
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Gray,
            )
        }

        floatingWindows.forEach { window ->
            key(window.id) {
                floatingWindowComponent(
                    window = window,
                    isActive = window.id == activeWindowId,
                    onClose = { onCloseWindow(window.id) },
                    onMove = { newOffset -> onMoveWindow(window.id, newOffset) },
                    onActivate = { onActivateWindow(window.id) },
                )
            }
        }
    }
}
