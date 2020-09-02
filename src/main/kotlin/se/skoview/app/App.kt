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
import pl.treksoft.kvision.core.FlexDirection
import pl.treksoft.kvision.core.JustifyContent
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.panel.flexPanel
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.require
import pl.treksoft.kvision.startApplication
import pl.treksoft.kvision.utils.px
import tabs.rivta.DInfo
import tabs.rivta.DomainListPage
import tabs.rivta.load

class App : Application() {
    init {
        require("css/helloworld.css")
    }

    override fun start() {

        root("app") {
            println("In App:init()")
            load({ add(DomainListPage) })
        }
    }
}

fun main() {
    startApplication(::App)
}
