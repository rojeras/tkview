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

import se.skoview.controller.View

/**
 * The redux reducer. It updates the state based on the dispatch of an action.
 *
 * The [RivState.copy] function is used to create an updated state object.
 *
 * @param state The current state object.
 * @param action An action which specifies how the state should be updated.
 *
 * @return An updated state object.
 */
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
            selectedDomainVersion = state.defaultDomainVersion(DomainMap[action.domainName]),
            view = View.DOMAIN
        )
        is RivAction.SelectDomainVersion -> state.copy(
            selectedDomainVersion = action.domainVersion
        )
        is RivAction.ShowHiddenDomain -> state.copy(
            showHiddenDomain = action.isVisible
        )
        is RivAction.ShowHiddenVersion -> {
            val newState = state.copy(showHiddenVersion = action.isVisible)
            newState.copy(
                selectedDomainVersion = state.defaultDomainVersion(DomainMap[state.selectedDomainName])
            )
        }
        is RivAction.ShowRcVersion -> {
            val newState = state.copy(showRcVersion = action.isVisible)
            newState.copy(
                selectedDomainVersion = state.defaultDomainVersion(DomainMap[state.selectedDomainName])
            )
        }
        is RivAction.ShowUnderscoreVersion -> {
            val newState = state.copy(showUnderscoreVersion = action.isVisible)
            newState.copy(
                selectedDomainVersion = state.defaultDomainVersion(DomainMap[state.selectedDomainName])
            )
        }
        is RivAction.ShowTrunkVersion -> {
            val newState = state.copy(showTrunkVersion = action.isVisible)
            newState.copy(
                selectedDomainVersion = state.defaultDomainVersion(DomainMap[state.selectedDomainName])
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


