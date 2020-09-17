package se.skoview.model


fun rivReducer(state: RivState, action: RivAction): RivState {
    println("In Reducer, action: $action")
    val newState: RivState = when (action) {
        is RivAction.SetCurrentPage -> state.copy(
            displayPage = action.page
        )
        is RivAction.SelectDomain -> state.copy(
            selectedDomain = action.domain,
            selectedDomainVersion = null
        )
        is RivAction.SelectDomainVersion -> state.copy(
            selectedDomainVersion = action.domainVersion
        )
        is RivAction.SelectDomainType -> state.copy(
            domainType = action.type
        )
    }
    println("<<<===== ${action::class}")
    console.log(newState)

    return newState
}