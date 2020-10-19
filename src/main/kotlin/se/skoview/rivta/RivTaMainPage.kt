/**
 * Copyright (C) 2020 Lars Erik Röjerås
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package se.skoview.rivta

import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.form.check.checkBoxInput
import pl.treksoft.kvision.form.select.simpleSelectInput
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.*
import pl.treksoft.kvision.routing.routing
import pl.treksoft.kvision.state.bind
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vh
import se.skoview.app.View
import se.skoview.app.formControlXs
import se.skoview.app.store
import se.skoview.model.*
import tabs.rivta.ContractListPage
import tabs.rivta.DomainListPage

var rivTaPageTop: Div = Div()

object RivTaMainPage : SimplePanel() {
    init {
        // background = Background(Color.hex(0x00706E))
        fontFamily = "Times New Roman"
        id = "top-init"
        // height = 95.vh
        this.marginTop = 10.px
        // background = Background(Color.name(Col.YELLOW))
        rivTaPageTop = div {
        }.bind(store) { state ->
            id = "top-bind"

            simplePanel {
                background = Background(Color.hex(0x00706E))
                overflow = Overflow.HIDDEN
                div {
                    h2("Information om tjänstedomäner och tjänstekontrakt")
                    // div("Detaljerad statistik med diagram och möjlighet att ladda ner informationen för egna analyser.")
                }.apply {
                    // width = 100.perc
                    id = "RivtaPage-HeadingArea:Div"
                    // background = Background(Color.hex(0x00706E))
                    color = Color.name(Col.WHITE)
                    align = Align.CENTER
                    color = Color.name(Col.WHITE)
                    marginTop = 5.px
                }
                //  flexPanel( FlexDirection.ROW, FlexWrap.WRAP, JustifyContent.SPACEBETWEEN, AlignItems.CENTER ) {
                hPanel {
                    fontSize = 20.px
                    background = Background(Color.hex(0x091F5F3))
                    // spacing = 5
                    clear = Clear.BOTH
                    margin = 0.px
                    // background = Background(Color.hex(0xf6efe9))
                    id = "RivtaPage-ControlPanel:FlexPanel-Bind"
                    // background = Background(Color.name(Col.SALMON))

                    vPanel {
                        marginLeft = 5.px
                        marginRight = 10.px
                        marginTop = 5.px
                        add(SelectPageButton("Lista Tjänstedomäner", DisplayPage.DOMAIN_LIST))
                        add(SelectPageButton("Lista Tjänstekontrakt", DisplayPage.CONTRACT_LIST))
                    }
                    vPanel {
                        marginLeft = 10.px
                        if (state.displayPage != DisplayPage.DOMAIN) {
                            add(
                                FilterCheckBox(
                                    "Inkludera dolda tjänstedomäner",
                                    RivAction.ShowHiddenDomain::class.simpleName,
                                    state.showHiddenDomain
                                )
                            )
                            hPanel {
                                span("Typ av tjänstedomän:")
                                add(
                                    SelectDomainType(state)
                                        .apply { marginLeft = 50.px }
                                )
                            }
                        }
                        if (state.displayPage == DisplayPage.DOMAIN)
                            add(
                                FilterCheckBox(
                                    "Inkludera dolda versioner",
                                    RivAction.ShowHiddenVersion::class.simpleName,
                                    state.showHiddenVersion
                                )
                            )
                        if (state.displayPage == DisplayPage.DOMAIN)
                            add(
                                FilterCheckBox(
                                    "inkludera rc-versioner",
                                    RivAction.ShowRcVersion::class.simpleName,
                                    state.showRcVersion
                                )
                            )

                        if (state.displayPage == DisplayPage.DOMAIN)
                            add(
                                FilterCheckBox(
                                    "Inkludera trunk",
                                    RivAction.ShowTrunkVersion::class.simpleName,
                                    state.showTrunkVersion
                                )
                            )
                    }
                    vPanel {
                        if (state.displayPage == DisplayPage.DOMAIN_LIST || state.displayPage == DisplayPage.CONTRACT_LIST) {
                        }
                    }
                }
            }

            when (state.displayPage) {
                DisplayPage.DOMAIN_LIST -> add(DomainListPage)
                DisplayPage.CONTRACT_LIST -> add(ContractListPage)
                DisplayPage.DOMAIN -> add(DomainPage)
            }
        }
    }
}

private class SelectDomainType(state: RivState) : SimplePanel() {
    init {
        val options =
            listOf(
                Pair(DomainTypeEnum.NATIONAL.toString(), "Nationella tjänstedomäner"),
                Pair(DomainTypeEnum.APPLICATION_SPECIFIC.toString(), "Applikationsspecifika tjänstedomäner"),
                Pair(DomainTypeEnum.EXTERNAL.toString(), "Externa tjänstedomäner")
            )
        simpleSelectInput(
            options = options,
            value = state.domainType.toString()
        ) {
            // fontStyle = FontStyle.OBLIQUE
            // fontWeight = FontWeight.BOLD
            background = Background(Color.name(Col.WHITE))
            fontSize = 20.px
            addCssStyle(formControlXs)
        }.onEvent {
            change = {
                val selected: String = self.value ?: ""
                val selectedType: DomainTypeEnum =
                    when (selected) {
                        DomainTypeEnum.NATIONAL.toString() -> DomainTypeEnum.NATIONAL
                        DomainTypeEnum.APPLICATION_SPECIFIC.toString() -> DomainTypeEnum.APPLICATION_SPECIFIC
                        DomainTypeEnum.EXTERNAL.toString() -> DomainTypeEnum.EXTERNAL
                        else -> DomainTypeEnum.NATIONAL
                    }
                store.dispatch(RivAction.SelectDomainType(selectedType))
            }
        }
    }
}

private class SelectPageButton(label: String, page: DisplayPage) : SimplePanel() {
    init {
        val state = store.getState()

        marginBottom = 5.px
        button(label)
            .onClick {
                println("Button '$label' clicked")
                store.dispatch(RivAction.SetCurrentPage(page))
            }.apply {
                size = ButtonSize.SMALL
                addBsBgColor(BsBgColor.LIGHT)
                addBsColor(BsColor.BLACK50)
                marginBottom = 5.px
                disabled = state.displayPage == page
            }
    }
}

private class FilterCheckBox(label: String, action: String?, currentlySet: Boolean) : SimplePanel() {
    init {
        val state = store.getState()
        div {
            checkBoxInput(
                value = currentlySet,
            ).onClick {
                // todo: Figure out how to dispatch the action directly whithout "when"
                when (action) {
                    RivAction.ShowHiddenDomain::class.simpleName -> store.dispatch(RivAction.ShowHiddenDomain(value))
                    RivAction.ShowHiddenVersion::class.simpleName -> store.dispatch(RivAction.ShowHiddenVersion(value))
                    RivAction.ShowTrunkVersion::class.simpleName -> store.dispatch(RivAction.ShowTrunkVersion(value))
                    RivAction.ShowRcVersion::class.simpleName -> store.dispatch(RivAction.ShowRcVersion(value))
                    else -> println("Error in FilterCheckBox - unknown action $action")
                }
            }
            +" $label"
        }
    }
}
