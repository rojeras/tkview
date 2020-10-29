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
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.simplePanel
import pl.treksoft.kvision.tabulator.*
import pl.treksoft.kvision.utils.perc
import pl.treksoft.kvision.utils.vw
import se.skoview.app.getHeightToRemainingViewPort
import se.skoview.model.DomainArr
import se.skoview.model.RivState
import se.skoview.model.ServiceDomain
import se.skoview.model.getDomainType

var contractTextDiv = Div()

fun Container.contractListView(state: RivState) {
    div {
        background = Background(Color.name(Col.WHITE))
        marginLeft = 1.vw
        width = 98.vw

        simplePanel {
            // background = Background(Color.name(Col.LIGHTCORAL))
            // setStyle("height", getHeightToRemainingViewPort(domainTextDiv, 40))

            println("After bind")

            val valueList = ContractListRecord.objectList
                .filter { it.domain.getDomainType() == state.domainType }
                .filter { state.showHiddenDomain || !it.domain.hidden }
                .sortedBy { it.contractName }
            contractTextDiv =
                div {
                    // background = Background(Color.name(Col.LIGHTGRAY))
                    h1 {
                        width = 100.perc
                        +"Tjänstekontrakt"
                    }
                    p { +"Här hittar du en förteckning över samtliga tjänstekontakt. I tabellen kan du också se om tjänstekontrakten är installerade i den nationella Tjänsteplattformen eller inte." }
                    p { +"Informationen på denna sida är direkt hämtad från WSDL-filer i subversion samt tjänsteadresseringskatalogerna i den nationella Tjänsteplattformen. Klicka på länkarna i tabellen för mer information." }
                }
            simplePanel {
                setStyle("height", getHeightToRemainingViewPort(contractTextDiv, 100))
                println("Contract data:")
                // console.log(state)
                console.log(valueList)

                tabulator(
                    // ContractListRecord.objectList.sortedBy { it.contractName },
                    valueList,
                    options = TabulatorOptions(
                        // pagination = PaginationMode.LOCAL,
                        layout = Layout.FITCOLUMNS,
                        paginationSize = 1000,
                        paginationButtonCount = 0,
                        columns = listOf(
                            ColumnDefinition(
                                "Tjänstekontrakt (${valueList.size})",
                                "contractName",
                                headerFilter = Editor.INPUT,
                                // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                                headerFilterPlaceholder = "Sök...",
                                width = "25%",
                                // widthGrow = 1,
                                formatter = Formatter.TEXTAREA
                            ),
                            ColumnDefinition(
                                "Beskrivning",
                                "description",
                                headerFilter = Editor.INPUT,
                                // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                                headerFilterPlaceholder = "Sök...",
                                width = "48%",
                                // widthGrow = 3,
                                formatter = Formatter.TEXTAREA
                            ),
                            ColumnDefinition(
                                "Tjänstedomän",
                                "domain",
                                headerFilter = Editor.INPUT,
                                headerFilterPlaceholder = "Sök...",
                                width = "25%",
                                // widthGrow = 3,
                                formatter = Formatter.TEXTAREA,
                                formatterComponentFunction = { _, _, item ->
                                    getClickableDomainComponent(item.domain.name)
                                }
                            )
                        ),
                    )
                ) {
                    // background = Background(Color.name(Col.BEIGE))
                    height = 100.perc
                    wordBreak = WordBreak.NORMAL
                    whiteSpace = WhiteSpace.PREWRAP
                }
            }
        }
    }
}

data class ContractListRecord(
    val contractName: String,
    val description: String,
    val domain: ServiceDomain
) {
    companion object {
        val objectList = mutableListOf<ContractListRecord>()

        fun initialize() {
            for (domain in DomainArr) {
                if (domain.interactions != null) {
                    for (interaction in domain.interactions.distinctBy { it.name }) {
                        var description = "tom"
                        if (interaction.interactionDescriptions.isNotEmpty()) {
                            description = interaction.interactionDescriptions[0].description
                        }
                        objectList.add(
                            ContractListRecord(
                                interaction.name.removeSuffix("Interaction"),
                                description,
                                domain
                            )
                        )
                    }
                }
            }
        }
    }
}
