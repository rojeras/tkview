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
import pl.treksoft.kvision.tabulator.Align
import se.skoview.rivta.DomainArr
import se.skoview.rivta.getClickableDomainComponent
import se.skoview.rivta.mkHippoUrl

object DomainListPage : SimplePanel() {

    init {
        val valueList =
            DomainArr
                .filterNot { it.hidden }
                .sortedBy { it.name }
                .toList()
        h2 { +"Lista av tjänstedomäner" }
        div {
            tabulator(
                valueList,
                options = TabulatorOptions(
                    //layout = Layout.FITCOLUMNS,
                    pagination = PaginationMode.LOCAL,
                    paginationSize = 100,
                    height = "80.vh",
                    columns = listOf(
                        ColumnDefinition(
                            "Svenskt kortnamn",
                            "swedishShort",
                            headerFilter = Editor.INPUT,
                            //headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                            headerFilterPlaceholder = "Sök...",
                            widthGrow = 2,
                            formatter = Formatter.TEXTAREA
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
                            "Engelskt namn",
                            "name",
                            headerFilter = Editor.INPUT,
                            //headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                            headerFilterPlaceholder = "Sök...",
                            widthGrow = 1,
                            formatter = Formatter.TEXTAREA,
                            formatterComponentFunction = { _, _, item ->
                                getClickableDomainComponent(item.name)
                            }

                        ),
                        ColumnDefinition(
                            "Anslutningar",
                            "name",
                            align = Align.CENTER,
                            headerSort = false,
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
            )
        }
    }
}