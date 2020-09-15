package se.skoview.model

import pl.treksoft.kvision.redux.RAction

sealed class RivAction: RAction {
    data class SetCurrentPage(val page: DisplayPage): RivAction()
    data class SelectDomain(val domain: String): RivAction()
}