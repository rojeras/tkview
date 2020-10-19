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
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.panel.simplePanel
import pl.treksoft.kvision.routing.routing
import pl.treksoft.kvision.state.bind
import pl.treksoft.kvision.table.TableType
import pl.treksoft.kvision.tabulator.*
import pl.treksoft.kvision.tabulator.Align
import pl.treksoft.kvision.utils.perc
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vw
import se.skoview.app.View
import se.skoview.app.getHeightToRemainingViewPort
import se.skoview.app.store
import se.skoview.model.*
import se.skoview.rivta.Texts
import se.skoview.rivta.getClickableDomainComponent
import se.skoview.rivta.mkHippoUrl

var domainTextDiv = Div()

object DomainListPage : SimplePanel() {

    init {
        background = Background(Color.name(Col.WHITE))
        marginLeft = 1.vw
        width = 98.vw

        // routing.navigate(View.DOMAIN_LIST.url)
        routing.navigate("DomainListPage")

        div {}.bind(store) { state ->


            val valueList =
                DomainArr
                    .filter { state.showHiddenDomain || !it.hidden }
                    .filter { it.getDomainType() == state.domainType }
                    .sortedBy { it.name }

            domainTextDiv =
                div {
                    background = Background(Color.name(Col.LIGHTGRAY))
                    // height = 100.perc
                    // overflow = Overflow.HIDDEN

                    val heading: String = "${Texts.domainTypeText[state.domainType]}a tjänstedomäner (${valueList.size})"

                    div {
                        h1 {
                            align = pl.treksoft.kvision.html.Align.CENTER
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
                        // background = Background(Color.name(Col.LIGHTPINK))
                        // background = Background(Color.hex(0x113d3d))
                        // align = pl.treksoft.kvision.html.Align.CENTER
                        // color = Color.name(Col.WHITE)
                        marginTop = 5.px
                    }
                    // }
                }

            println("Antal tjänstedomäner: ${valueList.size}")

            simplePanel {
                // background = Background(Color.name(Col.LIGHTCORAL))
                setStyle("height", getHeightToRemainingViewPort(domainTextDiv, 185))

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
                                "Svenskt kortnamn",
                                "swedishShort",
                                headerFilter = Editor.INPUT,
                                // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                                headerFilterPlaceholder = "Sök...",
                                // widthGrow = 2,
                                width = "20%",
                                // formatter = Formatter.TEXTAREA
                                /*
                    formatterComponentFunction = { _, _, item ->
                        println(item.description)
                        Div(item.description)
                    }
                     */
                            ),

                            ColumnDefinition(
                                "Svenskt domännamn",
                                "swedishLong",
                                headerFilter = Editor.INPUT,
                                // headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                                headerFilterPlaceholder = "Sök...",
                                // widthGrow = 3,
                                width = "40%",
                                // formatter = Formatter.TEXTAREA,
                            ),

                            ColumnDefinition(
                                "Engelskt namn",
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
                                "Anslutningar",
                                "name",
                                hozAlign = Align.CENTER,
                                headerSort = false,
                                width = "8%",
                                formatterComponentFunction =
                                    { _, _, item ->
                                        val url = mkHippoUrl(item.name)
                                        val linkText =
                                            if (url.isNotBlank()) "<a href=\"$url\" target=\"_blank\"><img alt=\"Utforska i hippo\" src=\"tpnet.png\" width=\"20\" height=\"20\"></a>"
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
}
