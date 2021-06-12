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
package se.skoview.controller

import io.kvision.Application
import io.kvision.core.Overflow
import io.kvision.html.footer
import io.kvision.html.header
import io.kvision.html.main
import io.kvision.module
import io.kvision.pace.Pace
import io.kvision.pace.PaceOptions
import io.kvision.panel.ContainerType
import io.kvision.panel.root
import io.kvision.require
import io.kvision.startApplication
import io.kvision.utils.px
import se.skoview.view.* // ktlint-disable no-wildcard-imports
import tabs.rivta.domainListView

/**
 * To run standalone during development:
 * http://localhost:4000/standalone.html#/domains
 */

// todo: Format the footer line
// todo: Evaluate if domains.html can be removed
// todo: Ha samma fontstorlek i sökrutorna som för tabelltext
// todo: Red ut varför vissa domäner är ofullständiga och inte kan läsas in (se tkview i zim)
// todo: Red ut om det finns, eller går att få fram, vettiga datum att visa
// todo: Change domainversion view to use Tab panels (refer to Showcase/Containers)

// done: Update to Kvision 4
// done: Display version info i grey bottom bar
// done: Change hippo link to 7.1 format
// done: GetCareContacts v3 does not have a hippo-link. Was due to namespace error in TPDB for this contract. Patched in tkview.
// done: Utred. Kolla clinicalprocess.activitiyprescrption.actoutcome 2.1.1. Enligt tkview så ingår MH 2.1. Enligt källkoden är det GMH 2.0?! : hippo (TPs) does not know about minor versions
// done: Ensure sane font sizes everywhere, also domain page and version selector
// done: It must be obvious that the domain version selector is a dropdown
// done: Make contract table striped
// done: Fixa till footer() så att den grå rutan ligger still längts ner. Gärna också på domänsidorna.
// done: Applicare Inera CSS: https://flamboyant-meninsky-54afd1.netlify.app/?path=/story/about--home
// done: Lägg upp i hemlig folder på rivta.se och verifiera att det fungerar med https
// done: Lös detta med CORS. Ev ta in REST-lösningen från Roberts showcase. Annat alternativ är att rivta.se dygnsvis hämtar filen och lagrar lokalt... Det är nog bäst.
// done: Skapa lite luft i vänstermarginalen
// done: hippolänkar till kontrakt
// done: Gör det möjligt att slå på/av kryssrutorna
// done: Back-knapp
// done: Paginering
// done: Red ut kotlins serialization och instansiering och defalutvärden

/**
 * Application class. Responsible for invocation and defines the main loop.
 */
class App : Application() {
    init {
        require("css/tkview.css")
    }

    /**
     * Application startup.
     */
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
            overflow = Overflow.HIDDEN
            header(RivManager.rivStore) { state ->
                // The old RivTaMainPage
                headerNav(state)
            }
            /*
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
            footer(RivManager.rivStore) { state ->
                footerInfo(state)
            }
        }
    }
}

/**
 * Kotlin application entry point. Will invoke [App.start].
 *
 */
fun main() {
    startApplication(::App, module.hot)
}
