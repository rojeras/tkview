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

import kotlinx.browser.window
import io.kvision.redux.createReduxStore
import io.kvision.navigo.Navigo
import io.kvision.routing.Routing
import se.skoview.model.* // ktlint-disable no-wildcard-imports
import se.skoview.model.tpdbLoad
import se.skoview.view.ContractListRecord

/**
 * Main application controller object. Responsible for initialization, navigation and redux dispatching.
 */
object RivManager {

    /**
     * Navigo set up to handle routing.
     */
    private val routing = Navigo(null, true, "#")

    /**
     * The redux store is set up.
     */
    val rivStore = createReduxStore(::rivReducer, RivState())

    /**
     * Initializing and load of base data from the APIs.
     */
    fun initialize() {
        Routing.init()
        routing.initialize().resolve()
        takApiLoad()
        tpdbLoad()
        domdbLoad()
    }

    /**
     * Function to set view. Done via update of URL.
     *
     * @param view The view (page) to show.
     */
    fun fromAppShowView(view: View) {
        println("RivManager.fromAppShowView($view)")
        routing.navigate(view.url)
    }

    /**
     * Function invoked when URL is changed, by [Navigo.initialize], and which dispatch view action.
     *
     * @param view The view (page) to show. Used for domain- and contract lists.
     */
    fun fromUrlShowView(view: View) {
        println("RivManager.fromUrlShowView($view)")
        rivStore.dispatch(RivAction.SetView(view))
    }

    /**
     * Function to show domain page. Done via update of URL.
     *
     * @param domainName Name (namespace) of domain to show.
     */
    fun fromAppShowDomainView(domainName: String) {
        println("In fromAppShowDomainView, domainName = $domainName")
        routing.navigate(View.DOMAIN.url + "/$domainName")
    }

    /**
     * Function invoked when URL is changed to domain view (domain page).
     *
     * @param domainName Name (namespace) of domain to show.
     */
    fun fromUrlShowDomainView(domainName: String) {
        println("In fromUrlshowDomainView, domainName = $domainName")
        rivStore.dispatch(RivAction.SelectAndShowDomain(domainName))
    }

    /**
     * Function invoked when URL is changed to enable/disable (secret) admin view
     */
    fun fromUrlAdmin(onOff: String) {
        println("In fromUrlAdmin, onOff = $onOff")
        rivStore.dispatch(RivAction.SetAdminMode(onOff))
    }

    /**
     * Function to call to indicate domdb loading complete.
     */
    fun domdbLoadingComplete() {
        ContractListRecord.initialize()
        rivStore.dispatch(RivAction.DomdbLoadingComplete(true))
    }

    /**
     * Creates a state change event as a GUI refresh (state is not changed).
      */
    fun refresh() {
        rivStore.dispatch(RivAction.Refresh)
    }

    /**
     * Function to select a domain version.
     *
     * @param domainVersion Version to select.
     */
    fun selectDomainVersion(domainVersion: Version) {
        rivStore.dispatch(RivAction.SelectDomainVersion(domainVersion))
    }

    /**
     * Function to set/reset a state boolean flag.
     *
     * @param flag String with name of state property to set/reset.
     * @param value Boolean flag value.
     */
    fun setFlag(flag: String, value: Boolean) {
        when (flag) {
            RivAction.ShowHiddenDomain::class.simpleName -> rivStore.dispatch(
                RivAction.ShowHiddenDomain(
                    value
                )
            )
            RivAction.ShowHiddenVersion::class.simpleName -> rivStore.dispatch(
                RivAction.ShowHiddenVersion(
                    value
                )
            )
            RivAction.ShowTrunkVersion::class.simpleName -> rivStore.dispatch(
                RivAction.ShowTrunkVersion(
                    value
                )
            )
            RivAction.ShowRcVersion::class.simpleName -> rivStore.dispatch(RivAction.ShowRcVersion(value))
            else -> println("Error in RivManager.setFlag() - unknown flag $flag")
        }
    }

    /**
     * Function to request the a refresh of the server cache.
     */
    fun resetCache() {
        println("Manager reset cache")

        rivStore.dispatch(RivAction.DomdbLoadingComplete(false))

        val url = "${getBaseUrl()}/reset"
        getSync(url)

        window.location.reload()
    }
}
