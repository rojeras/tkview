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

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import se.skoview.app.RivManager
import se.skoview.app.getAsync

@Serializable
data class TpdbServiceContract(
    val id: Int,
    val serviceDomainId: Int,
    val name: String,
    val namespace: String,
    val major: Int,
    val synonym: String? = null
) {
    init {
        tpdbContractMap[Pair(name, major)] = this
    }
}

@Serializable
data class TpdbServiceDomain(
    val id: Int,
    val domainName: String,
    val synonym: String? = null
) {
    init {
        tpdbDomainMap[domainName] = this
    }
}

val tpdbDomainMap by lazy { mutableMapOf<String, TpdbServiceDomain>() }
val tpdbContractMap: MutableMap<Pair<String, Int>, TpdbServiceContract> = mutableMapOf()

fun tpdbLoad() {
    // fun load(callback: () -> Unit) {
    println("In tpdbload()")

    val domainsUrl = "https://integrationer.tjansteplattform.se/tpdb/tpdbapi.php/api/v1/domains"

    // Older version which I try again to get it to create the actual parsed objects
    getAsync(domainsUrl) { response ->
        println("Size of domains are: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        val serviceDomains: List<TpdbServiceDomain> =
            json.decodeFromString(ListSerializer(TpdbServiceDomain.serializer()), response)
        console.log(serviceDomains)

        RivManager.refresh()
    }

    val contractsUrl = "https://integrationer.tjansteplattform.se/tpdb/tpdbapi.php/api/v1/contracts"

    getAsync(contractsUrl) { response ->
        println("Size of contracts are: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        val serviceContracts: List<TpdbServiceContract> =
            json.decodeFromString(ListSerializer(TpdbServiceContract.serializer()), response)
        console.log(serviceContracts)

        RivManager.refresh()
    }
}

fun mkHippoDomainUrl(domainName: String): String {
    val domain: TpdbServiceDomain? = tpdbDomainMap[domainName]
    if (domain == null) return ""

    if (!takInstalledDomain(domainName)) return ""

    return "https://integrationer.tjansteplattform.se/hippo/?filter=d${domain.id}"
}

fun mkHippoContractUrl(contractName: String, major: Int): String {
    val contract: TpdbServiceContract? = tpdbContractMap[Pair(contractName, major)]
    if (contract == null) return ""

    if (!takInstalledContractNamespace.contains(contract.namespace)) return ""

    return "https://integrationer.tjansteplattform.se/hippo/?filter=C${contract.id}"
}
