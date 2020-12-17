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

import pl.treksoft.kvision.core.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.html.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.panel.simplePanel
import pl.treksoft.kvision.table.TableType
import pl.treksoft.kvision.tabulator.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.tabulator.Align
import pl.treksoft.kvision.utils.perc
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vw
import se.skoview.app.getHeightToRemainingViewPort
import se.skoview.model.DomainArr
import se.skoview.model.RivState
import se.skoview.model.mkHippoDomainUrl
import se.skoview.rivta.Texts
import se.skoview.rivta.getClickableDomainComponent

var domainTextDiv = Div()

fun Container.domainListView(state: RivState) {
    println("In domainListView")
    div {
        background = Background(Color.name(Col.WHITE))
        marginLeft = 1.vw
        width = 98.vw

        val valueList =
            DomainArr
                .filter { state.showHiddenDomain || !it.hidden }
                // .filter { it.getDomainType() == state.domainType }
                .sortedBy { it.name }

        domainTextDiv =
            div {
                div {
                    h1 {
                        +"Tjänstedomäner"
                    }
                    p {
                        +"Här hittar du en förteckning över tjänstedomäner. "
                        +"Klicka på raderna i tabellen för mer information."
                    }
                    // p { +"${Texts.domainTypeAltText[state.domainType]}" }
                }.apply {
                    id = "pageHeaderDiv"
                    marginTop = 5.px
                }
            }

        println("Antal tjänstedomäner: ${valueList.size}")
        console.log(valueList)

        simplePanel {
            setStyle("height", getHeightToRemainingViewPort(domainTextDiv, 45))

            tabulator(
                valueList,
                types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
                options = TabulatorOptions(
                    layout = Layout.FITCOLUMNS,
                    paginationSize = 1000,
                    paginationButtonCount = 0,
                    columns = listOf(

                        ColumnDefinition(
                            title = "Tjänstedomän (${valueList.size})",
                            field = "name",
                            headerFilter = Editor.INPUT,
                            headerFilterPlaceholder = "Sök...",
                            width = "30%",
                            formatter = Formatter.TEXTAREA,
                            formatterComponentFunction = { _, _, item ->
                                getClickableDomainComponent(item.name)
                            }
                        ),

                        ColumnDefinition(
                            title = "Svenskt kortnamn",
                            field = "swedishShort",
                            headerFilter = Editor.INPUT,
                            headerFilterPlaceholder = "Sök...",
                            width = "18%",
                            formatterComponentFunction = { _, _, item ->
                                getClickableDomainComponent(
                                    item.name,
                                    item.swedishShort,
                                    Color.name(Col.BLACK)
                                )
                            }
                        ),

                        ColumnDefinition(
                            title = "Svenskt domännamn",
                            field = "swedishLong",
                            headerFilter = Editor.INPUT,
                            headerFilterPlaceholder = "Sök...",
                            width = "30%",
                            formatterComponentFunction = { _, _, item ->
                                getClickableDomainComponent(
                                    item.name,
                                    item.swedishLong,
                                    Color.name(Col.BLACK)
                                )
                            }
                        ),

                        ColumnDefinition(
                            title = "Domäntyp",
                            field = "domainTypeString",
                            headerFilter = Editor.INPUT,
                            headerFilterPlaceholder = "Sök....",
                            width = "10%",
                            // formatter = Formatter.TEXTAREA,
                            formatterComponentFunction = { _, _, domain ->
                                Div {
                                    content = Texts.domainTypeText[domain.domainType.type]
                                    title = Texts.domainTypeAltText[domain.domainType.type]
                                }
                            }
                        ),

                        ColumnDefinition(
                            title = "Anslutningar",
                            field = null,
                            align = Align.CENTER,
                            headerSort = false,
                            width = "10%",
                            formatterComponentFunction = { _, _, item ->
                                val url = mkHippoDomainUrl(item.name)
                                val linkText =
                                    if (url.isNotBlank()) "<a href=\"$url\" target=\"_blank\"><img alt=\"Utforska i hippo\" src=\"/tkview/tpnet.png\" width=\"20\" height=\"20\"></a>"
                                    else ""
                                Div(
                                    rich = true,
                                    content = linkText
                                ) {
                                    title = "Se anslutningar för denna domän i hippo"
                                }
                            }
                        )
                    )
                )
            ) {
                height = 100.perc
                wordBreak = WordBreak.BREAKALL
                whiteSpace = WhiteSpace.PREWRAP
                fontSize = 16.px
            }
            println("Leaving tabulator")
        }
    }
}
