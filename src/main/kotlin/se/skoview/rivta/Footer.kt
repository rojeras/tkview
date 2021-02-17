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
package se.skoview.rivta

import pl.treksoft.kvision.core.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.html.* // ktlint-disable no-wildcard-imports
import pl.treksoft.kvision.panel.flexPanel
import pl.treksoft.kvision.panel.hPanel
import pl.treksoft.kvision.panel.simplePanel
import pl.treksoft.kvision.utils.perc
import pl.treksoft.kvision.utils.px
import se.skoview.app.getVersion
import se.skoview.model.RivState

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
