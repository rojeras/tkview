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

import io.kvision.*
import io.kvision.core.Overflow
import io.kvision.html.footer
import io.kvision.html.header
import io.kvision.html.main
import io.kvision.pace.Pace
import io.kvision.pace.PaceOptions
import io.kvision.panel.root
import io.kvision.state.bind
import io.kvision.utils.px
import se.skoview.view.* // ktlint-disable no-wildcard-imports

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
        // root(id = "tkview", containerType = ContainerType.NONE, addRow = false,) {
        root(id = "tkview") {
            fontSize = 16.px
            overflow = Overflow.HIDDEN
            header().bind(RivManager.rivStore) { state ->
                // The old RivTaMainPage
                headerNav(state)
            }
            /*
             * main() below subscribe to state changes.
             * The application main dispatcher that sets the view in the application will end up here where * the correct page can be rendered.
             */
            main().bind(RivManager.rivStore) { state ->
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
            footer().bind(RivManager.rivStore) { state ->
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
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        FontAwesomeModule,
        BootstrapSelectModule,
        BootstrapDatetimeModule,
        BootstrapSpinnerModule,
        BootstrapTypeaheadModule,
        BootstrapUploadModule,
        RichTextModule,
        TabulatorModule,
        CoreModule,
        panelsCompatibilityMode = true
    )
}
