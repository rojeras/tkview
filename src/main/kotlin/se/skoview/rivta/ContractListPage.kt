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

package tabs.rivta

import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.tabulator.*
import se.skoview.rivta.DomainArr
import se.skoview.rivta.getClickableDomainComponent

data class ContractListRecord(
    val contractName: String,
    val description: String,
    val domain: String,
) {
    companion object {
        val objectList = mutableListOf<ContractListRecord>()

        fun initialize() {
            for (domain in DomainArr.filterNot { it.hidden }) {
                if (domain.interactions != null) {
                    for (interaction in domain.interactions.distinctBy { it.name }) {
                        var description = "tom"
                        if (interaction.interactionDescriptions.size > 0) {
                            description = interaction.interactionDescriptions[0].description
                        }
                        objectList.add(
                            ContractListRecord(
                                interaction.name.removeSuffix("Interaction"),
                                description,
                                domain.name
                            )
                        )
                    }
                }
            }

        }
    }
}

object ContractListPage : SimplePanel() {

    init {
        ContractListRecord.initialize()

        h2 { +"Lista av tjänstekontrakten" }
        tabulator(
            ContractListRecord.objectList.sortedBy { it.contractName },
            options = TabulatorOptions(
                pagination = PaginationMode.LOCAL,
                layout = Layout.FITCOLUMNS,
                paginationSize = 1000,
                columns = listOf(
                    ColumnDefinition(
                        "Tjänstekontrakt",
                        "contractName",
                        headerFilter = Editor.INPUT,
                        //headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                        headerFilterPlaceholder = "Sök...",
                        widthGrow = 1,
                        formatter = Formatter.TEXTAREA
                        /*
                    formatterComponentFunction = { _, _, item ->
                        println(item.description)
                        Div(item.description)
                    }
                     */
                    ),
                    ColumnDefinition(
                        "Beskrivning",
                        "description",
                        headerFilter = Editor.INPUT,
                        //headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                        headerFilterPlaceholder = "Sök...",
                        widthGrow = 3,
                        formatter = Formatter.TEXTAREA
                        /*
                    formatterComponentFunction = { _, _, item ->
                        println(item.description)
                        Div(item.description)
                    }
                     */
                    ),
                    ColumnDefinition(
                        "Engelskt domännamn",
                        "domain",
                        headerFilter = Editor.INPUT,
                        headerFilterPlaceholder = "Sök...",
                        widthGrow = 3,
                        formatter = Formatter.TEXTAREA,
                        formatterComponentFunction = { _, _, item ->
                            getClickableDomainComponent(item.domain)
                            /*
                            Div(
                                //item.domain
                            ).apply {
                                span { +item.domain }.onClick {
                                    println("in onClick")
                                    println("Will dispatch SelectDomain")
                                    store.dispatch(RivAction.SelectDomain(item.domain))
                                    println("Will dispatch SetCurrentPAge")
                                    store.dispatch(RivAction.SetCurrentPage(DisplayPage.DOMAIN))
                                }
                                color = Color.hex(0x008583)
                            }
                            */
                        }
                    )
                ),
                //selectable = true,
                /*
                rowSelected = { row ->
                    val item = row.getData() as ContractListRecord
                    store.dispatch(RivAction.SelectDomain(item.domain))
                    store.dispatch(RivAction.SetCurrentPage(DisplayPage.DOMAIN))
                }
                 */

            )
        ).apply {
            //height = 80.perc
        }
    }
}