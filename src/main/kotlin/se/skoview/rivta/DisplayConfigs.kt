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

import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.core.onClick
import pl.treksoft.kvision.html.Div
import pl.treksoft.kvision.html.span
import se.skoview.app.store
import se.skoview.model.DisplayPage
import se.skoview.model.RivAction

fun getClickableDomainComponent(domainName: String): Div  {
    return Div(
        //item.domain
    ).apply {
        span { +domainName }.onClick {
            println("in onClick")
            println("Will dispatch SelectDomain")
            store.dispatch(RivAction.SelectDomain(domainName))
            println("Will dispatch SetCurrentPAge")
            store.dispatch(RivAction.SetCurrentPage(DisplayPage.DOMAIN))
        }
        color = Color.hex(0x008583)
    }
}
