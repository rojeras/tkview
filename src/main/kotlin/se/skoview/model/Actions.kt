package se.skoview.model

import pl.treksoft.kvision.redux.RAction

sealed class RivAction: RAction {
    data class SetCurrentPage(val page: DisplayPage): RivAction()
    data class SelectDomainType(val type: DomainType): RivAction()
    data class SelectDomain(val domain: String): RivAction()
    data class SelectDomainVersion(val domainVersion: String): RivAction()
}