package se.skoview.model

enum class DisplayPage {
    DOMAIN_LIST,
    CONTRACT_LIST,
    DOMAIN
}

data class RivState(
    val displayPage: DisplayPage,
    val domainType: DomainTypeEnum,
    val selectedDomain: ServiceDomain?,
    val selectedDomainVersion: Version?,
    val showHiddenDomain: Boolean,
    val showHiddenVersion: Boolean,
    val showRcVersion: Boolean,
    val showUnderscoreVersion: Boolean,
    val showTrunkVersion: Boolean
)

fun initializeRivState(): RivState {
    return RivState(
        displayPage = DisplayPage.DOMAIN_LIST,
        domainType = DomainTypeEnum.NATIONAL,
        selectedDomain = null,
        selectedDomainVersion = null,
        false,
        false,
        false,
        false,
        false
    )
}
