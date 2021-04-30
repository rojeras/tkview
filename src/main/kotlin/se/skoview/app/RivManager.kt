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
package se.skoview.app

import io.kvision.navigo.Navigo
import io.kvision.redux.createReduxStore
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import se.skoview.model.* // ktlint-disable no-wildcard-imports
import se.skoview.rivta.ContractListRecord

object RivManager {

    /**
     * The routing.navigate() calls below will set the URL AND invoke the callbacks in Routing.tk
     * The state is not updated automatically. It must be updated here and mirror the view to show.
     * When the view is updated main() will be called to display the view.
     */
    private val routing = Navigo(null, true, "#")

    val rivStore = createReduxStore(::rivReducer, RivState())

    fun initialize() {

        routing.initialize().resolve()

        GlobalScope.launch {
            val metaJob = GlobalScope.launch {
                println("Enter metaJob")
                loadApiItem("all.domainmeta.json", ListSerializer(DomainMeta.serializer()))
                println("Exit metaJob")
            }
            val tpdbDomainJob = GlobalScope.launch {
                println("Enter tpdbDomainJob")
                loadApiItem(
                    "https://integrationer.tjansteplattform.se/tpdb/tpdbapi.php/api/v1/domains",
                    ListSerializer(TpdbServiceDomain.serializer())
                )
                println("Exit tpdbDomainJob")
            }
            val tpdbContractJob = GlobalScope.launch {
                println("Enter tpdbContractJob")
                loadApiItem(
                    "https://integrationer.tjansteplattform.se/tpdb/tpdbapi.php/api/v1/contracts",
                    ListSerializer(TpdbServiceContract.serializer())
                )
                println("Exit tpdbContractJob")
            }

            val bbJob = GlobalScope.launch {
                println("Enter bbJob")
                bitbucketLoad()
                println("Exit bbJob")
            }

            println("Before joinAll()")
            joinAll(
                bbJob,
                metaJob,
                tpdbDomainJob,
                tpdbContractJob
            )
            println("After joinAll()")
            mkTkViewInfo()
        }
        println("After Globalscope")
    }

    fun fromUrlShowView(view: View) {
        println("RivManager.fromUrlShowView($view)")
        rivStore.dispatch(RivAction.SetView(view))
    }

    fun fromAppShowView(view: View) {
        println("RivManager.fromAppShowView($view)")
        routing.navigate(view.url)
    }

    fun fromUrlShowDomainView(domainName: String) {
        println("In fromUrlshowDomainView, domainName = $domainName")
        rivStore.dispatch(RivAction.SelectAndShowDomain(domainName))
    }

    fun fromUrlShowContractView(contractName: String) {
        println("In fromUrlShowContractView, contractName = $contractName")
        rivStore.dispatch(RivAction.SelectAndShowContract(contractName))
    }

    fun fromUrlAdmin(onOff: String) {
        println("In fromUrlAdmin, onOff = $onOff")
        rivStore.dispatch(RivAction.SetAdminMode(onOff))
    }

    fun fromAppShowDomainView(domainName: String) {
        println("In fromAppShowDomainView, domainName = $domainName")
        routing.navigate(View.DOMAIN.url + "/$domainName")
    }

    fun domdbLoadingComplete() {
        ContractListRecord.initialize()
        rivStore.dispatch(RivAction.DomdbLoadingComplete(true))
    }

    fun refresh() {
        rivStore.dispatch(RivAction.Refresh)
    }

    fun selectDomainType(type: DomainTypeEnum) {
        rivStore.dispatch(RivAction.SelectDomainType(type))
    }

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

    fun selectDomainVersion(domainVersion: Version) {
        rivStore.dispatch(RivAction.SelectDomainVersion(domainVersion))
    }

    fun resetCache() {
        println("Manager reset cache")

        rivStore.dispatch(RivAction.DomdbLoadingComplete(false))

        val url = "${getBaseUrl()}/reset"
        getSync(url)

        window.location.reload()
    }
}
