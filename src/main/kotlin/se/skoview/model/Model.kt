package se.skoview.model

enum class DisplayPage {
    DOMAIN_LIST,
    CONTRACT_LIST,
    DOMAIN
}

data class RivState(
    val displayPage: DisplayPage,
    val domainType: DomainTypeEnum,
    val selectedDomainName: String?,
    val selectedDomainVersion: Version?,
    val showHiddenDomain: Boolean,
    val showHiddenVersion: Boolean,
    val showRcVersion: Boolean,
    val showUnderscoreVersion: Boolean,
    val showTrunkVersion: Boolean,
    val domdbLoadingComplete: Boolean
)

fun initializeRivState(): RivState {
    return RivState(
        displayPage = DisplayPage.DOMAIN_LIST,
        domainType = DomainTypeEnum.NATIONAL,
        selectedDomainName = null,
        selectedDomainVersion = null,
        false,
        false,
        false,
        false,
        false,
        false
    )
}
