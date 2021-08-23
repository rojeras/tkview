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

import se.skoview.controller.View

/**
 * The redux state class. The default values are set initially at application startup.
 *
 * @param view The view (page) to display.
 * @param selectedDomainName Name (namespace) of the currently selected domain, or null if none is selected.
 * @param selectedDomainVersion Currently selected domain version, or null if none is selected.
 * @param showHiddenDomain Flag to control what is displayed in (secret) admin mode
 * @param showHiddenVersion Flag to control what is displayed in (secret) admin mode
 * @param showRcVersion Flag to control what is displayed in (secret) admin mode
 * @param showUnderscoreVersion Flag to control what is displayed in (secret) admin mode
 * @param showTrunkVersion Flag to control what is displayed in (secret) admin mode
 * @param showHiddenDomain Flag to control what is displayed in (secret) admin mode
 * @param domdbLoadingComplete Status flag.
 * @param adminMode Turns on (secret) admin mode.
 * @param lastUpdateTime Timestamp when the cache was last updated (= age of the data currently displayed)
 */
data class RivState(
    val view: View = View.DOMAIN_LIST,
    val selectedDomainName: String? = null,
    val selectedDomainVersion: Version? = null,
    val showHiddenDomain: Boolean = false,
    val showHiddenVersion: Boolean = false,
    val showRcVersion: Boolean = false,
    val showUnderscoreVersion: Boolean = false,
    val showTrunkVersion: Boolean = false,
    val domdbLoadingComplete: Boolean = false,
    val adminMode: Boolean = false,
    val lastUpdateTime: String? = null
)

/**
 * Extension function. Get the current domain version if it is within the domain and current selection, otherwise the one with highest prio. That is the first visible one from [mkFilteredDomainVersionsList] list.
 *
 * @param domain The domain whose version we are looking for.
 *
 * @return The currently selected, or highest priority, version of the domain.
 */
fun RivState.defaultDomainVersion(domain: ServiceDomain?): Version? {

    if (domain == null) return null

    val versions = mkFilteredDomainVersionsList(domain)
    if (versions.isEmpty()) return null

    if (this.selectedDomainVersion in versions) return this.selectedDomainVersion

    for (version in versions) {
        if (
            !version.name.contains("trunk") &&
            !version.name.contains("_")
        ) return version
    }

    for (version in versions) {
        if (!version.name.contains("trunk")) return version
    }

    return versions[0]
}

/**
 * Extension function. Based on the state, make a reverse sorted list of currently visible domain versions.
 *
 * @param domain The domain whose versions should be found.
 *
 * @return A list, sorted in reverse order, of visible versions of the domain.
 */
fun RivState.mkFilteredDomainVersionsList(domain: ServiceDomain): List<Version> {
    return if (domain.versions == null)
        listOf()
    else
        domain.versions
            .filter { this.showHiddenVersion || !it.hidden }
            .filter { this.showRcVersion || !it.name.contains("RC") }
            .filter { this.showTrunkVersion || !it.name.contains("trunk") }
            .sortedBy { it.name }
            .reversed()
}
