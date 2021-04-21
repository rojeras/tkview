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

import se.skoview.app.View
import se.skoview.rivta.getDefaultDomainVersion
import se.skoview.rivta.mkFilteredDomainVersionsList

fun rivReducer(state: RivState, action: RivAction): RivState {
    println("=====>>> In Reducer, action:")
    console.log(action)

    val newState: RivState = when (action) {
        is RivAction.HomePage -> state.copy(
            view = View.HOME
        )
        is RivAction.SetView -> {
            val newPage = action.view
            state.copy(
                view = newPage
            )
        }
        is RivAction.SelectAndShowDomain -> state.copy(
            selectedDomainName = action.domainName,
            selectedDomainVersion = updateDomainVersion(state, DomainMap[action.domainName]),
            view = View.DOMAIN
        )
        is RivAction.SelectAndShowContract -> state.copy(
            selectedContractName = action.contractName,
            // selectedDomainVersion = updateDomainVersion(state, DomainMap[action.domainName]),
            view = View.CONTRACT
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
            newState.copy(
                selectedDomainVersion = updateDomainVersion(state, DomainMap[state.selectedDomainName])
            )
        }
        is RivAction.ShowRcVersion -> {
            val newState = state.copy(showRcVersion = action.isVisible)
            newState.copy(
                selectedDomainVersion = updateDomainVersion(state, DomainMap[state.selectedDomainName])
            )
        }
        is RivAction.ShowUnderscoreVersion -> {
            val newState = state.copy(showUnderscoreVersion = action.isVisible)
            newState.copy(
                selectedDomainVersion = updateDomainVersion(state, DomainMap[state.selectedDomainName])
            )
        }
        is RivAction.ShowTrunkVersion -> {
            val newState = state.copy(showTrunkVersion = action.isVisible)
            newState.copy(
                selectedDomainVersion = updateDomainVersion(state, DomainMap[state.selectedDomainName])
            )
        }
        is RivAction.DomdbLoadingComplete -> state.copy(
            domdbLoadingComplete = action.isComplete,
            lastUpdateTime = DomDb.lastUpdateTime
        )
        is RivAction.SetAdminMode -> state.copy(
            adminMode = action.onOff == "on"
        )
        is RivAction.Refresh -> state
    }
    println("<<<===== ${action::class}")
    console.log(newState)

    return newState
}

fun updateDomainVersion(state: RivState, domain: ServiceDomain?): Version? {
    if (domain == null) return null
    val versions = mkFilteredDomainVersionsList(state, domain)

    return if (state.selectedDomainVersion in versions) state.selectedDomainVersion
    else getDefaultDomainVersion(state, domain)
}
