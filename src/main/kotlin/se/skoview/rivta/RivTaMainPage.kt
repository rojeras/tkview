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

        vPanel {
            /*
            window("Window", 600.px, 600.px, closeButton = true,
                maximizeButton = true, minimizeButton = true, icon = "fas fa-edit") {
                left = ((Random.nextDouble() * 800).toInt()).px
                top = ((Random.nextDouble() * 300).toInt()).px
                span("A window content")
            }
             */

            div {
                h1("Tjänstedomäner och tjänstekontrakt")
                div {
                    +"Här hittar du en förteckning över samtliga tjänstedomäner . I tabellen kan du också se om de är installerade i den nationella Tjänsteplattformen eller inte."
                    +"Informationen på denna sida är hämtad från subversion, tjänstekontraktsbeskrivningar samt tjänsteadresseringskatalogerna i den nationella tjänsteplattformen. Klicka på länkarna i tabellen för mer information."
                }
            }.apply {
                //width = 100.perc
                id = "pageHeaderDiv"
                background = Background(Color.hex(0x113d3d))
                align = pl.treksoft.kvision.html.Align.CENTER
                color = Color.name(Col.WHITE)
                marginTop = 5.px
            }
        }
        button("Lista tjänstedomäner")
            .onClick {
                store.dispatch(RivAction.SetCurrentPage(DisplayPage.DOMAIN_LIST))
            }.apply {
                addBsBgColor(BsBgColor.LIGHT)
                addBsColor(BsColor.BLACK50)
                marginBottom = 5.px
            }
        button("Lista tjänstekontrakt")
            .onClick {
                store.dispatch(RivAction.SetCurrentPage(DisplayPage.CONTRACT_LIST))
            }.apply {
                addBsBgColor(BsBgColor.LIGHT)
                addBsColor(BsColor.BLACK50)
                marginBottom = 5.px
            }

        div {  }.bind(store) { state ->
            when (state.displayPage) {
                DisplayPage.DOMAIN_LIST -> add(DomainListPage)
                DisplayPage.CONTRACT_LIST -> add(ContractListPage)
                DisplayPage.DOMAIN -> add(DomainPage)
            }
        }
    }
}
