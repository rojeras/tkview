package se.skoview.rivta

import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.html.Div
import pl.treksoft.kvision.html.button
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.h1
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.panel.vPanel
import pl.treksoft.kvision.state.bind
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vh
import se.skoview.app.store
import se.skoview.model.DisplayPage
import se.skoview.model.RivAction
import tabs.rivta.ContractListPage
import tabs.rivta.DomainListPage

var rivTaPageTop: Div = Div()

object RivTaMainPage : SimplePanel() {
    init {
        id = "top-init"
        height = 100.vh
        background = Background(Color.name(Col.YELLOW))
        rivTaPageTop = div {
        }.bind(store) { state ->
            id = "top-bind"
            background = Background(Color.name(Col.SALMON))
            button("Lista tjänstedomäner")
                .onClick {
                    store.dispatch(RivAction.SetCurrentPage(DisplayPage.DOMAIN_LIST))
                }.apply {
                    addBsBgColor(BsBgColor.LIGHT)
                    addBsColor(BsColor.BLACK50)
                    marginBottom = 5.px
                    disabled = state.displayPage == DisplayPage.DOMAIN_LIST
                }

            button("Lista tjänstekontrakt")
                .onClick {
                    store.dispatch(RivAction.SetCurrentPage(DisplayPage.CONTRACT_LIST))
                }.apply {
                    addBsBgColor(BsBgColor.LIGHT)
                    addBsColor(BsColor.BLACK50)
                    marginBottom = 5.px
                    disabled = state.displayPage == DisplayPage.CONTRACT_LIST
                }

            when (state.displayPage) {
                DisplayPage.DOMAIN_LIST -> add(DomainListPage)
                DisplayPage.CONTRACT_LIST -> add(ContractListPage)
                DisplayPage.DOMAIN -> add(DomainPage)
            }
        }
    }
}
