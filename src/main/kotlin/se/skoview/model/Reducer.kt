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
package se.skoview.model

import se.skoview.rivta.getDefaultDomainVersion
import se.skoview.rivta.mkFilteredDomainVersionsList

fun rivReducer(state: RivState, action: RivAction): RivState {
    println("In Reducer, action: $action")
    val newState: RivState = when (action) {
        is RivAction.SetCurrentPage -> state.copy(
            displayPage = action.page
        )
        is RivAction.SelectDomain -> state.copy(
            selectedDomain = action.domain,
            selectedDomainVersion = updateDomainVersion(state, action.domain),
            displayPage = DisplayPage.DOMAIN
        )

        is RivAction.SelectDomainVersion -> state.copy(
            selectedDomainVersion = action.domainVersion
        )
        is RivAction.SelectDomainType -> state.copy(
            domainType = action.type
        )
        is RivAction.ShowHiddenDomain -> state.copy(
            showHiddenDomain = action.isVisible
        )
        is RivAction.ShowHiddenVersion -> {
            val newState = state.copy(showHiddenVersion = action.isVisible)
            newState.copy(selectedDomainVersion = updateDomainVersion(newState, newState.selectedDomain))
        }
        is RivAction.ShowRcVersion -> {
            val newState = state.copy(showRcVersion = action.isVisible)
            newState.copy(selectedDomainVersion = updateDomainVersion(newState, newState.selectedDomain))
        }
        is RivAction.ShowUnderscoreVersion -> {
            val newState = state.copy(showUnderscoreVersion = action.isVisible)
            newState.copy(selectedDomainVersion = updateDomainVersion(newState, newState.selectedDomain))
        }
        is RivAction.ShowTrunkVersion -> {
            val newState = state.copy(showTrunkVersion = action.isVisible)
            newState.copy(selectedDomainVersion = updateDomainVersion(newState, newState.selectedDomain))
        }
    }
    println("<<<===== ${action::class}")
    console.log(newState)

    return newState
}

private fun updateDomainVersion(state: RivState, domain: ServiceDomain?): Version? {
    if (domain == null) return null
    val versions = mkFilteredDomainVersionsList(state, domain)

    return if (state.selectedDomainVersion in versions) state.selectedDomainVersion
    else getDefaultDomainVersion(state, domain)
}
