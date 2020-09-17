package se.skoview.rivta

import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.html.button
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.h1
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.panel.vPanel
import pl.treksoft.kvision.state.bind
import pl.treksoft.kvision.utils.px
import se.skoview.app.store
import se.skoview.model.DisplayPage
import se.skoview.model.RivAction
import tabs.rivta.ContractListPage
import tabs.rivta.DomainListPage


object RivTaMainPage : SimplePanel() {
    init {
        println("In DomainListPage")

        div {  }.bind(store) { state ->
            when (state.displayPage) {
                DisplayPage.DOMAIN_LIST -> add(DomainListPage)
                DisplayPage.CONTRACT_LIST -> add(ContractListPage)
                DisplayPage.DOMAIN -> add(DomainPage)
            }
        }
    }
}
