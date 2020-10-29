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

import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.simplePanel
import pl.treksoft.kvision.table.TableType
import pl.treksoft.kvision.tabulator.*
import pl.treksoft.kvision.tabulator.Align
import pl.treksoft.kvision.utils.perc
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vw
import se.skoview.app.getHeightToRemainingViewPort
import se.skoview.model.*
import se.skoview.model.mkHippoDomainUrl
import se.skoview.rivta.Texts
import se.skoview.rivta.getClickableDomainComponent

var domainTextDiv = Div()

fun Container.domainListView(state: RivState) {
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
                // background = Background(Color.name(Col.LIGHTGRAY))
                // height = 100.perc
                // overflow = Overflow.HIDDEN

                // val heading: String = "${Texts.domainTypeText[state.domainType]}a tjänstedomäner (${valueList.size})"
                val heading = "Tjänstedomäner"

                div {
                    h1 {
                        +heading
                    }
                    p {
                        +"Här hittar du en förteckning över $heading. "
                        +"Informationen på denna sida är hämtad från subversion och tjänstekontraktsbeskrivningar. Klicka på länkarna i tabellen för mer information."
                    }
                    p { +"${Texts.domainTypeAltText[state.domainType]}" }
                }.apply {
                    // width = 100.perc
                    id = "pageHeaderDiv"
                    marginTop = 5.px
                }
                // }
            }

        println("Antal tjänstedomäner: ${valueList.size}")

        simplePanel {
            // background = Background(Color.name(Col.LIGHTCORAL))
            setStyle("height", getHeightToRemainingViewPort(domainTextDiv, 130))

            tabulator(
                valueList,
                types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
                options = TabulatorOptions(
                    layout = Layout.FITCOLUMNS,
                    // pagination = PaginationMode.LOCAL,
                    paginationSize = 1000,
                    paginationButtonCount = 0,
                    // height = "80.vh",
                    columns = listOf(

                        ColumnDefinition(
                            "Tjänstedomän (${valueList.size})",
                            "name",
                            headerFilter = Editor.INPUT,
                            // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                            headerFilterPlaceholder = "Sök...",
                            // widthGrow = 1,
                            width = "30%",
                            formatter = Formatter.TEXTAREA,
                            formatterComponentFunction = { _, _, item ->
                                getClickableDomainComponent(item.name)
                            }
                        ),

                        ColumnDefinition(
                            "Svenskt kortnamn",
                            "swedishShort",
                            headerFilter = Editor.INPUT,
                            // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                            headerFilterPlaceholder = "Sök...",
                            // widthGrow = 2,
                            width = "18%",
                        ),

                        ColumnDefinition(
                            "Svenskt domännamn",
                            "swedishLong",
                            headerFilter = Editor.INPUT,
                            // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                            headerFilterPlaceholder = "Sök...",
                            // widthGrow = 3,
                            width = "30%",
                            // formatter = Formatter.TEXTAREA,
                        ),

                        ColumnDefinition(
                            "Domäntyp",
                            "name",
                            headerFilter = Editor.INPUT,
                            // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                            headerFilterPlaceholder = "Sök...",
                            // widthGrow = 1,
                            width = "10%",
                            formatter = Formatter.TEXTAREA,
                            formatterComponentFunction = { _, _, item ->
                                println("Domäntyp = ${item.getDomainType().displayName}")
                                Span(item.getDomainType().displayName)
                            }
                        ),

                        ColumnDefinition(
                            "Anslutningar",
                            "name",
                            hozAlign = Align.CENTER,
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
                                )
                            }
                        )
                    )
                )
            ) {
                height = 100.perc
                wordBreak = WordBreak.BREAKALL
                whiteSpace = WhiteSpace.PREWRAP
            }
        }
    }
}
