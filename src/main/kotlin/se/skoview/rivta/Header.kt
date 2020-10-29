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
import pl.treksoft.kvision.core.FlexWrap
import pl.treksoft.kvision.form.check.checkBoxInput
import pl.treksoft.kvision.form.select.simpleSelectInput
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.*
import pl.treksoft.kvision.utils.perc
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vh
import se.skoview.app.RivManager
import se.skoview.app.View
import se.skoview.app.formControlXs
import se.skoview.model.*

var rivTaPageTop: Div = Div()

fun Container.headerNav(state: RivState) {

    div {
        // background = Background(Color.hex(0x00706E))
        fontFamily = "Times New Roman"
        id = "top-init"
        rivTaPageTop = div {
            id = "top-bind"

            simplePanel {
                // background = Background(Color.hex(0x00706E))
                overflow = Overflow.HIDDEN

                hPanel {
                    fontSize = 20.px
                    background = Background(Color.hex(0x091F5F3))
                    // spacing = 5
                    clear = Clear.BOTH
                    margin = 0.px
                    id = "RivtaPage-ControlPanel:FlexPanel-Bind"

                    if (state.adminMode) vPanel {
                        marginLeft = 5.px
                        marginRight = 10.px
                        marginTop = 5.px
                        add(SelectPageButton(state, "Lista Tjänstedomäner", View.DOMAIN_LIST))
                        add(SelectPageButton(state, "Lista Tjänstekontrakt", View.CONTRACT_LIST))
                    }

                    vPanel {
                        marginLeft = 10.px
                        if (
                            state.view != View.DOMAIN
                        ) {
                            if (state.adminMode)
                                add(
                                    FilterCheckBox(
                                        "Inkludera dolda tjänstedomäner",
                                        RivAction.ShowHiddenDomain::class.simpleName!!,
                                        state.showHiddenDomain
                                    )
                                )
                            /*
                            hPanel {
                                span("Typ av tjänstedomän:")
                                add(
                                    SelectDomainType(state)
                                        .apply {
                                            marginLeft = 50.px
                                        }
                                )
                            }
                            */
                        }
                        if (
                            state.adminMode &&
                            state.view == View.DOMAIN
                        ) {
                            add(
                                FilterCheckBox(
                                    "Inkludera dolda versioner",
                                    RivAction.ShowHiddenVersion::class.simpleName!!,
                                    state.showHiddenVersion
                                )
                            )
                            // if (state.view == View.DOMAIN)
                            add(
                                FilterCheckBox(
                                    "Inkludera rc-versioner",
                                    RivAction.ShowRcVersion::class.simpleName!!,
                                    state.showRcVersion
                                )
                            )

                            // if (state.view == View.DOMAIN)
                            add(
                                FilterCheckBox(
                                    "Inkludera trunk",
                                    RivAction.ShowTrunkVersion::class.simpleName!!,
                                    state.showTrunkVersion
                                )
                            )
                        }
                    }

                    vPanel {
                        if (state.view == View.DOMAIN_LIST || state.view == View.CONTRACT_LIST) {
                        }
                    }
                }
            }
        }
    }
}

private fun Container.headerDiv(label: String, url: String, size: Int): Tag {
    return div().apply {
        // paddingTop = ((size / 2) - 10).px
        align = Align.CENTER
        background = Background(Color.name(Col.LIGHTGREEN))
        width = size.px
        // height = size.px
        link(
            label = label,
            url = url
        )
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
                RivManager.selectDomainType(selectedType)
            }
        }
    }
}

private class SelectPageButton(state: RivState, label: String, view: View) : SimplePanel() {
    init {
        marginBottom = 5.px
        button(label)
            .onClick {
                println("Button '$label' clicked")
                RivManager.fromAppShowView(view)
            }.apply {
                size = ButtonSize.SMALL
                addBsBgColor(BsBgColor.LIGHT)
                addBsColor(BsColor.BLACK50)
                marginBottom = 5.px
                disabled = state.view == view
            }
    }
}

private class FilterCheckBox(label: String, action: String, currentlySet: Boolean) : SimplePanel() {
    init {
        div {
            checkBoxInput(
                value = currentlySet,
            ).onClick {
                // todo: Figure out how to dispatch the action directly whithout "when"
                RivManager.setFlag(action, value)
            }
            +" $label"
        }
    }
}
