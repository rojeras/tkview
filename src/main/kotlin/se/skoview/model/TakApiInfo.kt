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

/**
 * Holds the cached answer from TAK-api. Will be populated as part of the deserialization in [takApiLoad].
 *
 * @param answer A list of [InstalledContracts]
 * @param lastChangeTime Time when the server cache was last updated.
 */
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

/**
 * Kotlin class representing deserialized JSON data from TAK-api. Connects a contract [serviceContract] to a platform [connectionPoint].
 *
 * @param id API id.
 * @param connectionPoint Represent a platform.
 * @param serviceContract A service contract.
 */
@Serializable
data class InstalledContracts(
    val id: Int,
    val connectionPoint: ConnectionPoint,
    val serviceContract: TakServiceContract
)

/**
 * Kotlin class representing deserialized JSON data from TAK-api. Represent a platform.
 *
 * @param id API id.
 * @param platform Owner of the platform ("NTJP", "SLL"...)
 * @param environment Type of platform ("PROD", "QA", "TEST")
 * @param snapshotTime The last time this information was updated in the TAK-api.
 */
@Serializable
data class ConnectionPoint(
    val id: Int,
    val platform: String,
    val environment: String,
    val snapshotTime: String
)

/**
 * Kotlin class representing deserialized JSON data from TAK-api. Tak service contract.
 *
 * @property id API id.
 * @property name Contract name.
 * @property namespace Contract namespace.
 * @property major Major version.
 * @property minor Minor version.
 * @constructor Create empty Tak service contract
 */
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
        println("Saving contract $namespace")
    }
}

/**
 * Tak api load
 *
 */
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

/**
 * Tak installed domain
 *
 * @param domainName
 * @return Return <i>true</i> if any contract in the domain is installed in any platform, else <i>false</i>.
 */
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
