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

import pl.treksoft.kvision.html.h1
import pl.treksoft.kvision.html.span
import pl.treksoft.kvision.panel.SimplePanel

class DomainInfoPage(domainName: String) : SimplePanel() {
    init {
        println("In DomainInfoPage")
        val domain = DomainMap[domainName]

        if (domain == null) {
            span { +"Ingen domäninformation hittades" }
        } else {
            h1("${domain.swedishShort} - $domainName")
        }


    }
}