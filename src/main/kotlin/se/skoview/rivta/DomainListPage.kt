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
import pl.treksoft.kvision.form.select.*
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.state.bind
import pl.treksoft.kvision.tabulator.*
import pl.treksoft.kvision.tabulator.Align
import pl.treksoft.kvision.utils.px
import pl.treksoft.kvision.utils.vw
import se.skoview.app.formControlXs
import se.skoview.app.store
import se.skoview.model.*
import se.skoview.rivta.Texts
import se.skoview.rivta.getClickableDomainComponent
import se.skoview.rivta.mkHippoUrl

object DomainListPage : SimplePanel() {

    init {
        div { }.bind(store) { state ->

            val options =
                listOf(
                    Pair(DomainType.NATIONAL.toString(), "Nationella tjänstedomäner"),
                    Pair(DomainType.APPLICATION_SPECIFIC.toString(), "Applikationsspecifika tjänstedomäner"),
                    Pair(DomainType.EXTERNAL.toString(), "Externa tjänstedomäner")
                )

            console.log(options)
            //simpleSelectInput(
            simpleSelectInput(
                options = options,
                value = state.domainType.toString(),
                // label = "Filtrera på typ av tjänstedomän"
            ) {
                //selectWidthType = SelectWidthType.AUTO
                //fontStyle = FontStyle.OBLIQUE
                fontWeight = FontWeight.BOLD
                //fontSize = 40.px
                addCssStyle(formControlXs)
                //background = Background(Color.name(Col.AQUA))
            }.onEvent {
                change = {
                    val selected: String = self.value ?: ""
                    val selectedType: DomainType =
                        when (selected) {
                            DomainType.NATIONAL.toString() -> DomainType.NATIONAL
                            DomainType.APPLICATION_SPECIFIC.toString() -> DomainType.APPLICATION_SPECIFIC
                            DomainType.EXTERNAL.toString() -> DomainType.EXTERNAL
                            else -> DomainType.NATIONAL
                        }
                    store.dispatch(RivAction.SelectDomainType(selectedType))
                }
            val heading: String = "${Texts.domainTypeText[state.domainType]}a tjänstedomäner"
                /*
                when (state.domainType) {
                    DomainType.NATIONAL -> "Nationella tjänstedomäner"
                    DomainType.APPLICATION_SPECIFIC -> "Applikationsspecifika tjänstedomäner"
                    DomainType.EXTERNAL -> "Externa tjämstedomäner"
                }
                 */

            div {
                //h1(heading)
                p {
                    +"Här hittar du en förteckning över $heading. "
                    +"Informationen på denna sida är hämtad från subversion och tjänstekontraktsbeskrivningar. Klicka på länkarna i tabellen för mer information."
                }
                p { +"${Texts.domainTypeAltText[state.domainType]}" }
            }.apply {
                //width = 100.perc
                id = "pageHeaderDiv"
                //background = Background(Color.hex(0x113d3d))
                //align = pl.treksoft.kvision.html.Align.CENTER
                //color = Color.name(Col.WHITE)
                marginTop = 5.px
            }


            button("Lista tjänstekontrakt")
                .onClick {
                    store.dispatch(RivAction.SetCurrentPage(DisplayPage.CONTRACT_LIST))
                }.apply {
                    addBsBgColor(BsBgColor.LIGHT)
                    addBsColor(BsColor.BLACK50)
                    marginBottom = 5.px
                }

                //h2 { +heading }

                val valueList =
                    DomainArr
                        .filterNot { it.hidden }
                        .filter { it.getDomainType() == state.domainType }
                        .sortedBy { it.name }
                        .toList()
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
                                    //formatter = Formatter.TEXTAREA
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
                                    //formatter = Formatter.TEXTAREA,
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
    }
}