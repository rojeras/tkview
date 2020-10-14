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
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.redux.createReduxStore
import pl.treksoft.kvision.require
import pl.treksoft.kvision.startApplication
import se.skoview.model.initializeRivState
import se.skoview.model.load
import se.skoview.model.rivReducer
import se.skoview.rivta.RivTaMainPage

// todo: Läs ner TPDB domainId dynamiskt: https://integrationer.tjansteplattform.se/tpdb/tpdbapi.php/api/v1/domains
// todo: hippolänkar till kontrakt
// todo: Back-knapp
// todo: Lös detta med CORS. Ev ta in REST-lösningen från Roberts showcase. Annat alternativ är att rivta.se dygnsvis hämtar filen och lagrar lokalt... Det är nog bäst.
// done: Paginering
// done: Red ut kotlins serialization och instansiering och defalutvärden

// Initialize the redux store
val store = createReduxStore(
    ::rivReducer,
    initializeRivState()
)

class App : Application() {
    init {
        require("css/helloworld.css")
    }

    override fun start() {

        root("app") {
            println("In App:init()")
            load({ add(RivTaMainPage) })
        }
    }
}

fun main() {
    startApplication(::App)
}
