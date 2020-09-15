package se.skoview.model

enum class DisplayPage {
    DOMAIN_LIST,
    CONTRACT_LIST,
    DOMAIN
}

data class RivState(
    val displayPage: DisplayPage,
    val selectedDomain: String?,
    val selectedDomainVersion: String?
)

fun initializeRivState(): RivState {
    return RivState(
        displayPage = DisplayPage.DOMAIN_LIST,
        selectedDomain = null,
        selectedDomainVersion = null
    )
}