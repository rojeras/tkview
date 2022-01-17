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

package se.skoview.view

import io.kvision.core.* // ktlint-disable no-wildcard-imports
import io.kvision.html.* // ktlint-disable no-wildcard-imports
import io.kvision.panel.simplePanel
// import io.kvision.tabulator.* // ktlint-disable no-wildcard-imports
import io.kvision.tabulator.ColumnDefinition
import io.kvision.tabulator.Editor
import io.kvision.tabulator.Formatter
import io.kvision.tabulator.Layout
import io.kvision.tabulator.TableType
import io.kvision.tabulator.TabulatorOptions
import io.kvision.tabulator.tabulator
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vw
import se.skoview.controller.getHeightToRemainingViewPort
import se.skoview.model.* // ktlint-disable no-wildcard-imports

/**
 * This div is used as a base to calculate page height.
 */
var contractTextDiv = Div()

/**
 * Contract list view. The container contains the list of contracts.
 *
 * @param state The current state object (redux state).
 */
fun Container.contractListView(state: RivState) {
    println("In contractListView")

    ContractListRecord.initialize(state)

    div {
        background = Background(Color.name(Col.WHITE))
        marginLeft = 1.vw
        width = 98.vw

        /**
         * Defines the main panel for this view with the table of service contracts.
         */
        simplePanel {
            println("After bind")

            contractTextDiv =
                div {
                    h1 {
                        width = 100.perc
                        +"Tjänstekontrakt"
                    }
                    p { +"Här hittar du en förteckning över samtliga tjänstekontakt. Klicka på raderna i tabellen för mer information." }
                }
            /**
             * Contains all of the page except the main heading and description.
             */
            simplePanel {
                setStyle("height", getHeightToRemainingViewPort(contractTextDiv, 40))

                /**
                 * Value list. Defines the information to include in the contract list table. Contracts from hidden domains are filtered out, and sorting is based on contract name.
                 */
                val valueList = ContractListRecord.objectList
                    // .filter { it.domain.domainType.type == state.domainType } // Unclear why this filtering exists.
                    .filter { state.showHiddenDomain || !it.domain.hidden }
                    .sortedBy { it.contractName }

                /**
                 * The actual table, created with (http://tabulator.info/) .
                 */
                tabulator(
                    valueList,
                    types = setOf(
                        TableType.BORDERED,
                        TableType.STRIPED,
                        TableType.HOVER
                    ),
                    // types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
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

/**
 * Contract list record. Defines each row in the contract table. The rows are stored in the companion (singleton) [objectList]. It is populated by the companion [initialize] function.
 *
 * @property contractName
 * @property description
 * @property domain
 * @property domainName
 * @constructor Create empty Contract list record
 */
data class ContractListRecord(
    val contractName: String,
    val description: String,
    val domain: ServiceDomain,
    val domainName: String
) {
    // The equals check does not include the description
    // We do not want to get duplicates due to different description fields
    override fun equals(other: Any?): Boolean {
        return (
            (other is ContractListRecord) &&
                this.contractName == other.contractName &&
                this.domain == other.domain
            )
    }

    companion object {
        val objectList = mutableListOf<ContractListRecord>()

        fun initialize(state: RivState) {
            for (domain in DomainArr) {
                val versions =
                    state.mkFilteredDomainVersionsList(domain) // Returns visible versions in reverse version order (highest first)
                for (version in versions) {
                    if (!version.hidden) {
                        version.interactionDescriptions
                            .distinctBy { it.wsdlContract().first + it.wsdlContract().second + it.wsdlContract().third } // Remove duplicates, see Issue #5
                            .sortedBy { it.wsdlContract().first }
                            .map {
                                val description = it.description
                                val contractName = it.wsdlContract().first
                                val contractListRecord = ContractListRecord(
                                    contractName = contractName,
                                    description = description,
                                    domain = domain,
                                    domainName = domain.name
                                )
                                if (!objectList.contains(contractListRecord)) objectList.add(contractListRecord)
                            }
                    }
                }
            }
        }
    }
}
