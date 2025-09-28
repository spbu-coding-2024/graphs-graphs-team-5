package boyaan.model

sealed class ScreenState {
    object Home : ScreenState()

    object Graph : ScreenState()
}
