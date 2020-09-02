package tabs.rivta

import pl.treksoft.kvision.core.Background
import pl.treksoft.kvision.core.Col
import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.core.Color.Companion.name
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.state.observableListOf
import pl.treksoft.kvision.tabulator.*
import pl.treksoft.kvision.types.toDateF
import kotlin.js.Date

object DomainListPage : SimplePanel() {
    init {
        println("In DomainListPage")
        h1 { +"Tjänstedomäner" }
        div {
            p {
                +"Här hittar du en förteckning över samtliga tjänstedomäner . I tabellen kan du också se om de är installerade i den nationella Tjänsteplattformen eller inte."}
                p { +"Informationen på denna sida är hämtad från subversion, tjänstekontraktsbeskrivningar samt tjänsteadresseringskatalogerna i den nationella tjänsteplattformen. Klicka på länkarna i tabellen för mer information." }
            }
            val valueList = DomainIndex.sortedBy { it.name }.toList()
            println("valuelist:")
            console.log(valueList)

            tabulator(
                valueList,
                options = TabulatorOptions(
                    //layout = Layout.FITCOLUMNS,
                    pagination = PaginationMode.LOCAL,
                    paginationSize = 100,
                    columns = listOf(
                        ColumnDefinition(
                            "Svenskt kortnamn",
                            "swedishShort",
                            headerFilter = Editor.INPUT,
                            //headerFilterPlaceholder = "Sök ${heading.toLowerCase()}",
                            headerFilterPlaceholder = "Sök...",
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
                            /*
                            formatterComponentFunction = { _, _, item ->
                                println(item.description)
                                Div(item.description)
                            }
                             */
                        )
                    )
                )
            )
        }
    }