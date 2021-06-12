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
import kotlinx.serialization.json.Json
import se.skoview.controller.RivManager
import se.skoview.controller.getAsync
import se.skoview.controller.getBaseUrl

@Serializable
data class TakApiDto(
    val answer: List<InstalledContracts>,
    val lastChangeTime: String
) {
    init {
        lastUpdateTime = lastChangeTime
    }
    companion object {
        lateinit var lastUpdateTime: String
    }
}

@Serializable
data class InstalledContracts(
    val id: Int,
    val connectionPoint: ConnectionPoint,
    val serviceContract: TakServiceContract
)

@Serializable
data class ConnectionPoint(
    val id: Int,
    val platform: String,
    val environment: String,
    val snapshotTime: String
)

@Serializable
data class TakServiceContract(
    val id: Int,
    val name: String = "",
    val namespace: String,
    val major: Int,
    val minor: Int
) {
    init {
        takInstalledContractNamespace.add(namespace)
    }
}

fun takApiLoad() {
    val url = "${getBaseUrl()}/http://api.ntjp.se/coop/api/v1/installedContracts"

    getAsync(url) { response ->
        println("Size of TAK-api InstalledContracts are: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        val takApiDto: TakApiDto = json.decodeFromString(TakApiDto.serializer(), response)
        console.log(takApiDto)
        console.log(takInstalledContractNamespace)
        RivManager.refresh()
    }
}

val takInstalledContractNamespace = mutableSetOf<String>()

fun takInstalledDomain(domainName: String): Boolean {
    val domain: TpdbServiceDomain? = tpdbDomainMap[domainName]
    if (domain == null) return false

    val tpdbDomainId = domain.id

    for ((_, tpdbServiceContract) in tpdbContractMap) {
        if (
            tpdbServiceContract.serviceDomainId == tpdbDomainId &&
            takInstalledContractNamespace.contains(tpdbServiceContract.namespace)
        )
            return true
    }
    return false
}
