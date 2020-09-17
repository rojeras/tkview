package se.skoview.model

enum class DisplayPage {
    DOMAIN_LIST,
    CONTRACT_LIST,
    DOMAIN
}

enum class FILTER_TYPE {
    HIDDEN,
    RC,
    UNDERSCORE,
    TRUNK
}

data class RivState(
    val displayPage: DisplayPage,
    val domainType: DomainType,
    val selectedDomain: String?,
    val selectedDomainVersion: String?,
    val filterMapp: Map<FILTER_TYPE, Boolean>
)

fun initializeRivState(): RivState {
    return RivState(
        displayPage = DisplayPage.DOMAIN_LIST,
        domainType = DomainType.NATIONAL,
        selectedDomain = null,
        selectedDomainVersion = null,
        filterMapp = mapOf(FILTER_TYPE.HIDDEN to true, FILTER_TYPE.RC to true, FILTER_TYPE.UNDERSCORE to true, FILTER_TYPE.TRUNK to true)
    )
}