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
package se.skoview.app

import pl.treksoft.kvision.Application
import pl.treksoft.kvision.html.footer
import pl.treksoft.kvision.html.header
import pl.treksoft.kvision.html.main
import pl.treksoft.kvision.module
import pl.treksoft.kvision.pace.Pace
import pl.treksoft.kvision.pace.PaceOptions
import pl.treksoft.kvision.panel.ContainerType
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.require
import pl.treksoft.kvision.startApplication
import pl.treksoft.kvision.utils.px
import se.skoview.rivta.*
import tabs.rivta.domainListView

/**
 * To run standalone during development:
 * http://localhost:4000/standalone.html#/domains
 */

// todo: Ha samma fontstorlek i sökrutorna som för tabelltext
// todo: Läs ner integrations från tpdb och ha det som grund för vilka items som ska ha länk till hippo
// todo: Fixa till footer() så att den grå rutan ligger still längts ner. Gärna också på domänsidorna.

// done: Applicare Inera CSS: https://flamboyant-meninsky-54afd1.netlify.app/?path=/story/about--home
// done: Lägg upp i hemlig folder på rivta.se och verifiera att det fungerar med https
// done: Lös detta med CORS. Ev ta in REST-lösningen från Roberts showcase. Annat alternativ är att rivta.se dygnsvis hämtar filen och lagrar lokalt... Det är nog bäst.
// done: Skapa lite luft i vänstermarginalen
// done: hippolänkar till kontrakt
// done: Gör det möjligt att slå på/av kryssrutorna
// done: Back-knapp
// done: Paginering
// done: Red ut kotlins serialization och instansiering och defalutvärden

class App : Application() {
    init {
        require("css/tkview.css")
    }

    override fun start() {
        Pace.init(require("pace-progressbar/themes/green/pace-theme-bounce.css"))
        Pace.setOptions(PaceOptions(manual = true))
        RivManager.initialize()
        root(
            id = "tkview",
            containerType = ContainerType.NONE,
            addRow = false,
        ) {
            fontSize = 16.px
            header(RivManager.rivStore) { state ->
                // The old RivTaMainPage
                headerNav(state)
            }
            /**
             * main() below subscribe to state changes.
             * The application main dispatcher that sets the view in the application will end up here where * the correct page can be rendered.
             */
            main(RivManager.rivStore) { state ->
                println("State updated in main")
                if (state.domdbLoadingComplete) {
                    when (state.view) {
                        View.HOME -> {
                            // dummyView(state, "HOME")
                        }
                        View.DOMAIN_LIST -> {
                            domainListView(state)
                        }
                        View.CONTRACT_LIST -> {
                            contractListView(state)
                        }
                        View.DOMAIN -> {
                            domainView(state)
                        }
                        View.ADMIN -> {
                        }
                    }
                }
            }
            footer(RivManager.rivStore) {state ->
                footerInfo(state)
            }
        }
    }
}

fun main() {
    startApplication(::App, module.hot)
}
