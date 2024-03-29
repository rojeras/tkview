/**
 * Copyright (C) 2013-2020 Lars Erik Röjerås
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

import io.kvision.navigo.Navigo

/**
 * Definition of the possible views and their URL representations.
 */
enum class View(val url: String) {
    HOME("/"),
    DOMAIN_LIST("/domains"),
    CONTRACT_LIST("/contracts"),
    DOMAIN("/domain"),
    ADMIN("/admin"),
}

/**
 * Creates the navigation Navigo object.
 *
 * @return Navigo object to use in this application.
 */
fun Navigo.initialize(): Navigo {
    return on(
        View.HOME.url,
        { _ ->
            RivManager.fromUrlShowView(View.HOME)
        }
    ).on(
        View.DOMAIN_LIST.url,
        { _ ->
            RivManager.fromUrlShowView(View.DOMAIN_LIST)
        }
    ).on(
        View.CONTRACT_LIST.url,
        { _ ->
            RivManager.fromUrlShowView(View.CONTRACT_LIST)
        }
    ).on(
        "${View.DOMAIN.url}/:slug",
        { params ->
            RivManager.fromUrlShowDomainView(stringParameter(params, "slug"))
        }
    ).on(
        "${View.ADMIN.url}/:slug",
        { params ->
            RivManager.fromUrlAdmin(stringParameter(params, "slug"))
        }
    )
}

/**
 * Extract query string from URL
 */
fun stringParameter(params: dynamic, parameterName: String): String {
    return (params[parameterName]).toString()
}
