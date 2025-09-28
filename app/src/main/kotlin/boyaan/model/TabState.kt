package boyaan.model

import boyaan.model.core.base.Graph
import boyaan.model.core.defaults.DefaultGraph

data class TabState(
    var title: String,
    var screen: ScreenState = ScreenState.Home,
    var selectedVertex: Int? = null,
    var floatingWindows: List<FloatingWindow> = emptyList(),
    var activeWindowId: String? = null,
    var graph: Graph<String, String> = DefaultGraph(),
)
