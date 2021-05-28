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

import io.kvision.core.* // ktlint-disable no-wildcard-imports
import io.kvision.html.* // ktlint-disable no-wildcard-imports
import io.kvision.panel.simplePanel
import io.kvision.table.TableType
import io.kvision.tabulator.* // ktlint-disable no-wildcard-imports
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vw
import se.skoview.app.getHeightToRemainingViewPort
import se.skoview.model.DomainArr
import se.skoview.model.RivState
import se.skoview.model.ServiceDomain

var contractTextDiv = Div()

fun Container.contractListView(state: RivState) {
    println("In contractListView")
    div {
        background = Background(Color.name(Col.WHITE))
        marginLeft = 1.vw
        width = 98.vw

        simplePanel {
            println("After bind")

            val valueList = ContractListRecord.objectList
                .filter { it.domain.domainType.type == state.domainType }
                .filter { state.showHiddenDomain || !it.domain.hidden }
                .sortedBy { it.contractName }
            contractTextDiv =
                div {
                    h1 {
                        width = 100.perc
                        +"Tjänstekontrakt"
                    }
                    p { +"Här hittar du en förteckning över samtliga tjänstekontakt. Klicka på raderna i tabellen för mer information." }
                }
            simplePanel {
                setStyle("height", getHeightToRemainingViewPort(contractTextDiv, 40))

                tabulator(
                    valueList,
                    types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
                    options = TabulatorOptions(
                        layout = Layout.FITCOLUMNS,

                        paginationSize = 1000,
                        paginationButtonCount = 0,
                        columns = listOf(
                            ColumnDefinition(
                                "Tjänstekontrakt (${valueList.size})",
                                "contractName",
                                headerFilter = Editor.INPUT,
                                headerFilterPlaceholder = "Sök...",
                                width = "25%",
                                formatter = Formatter.TEXTAREA,
                                formatterComponentFunction = { _, _, item ->
                                    getClickableDomainComponent(item.domainName, item.contractName)
                                }
                            ),
                            ColumnDefinition(
                                "Beskrivning",
                                "description",
                                headerFilter = Editor.INPUT,
                                headerFilterPlaceholder = "Sök...",
                                width = "48%",
                                formatter = Formatter.TEXTAREA,
                                formatterComponentFunction = { _, _, item ->
                                    getClickableDomainComponent(
                                        item.domainName,
                                        item.description,
                                        Color.name(Col.BLACK)
                                    )
                                }
                            ),
                            ColumnDefinition(
                                "Tjänstedomän",
                                "domainName",
                                headerFilter = Editor.INPUT,
                                headerFilterPlaceholder = "Sök...",
                                width = "25%",
                                formatter = Formatter.TEXTAREA,
                                formatterComponentFunction = { _, _, item ->
                                    getClickableDomainComponent(
                                        item.domainName,
                                        item.domainName,
                                    )
                                }
                            )
                        ),
                    )
                ) {
                    height = 100.perc
                    wordBreak = WordBreak.NORMAL
                    whiteSpace = WhiteSpace.PREWRAP
                    fontSize = 16.px
                }
            }
        }
    }
}

data class ContractListRecord(
    val contractName: String,
    val description: String,
    val domain: ServiceDomain,
    val domainName: String
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
                                domain,
                                domain.name
                            )
                        )
                    }
                }
            }
        }
    }
}
