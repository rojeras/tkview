package se.skoview.model

import pl.treksoft.kvision.redux.RAction

sealed class RivAction: RAction {
    data class SetCurrentPage(val page: DisplayPage): RivAction()
    data class SelectDomainType(val type: DomainTypeEnum): RivAction()
    data class SelectDomain(val domainName: String): RivAction()
    data class SelectDomainVersion(val domainVersion: Version): RivAction()
    data class ShowHiddenDomain(val isVisible: Boolean): RivAction()
    data class ShowHiddenVersion(val isVisible: Boolean): RivAction()
    data class ShowRcVersion(val isVisible: Boolean): RivAction()
    data class ShowUnderscoreVersion(val isVisible: Boolean): RivAction()
    data class ShowTrunkVersion(val isVisible: Boolean): RivAction()
    data class DomdbLoadingComplete(val isComplete: Boolean): RivAction()
}