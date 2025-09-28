package boyaan.model

data class TabState(
    var title: String,
    var screen: ScreenState = ScreenState.Home,
    var selectedNode: String? = null,
    var floatingWindows: List<FloatingWindow> = emptyList(),
    var activeWindowId: String? = null,
)
