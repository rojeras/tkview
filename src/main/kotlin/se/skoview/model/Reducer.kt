package se.skoview.model

fun rivReducer(state: RivState, action: RivAction): RivState {
    println("In Reducer, action: $action")
    val newState: RivState = when (action) {
        is RivAction.SetCurrentPage -> state.copy(
            displayPage = action.page
        )
        is RivAction.SelectDomain -> state.copy(
            selectedDomain = action.domain
        )
    }
    println("<<<===== ${action::class}")
    console.log(newState)

    return newState
}