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
package se.skoview.model

import se.skoview.app.View

data class RivState(
    val view: View = View.DOMAIN_LIST,
    val domainType: DomainTypeEnum = DomainTypeEnum.NATIONAL,
    val selectedDomainName: String? = null,
    val selectedDomainVersion: Version? = null,
    val showHiddenDomain: Boolean = false,
    val showHiddenVersion: Boolean = false,
    val showRcVersion: Boolean = false,
    val showUnderscoreVersion: Boolean = false,
    val showTrunkVersion: Boolean = false,
    val domdbLoadingComplete: Boolean = false,
    val adminMode: Boolean = false
)

