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
package se.skoview.view

import io.kvision.core.* // ktlint-disable no-wildcard-imports
import io.kvision.html.* // ktlint-disable no-wildcard-imports
import io.kvision.panel.flexPanel
import io.kvision.utils.perc
import io.kvision.utils.px
import se.skoview.controller.getVersion
import se.skoview.model.RivState

/**
 * Footer info. Defines the text to show at the bottom of each page.
 *
 * @param state - current state
 */
fun Container.footerInfo(state: RivState) {
    flexPanel(direction = FlexDirection.ROW, justify = JustifyContent.SPACEEVENLY, alignItems = AlignItems.CENTER) {
        marginTop = 5.px
        width = 100.perc
        // align = Align.CENTER
        background = Background(Color.name(Col.LIGHTGRAY))
        div(rich = true) {
            width = 100.perc
            // background = Background(Color.name(Col.LIGHTCYAN))
            align = Align.CENTER
            if (!state.lastUpdateTime.isNullOrBlank()) {
                +"<b>Senast uppdaterad</b> ${state.lastUpdateTime}"
                // add(ResetCacheButton("Uppdatera nu"))
            }
        }.apply { align = Align.CENTER }
        div {
            // background = Background(Color.name(Col.LIGHTGREEN))
            align = Align.RIGHT
            +"TkView ${getVersion()}"
        }
    }
}
